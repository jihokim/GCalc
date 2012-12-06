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

import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * Encapsulation of a canvas with an persistent image.
 */
public class ImageCanvas extends Canvas
{
    private Image im;
    private Image image;
    private Graphics page;

    ImageCanvas(Image i, int x, int y)
    {
	super();
	image = i;
	setSize(x,y);
    }

    public void paint(Graphics g)
    {
	update(g);
    }

    public void update(Graphics g)
    {
	if (im==null) {
	    im = createImage(this.getSize().width, this.getSize().height);
	    page = im.getGraphics();
	    page.drawImage(image, 0,0, this);
	}

	g.drawImage(im, 0,0, this);
    }

}
