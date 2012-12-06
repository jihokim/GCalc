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
import net.gcalc.juu.environment.Reference;
import net.gcalc.juu.environment.Variable;
import net.gcalc.juu.parser.Node;


class Letrec extends ObjectLister
{
	protected Variable[] lhs;
	protected int length;

	Letrec(Node node) {
		super(node);
		assert(arity==2);

		length = getChild(0).jjtGetNumChildren();
		lhs = new Variable[length];
		for (int i=0; i<length; i++)
			lhs[i] = (Variable) getChild(0).jjtGetChild(i).getValue();
	}

	protected Environment start(Deque<Call> stack, Environment env) {
		env = super.start(stack, env);

		// make a place holder for lhs variables that are visible to the
		// RHS. These will be overwritten by Environment.set() after all the
		// RHS computed.
		for (int i=0; i<length; i++) {
			env = env.extend(lhs[i], null);
		}
		return env;
	}

	protected Environment finish(Deque<Call> stack, Deque<Object> objects, Environment env) {
		//TODO: this code does a lookup of the lhs.  It's largely harmless and serves 
		// to make the code smaller, but it's a unnecessary performance hit.

		Object[] rhs = (Object[]) value.pollLast();

		assert(length==rhs.length);

		//bind the left side to the right side
		for (int i=0; i<length; i++) {
			if (rhs[i] instanceof Variable) {
				rhs[i] = new Reference((Variable) rhs[i]);
			}

			env.set(lhs[i], rhs[i]); //mutate environment
		}

		return env;
	}
}

