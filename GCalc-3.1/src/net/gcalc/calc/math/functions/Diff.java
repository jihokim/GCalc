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


package net.gcalc.calc.math.functions;

import java.util.Vector;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.parser.ParseTree;
import net.gcalc.calc.parser.Token;

public class Diff extends Function
{
	Function der = null;

    public Diff(ParseTree pt)
    {
		super(pt);
		
		/*
		
		System.out.println("*"+this.getArgs().elementAt(0));
		System.out.println("*"+this.getArgs().elementAt(1));
		*/
		Vector vars = getArgs();
		
		Function F = FunctionFactory.getFunction((ParseTree) this.getArgs().elementAt(0));		
	
		der = F;
		for (int i=1; i<vars.size(); i++) {
		    Token var = ((ParseTree) vars.elementAt(i)).getRoot();		
		    der = der.derivative(var);
		}
		
		//System.out.println(der);
    }
    
    public double evaluate(SymbolTable st, ValueTable vt)
    {
    	return der.evaluate(st,vt);
    }

    public Function derivative(Vector vars)
    {
		return der.derivative(vars);
    }
    
	public boolean isConstant()
	{
		return der.isConstant();
	}

	public boolean isZero()
	{
		return der.isZero();
	}

	public boolean isOne()
	{
		return der.isOne();
	}

	public Function simplify()
	{
		if (simpleVersion != null)
			return simpleVersion;

		return simpleVersion = der.simplify();
	}

}

