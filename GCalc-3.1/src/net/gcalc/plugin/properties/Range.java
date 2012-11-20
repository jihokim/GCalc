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


package net.gcalc.plugin.properties;

import java.util.Observable;

public class Range extends Observable {
    public final static int MIN = 0;
    public final static int MAX = 1;
    public final static int SCALE = 2;
    
    private double min, max, scl;

	public Range() {
		this(0, 1, .1);
	}
	
	public Range(double l, double r)
	{
		this(l,r,Double.MAX_VALUE);
	}

	public Range(double l, double r, double s) {
		set(l, r, s);
	}

	private void set(double l, double r, double s) {
		if (!isValid(l, r, s))
			throw new IllegalArgumentException();

		min = l;
		max = r;
		scl = s;
	}


//	private void set(Range r) {
//		min = r.getMin();
//		max = r.getMax();
//		scl = r.getScale();
//		setChanged();
//		notifyObservers();
//	}

	public static boolean isValid(double l, double r, double s) {
		return !Double.isNaN(l)
			&& !Double.isNaN(r)
			&& !Double.isNaN(s)
			&& l < r
			&& s > 0;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getScale() {
		return scl;
	}

	public double getWidth() {
		return Math.abs(max - min);
	}
	
	public double getCenter()
	{
	    return (max+min)/2;
	}

	public String toString()
	{
			return "["+min+" to "+max+" by " +scl +"]";
	
	}
}

