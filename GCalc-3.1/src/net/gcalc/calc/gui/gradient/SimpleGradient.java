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


package net.gcalc.calc.gui.gradient;

import java.awt.Color;



/**
 * @author jkim
 */
public class SimpleGradient extends Gradient
{
	private Color left, right;
	
	public SimpleGradient()
	{
		this(Color.black, Color.black);
	}
	
	public SimpleGradient(Color l, Color r)
	{
		this(l, r, Gradient.RGB_MODE);
	}
	
	public SimpleGradient(Color l, Color r, int mode)
	{
		super(mode);
		left = l;
		right = r;
	}
	
	public Color getColor(double p)
	{
		if (p<0 || p>1)
			throw new IllegalArgumentException ("Gradient parameter must be between 0 and 1");
		
		float[] c = new float[3];
		float[] leftComp = left.getRGBColorComponents(null);
		float[] rightComp = right.getRGBColorComponents(null);
		
		//assuming that there are two mode, HSB and RGB. RGB is default.
		if (getMode()==Gradient.HSB_MODE) {
			leftComp = getHSB(left, leftComp);
			rightComp = getHSB(right, rightComp);
					
		}
				
		//compute weighted average
		for(int i=0; i<leftComp.length; i++) {
			c[i]=(float) (p*rightComp[i]+(1-p)*leftComp[i]);
		}
		
		//construct the color
		Color color = null;
		if (getMode()==Gradient.HSB_MODE) {
			color = Color.getHSBColor(c[0],c[1],c[2]);
		}
		else {
			color = new Color(c[0], c[1],c[2]);
		}
		
		return color;
			
	}
}

