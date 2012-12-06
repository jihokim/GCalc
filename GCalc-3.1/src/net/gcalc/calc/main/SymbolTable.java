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


package net.gcalc.calc.main;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import net.gcalc.calc.math.CircularDefinitionException;
import net.gcalc.calc.math.InvalidSymbolException;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.parser.ParseTree;
import net.gcalc.calc.parser.Token;
import net.gcalc.calc.parser.VariableToken;


public class SymbolTable
{
    //hashtable's methods are synchronized... otherwise we need to
    //have synchronized on our methods.
    private final Hashtable H;

    public static void main(String[] args) throws Exception
    {
	SymbolTable symtable = new SymbolTable();

	symtable.setVariable(new VariableToken("f"), FunctionFactory.getFunction("x^2+y z"));	
    }


    public SymbolTable()
    {
	H = new Hashtable();
    }

    public Function getFunction(Token var)
    {
    	if (H.containsKey(var))
    	    return (Function) H.get(var);

    	return null;
    }
    
    public ParseTree getParseTree(Token var) {
    	if (H.containsKey(var))
    	    return (Function) H.get(var);

    	return null;
    }

    public void removeVariable(Token var)
    {
	if (H.containsKey(var))
	    H.remove(var);
    }

    public boolean containsVariable(Token var)
    {
	return H.containsKey(var);
    }

    public void setVariable(Token var, ParseTree expr) throws SymbolTableException
    {
        boolean DEBUG=false;
        
        if (! var.isVariable())
            throw new InvalidSymbolException(var+" cannot be placed into the symbol table.");
        
        Stack S = new Stack();
        
        if (DEBUG)
            System.out.println("[SymbolTable.setExpression] Adding "+var+"="+expr);
        
        
        //	System.out.println(expr.getClass());
        
        Vector varList = expr.getVars();
        Object temp;
        for (int i=0; i<varList.size(); i++) {
            temp = varList.elementAt(i);
            if (var.equals(temp)) 
                throw new CircularDefinitionException("Circular definition.  Can't proceed.");
            
            if (! S.contains(temp))
                S.push(temp);
        }
        
        Token V;
        Function E;
        while (!S.isEmpty()) {
            V= (Token) S.pop();
            
            E = getFunction(V);
            
            if (E!=null) {
                varList = E.getVars();
                for (int i=0; i<varList.size(); i++) {
                    temp = varList.elementAt(i);
                    if (var.equals(temp)) 
                        throw new CircularDefinitionException();
                    
                    if (! S.contains(temp))
                        S.push(temp);
                }
            }
            
        } 
        
        
        H.put(var,expr);
    }
    


    public synchronized void clear()
    {
	H.clear();
    }
    
    public String toString()
    {
    	return H.toString();
    }
    
}

