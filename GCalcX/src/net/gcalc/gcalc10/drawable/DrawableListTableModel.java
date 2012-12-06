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
package net.gcalc.gcalc10.drawable;

import java.awt.Color;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;

public class DrawableListTableModel extends DefaultTableModel implements ListDataListener
{
	private DrawableList<ColoredDrawable> list;

	public DrawableListTableModel(DrawableList<ColoredDrawable> list) {
		this.list = list;
		this.list.getListModel().addListDataListener(this);
	}
		
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue==null) 
			return;
		
		if (column==1) {
			list.set(row, (ColoredDrawable) aValue);
		}
		else if (column==2) {
			list.get(row).setColor((Color) aValue);
		}
		
		fireTableDataChanged();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex>0;
	}

	public int getRowCount() {
		return list==null ? 0 : list.size();
	}

	public int getColumnCount() {
		return 3;
	}
	
	public Class<?> getColumnClass(int col) {
		Class<?>[] classes = {
				String.class,
				ColoredDrawable.class,
				Color.class
		};
		
		return classes[col];
	}
	
	

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0:
			return list.get(rowIndex).visit(new DrawableType());

		case 1:
			return list.get(rowIndex);
			
		case 2:
			return list.get(rowIndex).getColor();
		}
		
		return null;
	}

	public void intervalAdded(ListDataEvent e) {
		this.fireTableRowsInserted(e.getIndex0(), e.getIndex1());
	}

	public void intervalRemoved(ListDataEvent e) {
		this.fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
	}

	public void contentsChanged(ListDataEvent e) {
		this.fireTableDataChanged();
	}
}
