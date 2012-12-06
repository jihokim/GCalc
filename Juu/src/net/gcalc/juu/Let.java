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


class Let extends ObjectLister
{
	protected Variable[] lhs;
	protected int length;

	Let(Node node) {
		super(node);
		assert(arity==2);

		length = getChild(0).jjtGetNumChildren();
		lhs = new Variable[length];
		for (int i=0; i<length; i++)
			lhs[i] = (Variable) getChild(0).jjtGetChild(i).getValue();
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

			env = env.extend(lhs[i], rhs[i]); //extend environment
		}

		return env;
	}
}

