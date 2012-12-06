/**
 * Juu Programming Language 
 * Copyright (C) 2010 Jiho Kim
 * 
 * This file is part of Juu.
 * 
 * Juu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Juu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Juu. If not, see <http://www.gnu.org/licenses/>.
 */

package net.gcalc.juu;

import java.util.Deque;

import net.gcalc.juu.environment.Environment;
import net.gcalc.juu.environment.Procedure;
import net.gcalc.juu.parser.Node;

class FunctionCall extends ObjectLister
{
	private final static Object[] ZERO_ARRAY = new Object[0];
	
	FunctionCall(Node node) {
		super(node);
		assert(0<arity && arity<3);
	}

	protected Environment finish(Deque<Call> stack, Deque<Object> objects, Environment env) {
		assert(value.size()<=2);

		Object car = value.getFirst();
		Object cdr = value.getLast();
		
		boolean zeroary = car==cdr;
		
		if (! (car instanceof Procedure))
			throw new EvaluationException("Expected procedure but got: "+car);
		if (! zeroary && ! (cdr instanceof Object[]))
			throw new EvaluationException("Expected Object array but got: "+cdr);
		
		Procedure f = (Procedure) car;
		Object[] args = zeroary ? ZERO_ARRAY : (Object[]) cdr;
		
		if (f.arity()!=args.length) {
			throw new EvaluationException("Arity mispatch.  This function requires exactly "+f.arity()+" argument(s).");
		}
		
		objects.push(args);
		
		
		f.apply(stack, objects);
		return null;
	}
} 