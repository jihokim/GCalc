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
/**
 *  GCalc 10 - Copyright Jiho Kim 2010
 *
 *  Do not redistribute.
 */

package net.gcalc.gcalc10.drawable;

import java.awt.Color;
import java.text.DecimalFormat;

public class Labels implements Drawable {
	public final Color color = Color.black;
	private double xscale;
	private double yscale;
	private static DecimalFormat decimalFormat = new DecimalFormat();
	
	public Labels(double x, double y) {
		xscale = x;
		yscale = y;
		decimalFormat.setMinimumFractionDigits(0);
		decimalFormat.setMaximumFractionDigits(3);
		decimalFormat.setGroupingUsed(false);
	}
	
	public static String convert(double x)
	{
		if (Math.abs(x)>1e5 || (x!=0 && Math.abs(x)<1e-2)) {
			decimalFormat.applyPattern("0.#####E0");
		}
		else {
			decimalFormat.applyPattern("####0.###");
		}
		
		String s = decimalFormat.format(x);

		return s;
	}
	
	public <T> T visit(DrawableVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public double getXscale() {
		return xscale;
	}

	public void setXscale(double xscale) {
		this.xscale = xscale;
	}

	public double getYscale() {
		return yscale;
	}

	public void setYscale(double yscale) {
		this.yscale = yscale;
	}

}
