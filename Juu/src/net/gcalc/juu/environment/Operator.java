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

package net.gcalc.juu.environment;

import java.util.Deque;
import java.util.HashMap;

import net.gcalc.juu.Call;

public class Operator implements Procedure {
	public final static HashMap<MathOps, Operator> hash = new HashMap<MathOps,Operator>();

	public static Operator get(MathOps type) {
		Operator op = hash.get(type);
		if (op==null) { 
			op = new Operator(type,1);
			hash.put(type,op);
		}		
		return op;
	}
	
	private int arity = -1;
	private MathOps type;
	
	private Operator(MathOps type, int arity) {
		this.type = type;
		this.arity = arity;
	}
	
	public void apply(Deque<Call> stack, Deque<Object> objects) {
		Object[] args = (Object[]) objects.pop();
		objects.push(type.evaluate((GNumber) args[0]));
	}
	
	public Environment eval(Deque<Call> stack, Deque<Object> objects, Environment env) {
		objects.push(this);
		return env;
	}
	
	public String toString() {
		return "op("+type+")";
	}

	public int arity() {
		return arity;
	}
}
