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


package net.gcalc.calc.math.functions;

import java.util.Vector;

import net.gcalc.calc.parser.ParseTree;

public abstract class BooleanOperation extends Function
{
    public BooleanOperation(ParseTree pt)
    {
        super(pt);
    }
    
    public Function derivative(Vector vars)
    {
        //TODO should this be NOT_A_NUMBER?
        
        //The value 0 seems to make sense most of the time here, but would
        //not make sense for reciprocol of the 
        //characteristic function for the irrationals, for example.
        
		return ZERO;
    }
    
    final static boolean getBooleanValue(double d)
    {
        return ! Double.isNaN(d);
    }

    //strict inequality means that the relation does not
    //contain the equality relation.
    public boolean isStrict()
    {
        return true;
    }
    
    public abstract Function getZeroSetFunction();
}

