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
package net.gcalc.gcalc10.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
	Color currentColor = null;
	JColorChooser colorChooser = new JColorChooser();
	JDialog dialog = null;
	JButton button;
	
	protected int clickCountToStart = 2;
	
	public ColorEditor() {
		button = new JButton(new AbstractAction("Edit") {
			public void actionPerformed(ActionEvent e) {
				if (dialog==null) {
					Window window = SwingUtilities.windowForComponent(button);
					dialog = JColorChooser.createDialog(window,"Color Chooser",true,colorChooser,ColorEditor.this,null);
				}
				
				colorChooser.setColor(currentColor);
				dialog.setVisible(true);
				fireEditingStopped();
			}
		});
		button.setMargin(new Insets(0,0,0,0));
	}
	
	public void actionPerformed(ActionEvent e) {
		currentColor = colorChooser.getColor();
	}
	
	public Object getCellEditorValue() {
		return currentColor;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		currentColor = (Color) value;
		return button;
	}
	
	public void setClickCountToStart(int n)
	{
		clickCountToStart = n;
	}
	
	public boolean isCellEditable(EventObject e) {
		if (e instanceof MouseEvent) {
			return ((MouseEvent) e).getClickCount() >= clickCountToStart;
		}
		
		return true;
	}
	
	

	
}
