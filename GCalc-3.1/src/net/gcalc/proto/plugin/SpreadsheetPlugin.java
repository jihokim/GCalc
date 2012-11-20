/** 
GCalc 3.0
Copyright (C) 2005 Jiho Kim 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Email: jiho@gcalc.net
Web: http://gcalc.net

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.proto.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.gcalc.calc.main.AbstractPlugin;
import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.SymbolTableException;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.CircularDefinitionException;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.Token;
import net.gcalc.calc.parser.VariableToken;

public class SpreadsheetPlugin extends AbstractPlugin {

	private FormulaField textfield;
	private Spreadsheet sheet;
	private Listener columnListener,rowListener;

	public SpreadsheetPlugin() {
		super();
	
		sheet = new Spreadsheet();
		JScrollPane jsp = new JScrollPane(sheet,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		getContentPane().add(BorderLayout.NORTH, textfield = new FormulaField((SpreadsheetModel) sheet.getModel()));
		getContentPane().add(BorderLayout.CENTER,jsp);
		
		columnListener = new Listener();
		rowListener = new Listener();
		
		sheet.getSelectionModel().addListSelectionListener(rowListener);
		sheet.getColumnModel().getSelectionModel().addListSelectionListener(columnListener);
		
	}
	
	public void init() 
	{
		setSize(400, 500);
		setResizable(true);
	}
	
	
	public String getPluginName() {
		return "Spreadsheet Plugin";
	}

	public String getCreatorName() {
		return "Jiho Kim (jiho@gcalc.net)";
	}

	public String getDescription() {
		return "<p>Spreadsheet prototype</p>";
	}
	
	class Listener implements ListSelectionListener
	{
		private int k=-1;
		public void valueChanged(ListSelectionEvent e) {
			k = ((ListSelectionModel) e.getSource()).getMinSelectionIndex();
			changeSelection();
		}
		
		public int getIndex()
		{
			return k;
		}
	}
	
	private void changeSelection()
	{
		int row = rowListener.getIndex();
		int col = columnListener.getIndex();
		
		Object value = null;
		if (row>=0 && col>=0) {
			value = sheet.getValueAt(row,col);
			if (value!=null && (value instanceof Formula)) {
				textfield.setFormula(row,col, (Formula) value);
			}
			else {
				if (value!=null)
					textfield.setText(value.toString());
				else
					textfield.setText("");
			}
		}
		
		
	//	System.out.println(row+" "+col);
	}
}


class FormulaField extends JTextField implements ActionListener
{
	private int row, col;
	private SpreadsheetModel tableModel;
	
	public FormulaField(SpreadsheetModel tm)
	{
		super();
		tableModel = tm;
		addActionListener(this);
	}
	
	public void setFormula(int r, int c, Formula f)
	{
		row = r;
		col = c;
		
		setText(f.getFormulaString());
	}
	
	public void actionPerformed(ActionEvent e)
	{
		tableModel.setValueAt(getText(), row,col);
	}
}



class Spreadsheet extends JTable 
{
	public Spreadsheet()
	{
		super(new SpreadsheetModel());
	
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
			
	//	setDefaultEditor(Formula.class, new FormulaCellEditor());
	}

	
}

class FormulaCellEditor extends DefaultCellEditor
{
	/**
	 * @param arg0
	 */
	public FormulaCellEditor() {
		super(new JTextField());
		// TODO Auto-generated constructor stub
	}
	
	public Component getTableCellEditorComponent(JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column)	
	{
		Component c = super.getTableCellEditorComponent( table, value, isSelected, row, column); 
	
		if (value instanceof Formula) {
			Formula formula = (Formula) value;
			JTextField jtf = (JTextField) (c);
			jtf.setText(formula.getFormulaString());
			
			System.out.println(formula);
		}
		
		System.out.println(value);
		
		return c;
	}
}

class SpreadsheetModel extends DefaultTableModel
{
	private SymbolTable st;
	private ValueTable vt;
	
	public SpreadsheetModel() {
		super();
		
		st = new SymbolTable();
		vt = new ValueTable();
	}
	
	public int getColumnCount() { return 5; }
	public int getRowCount() { return 100;}
      
	public String getColumnName(int col)
	{
		int c = 'A'+(col%26);
		return new String(new char[]{(char) c});
	}
	
	public void setValueAt(Object val, int row, int col)  
	{
		if (val instanceof String) {
			String str = ((String) val).trim();
			
			//deal with notation from normal spreadsheets
			if (str.startsWith("="))
				str = str.substring(1);
			
			//try to make a formula if we can.
			try {
				if (str.length()!=0) {
					Function f = FunctionFactory.getFunction(str);
					val = new Formula(f,st,vt);
					Token tk = new VariableToken(getColumnName(col)+(row+1));
					st.setVariable(tk, f);
				}
			} 
			catch (BadSyntaxException e) {
				System.out.println(e);
				val = "\""+str+"\"";
			}			
			catch (CircularDefinitionException e)
			{
				System.out.println(e);
				throw new BadCellInputException();
			}
			catch (SymbolTableException e)
			{
				System.out.println(e);
				throw new BadCellInputException();
			}
		}				
		
		super.setValueAt(val,row,col);
	}
	

	

}

class Formula 
{
	private Function function;
	private SymbolTable st;
	private ValueTable vt;
		
	public Formula(Function f, SymbolTable st, ValueTable vt)
	{
		function = f;
		this.st = st;
		this.vt = vt;
	}
	
	public String toString() {
		return ""+function.evaluate(st,vt);
	}
	
	public String getFormulaString()
	{
		return function.getID().trim();
	}
}

class BadCellInputException extends RuntimeException
{
	public BadCellInputException()
	{
		super();
	}
}
