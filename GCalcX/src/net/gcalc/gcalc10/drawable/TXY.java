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

public class TXY extends ColoredDrawable {
	private Function x, y;
	private DoubleString tmin = Util.get("0");
	private DoubleString tmax = Util.get("2pi");
	private int tsegs = 100;
	private boolean useDefaultDomain = true;
	private SketchingAlgorithm algorithm = SketchingAlgorithm.CONNECT_THE_DOTS;
	
	public TXY(TXY txy) {
		super(txy.getColor());
		x = txy.x;
		y = txy.y;
		tmin = txy.tmin;
		tmax = txy.tmax;
		tsegs = txy.tsegs;
		this.useDefaultDomain = txy.useDefaultDomain;
		this.algorithm = txy.algorithm;
	}	

	public TXY(String x, String y) {
		setXY(x,y);
	}
	
	public void setXY(String x, String y) {
		this.x = Function.createFunction(x, T_VAR);
		this.y = Function.createFunction(y, T_VAR);
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
		if (tmax.compareTo(tmin)<=0) {
			throw new IllegalArgumentException();
		}
		
		this.tmax = tmax;
	}

	public int getTsegs() {
		return tsegs;
	}

	public void setTsegs(int tsegs) {
		this.tsegs = tsegs;
	}

	@Override
	public <T> T visit(DrawableVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	public Function getX() {
		return x;
	}
	
	public Function getY() {
		return y;
	}
	
	public void setX(Function x) {
		this.x = x;
	}
	
	public void setY(Function y) {
		this.y = y;
	}	
	
	public SketchingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SketchingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String toString() {
		return "("+x+","+y+")";
	}
}