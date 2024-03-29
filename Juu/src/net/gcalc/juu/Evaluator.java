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

import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

import net.gcalc.juu.environment.Environment;
import net.gcalc.juu.parser.Juu;
import net.gcalc.juu.parser.JuuTreeConstants;
import net.gcalc.juu.parser.Node;
import net.gcalc.juu.parser.ParseException;

/**
 * Core of Juu programming language interpreter. The main object of interest is
 * Environment.
 * 
 * @author jihokim
 * 
 */
public class Evaluator implements JuuTreeConstants {
	/**
	 * Official starting point of evalution.
	 */
	public final static Environment INITIAL_ENVIRONMENT = Environment.getStandard();

	/**
	 * The kill flag for trampoline loop.
	 */
	private volatile boolean alive = true;

	/**
	 * Timeout count. The timeout works by decrementing timeout to 0 for each
	 * trampoline jump. If timeout starts with -1, it takes a long time.
	 */
	private long timeout = -1;

	/**
	 * Stack of object.
	 */
	private Deque<Object> objects = new ArrayDeque<Object>();

	/**
	 * Returns the top of the object stack.  
	 * 
	 * @return
	 */
	public Object getLast() {
		if (objects.isEmpty())
			return null;
		
		return objects.getLast();
	}

	/**
	 * Terminates trampoline loop.
	 */
	public void terminate() {
		alive = false;
	}

	/**
	 * Getter for alive.
	 * 
	 * @return whether the trampoline loop is alive.
	 */
	public boolean hasTerminated() {
		return !alive;
	}

	/**
	 * Setter for timeout.
	 * 
	 * @param steps
	 */
	public void setTimeout(long steps) {
		this.timeout = steps;
	}

	/**
	 * Getter for timeout.
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * Public interface to evaluate a string.
	 * 
	 * @param string The expression to parse and interpret.
	 */
	public Environment evaluate(String string) throws InterruptedException {
		return evaluate(string, Environment.STANDARD);
	}

	
	/**
	 * Evaluates a string with an environment specified.  Parses string and calls
	 * <code>evaluate(Node,Environment)</code>.
 	 * 
	 * @param string The expression to parse and interpret.
	 * @param env The environment.
	 * @return
	 * @throws InterruptedException
	 */
	private Environment evaluate(String string, Environment env) throws InterruptedException {
		Node node = null;
		
		try {
			node = new Juu(new StringReader(string)).Start();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		return evaluate(node,env);
	}

	/**
	 * Evaluates a node with an environment specified. The trampoline loop is
	 * implemented here. A stack of Calls is maintained. When the stack is
	 * empty, the evaluation is done. Each call does one or more of the
	 * following: (1) extends the environment, (2) add/removes from the call
	 * stack, or (3) add/removes from the object stack.
	 * 
	 * Each call to <code>eval</code> may <b>not</b> be recursive. This is very
	 * important to make the evaluation not run out of stack space in the JVM.
	 * 
	 * @param head
	 *            The head node of the expression tree.
	 * @param headEnv
	 *            The environment.
	 * @return
	 * @throws InterruptedException
	 */
	public Environment evaluate(Node head, Environment headEnv) throws InterruptedException {
		Environment env = headEnv;
		Deque<Call> stack = new ArrayDeque<Call>();
		
		stack.push(new Call(head));
		
		while (! stack.isEmpty() && alive) {
			Call call = stack.pop();

			env = call.eval(stack, objects, env);
			timeout--;
			
			if (timeout==0) {
				throw new EvaluationException("Timed out");
			}
			
			if ((timeout%1000)==0) {
				Thread.sleep(10);
			}
		}
		
		return env;
	}
}
