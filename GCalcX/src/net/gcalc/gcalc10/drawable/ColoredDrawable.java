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

import net.gcalc.juu.environment.Variable;

public abstract class ColoredDrawable implements Drawable {
	protected final static Variable X_VAR = Variable.get("x"); 
	protected final static Variable Y_VAR = Variable.get("y"); 
	protected final static Variable T_VAR = Variable.get("t"); 
	private Color color;
	
	protected ColoredDrawable() {
		color = Color.black;
	}
	
	protected ColoredDrawable(Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
