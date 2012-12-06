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
package net.gcalc.gcalc10.drawable;

import net.gcalc.gcalc10.drawable.DrawableSketcher.SketchingAlgorithm;
import net.gcalc.gcalc10.script.Function;

public class XY extends ColoredDrawable {
	private Function f;
	private SketchingAlgorithm algorithm = SketchingAlgorithm.CONNECT_THE_DOTS;
	
	public XY(XY xy) {
		super(xy.getColor());
		f = xy.f;
		algorithm = xy.algorithm;
	}
	
	public XY(String expr) {
		setY(expr);
	}
	
	public void setY(String expr)  {
		f = Function.createFunction(expr, X_VAR);
	}
	
	public void setY(Function f) {
		this.f = f;
	}
	
	public Function getY()
	{
		return f;
	}
	
	@Override
	public <T> T visit(DrawableVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return f.expression();
	}

	public SketchingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SketchingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
}