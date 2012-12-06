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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import net.gcalc.gcalc10.input.InputException;
import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.juu.parser.ParseException;

public class DoubleStringEditor extends DefaultCellEditor implements TableCellEditor  {
	JTextField tf;
	DoubleString doubleString;
	
	private FocusAdapter focusAdapter = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent event) {
			stopCellEditing();
		}

		@Override
		public void focusGained(FocusEvent e) {
			tf.setForeground(null);
		}

	};
	
	public DoubleStringEditor() {
		super(new JTextField());
		tf = (JTextField) editorComponent;
		tf.addFocusListener(focusAdapter);
		
	}
	
	@Override
	public boolean stopCellEditing() {
		String text = tf.getText();
		try {
			doubleString = DoubleString.getInstance(text);
		} catch (ParseException e) {
			tf.setForeground(Color.red);
			return false;
		}
		catch (InputException e) {
			tf.setForeground(Color.red);
			return false;
		}
		return super.stopCellEditing();
	}
	
	@Override
	public Object getCellEditorValue() {
		return doubleString;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		
		DoubleString ds = (DoubleString) value;
		
		tf.setText(ds.string);
		
		return tf;
	}

}
