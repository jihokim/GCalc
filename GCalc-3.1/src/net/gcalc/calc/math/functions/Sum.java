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

public class Sum extends VariableArityFunction
{
    public Sum(ParseTree pt)
    {
        super(pt); 
        IDENTITY=ZERO;
    }
    
    public double evaluate(SymbolTable st, ValueTable vt)
    {
        //empty sum is 0
        if (this.getNumberOfArgs()==0)
            return 0;
        
        double t;
        double sum=0;
        
        for (int i=0; i<getNumberOfArgs(); i++) {
            t = ((Function) getArg(i)).evaluate(st, vt);
            if (Double.isNaN(t))
                return t;
            
            sum+=t;
        }
        
        return sum;
    }
    
    public Function derivative(Vector vars)
    {
        Vector newArgs = new Vector(getNumberOfArgs());
        
        for (int i=0; i<newArgs.size(); i++) 
            newArgs.add(FunctionFactory.getFunction((ParseTree) getArg(i)).derivative(vars));
        
        return FunctionFactory.getFunction(this.getRoot(), newArgs).simplify();
        
    }
    
    
    protected boolean isIdentity(Function f)
    {
        return f.isZero();
    }
    
}

