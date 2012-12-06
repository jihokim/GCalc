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

public class Division extends BinaryOperation
{
    public Division(ParseTree pt)
    {
	super(pt);
    }

    public double evaluate(SymbolTable st, ValueTable vt)
    {
	return L.evaluate(st,vt)/R.evaluate(st,vt);
    }

    public Function derivative(Vector vars)
    {
	Function TL=FunctionFactory.getFunction(MULT_TOKEN, L.derivative(vars), R);
	Function TR=FunctionFactory.getFunction(MULT_TOKEN, L, R.derivative(vars));
	Function NUM=FunctionFactory.getFunction(MINUS_TOKEN, TL, TR);
	Function DEN=FunctionFactory.getFunction(MULT_TOKEN, R, R);

	return FunctionFactory.getFunction(this.getRoot(), NUM, DEN);
    }

    public Function simplify()
    {
	if (simpleVersion!=null)
	    return simpleVersion;

	if (this.isConstant())
	    return simpleVersion = new Constant(evaluate());
	if (R.isZero())
	    return simpleVersion = NOT_A_NUMBER;
	if (L.isZero())
	    return simpleVersion = ZERO;


	return super.simplify();
    }
    
}

