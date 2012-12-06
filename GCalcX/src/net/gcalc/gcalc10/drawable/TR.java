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
import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.gcalc10.util.Util;

public  class TR extends ColoredDrawable {
	private Function f;
	private DoubleString tmin = Util.get("0");
	private DoubleString tmax = Util.get("2pi");
	private int tsegs = 100;
	private boolean useDefaultDomain = true;
	private SketchingAlgorithm algorithm = SketchingAlgorithm.CONNECT_THE_DOTS; 

	public TR(TR tr) {
		super(tr.getColor());
		f = tr.f;
		tmin = tr.tmin;
		tmax = tr.tmax;
		tsegs = tr.tsegs;
		useDefaultDomain = tr.useDefaultDomain;
		this.algorithm = tr.algorithm;
	}

	public TR(String expr) {
		setR(expr);
	}

	public Function getR() {
		return f;	
	}

	public void setR(String expr)  {
		f = Function.createFunction(expr, T_VAR);
	}


	public void setR(Function f)  {
		this.f = f;
	}

	@Override
	public <T> T visit(DrawableVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public void setUseDefaultDomain(boolean b) {
		useDefaultDomain = b;
	}

	public boolean useDefaultDomain() {
		return useDefaultDomain;
	}

	public DoubleString getTmin() {
		return tmin;
	}

	public void setTmin(DoubleString tmin) {
		this.tmin = tmin;
	}

	public DoubleString getTmax() {
		return tmax;
	}

	public void setTmax(DoubleString tmax) {
		this.tmax = tmax;
	}

	public int getTsegs() {
		return tsegs;
	}

	public void setTsegs(int tsegs) {
		this.tsegs = tsegs;
	}

	public SketchingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SketchingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public boolean isUseDefaultDomain() {
		return useDefaultDomain;
	}

	public String toString() {
		return f.expression();
	}
}