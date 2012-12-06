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
import net.gcalc.juu.environment.Variable;
import net.gcalc.juu.parser.JuuTreeConstants;
import net.gcalc.juu.parser.Node;

/**
 * The generic Call, which may be thought of as a wrapper for an expression tree
 * node. <code>Call</code> is the root of a type hierarchy that defines the
 * semantics of the Juu language. Each subclass is responsible for implementing
 * <code>eval()</code> which mutates the call and objects stacks and extends the
 * given environment. Different ways of doing that implements different features
 * of the Juu language.
 * 
 * @author jihokim
 * 
 */
public class Call implements JuuTreeConstants
{
	/**
	 * Used in cases where <code>null</code> might be appropriate but is
	 * unsupported by the collection, e.g. java.util.Deque.
	 */
	private static Object NULL = new Object();
	
	/**
	 * The expression tree node which this call represents.
	 */
	public Node node = null;

	
	/**
	 * Constructor
	 * 
	 * @param node Expression tree Node
	 */
	public Call(Node node) {
		this.node = node;
	}

	/**
	 * The implementation of the bottom half of the trampoline jump. Given the
	 * node's id, pushes a more specific Call onto the call stack. It will also
	 * mutate the object stack when appropriate.
	 * 
	 * It makes a small step toward evaluating <code>node</code> given the
	 * states passed in through the arguments.
	 * 
	 * @param stack
	 *            The call stack
	 * @param objects
	 *            The object stack
	 * @param env
	 *            The environment
	 * @return The given environment <code>env</code> unchanged.
	 */
	public Environment eval(Deque<Call> stack, Deque<Object> objects, Environment env) 
	{ 
		switch (node.getId()) {

		//debug
		case JJTDEBUG:
			System.out.println("ENV: "+env);
			System.out.println("OBJ: "+objects);
			break;

		//assignment lhs.
		case JJTLHS:
			objects.push(NULL);
			break;

		// identifier
		case JJTIDENTIFIER:
			objects.push(env.get((Variable) node.getValue()));
			break;

		// literals
		case JJTBOOLEAN:
		case JJTFLOATINGPOINT:
		case JJTINTEGER:
			objects.push(node.getValue());
			break;

			
		// need more processing
		case JJTEQUAL:
			stack.push(new Equality(node));
			break;
		case JJTORDERREL:
			stack.push(new Order(node));
			break;
		case JJTNEG:
			stack.push(new Negation(node));
			break;
		case JJTEXP:
			stack.push(new Exponentiation(node));
			break;
		case JJTPROD:
			stack.push(new Product(node));
			break;
		case JJTSUM:
			stack.push(new Sum(node));
			break;
		case JJTBLOCK:
			stack.push(new Block(node));
			break;
		case JJTCONJUNCTION:
			stack.push(new Conjunction(node));
			break;
		case JJTDISJUNCTION:
			stack.push(new Disjunction(node));
			break;
		case JJTLET:
			stack.push(new Let(node));
			break;
		case JJTLETREC:
			stack.push(new Letrec(node));
			break;
		case JJTPROC:
			stack.push(new UserProcedure(node, env));
			break;
		case JJTFUNCTIONCALL:
			stack.push(new Recall(env));
			stack.push(new FunctionCall(node));
			break;
		case JJTWHILESTATEMENT:
			stack.push(new While(node));
			break;
		case JJTRHS:
		case JJTARGUMENTLIST:
			stack.push(new ObjectLister(node));
			break;
		case JJTIFSTATEMENT:
			stack.push(new IfThen(node));
			break;

		// Pass through.  Recycle current call object.
		case JJTEQ:
		case JJTNEQ:
		case JJTLT:
		case JJTLE:
		case JJTGT:
		case JJTGE:
		case JJTDIV:
		case JJTIMPLICITMULT:
		case JJTMULT:
		case JJTPLUS:
		case JJTMINUS:
		case JJTPOS:
			assert (node.jjtGetNumChildren() == 1);
			this.node = node.jjtGetChild(0);
			stack.push(this);
			break;

		default:
			throw new EvaluationException("Symbol not recognized: "+jjtNodeName[node.getId()]);
		}
		
		return env;
	}

}