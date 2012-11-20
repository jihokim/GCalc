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


package net.gcalc.calc.main;

import java.util.Hashtable;

import net.gcalc.calc.parser.Token;
import net.gcalc.calc.parser.VariableToken;


public class ValueTable 
{
    private final Hashtable H;

    public ValueTable()
    {
	H = new Hashtable();

	setValue(new VariableToken("pi"), Math.PI);
	setValue(new VariableToken("e"), Math.E);
    }

    public boolean containsVariable(Token var)
    {
    	return H.containsKey(var);
    }

    public synchronized void setValue(Token variable, double value)
    {
	if (H.containsKey(variable)) 
	    ((Value) H.get(variable)).setValue(value);
	else 
	    H.put(variable, new Value(value));
    }

    public synchronized void clearValue(Token variable)
    {
	if (H.containsKey(variable))
	    H.remove(variable);
    }

    public synchronized double getValue(Token variable)
    {
	if (H.containsKey(variable))
	    return ((Value) H.get(variable)).getValue();

	return Double.NaN;
    }
}

/* Mutable wrapper for double type
 */
class Value
{
    private double value;

    public Value(double d)
    {
	setValue(d);
    }

    public synchronized void setValue(double d)
    {
	value = d;
    }

    public synchronized double getValue()
    {
	return value;
    }
}

