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

Email: gcalc@humblestar.net
Web: http://www.humblestar.net/GCalc

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class ColorLabel extends Button implements ActionListener
{
    Color color;
    Frame f;
    Dialog d;

    ColorLabel(Frame f, Color c)
    {
	super();

	color =c;
	setBackground(c);

	this.addActionListener(this);

	this.f = f;
    }

    ColorLabel(Dialog d, Color c)
    {
	super();

	color =c;
	setBackground(c);

	this.addActionListener(this);

	this.d = d;
    }

    public void actionPerformed(ActionEvent e)
    {
	if (e.getSource()!=this)
	    return;

	if (f!=null)
	    setColor( ColorChooser.getColor(f, color));
	else  if (d!=null)
	    setColor( ColorChooser.getColor(d, color));

    }

    public void setColor(Color c)
    {
	setBackground(color = c);
    }

    public Color getColor()
    {
	return color=getBackground();
    }

}
