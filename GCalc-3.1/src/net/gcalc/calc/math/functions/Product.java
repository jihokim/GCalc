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

public class Product extends VariableArityFunction
{
    public Product(ParseTree pt)
    {
        super(pt); 
        IDENTITY = ONE;
    }
    
    public double evaluate(SymbolTable st, ValueTable vt)
    {
        //empty product is 1.
        if (this.getNumberOfArgs()==0)
            return 1;
        
        double t;
        double p=1;
        
        Vector args = getArgs();
        
        for (int i=0; i<args.size(); i++) {
            t = ((Function) args.elementAt(i)).evaluate(st, vt);
            if (Double.isNaN(t))
                return t;
            
            p*=t;
        }
         
        return p;
    }
    
    public Function derivative(Vector vars)
    {
        int n = getNumberOfArgs();
        
        Vector sumArgs = new Vector(n);
        Vector prodArgs = null;
        Function temp = null;
        
        for (int i=0; i<n; i++) {
            prodArgs = new Vector(n);
            for (int j=0; j<n; j++) {
                if (i==j)
                    temp = ((Function) getArg(j)).derivative(vars);
                else
                    temp = (Function) getArg(j);
                
                prodArgs.add(temp);
            }
            sumArgs.add(FunctionFactory.getFunction(this.getRoot(), prodArgs).simplify());
        }
        
        return FunctionFactory.getFunction(SUM_TOKEN, sumArgs).simplify();
    }
    
    public boolean isIdentity(Function f)
    {
        return f.isOne();
    }
    
    public Function simplify()
    {
        Function f;
        Vector args = getArgs();

        for (int i=0; i<args.size(); i++) {
            f = ((Function) args.elementAt(i));
            if (f.isZero())
                return ZERO;
        }
        
        return super.simplify();
    }
    
    public void addToArguments(Vector dst, Function f)
    {
        if (f.isOne())
            return ;
        
        super.addToArguments(dst, f);
    }
    
}

