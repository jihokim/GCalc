/** 
GCalcX
Copyright (C) 2010 Jiho Kim 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/
/**
 *  GCalc 10 - Copyright Jiho Kim 2010
 *
 *  Do not redistribute.
 */

package net.gcalc.gcalc10.script;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;

import net.gcalc.gcalc10.input.InputException;
import net.gcalc.juu.Evaluator;
import net.gcalc.juu.environment.Environment;
import net.gcalc.juu.environment.GDouble;
import net.gcalc.juu.environment.GNumber;
import net.gcalc.juu.environment.Variable;
import net.gcalc.juu.parser.Juu;
import net.gcalc.juu.parser.Node;
import net.gcalc.juu.parser.ParseException;
import net.gcalc.juu.parser.TokenMgrError;

public class Function {
	private final Evaluator evaluator = new Evaluator();
	
	private final Node astNode;
	private final Variable[] variable;
	private final String input;
	
	private Function(Node node, String input, Variable ... var) {
		variable = var;
		astNode = node;
		this.input = input.trim();
	}
	
	public double eval(double ... x) {
		if (variable.length!=x.length)
			throw new IllegalArgumentException("Incompatible arity");
		
		Environment env = Environment.STANDARD;
		for (int i=0; i<variable.length; i++) {
			env = env.extend(variable[i], new GDouble(x[i]));
		}
		
		try {
			evaluator.setTimeout(-1);
			evaluator.evaluate(astNode, env);
			double y = ((GNumber) evaluator.getLast()).doubleValue();
			return y;
		}
		catch (Exception e) {
			
		}
		
		return Double.NaN;
	}
	public String toString() {
		return input;
	}
	
	public String expression() {
		return input;
	}
	
	public Variable[] getVariables() {
		return Arrays.copyOf(variable, variable.length);
	}
	
	public static void unaryEvaluation(Function unary, double[] x, double[] y, int idx, int count) {
		for (int i=0; i<count; i++) {
			y[i+idx] = unary.eval(x[i+idx]);
		}
	}
	
	public static HashMap<String, Node> nodeCache = new HashMap<String,Node>();
	
	public static Function createFunction(String expr, Variable ... var)  
	{
		Node node = nodeCache.get(expr);
		if (node==null) {
			String exp  = expr +";";
			try {
				Juu juu = new Juu(new StringReader(exp));
				node = juu.Start();
			} catch (TokenMgrError e) {
				throw new InputException("Cannot tokenize '"+exp+"'", e);
				
			} catch (ParseException e) {
				throw new InputException("Cannot parse '"+exp+"'", e);
			} 
			
		}
		return new Function(node, expr, var);
	}
}
