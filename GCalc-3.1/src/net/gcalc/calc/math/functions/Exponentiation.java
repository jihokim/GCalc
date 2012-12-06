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

public class Exponentiation extends BinaryOperation
{
    public Exponentiation(ParseTree pt)
    {
	super(pt);
    }

    public double evaluate(SymbolTable st, ValueTable vt)
    {
	return Math.pow(L.evaluate(st,vt),R.evaluate(st,vt));
    }

    public Function derivative(Vector vars)
    {
	Function F = null;
	
	boolean lconstant = L.isConstant();
	boolean rconstant = R.isConstant();

	if (R.isZero()) {
	    if (L.isZero()) {
		/* 0^0 */
		F = NOT_A_NUMBER;
	    }
	    else {
		/* n^0 */
		F = ZERO;
	    }
	}
	if (lconstant && rconstant) {
	    /* dx = 0 */
	    F = ZERO;
	}
	else if (rconstant) {
	    /* dy/dx = u^(v-1)*v*u' */

	    F=FunctionFactory.getFunction(MULT_TOKEN,
			  FunctionFactory.getFunction(CARET_TOKEN, 
				      L,
				      FunctionFactory.getFunction(MINUS_TOKEN,
						  R,
						  ONE)),
			  FunctionFactory.getFunction(MULT_TOKEN, 
				      R,
				      L.derivative(vars)));
	}
	else if (lconstant) {
	    /* dy/dx = u^v*ln(u) */
	    
	    F=FunctionFactory.getFunction(MULT_TOKEN,
			  FunctionFactory.getFunction(MULT_TOKEN,
				      this,
				      FunctionFactory.getFunction(LN_TOKEN, L)),
			  R.derivative(vars));
				      
	}
	else {
	    /* dy/dx = u^v (v/u*u'+ln(u)*v') */
	    
	    Function lhs = FunctionFactory.getFunction(MULT_TOKEN, 
				       FunctionFactory.getFunction(DIV_TOKEN, R, L), 
				       L.derivative(vars));
	    
	    Function rhs = FunctionFactory.getFunction(MULT_TOKEN, 
				       FunctionFactory.getFunction(LN_TOKEN, L), 
				       R.derivative(vars));
	    
	    F = FunctionFactory.getFunction(MULT_TOKEN, 
			    this, 
			    FunctionFactory.getFunction(PLUS_TOKEN, lhs, rhs));
	}

	return F;
    }

    public Function simplify()
    {
	if (simpleVersion!=null)
	    return simpleVersion;

	if (L.isZero() && L.isZero())
	    return simpleVersion = NOT_A_NUMBER;

	if (R.simplify().isZero())
	    return simpleVersion = ONE;

	if (R.simplify().isOne())
	    return simpleVersion = L.simplify();

	if (L.simplify().isOne())
	    return simpleVersion = ONE;
	

	return super.simplify();
    }
}

