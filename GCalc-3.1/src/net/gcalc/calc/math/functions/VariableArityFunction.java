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

public abstract class VariableArityFunction extends Function
{
	protected Function IDENTITY = null;

	protected VariableArityFunction(ParseTree pt)
	{
		super(pt);
		convertArgumentsToFunction();
	}
	/*
	public Function simplify()
	{
	if (args.size()==1)
	    return (Function) args.elementAt(0);
	
	Vector v = new Vector();
	
	for (int i=0; i<args.size(); i++)
	    v.add(((Function) args.elementAt(i)).simplify());
	
	return getFunction(this.getRoot(), v);
	}
	*/

	public boolean isConstant()
	{
		for (int i = 0; i < getNumberOfArgs(); i++)
			if (!((Function) getArg(i)).isConstant())
				return false;

		return true;
	}

	private void addToArguments(Vector dst, Vector src)
	{
		if (dst != null && src != null)
			for (int i = 0; i < src.size(); i++)
				dst.add(((Function) src.elementAt(i)).simplify());
	}

	protected void addToArguments(Vector dst, Function f)
	{
		if (f.getClass().equals(this.getClass()))
			addToArguments(dst, f.getArgs());
		else
			dst.add(f.simplify());
	}

	public Function simplify()
	{
		if (isConstant())
			return simpleVersion = FunctionFactory.getFunction(evaluate());

		Vector argList = new Vector();
		
		for (int i = 0; i < getNumberOfArgs(); i++)
			addToArguments(argList, ((Function) getArg(i)).simplify());

		if (argList.size() == 0)
			return simpleVersion = IDENTITY;

		if (argList.size() == 1)
			return simpleVersion = ((Function) argList.elementAt(0)).simplify();

		return simpleVersion = FunctionFactory.getFunction(this.getRoot(), argList);
	}
/*
	public boolean isIdentity()
	{
		return false;
	}
*/
}

