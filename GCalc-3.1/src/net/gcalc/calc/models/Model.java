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


package net.gcalc.calc.models;

import net.gcalc.calc.math.functions.Function;

public class Model
{
	private Function[] functions;
	private String[] reps;
	
	
	//need to decide what to do if the parameter is null
	protected Model(Function f, String s)
	{
		this(new Function[] {f}, new String[] {s});
	}
	
	protected Model(Function f)
	{
	    this(f, f.toInfix());
	}
	
	protected Model(Function[] f, String[] s)
	{
		functions = new Function[f.length];
		reps = new String[s.length];
		
		System.arraycopy(f,0,functions,0,functions.length);
		System.arraycopy(s,0,reps,0,reps.length);
	}

	public Function getFunction()
	{
		return getFunction(0);
	}

	public Function getFunction(int i)
	{
		return functions[i];
	}
	
	public int getNumberOfFunctions()
	{
		return functions.length;
	}
	
	public String[] getStringRepresentation()
	{
		return reps;
	}
	
	
	public String toString()
	{
	    if (functions==null || functions.length==0)
	        return null;
	    
	    StringBuffer sb = new StringBuffer();
		
	    if (reps.length>1)
    			sb.append("[");
	    
		for (int i = 0; i < reps.length; i++) {
	        sb.append((i!=0?", ":"")+reps[i]);
		}
		
	    if (reps.length>1)
    			sb.append("]");
		
	    return sb.toString();
	}
	
	
	
}


