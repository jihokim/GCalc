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
import net.gcalc.juu.parser.Node;

class IfThen extends Operation<Void> {
	IfThen(Node node) {
		super(node);
		assert (node.getId() == JJTIFSTATEMENT);
	}

	protected Environment handleData(Deque<Call> stack, Deque<Object> objects, Environment env) {
		assert (index > 0);

		if (index > 1) {
			// We need to make sure that no other clause is executed. We
			// accomplish that by changing index to arity.
			index = arity;
			return env;
		}

		assert (index == 1);

		Object data = objects.pop();

		if (!(data instanceof Boolean))
			throw new EvaluationException("Expected Boolean but got " + data);

		if (!(Boolean) data) {
			// The if-condition came back false. Then we need to skip the then
			// clause and proceed to the else clause. If there is no else
			// clause, then this will cause Operation<Void> to call finish()
			index = 2;
		}

		return env;
	}
}