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


package net.gcalc.plugin.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JTextField;

public class RecallJTextField extends JTextField implements KeyListener
{
	private int cursor;
	private Vector history;

	public RecallJTextField(String s)
	{
		super(s);

		clear();
		this.addKeyListener(this);
	}

	public RecallJTextField(String s, int len)
	{
		super(s, len);

		clear();
		this.addKeyListener(this);
	}

	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		int size = history.size();

		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_KP_UP)
		{
			cursor = (cursor + size - 1) % size;
			this.setText((String) history.elementAt(cursor));
		}
		else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_KP_DOWN)
		{
			cursor = (cursor + 1) % size;
			this.setText((String) history.elementAt(cursor));
		}
	}
	public void keyReleased(KeyEvent e)
	{}
	public void keyTyped(KeyEvent e)
	{}

	public void resetRecallCursor()
	{
		cursor = 0;
	}

	public void addToHistory(String s)
	{
		if (!history.lastElement().equals(s))
		{
			history.add(s);
		}
		resetRecallCursor();
	}

	public void clear()
	{
		history = new Vector();
		history.add("");
		resetRecallCursor();
	}

}

