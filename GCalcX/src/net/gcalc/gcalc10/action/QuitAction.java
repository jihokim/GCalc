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
package net.gcalc.gcalc10.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.gcalc.gcalc10.GCalcInit;

public class QuitAction extends AbstractAction {
	private Component parent;
	private GCalcInit init;
	public QuitAction(GCalcInit init, Component parent) {
		super("Quit");
		this.init = init;
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		int selection = JOptionPane.showConfirmDialog(parent,
				init.getString("dialog.goodbye.message"), init.getString("dialog.goodbye.title"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (selection == JOptionPane.YES_OPTION)
			System.exit(0);
	}
}
