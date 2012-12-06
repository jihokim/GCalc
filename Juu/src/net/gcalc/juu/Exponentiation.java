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
import net.gcalc.juu.environment.GNumber;
import net.gcalc.juu.parser.Node;

class Exponentiation extends NumberLister
{
	Exponentiation(Node node) {
		super(node);
		assert(node.getId()==JJTEXP);
	}

	protected Environment finish(Deque<Call> stack, Deque<Object> objects, Environment env) {
		GNumber exp = value.removeLast();
		while (! value.isEmpty()) {
			exp = value.removeLast().pow(exp);
		}
		
		objects.push(exp);
		
		return env;
	}
}

