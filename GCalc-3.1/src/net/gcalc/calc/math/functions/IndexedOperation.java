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

import net.gcalc.calc.parser.BadSyntaxRuntimeException;
import net.gcalc.calc.parser.ParseTree;

public abstract class IndexedOperation extends UnaryOperation
{
    private final static int BOGUS_INDEX = Integer.MAX_VALUE;

    protected int index=BOGUS_INDEX;
    
    protected IndexedOperation(ParseTree pt)
    {
        super(pt); 
        
        //System.out.println("IndexedOperation "+pt);
        
        
        Function idx = null;
        
        try {
            idx = FunctionFactory.getFunction((ParseTree) getArgs().elementAt(1));
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace(System.out);
        }
        
        if (! idx.isConstant())
            throw new BadSyntaxRuntimeException ("Bad Index: "+idx);
        
        double x = idx.evaluate();
        
        if (Double.isNaN(x))
            throw new BadSyntaxRuntimeException ("Bad Index: "+x);
        
        if ((int) x!=x)
            throw new BadSyntaxRuntimeException ("Bad Index: "+x);
        
        index = (int) x;
        
    }

    public Function simplify()
    {
	if (simpleVersion!=null)
	    return simpleVersion;

	return simpleVersion = FunctionFactory.getFunction(this.getRoot(), var.simplify(), new Constant(index));
    }


    public Function derivative(Vector vars)
    {
	return NOT_A_NUMBER;
    }

}

