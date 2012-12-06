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
package net.gcalc.gcalc10.util;

import net.gcalc.juu.Evaluator;
import net.gcalc.juu.environment.GNumber;
import net.gcalc.juu.parser.ParseException;

public class DoubleString implements Comparable<DoubleString>{
	public final double value;
	public final String string;
	
	public DoubleString(double value) {
		this(Double.toString(value), value);
	}
	
	protected DoubleString(String s, double value) {
		string = s;
		this.value = value;
	}
	
	public static DoubleString getInstance(String s) throws ParseException
	{	
		Evaluator evaluator = new Evaluator();
		try {
			evaluator.evaluate(s+";");
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new DoubleString(s, Double.POSITIVE_INFINITY);
		}
		Object object = evaluator.getLast();
		if (object instanceof GNumber) {
			double val = ((GNumber) object).doubleValue();
			if (! Double.isInfinite(val) && ! Double.isNaN(val)) {
				return new DoubleString(s,val);
			}
		}
		throw new RuntimeException();
	}
	
	public String toString() {
		return string;
	}

	public int compareTo(DoubleString o) {
		return (value < o.value) ? -1 : (value==o.value) ? 0 : 1; 
	}
}
