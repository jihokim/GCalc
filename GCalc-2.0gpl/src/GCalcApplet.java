/*  
GCalc 2.0 - Graphing calculator applet
Copyright (C) 2001 Jiho Kim

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

Email: jiho@gcalcul.us
Web: http://gcalcul.us

*/
import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.EmptyStackException;

public class GCalcApplet extends Applet implements ActionListener
{
    String notice = "GCalc\nVersion 2.0\nCopyright 1999-2003 Jiho Kim\n";
    GCalc gcalc;
    Button b = new Button("Click to Start GCalc");

    public void init()
    {
	this.setLayout(new BorderLayout());
	add("Center",b);

	b.addActionListener(this);
    }

    public void start()
    {
	System.out.println(notice);
    }

    public void destroy()
    {
	gcalc=null;
	b = null;
    }

    public void actionPerformed(ActionEvent e)
    {
	Object o = e.getSource();

	if (o==b) {
	    gcalc = new GCalc();

	}
    }
}

