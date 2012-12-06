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

import net.gcalc.juu.Evaluator;
import net.gcalc.juu.environment.Environment;
import net.gcalc.juu.environment.GDouble;
import net.gcalc.juu.environment.GNumber;
import net.gcalc.juu.environment.Variable;
import net.gcalc.juu.parser.Juu;
import net.gcalc.juu.parser.Node;
import net.gcalc.juu.parser.ParseException;

public class UnaryFunction {
	private Evaluator evaluator = new Evaluator();
	
	private Node astNode = null;
	private Variable variable = null;
	private String input = null;

	private UnaryFunction(Variable var, Node node, String input) {
		variable = var;
		astNode = node;
		this.input = input.trim();
	}
	
	public double eval(double x) {
		Environment env = Environment.STANDARD.extend(variable, new GDouble(x));
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
	
	public void eval(double[] vals) {
		for (int i=0; i<vals.length; i+=2)
			vals[i+1] = eval(vals[i]);
	}
	
	public String toString() {
		return input;
	}
	
	public static UnaryFunction createFunction(String var, String expr) throws ParseException 
	{		
		Juu juu = new Juu(new StringReader(expr+";"));
		return new UnaryFunction(Variable.get(var), juu.Start(), expr);
	}
}
