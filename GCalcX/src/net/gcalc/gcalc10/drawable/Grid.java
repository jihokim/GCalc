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
import java.awt.Paint;

public class Grid implements Drawable {
	public Paint gridPaint;
	public  Paint axesPaint;
	public  Paint ticksPaint;
	public double xscale;
	public double yscale;
	
	public Grid(double x, double y, Paint gp, Paint ap, Paint tp) {
		gridPaint = gp;
		axesPaint = ap;
		ticksPaint = tp;
		xscale = x;
		yscale = y;
	}
	
	public Grid(double x, double y) {
		this(x, y, Color.LIGHT_GRAY, Color.RED, Color.BLACK);
	}
	
	public <T> T visit(DrawableVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	public void setXGap(double x) {
		xscale = x;
	}
	public void setYGap(double y) {
		yscale = y;
	}

}
