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


package net.gcalc.plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.gui.PropertiesPanel;
import net.gcalc.plugin.gui.RecallJTextField;
import net.gcalc.plugin.plane.gui.FlagPanel;
import net.gcalc.plugin.plane.gui.ShowPropertiesPanelAction;
import net.gcalc.plugin.plane.gui.ViewPanel;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Range;
import net.gcalc.plugin.properties.View;

public class TableViewPlugin extends AbstractCartesianGraphPlugin implements Observer {
	protected double xmin, xmax, xscale; 
	
	
	protected JTable jt;
	protected MyTableModel tableModel;

	protected RecallJTextField inputTF;

	protected JButton inputButton;

	protected JMenuItem clearOutput, clearHistory, save, print, close, copy, cut, paste, showHelp, aboutPlugin;

	private ValueTable vt;
	
	private GraphProperties properties;

	private LocalActionListener lal;

	public TableViewPlugin() {
		super();
		vt = new ValueTable();
		
		properties = new GraphProperties();
		
		Range R = new Range(-10,10,.25);
		View view = new View(R);
		
		properties.put(GraphProperties.VIEW, view);
		tableModel = new MyTableModel(view);
		properties.addObserver(this);
	}

	public void init() {
		if (this.jt != null)
			return;

		jt = new JTable(tableModel) {
			protected JTableHeader createDefaultTableHeader() {
		        return new JTableHeader(columnModel) {
		            public String getToolTipText(MouseEvent e) {
		                java.awt.Point p = e.getPoint();
		                int index = columnModel.getColumnIndexAtX(p.x);
		                int realIndex = 
		                        columnModel.getColumn(index).getModelIndex();
		                
		                if (realIndex==0)
		                	return null;
		                
		                return ((Function) tableModel.F.elementAt(realIndex-1)).toInfix();
		            }
		        };
		    }
		};
		
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jt.getColumnModel().setColumnSelectionAllowed(true);

		Box inputBox = Box.createHorizontalBox();

		inputButton = new JButton("Add Column!");

		inputBox.add(inputTF = new RecallJTextField(""));
		inputBox.add(inputButton);
		inputBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Input"));

		lal = new LocalActionListener();

		inputButton.addActionListener(lal);
		inputTF.addActionListener(lal);

		Box componentBox = Box.createVerticalBox();
		componentBox.add(new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

		componentBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Table View"));

		this.getContentPane().add("Center", componentBox);
		this.getContentPane().add("North", inputBox);

		this.setJMenuBar(initMenuBar());
		initPropertiesPanel();

		setSize(400, 500);
		setResizable(true);
		inputTF.requestFocus();
	}
	
	protected JPanel makeFlagPanel()
    {
      return new FlagPanel(getProperties());
    }
	
    protected JPanel makeViewPanel()
	{
	        return new ViewPanel(getProperties(), false);
	}
	
	 protected void initPropertiesPanel()
	    {	
	 	System.out.println("hello");
	        //make various auxillery panels.
	    //    JPanel flagPanel = makeFlagPanel();
	        Box viewPanel = SwingGUI.wrap(makeViewPanel());
	  //      JPanel functionListPanel = makeModelListPanel();
	      //  colorChooser = new SimpleColorChooser(firstColor());
	        
	        PropertiesPanel propertiesDialog = getPropertiesPanel();
	        
	        //propertiesDialog.addTab("Color", colorChooser);
	   //     propertiesDialog.addTab("Function List", functionListPanel);    
	  //      propertiesDialog.addTab("Properties", flagPanel);
	        propertiesDialog.addTab("View", viewPanel);    
	     //	propertiesDialog.pack();
	    }

	/**
	 * Build and return the MenuBar for the plugin.
	 */
	protected JMenuBar initMenuBar() {
		JMenuBar jmb = new JMenuBar();
		JMenu editMenu = new JMenu("Edit");
		JMenu helpMenu = new JMenu("Help");
		JMenu pluginMenu = new JMenu("TableView");

		pluginMenu.add(clearOutput = new JMenuItem("Clear Table"));
		pluginMenu.add(clearHistory = new JMenuItem("Clear Input History"));
		pluginMenu.addSeparator();
		pluginMenu.add(close = new JMenuItem("Close Plugin"));

		close.addActionListener(new HideActionListener());
		//	clearOutput.addActionListener(new
		// TextComponentClearActionListener(jep));
		clearOutput.addActionListener(new TableClearActionListener());
		clearHistory.addActionListener(new TextComponentClearActionListener(inputTF));
	

		editMenu.add(copy = new JMenuItem("Copy"));
		editMenu.add(cut = new JMenuItem("Cut"));
		editMenu.add(paste = new JMenuItem("Paste"));
		editMenu.addSeparator();
		editMenu.add(new ShowPropertiesPanelAction(this));

		helpMenu.add(showHelp = new JMenuItem("Help..."));
		helpMenu.add(aboutPlugin = new JMenuItem("About Plugin..."));

		jmb.add(pluginMenu);
		jmb.add(editMenu);
		
		jmb.add(Box.createHorizontalGlue());
		jmb.add(helpMenu);

		return jmb;
	}
	
	public void update(Observable obj, Object key)
	{
	    if (key.equals(GraphProperties.VIEW))
	        tableModel.updateView(properties.getViewProperty(GraphProperties.VIEW));
	}

	public String inputBorderText() {
		return "Table View";
	}

	public String getPluginName() {
		return "Table Plugin";
	}

	public String getCreatorName() {
		return "Jiho Kim (jiho@gcalc.net)";
	}

	public String getDescription() {
		return "<p>Shows table of function values.</p>";
	}
	
	public GraphProperties getProperties()
	{
	    return properties;
	}

	class LocalActionListener implements ActionListener {
		private StringBuffer sb;

		public void actionPerformed(ActionEvent e) {
			String input = inputTF.getText().trim();

			if (input.length() == 0)
				return;

			try {
				Function F = FunctionFactory.getFunction(input);
				tableModel.addFunction(F);
				
				//System.out.println(F);
			} catch (BadSyntaxException exception) {
				String message = exception.getMessage();
				outputErrorMessage(message);
			}

			inputTF.addToHistory(input);
			inputTF.setText("");

		}

		private void outputErrorMessage(String message) {
			if (message == null)
				message = "";

			sb.append("<div align=right><font color=red>ERROR:" + message + "</font></div>");
		}

	}

	class MyTableModel extends AbstractTableModel {
		private Vector F;
		private double xmax, xmin, xscale;
		
		public MyTableModel(View view)
		{
		    updateView(view);
			clear();
		}
		
		public void updateView(View view)
		{
		    Range range = view.getRange(0);
		    
		    xmax = range.getMax();
		    xmin = range.getMin();
		    xscale = range.getScale();
		    fireTableStructureChanged() ;
		}
		
		public void clear()
		{
			F = new Vector();
			fireTableStructureChanged() ;
		
		}
		public Function getFunction(int i)
		{
			return (Function) F.elementAt(i);
		}

		public void addFunction(Function f)
		{
			F.add(f);
			fireTableStructureChanged() ;
		}

		public int getRowCount() {
		   return (int) ((xmax-xmin)/xscale+.5)+1;
		}

		public int getColumnCount() {
			return F.size()+1;
		}
		public String getColumnName(int column) {
			if (column==0)
				return "x";
			
			return getFunction(column-1).toInfix();
		}
		
		private double varValueAt(double row)
		{
		   return xmin+row*xscale;
		}

		public Object getValueAt(int row, int column) {

			if (column == 0)
				return "" + varValueAt(row);
			
			String s = null;
			try {
				s = ""+ evaluateFunction((Function) F.elementAt(column-1), varValueAt(row));
			}
			catch(Exception e) {
				s="";
			}
			
				return s;
		}

		private double evaluateFunction(Function f, double x) {
			vt.setValue(VariableToken.X_VAR, x);
			return f.evaluate(vt);
		}
	}

	class HideActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			shutdown();
		}
	}

	class TextComponentClearActionListener implements ActionListener {
		private JTextComponent tc = null;

		public TextComponentClearActionListener(JTextComponent tc) {
			this.tc = tc;
		}

		public void actionPerformed(ActionEvent e) {
			tc.setText("");
			if (tc instanceof RecallJTextField)
				((RecallJTextField) tc).clear();
		}
	}
	
	class TableClearActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			tableModel.clear();
		}
	}

}

