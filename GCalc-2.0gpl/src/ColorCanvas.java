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

class ColorCanvas extends Canvas
{
    Image image;
    Graphics page;

    Color color;

    ColorCanvas(Color c)
    {
	super();
	setColor(c);
	this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    ColorCanvas(Color c, int x, int y)
    {
	this(c);
	setSize(x,y);
    }

    public void paint(Graphics g)
    {
	update(g);
    }

    public void update(Graphics g)
    {
	if (image==null) {
	    image = createImage(this.getSize().width, this.getSize().height);
	    page = image.getGraphics();

	    drawColor();
	}
	g.drawImage(image, 0,0, this);
    }

    public void setColor(Color c)
    {
	color = c;
	drawColor();
    }

    public void drawColor()
    {
	if (page==null)
	    return;

	if (color==null) {
	    page.setColor(Color.white);
	    page.fillRect(0,0,getSize().width, getSize().height);
	    page.setColor(Color.black);
	    page.drawLine(0,0,getSize().width, getSize().height);
	    page.drawLine(getSize().width,0,0, getSize().height);
	}
	else {
	    page.setColor(color);
	    page.fillRect(0,0,getSize().width, getSize().height);
	}

	page.setColor(Color.black);
	page.drawRect(0,0,getSize().width-1, getSize().height-1);

	repaint();
    }

    public Color getColor()
    {
	return color;
    }

}

class HSBCanvas extends ColorCanvas
{
    float[] hsb;

    HSBCanvas(Color c)
    {
	super(c);
    }

    HSBCanvas(Color c, int x, int y)
    {
	super(c,x,y);
    }
    /*
      public void setBrightness(float b)
      {
      hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
      setHSB(hsb[0],hsb[1],b);
      }

    */
    public void setSaturation(float s)
    {
	hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
	setHSB(hsb[0],s,hsb[2]);
    }

    /*    public float getBrightness()
	  {
	  hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
	  return hsb[2];
	  }
    */

    public float getSaturation()
    {
	hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
	return hsb[1];
    }

    public void setHSB(float h, float s, float b)
    {
	setColor(new Color(Color.HSBtoRGB(h,s,b)));
	drawColor();
    }

    int dx=10;
    int dy=10;

    public void drawColor()
    {
	//	System.out.println("HSB DrawColor\t"+page);

	if (page==null)
	    return;

	if (hsb==null)
	    hsb = new float[3];

	hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

	float h = hsb[0];
	float s = hsb[1];
	float b = hsb[2];
	for (float j=0; j<getSize().height; j+=dy)
	    for (float i=0; i<getSize().width; i+=dx) {
		h =(getSize().width-1-i)/getSize().width;
		b =(getSize().height-1-j)/getSize().height;

		page.setColor(new Color(Color.HSBtoRGB(h,s,b)));
		page.fillRect((int) i,(int) j,dx,dy);
	    }

	repaint();
    }

    public Color getColorAt(float x,float y)
    {
	if (hsb==null)
	    hsb = new float[3];

	hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

	hsb[0] =(getSize().width-1-x)/getSize().width;
	hsb[2] =(getSize().height-1-y)/getSize().height;

	return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

}

