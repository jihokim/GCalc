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

class Block extends SmartCall
{
	public static Forget CLEAR_OBJECTS_STACK = new Forget();
	
	Block(Node node) {
		super(node);
	}
	
	public Environment eval(Deque<Call> stack, Deque<Object> objects, Environment env) {
		for (int i=arity-1; i>=0; i--) {
			stack.push(new Call(getChild(i)));
			stack.push(CLEAR_OBJECTS_STACK);
		}
		return env;	
	}
}

class Forget extends SmartCall {

	public Forget() {
		super(-1);
	}

	public Environment eval(Deque<Call> stack, Deque<Object> answers, Environment env) {

		answers.clear();
		
		return env;
	}

}
