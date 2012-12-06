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

Email: jiho@gcalcul.us
Web: http://gcalcul.us

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.calc.gui.gradient;

import java.awt.Color;



/**
 * @author jkim
 */
public abstract class Gradient
{
	public static final int HSB_MODE=1;
	public static final int RGB_MODE=0;
	
	private int mode;
	
	protected Gradient(int m)
	{
		setMode(m);
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void setMode(int m)
	{
		mode = m;
	}
	
	public abstract Color getColor(double p);
	
	protected float[] getHSB(Color c, float[] hsbvals)
	{
		return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
	}
	
	public Color[] getPalette(int num)
	{
		if (num<2)
			throw new IllegalArgumentException("Cannot provide a palette with "+num+" colors.");
		
		Color[] palette = new Color[num];
		double max = (double) (num-1);
		for (int i=0; i<palette.length; i++)
			palette[i]=getColor(i/max);
		
		return palette;
	}
}

