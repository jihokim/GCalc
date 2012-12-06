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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import net.gcalc.calc.math.FileFormatException;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.BadSyntaxRuntimeException;
import net.gcalc.calc.parser.CalcParser;
import net.gcalc.calc.parser.ParseTree;
import net.gcalc.calc.parser.Token;

public class FunctionFactory
{
	private static Hashtable operationClasses = null;
	private final static String PACKAGE_NAME = "net.gcalc.calc.math.functions";
	private final static boolean CACHE = true;
	
	private static Hashtable cachedFunctions = new Hashtable();
	
	private static Vector functionNames, operationNames;
	
	public static boolean isFunctionName(String s)
	{	    
	    if (functionNames==null) 
	        init();
	    
	    return functionNames.contains(s);
	}
	
	private synchronized static void init()
	{
	    //weird construction...
	    //We need a class that is not loaded by the bootstrap loader
	    ClassLoader cl = (new FunctionFactory()).getClass().getClassLoader();
	    
	    
	    functionNames = new Vector();
	    operationNames = new Vector();
	    
		operationClasses = new Hashtable();
		
		BufferedReader br;
		String s = null;
		StringTokenizer st;
		String opname = null, classname = null;

		try
		{  
			br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("resources/function_list.txt")));

			while ((s = br.readLine()) != null)
			{
				s = s.trim();

				if (s.length() > 0 && s.charAt(0) != '#')
				{
					st = new StringTokenizer(s);
					if (st.countTokens() != 2)
						throw new FileFormatException();

					opname = st.nextToken();
					classname = PACKAGE_NAME + "." + st.nextToken();

					operationClasses.put(opname, Class.forName(classname));
					
					if (opname.toLowerCase().equals(opname.toUpperCase()))
					    operationNames.add(opname);
					else 
					    functionNames.add(opname);		
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Function getFunction(double x) throws BadSyntaxRuntimeException
	{
		return getFunction(new Constant(x));
	}

	public static Function getFunction(ParseTree pt) throws BadSyntaxRuntimeException
	{
		if (operationClasses == null) 
			init();
		

		//if it's already a function, return that.
		if (pt instanceof Function)
			return (Function) pt;
		
		//if it's cache, use the cached version. This should minimize
        //redundant parsetree structures in memory. This depends on
        // ParseTrees being immutable. To turn it off, make CACHE false.
		if (CACHE) {
		if(cachedFunctions.containsKey(pt))
		   return (Function) cachedFunctions.get(pt);
		}

		Function F = null;

		if (pt.getRoot().isConstant())
		{
			F = new Constant(pt);
		}
		else if (pt.getRoot().isVariable())
		{
			F = new Variable(pt);
		}
		else
		{
			try
			{
			    Class C = (Class) operationClasses.get(pt.getRoot().getName());
				F = (Function) C.getConstructors()[0].newInstance(new Object[] {pt});
			}
			catch (InstantiationException e)
			{
				/* Thrown when an application tries to create an instance
				 * of a class using the newInstance method in class Class,
				 * but the specified class object cannot be instantiated
				 * because it is an interface or is an abstract class. 
				 */

				System.err.println(e);
			}
			catch (IllegalAccessException e)
			{
				/* An IllegalAccessException is thrown when an application
				 * tries to reflectively create an instance (other than an
				 * array), set or get a field, or invoke a method, but the
				 * currently executing method does not have access to the
				 * definition of the specified class, field, method or
				 * constructor.
				 */

				System.err.println(e);
			}
			catch (InvocationTargetException e)
			{
				/*  InvocationTargetException is a checked exception that
				 *  wraps an exception thrown by an invoked method or
				 *  constructor.
				 *
				 * As of release 1.4, this exception has been retrofitted
				 * to conform to the general purpose exception-chaining
				 * mechanism. The "target exception" that is provided at
				 * construction time and accessed via the
				 * getTargetException() method is now known as the cause,
				 * and may be accessed via the Throwable.getCause()
				 * method, as well as the aforementioned "legacy method."
				 */

			    Throwable cause = e.getCause();
			    
			    if (cause instanceof BadSyntaxRuntimeException) {
			        throw (BadSyntaxRuntimeException) cause;
			    }
			    
			    System.err.println(cause);
			    cause.printStackTrace();
			}
		}
		
		cachedFunctions.put(pt, F);
	
		return F;
	}

	public static Function getFunction(String infix) throws BadSyntaxException
	{
		ParseTree pt = CalcParser.parse(infix);
		pt = ParseTree.removeUnnecessarySemiColon(pt);

		Function f = getFunction(pt);
		f.setID(infix);
		return f;
	}

	public static Function getFunction(Token constant)
	{
		return getFunction(new ParseTree(constant));
	}

	public static Function getFunction(Token tk, Function x1)
	{
		Vector v = new Vector(1);
		v.add(x1);

		return getFunction(tk, v);
	}

	public static Function getFunction(Token tk, Function x1, Function x2)
	{
		Vector v = new Vector(1);
		v.add(x1);
		v.add(x2);

		return getFunction(tk, v);
	}

	public static Function getFunction(Token tk, Vector v) 
	{
		return getFunction(new ParseTree(tk, v));
	}

	protected FunctionFactory() {};
	
}

