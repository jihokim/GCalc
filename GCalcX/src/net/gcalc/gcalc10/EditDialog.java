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
package net.gcalc.gcalc10;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import net.gcalc.gcalc10.drawable.AbstractDrawableTableModel;
import net.gcalc.gcalc10.drawable.ColoredDrawable;
import net.gcalc.gcalc10.drawable.DrawableTableModelVisitor;
import net.gcalc.gcalc10.editor.HeteroTable;

public class EditDialog extends JDialog {
	private DrawableTableModelVisitor dftm = new DrawableTableModelVisitor();
	private AbstractDrawableTableModel<? extends ColoredDrawable> model;
	private JTable localTable = new HeteroTable() {
		public void setModel(TableModel model) {
			super.setModel(model);
			if (model.getColumnCount()>0) {
				this.getColumnModel().getColumn(0).setMaxWidth(100);

				for (int i=0; i<model.getRowCount(); i++) {
					TableCellEditor editor = getCellEditor(i,1);
					if (editor instanceof DefaultCellEditor) {
						((DefaultCellEditor) editor).setClickCountToStart(1);
					}
				}
			}		
		}
	};
	private Object action;


	private JButton ok = new JButton(new AbstractAction("OK") {
		public void actionPerformed(ActionEvent e) {
			action = ok;
			TableCellEditor editor = localTable.getCellEditor();
			if (editor!=null)
				editor.stopCellEditing();
			setVisible(false);
		}
	});

	private JButton cancel = new JButton(new AbstractAction("Cancel") {
		public void actionPerformed(ActionEvent e) {
			action = cancel;
			setVisible(false);
		}
	});


	public EditDialog(Window c) {
		super(c);
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(ok);
		box.add(Box.createHorizontalStrut(10));
		box.add(cancel);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(10,10,10,10));

		JPanel panel = new JPanel(new BorderLayout());				
		panel.setBorder(new EmptyBorder(10,10,10,10));
		panel.add(BorderLayout.CENTER, new JScrollPane(localTable));

		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getContentPane().add(BorderLayout.CENTER, panel);
		getContentPane().add(BorderLayout.SOUTH, box);
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);			
	}

	public ColoredDrawable edit(ColoredDrawable f) {
		action = cancel;
		model = f.visit(dftm);
		localTable.setModel(model);	
		
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);

		return action==cancel ? null : model.getValue();
	}
}
