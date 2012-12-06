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

import net.gcalc.calc.parser.ParseTree;

public abstract class BinaryOperation extends Function {
    protected Function L;
    protected Function R;

    protected boolean DEBUG = false;

    protected BinaryOperation(ParseTree pt)
    {
	super(pt);
	
	Vector args = getArgs();

	for (int i=0; i<2; i++)
	    args.set(i, FunctionFactory.getFunction((ParseTree) args.elementAt(i)));

	L = (Function) args.elementAt(0);
	R = (Function) args.elementAt(1);
    }

    public boolean isConstant()
    {
	return L.isConstant() && R.isConstant();
    }

    public Function simplify()
    {
	if (simpleVersion!=null)
	    return simpleVersion;

	if (this.isConstant())
	    return simpleVersion = new Constant(this.evaluate());

	return simpleVersion = FunctionFactory.getFunction(this.getRoot(), L.simplify(), R.simplify());
    }


}

