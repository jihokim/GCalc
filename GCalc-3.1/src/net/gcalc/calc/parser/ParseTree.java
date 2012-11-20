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


package net.gcalc.calc.parser;

import java.util.Vector;

import net.gcalc.calc.math.functions.FunctionFactory;

public class ParseTree implements Cloneable, Comparable
{
	private String id;
	private Vector args;
	private Token root;

	protected Vector vars = null;

	public Object clone()
	{
		ParseTree pt =  new ParseTree(root, args);
		pt.setID(getID());
		
		return pt;
	}

	public ParseTree(Token tk, Vector v)
	{
		args = v;
		root = tk;
	}

	public ParseTree(Token tk)
	{
		this(tk, null);
	}

	protected ParseTree(ParseTree pt)
	{
		this(pt.root, pt.args);
		vars = pt.vars;
	}

//this broke immutability	
//	public void setRoot(Token T)
//	{
//		root = T;
//	}

	public void setID(String s)
	{
		id = s;
	}
	public String getID()
	{
		if (id==null)
			return "";
		
		return id;
	}
	
	public ParseTree getArg(int n)
	{
	     return (ParseTree) args.elementAt(n);
	}

	
	public Vector getVars()
	{
		return getVars(null);
	}
	
	
	protected Vector getVars(Vector vars)
	{
		//	System.out.println("getVars: "+args);

		if (vars == null)
			vars = new Vector();

		if (args == null)
		{
			if (root.isVariable() && !vars.contains(root))
				vars.add(root);

			return vars;
		}

		for (int i = 0; i < args.size(); i++)
			vars.addAll(getArg(i).getVars());
		
		return vars;
	}

	public Token getRoot()
	{
		return root;
	}

	public int getNumberOfArgs()
	{
		if (args == null)
			return 0;

		return args.size();
	}

	public Vector getArgs()
	{
		return new Vector(args);
	}

	public static ParseTree removeUnnecessaryComma(ParseTree parseTree)
	{
		String id = parseTree.getID();
			
		if (parseTree.getNumberOfArgs() == 1 && parseTree.getRoot().isComma())
			parseTree = parseTree.getArg(0);
		
		parseTree.setID(id);
			return parseTree;
	}

	public static ParseTree removeUnnecessarySemiColon(ParseTree parseTree)
	{
		String id = parseTree.getID();
		
		if (parseTree.getNumberOfArgs()==1 && parseTree.getRoot().isSemiColon())
		    parseTree = parseTree.getArg(0);
		parseTree.setID(id);
		
		return parseTree;
	}


public String toString()
	{
//	    It is super critical that this method generates different strings 
//		for different parsetrees!  Some optimizations and their correctness 
//		depend upon this feature.
		
	    StringBuffer sb = new StringBuffer();
		sb.append("[(");
		sb.append(root.toString());
		//	sb.append(" ");
		//sb.append(this.getClass().toString());
		sb.append(") ");
		if (args != null)
		{
			for (int i = 0; i < args.size(); i++)
				sb.append(getArg(i).toString());
		}
		sb.append("]");

		return sb.toString();
	}
	
	public int hashCode()
	{
	    return toString().hashCode();
	}
	
	public int compareTo(Object o) {
		int n = hashCode();
		int m = o.hashCode();
		
		if (n<m)
			return -1;
		if (n>m)
			return 1;
		
		return 0;
	}
	
	public boolean equals(Object o)
	{
	    return (o instanceof ParseTree) && o.toString().equals(toString());
	}

	public String toInfix()
	{
		if (args == null)
			return root.toString();

		if (root.isSemiColon())
		{
			String s = ((ParseTree) args.elementAt(0)).toInfix();

			for (int i = 1; i < args.size(); i++)
				s += ";" + ((ParseTree) args.elementAt(i)).toInfix();

			return s;
		}

		if (args.size()==0)
			return root.toString();
		
		if (args.size() == 1)
			return root.toString() + "(" + ((ParseTree) args.elementAt(0)).toInfix() + ")";

		if (root.isBinary()) {
			ParseTree a0 = (ParseTree) args.elementAt(0);
			ParseTree a1 = (ParseTree) args.elementAt(1);
			
			String s="";
			
			if (a0.root.isBinary() && a0.root.precedence()>root.precedence())
				s+="("+a0.toInfix()+")";
			else
				s+=a0.toInfix();
			
			
			s+=root.toString();
			
			if (a1.root.isBinary() && a1.root.precedence()>root.precedence())
				s+="("+a1.toInfix()+")";
			else
				s+=a1.toInfix();
			
			return s;
		}

		String s = root.toString() + "(" + ((ParseTree) args.elementAt(0)).toInfix();

		for (int i = 1; i < args.size(); i++)
			s += "," + ((ParseTree) args.elementAt(i)).toInfix();

		return s + ")";
	}

	public static void print(ParseTree pt)
	{
		print(pt, 0);
	}

	private static void print(ParseTree pt, int n)
	{
		for (int i = 0; i < n; i++)
			System.out.print(" ");

		System.out.println(pt.root);

		if (pt.args != null)
			for (int i = 0; i < pt.args.size(); i++)
				print((ParseTree) pt.args.elementAt(i), n + 1);
	}
	
	//TODO: This method breaks immutability.  Figure out some other method.
	//Only call from constructors!
	protected void convertArgumentsToFunction()
	{
		for (int i = 0; i < args.size(); i++) {
			args.set(i, FunctionFactory.getFunction((ParseTree) args.elementAt(i)));		
		}
	}

}

