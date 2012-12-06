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


package net.gcalc.plugin.properties;

public class View {
	private Range[] ranges = null;
	private String[] names = null;

	public View(Range x, Range y) {
		ranges = new Range[] {x, y};
		names = new String[] {"x", "y"};
	}
	
	public View(Range x) {
		ranges = new Range[] {x};
		names = new String[] {"x"};
	}
	
	public Range getRange(int i)
	{
		return ranges[i];
	}

	public View(Range x, Range y, Range z) {
		ranges = new Range[3];
		ranges[0] = x;
		ranges[1] = y;
		ranges[2] = z;
		names = new String[] {"x", "y", "z"};
	}

	public View(Range[] r) {
	    ranges = new Range[r.length];
	    names = new String[r.length];
		for (int i = 0; i < r.length; i++) {
			ranges[i] = r[i];
			names[i] = ""+i;
		}
	}
	
	public int getDimension()
	{
	 return ranges.length;   
	}
	
	/**
	 * This method probably could be more safely implemented.
	 * 
	 * @param n
	 */
	public void setNames(String[] n)
	{
	    if (n.length==names.length)
	        System.arraycopy(n,0,names,0,names.length);
	}
	
	public String[] getVarNames()
	{
	    String[] temp = new String[names.length];
	    System.arraycopy(names,0,temp,0,names.length);
	    
	    return temp;
	}
	
	public String toString()
	{
		StringBuffer sb=new StringBuffer("[");
		
		for(int i=0; i<ranges.length; i++)
			sb.append(ranges[i].toString()+" ");
		
		return sb.toString()+"]";
	}
}

