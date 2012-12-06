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
import java.util.HashSet;
import java.util.Set;

import net.gcalc.juu.environment.Environment;
import net.gcalc.juu.environment.Procedure;
import net.gcalc.juu.environment.Variable;
import net.gcalc.juu.parser.Node;

class UserProcedure extends SmartCall implements Procedure {
	private Variable[] varList = null;
	private Node body = null;
	Environment env;

	public UserProcedure(Node node, Environment env) {
		super(node);
		assert(node.jjtGetNumChildren()==2);
		this.env = env;
		
		varList = initParameterList(); // walk through first child
		body = node.jjtGetChild(1); //second child
	}
	
	private Variable[] initParameterList() 
	{
		Node param = extNode.jjtGetChild(0);
		Variable[] params = new Variable[param.jjtGetNumChildren()];
		Set<Variable> varSet = new HashSet<Variable>();
		
		int i=0;
		for (Node child : param) {
			assert(child.getId()==JJTIDENTIFIER);
			params[i] = (Variable) child.getValue();
			
			if (varSet.contains(params[i]))
				throw new EvaluationException("Repeated formal parameter.");
			else 
				varSet.add(params[i]);
			i++;
		}
		
		return params;
	}

	public Environment eval(Deque<Call> stack, Deque<Object> objects, Environment env)
	{
		objects.push(this);
		return env;
	}

	public void apply(Deque<Call> stack, Deque<Object> objects) {
		Object[] args = (Object[]) objects.pop();
		Environment local = this.env;

		for (int i=0; i<varList.length; i++) {
			local = local.extend(varList[i], args[i]);
		}

		stack.push(new Call(body));
		stack.push(new Recall(local));
	}
	
	public int arity() {
		return varList.length;
	}
}
