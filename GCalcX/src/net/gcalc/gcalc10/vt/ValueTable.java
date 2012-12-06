/** 
GCalcX
Copyright (C) 2010 Jiho Kim 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/
package net.gcalc.gcalc10.vt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import net.gcalc.gcalc10.editor.DoubleRenderer;
import net.gcalc.gcalc10.editor.HeteroTable;
import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.gcalc10.vt.ValueTableModel.Range;
import net.gcalc.juu.environment.GAngleUnit;
import net.gcalc.juu.environment.GNumber;
import net.gcalc.juu.environment.Variable;

public class ValueTable extends JPanel
{
	private ValueTableModel valueTableModel = new ValueTableModel();
	private int decimalPrecision = 6;

	public ValueTable() {
		super(new BorderLayout());

		final HeteroTable table = new HeteroTable(valueTableModel.getValueTableModel()); 
		TableCellRenderer renderer = new DoubleRenderer() {
			Color highlite = new Color(235,255,235);
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, 
					int row, int column) {
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (isSelected) {
					return component;
				}

				if (column<valueTableModel.getVariableCount()) {
					component.setBackground(highlite);
				}
				else {
					component.setBackground(table.getBackground());
				}

				return component;
			};
			
			public int getDecimalPrecision()
			{
				return decimalPrecision;
			}
		};
		table.putRenderer(Double.class, renderer);
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		final HeteroTable ranges = new HeteroTable(valueTableModel.getVariableModel());
		final HeteroTable expr = new HeteroTable(valueTableModel.getExpressionTableModel());
		
		JScrollPane scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollRanges = new JScrollPane(ranges, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollExpr = new JScrollPane(expr, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JButton addVariableButton = new JButton(new AbstractAction("Add Variable") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Variable v = Variable.get("z1");
				for (int i=0; i<=ranges.getRowCount(); i++) {
					v = Variable.get("z"+(i+1));
					if (! valueTableModel.hasVariable(v)) {
						break;
					}
				}

				valueTableModel.addVariable(v, new Range(new DoubleString(0),new DoubleString(1),1));
				
			}
		});

		final JButton removeVariableButton = new JButton(new AbstractAction("Remove Variable") {

			@Override
			public void actionPerformed(ActionEvent e) {
				valueTableModel.removeVariables(ranges.getSelectedRows());
			}
		});
		ranges.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] rows = ranges.getSelectedRows();
				int n = rows.length;
				
				boolean enable = 0<n && n<ranges.getRowCount();

				removeVariableButton.setEnabled(enable);
			}

		});
		removeVariableButton.setEnabled(false);

		JButton addExpressionButton = new JButton(new AbstractAction("Add Expression") {

			@Override
			public void actionPerformed(ActionEvent e) {
				valueTableModel.addExpression("0");
				expr.editCellAt(expr.getRowCount()-1, 0);
			}
		});

		final JButton removeExpressionButton = new JButton(new AbstractAction("Remove Expression") {

			@Override
			public void actionPerformed(ActionEvent e) {
				valueTableModel.removeExpressions(expr.getSelectedRows());
			}
		});
		removeExpressionButton.setEnabled(false);
		expr.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] rows = expr.getSelectedRows();

				removeExpressionButton.setEnabled(rows.length>0);
			}
		});
		

		Box rangeBox = Box.createHorizontalBox();
		rangeBox.add(Box.createHorizontalGlue());
		rangeBox.add(addVariableButton);
		rangeBox.add(Box.createHorizontalStrut(10));
		rangeBox.add(removeVariableButton);
		rangeBox.add(Box.createHorizontalGlue());

		Box exprBox = Box.createHorizontalBox();
		exprBox.add(Box.createHorizontalGlue());
		exprBox.add(addExpressionButton);
		exprBox.add(Box.createHorizontalStrut(10));
		exprBox.add(removeExpressionButton);
		exprBox.add(Box.createHorizontalGlue());

		JPanel rangePanel = new JPanel(new BorderLayout());
		rangePanel.add(BorderLayout.CENTER, scrollRanges);
		rangePanel.add(BorderLayout.SOUTH, rangeBox);

		JPanel exprPanel = new JPanel(new BorderLayout());
		exprPanel.add(BorderLayout.CENTER, scrollExpr);
		exprPanel.add(BorderLayout.SOUTH, exprBox);

		scrollTable.setBorder(BorderFactory.createTitledBorder("Values"));
		rangePanel.setBorder(BorderFactory.createTitledBorder("Independent Variables"));
		exprPanel.setBorder(BorderFactory.createTitledBorder("Expressions"));

		Box box = Box.createHorizontalBox();
		box.add(exprPanel);
		box.add(rangePanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, box, scrollTable);

		Box toolbox = Box.createHorizontalBox();
		final JSpinner js = new JSpinner(new SpinnerListModel(Arrays.asList(1,2,3,4,5,6,7,8,9,10)) );
		js.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				decimalPrecision = (Integer) js.getValue();	
				table.repaint();
			}			
		});

		JRadioButton radButton = new JRadioButton(new AbstractAction("Rad") {

			@Override
			public void actionPerformed(ActionEvent e) {
				GNumber.angleUnit = GAngleUnit.RADIAN;
				table.repaint();
			}
		});
		radButton.setSelected(true);

		JRadioButton degButton = new JRadioButton(new AbstractAction("Deg") {

			@Override
			public void actionPerformed(ActionEvent e) {
				GNumber.angleUnit = GAngleUnit.DEGREE;
				table.repaint();
			}
		});
		ButtonGroup angleButtonGroup = new ButtonGroup();
		angleButtonGroup.add(radButton);
		angleButtonGroup.add(degButton);

		js.setValue(6);
		js.setPreferredSize(new Dimension(30,24));
		js.setMaximumSize(new Dimension(30,24));
		toolbox.add(new JLabel("Precision"));
		toolbox.add(Box.createHorizontalStrut(2));
		toolbox.add(js);
		toolbox.add(Box.createHorizontalStrut(5));
		toolbox.add(radButton);
		toolbox.add(degButton);
		toolbox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.add(BorderLayout.SOUTH, toolbox);
		this.add(BorderLayout.CENTER, splitPane);

		splitPane.setDividerLocation(.3);
		splitPane.setResizeWeight(.2);
		splitPane.setContinuousLayout(true);
	}

	public ValueTableModel getModel() {
		return valueTableModel;
	}

	public static void main(String[] args) {

		ValueTable vt = new ValueTable();
		vt.getModel().addVariable(Variable.get("x"), new Range(new DoubleString(-10),new DoubleString(10),200));

		JFrame frame = new JFrame("Value Table");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.CENTER, vt);

		frame.setSize(800,600);
		frame.setVisible(true);
	}
}
