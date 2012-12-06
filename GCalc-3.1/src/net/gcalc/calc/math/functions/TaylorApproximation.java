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
import net.gcalc.calc.parser.VariableToken;

public class TaylorApproximation extends UnaryOperation
{
    private Function f;
    private double x0;
    private int order;

    
    private double[] taylorCoeff;
    
    public TaylorApproximation(ParseTree pt)
    {
        super(pt);
        
        SymbolTable st =new SymbolTable();
    	ValueTable vt = new ValueTable();
        
    	try {
    	    f = FunctionFactory.getFunction(getArg(0));
    	    x0 = FunctionFactory.getFunction(getArg(1)).evaluate(st,vt);
    	    order = (int) FunctionFactory.getFunction(getArg(2)).evaluate();
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    	    e.printStackTrace(System.out);
    	    return;
    	}
    	
    	Function D = f;
    	taylorCoeff = new double[order+1];
    	
    	//TODO this is a problem!  Make the taylor approximation not always over x.
    	vt.setValue(VariableToken.X_VAR, x0);
    	
    	for (int i = 0; i <= order; i++)
    	{
    	    taylorCoeff[i] = D.evaluate(st, vt);
    	    
    	    for (int j = 2; j <= i; j++)
    	        taylorCoeff[i] = taylorCoeff[i] / j;
    	 
    	    D = D.derivative(VariableToken.X_VAR);
    	}
    	
    	
    }
    
    public double evaluate(SymbolTable st, ValueTable vt)
    {
        //So called Horner's Rule which evaluates a polynomial of degree n
        //in n multiplies and n adds.
        double x = vt.getValue(VariableToken.X_VAR);
        double val = 0;
        for (int i=taylorCoeff.length-1; i>=0; i--) {
            val = val*(x-x0)+taylorCoeff[i];
        }
        return val;
    }
    
    public Function derivative(Vector vars)
    {
        return NOT_A_NUMBER;
    }
}