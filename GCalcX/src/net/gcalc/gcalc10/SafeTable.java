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

import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.gcalc.gcalc10.util.Util;

public class SafeTable extends JTable {
	public SafeTable() {
		super();
	}

	public SafeTable(TableModel dm) {
		super(dm);
	}

	public void setValueAt(Object aValue, int row, int column)
	{
		try {
			super.setValueAt(aValue, row, column);
		}
		catch (Exception e) {
			Util.issueErrorDialog(e, this);
		}
	}
}