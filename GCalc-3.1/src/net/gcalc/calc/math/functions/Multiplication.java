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

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.parser.ParseTree;

public class Multiplication extends BinaryOperation 
{
    public Multiplication(ParseTree pt)
    {
	super(pt);
    }

    public double evaluate(SymbolTable st, ValueTable vt)
    {
	return L.evaluate(st,vt)*R.evaluate(st,vt);
    }

    public Function derivative(Vector vars)
    {
	Vector left = new Vector(2);
	Vector right = new Vector(2);

	left.add(L.derivative(vars));	
	left.add(R);	
	right.add(L);
	right.add(R.derivative(vars));

	Vector args = new Vector(2);

	args.add(FunctionFactory.getFunction(this.getRoot(), left));
	args.add(FunctionFactory.getFunction(this.getRoot(), right));

	return FunctionFactory.getFunction(PLUS_TOKEN, args).simplify();
    }


    public Function simplify()
    {
		if (simpleVersion!=null)
	    return simpleVersion;

	if (this.isConstant())
	    return simpleVersion = new Constant(L.evaluate()*R.evaluate());
	if (this.isZero())
	    return ZERO;
	if (L.isOne())
	    return simpleVersion = R.simplify();
	if (R.isOne())
	    return simpleVersion = L.simplify();

	if (R.isConstant()) {
	    return FunctionFactory.getFunction(this.getRoot(), R, L).simplify();
	}

	return super.simplify();
    }
    /*
    public boolean isConstant()
    {
	if (L.simplify().isZero() || R.simplify().isZero())
	    return true;

	return super.isConstant();
    }
    */

    public boolean isZero()
    {
	return L.isZero() || R.isZero();
    }

}

