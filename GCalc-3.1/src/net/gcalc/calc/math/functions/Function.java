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

import java.util.Hashtable;
import java.util.Vector;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.parser.ParseTree;
import net.gcalc.calc.parser.Token;

public abstract class Function extends ParseTree implements DefaultFunctions
{
	protected Function simpleVersion = null;

	protected Hashtable evaluationCache = new Hashtable();

	
	protected Function(ParseTree pt)
	{
		super(pt);
		simpleVersion = null;
	}
	
	public double evaluate()
	{
		return evaluate(null, null);
	}
	
	public double evaluate(ValueTable vt)
	{
	    return evaluate(null, vt);
	}

	public abstract double evaluate(SymbolTable st, ValueTable vt);

	public Function derivative(Token var)
	{
		Vector V = new Vector();
		V.add(var);

		return this.derivative(V);
	}

	public abstract Function derivative(Vector vars);
	
	public boolean isConstant()
	{
		return false;
	}
	
	public boolean isInequality()
	{
	    return false;
	}

	public boolean isZero()
	{
		return evaluate() == 0.0;
	}

	public boolean isOne()
	{
		return evaluate() == 1.0;
	}

	public Function simplify()
	{
		if (simpleVersion != null)
			return simpleVersion;

		if (isConstant())
			return simpleVersion = FunctionFactory.getFunction(evaluate());

		return simpleVersion = this;
	}

	public boolean isPolynomial()
	{
		if (isConstant())
			return true;
		
		Token root = getRoot();
		if (root.isVariable())
			return true;
		
		if (! root.isBinary())
			return false;
		
		Function f0 = FunctionFactory.getFunction(getArg(0));
		Function f1 = FunctionFactory.getFunction(getArg(1));

		if (root.isCaret() && f0.isPolynomial() && f1.isInteger() && f1.isPositive())
			return true;
		
		if ((root.isPlusSign() || root.isMinusSign() || root.isMultiply()) && 
				f0.isPolynomial() &&
				f1.isPolynomial())
			return true;
		
		if ((root.isPlusSign() || root.isMinusSign() || root.isMultiply()) && 
				f0.isPolynomial() &&
				f1.isPolynomial())
			return true;
		
			
		return false;
	}
	
	public boolean isInteger()
	{
		if (!isConstant())
			return false;
		
		double value = evaluate();
		
		return value == (int) value;
	}
	
	public boolean isPositive()
	{
		if (!isConstant())
			return false;
		
		double value = evaluate();
		
		return value >0;
	}

	/*
	public boolean mathEquals(Function f) {
		Function g = FunctionFactory.getFunction(DefaultFunctions.MINUS_TOKEN, this, f);
		
		return Simplify.simplify(g).equals(DefaultFunctions.ZERO);
	}
	*/
}

