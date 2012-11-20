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
import net.gcalc.calc.parser.BadSyntaxRuntimeException;
import net.gcalc.calc.parser.ParseTree;
import net.gcalc.calc.parser.Token;

public class NumericalIntegral extends UnaryOperation
{
    private final static int MAX_RECURSION_DEPTH=10;
    
    private double tol;
    private Function f, u,v;
    private Token variable;
    private SymbolTable symbolTable; 
    private ValueTable valueTable;
    
    public NumericalIntegral(ParseTree pt) throws BadSyntaxRuntimeException
    {
        super(pt);
             
    	Function t,s;
    	
    	try {
    	    f = FunctionFactory.getFunction(getArg(0));
    	    s = FunctionFactory.getFunction(getArg(1));
    	    u = FunctionFactory.getFunction(getArg(2));
    	    v = FunctionFactory.getFunction(getArg(3));
    	    t = FunctionFactory.getFunction(getArg(4));
    	    variable = (Token) ((Variable) s).getVars().elementAt(0);
    	    
    	    if (u.getVars().contains(variable) || v.getVars().contains(variable))
    	        throw new BadSyntaxRuntimeException("Cannot use dummy variable in integral end points");
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
     	   f = s = u = v = t = null;
     	   variable = null;
     	   throw new BadSyntaxRuntimeException("Not enough arguments");
     	}
    	catch (ClassCastException e) {
     	   f = s = u = v = t = null;
     	   variable = null;
     	   throw new BadSyntaxRuntimeException("The second argument of int must be a variable.");
     	}
    	

    	tol = t.evaluate();
    	
    	symbolTable =new SymbolTable();
    	valueTable = new ValueTable();
    }
    
    public double evaluate(SymbolTable st, ValueTable vt)
    {
        double a = u.evaluate(st,vt);
        double b = v.evaluate(st,vt);

        return definiteIntegral(f, a, b, tol);
    }
    
  
    //calculates the definite integral.  This is a front end to the
    //recursive method with more parameters.
    private double definiteIntegral(Function f, double a, double b, double tol)
    {
        return definiteIntegral(f,a,b,Double.MAX_VALUE, tol, 0);
    }
    
    //adaptive quadrature using simpson's rule, making sure not to 
    //go too deep into the recursion.
    private double definiteIntegral(Function f, double a, double b, double estimate, double tol, int level)
       {
        if (a>b)
            return -definiteIntegral(f, b,a, -estimate, tol, level);
        
        double z = (a+b)/2;
       
        double sL = simpsonsRule(f, a, z);
        double sR = simpsonsRule(f, z, b);
        double newEstimate = sL+sR;
        
        double error = estimate-newEstimate;
          
        if (Math.abs(error)<10*tol && level>=2)
            return newEstimate;
        
        if (level>MAX_RECURSION_DEPTH)
            return newEstimate;
         
        level++;
        
        return definiteIntegral(f, a, z, sL, tol/2, level)+definiteIntegral(f, z, b, sR, tol/2, level);
    }
    
    private double simpsonsRule(Function f, double a, double b)
    {
        valueTable.setValue(variable, a);
        double f0 = f.evaluate(symbolTable,valueTable);
        valueTable.setValue(variable, (a+b)/2);
        double f1 = f.evaluate(symbolTable,valueTable);
        valueTable.setValue(variable, b);
        double f2 = f.evaluate(symbolTable,valueTable);
        
        return (b-a)/6*(f0+4*f1+f2);
    }
        
    public Function derivative(Vector vars)
    {
        return NOT_A_NUMBER;
    }
}
