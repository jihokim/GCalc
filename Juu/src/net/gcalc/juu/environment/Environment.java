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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.gcalc.juu.EvaluationException;

/**
 * A persistent mostly-immutable mapping of variables to objects.  Implemented as
 * a bare singly-linked list with instances of this class as nodes.
 * 
 * @author jihokim
 */
public class Environment {

	/**
	 * Empty environment. There really is no reason to have multiple instances
	 * of a Environment.  So the singleton value is given here.
	 */
	public final static Environment EMPTY = new Environment();

	/**
	 * Juu's standard environment. Should include bindings for constants and
	 * built-in functions
	 */
	public final static Environment STANDARD = getStandard();
	
	/**
	 * Defines the standard Juu environment.
	 * 
	 * @return
	 */
	public static Environment getStandard() {
		Environment env = Environment.EMPTY;
		env = env.extend("print", new Print());

		env = env.extend("pi", GDouble.PI);
		env = env.extend("e", GDouble.E);
		env = env.extend("i", GNumber.IMAG);

		Class<?> typeclass = MathOps.class;
		Object[] constants = typeclass.getEnumConstants();

		for (Object constant : constants) {
			String name = constant.toString().toLowerCase();
			MathOps type = (MathOps) constant;
			env = env.extend(name, Operator.get(type));
		}
		
		return env;
	}
	
	/**
	 * Variable key for this node
	 */
	private Variable var=null;

	/**
	 * Object value for this node
	 */
	private Object object=this;
	
	
	/**
	 * Pointer to the rest of the chain
	 */
	private Environment next = this;
	
	
	/**
	 * Pointer to the rest of the chain
	 */
	private int size = 0;
	
	/**
	 * A cache of variable-key pairs.
	 */
	private HashMap<Variable, Object> hash = new HashMap<Variable,Object>();

	/**
	 * Constructor. Creating Environments must be done through
	 * <code>EMPTY<code> and <code>extend()</code>. Used once for constructing
	 * empty Environment.
	 */
	private Environment() {}

	/**
	 * Extension constructor.
	 * 
	 * @param var 
	 * @param val Object value
	 * @param next Rest of the chain.
	 */
	private Environment(Variable var, Object val, Environment next) {
		this.var = var;
		this.object = val;
		this.next = next;
		this.size = next.size+1;
	}
	
	/**
	 * Public interface to extend environment.
	 * 
	 * @param str Name of variable
	 * @param obj Object value
	 * @return
	 */
	public Environment extend(String str, Object obj) {
		return extend(Variable.get(str), obj);
	}
	
	/**
	 * Public interface to extend environment.
	 * 
	 * @param var Variable key
	 * @param obj Rest of the chain.
	 * @return
	 */
	public Environment extend(Variable var, Object obj) {
		assert(var!=null);
		
		if (var==this.var || var.equals(this.var)) {
			return new Environment(var,obj,next);
		}
		
		return new Environment(var, obj, this);
	}

	/**
	 * Mutates binding already given in the Environment.
	 * 
	 * @param var
	 * @param object
	 */
	public void set(Variable var, Object object) {
		Environment env = this;
		while (env!=EMPTY) {
			if (env.var==var) {
				env.object = object;
				return;
			}
			env = env.next;
		}
	}

	/**
	 * Get the bound object to the given Variable.
	 * 
	 * @param var
	 * @return
	 */
	public Object get(Variable var) {
		Environment env = this;
		Variable original = var;
		Set<Variable> set= new HashSet<Variable>();
		set.add(var);
		
		//TODO: Illegible looping.  Can we do better?
		while(env!=EMPTY) {
			if (var==env.var) {
				if (env.object instanceof Reference) {
					var = ((Reference) env.object).getVariable();
					env = this;
					
					if (set.contains(var)) {
						//we've already seen this variable.
						return original;
					}
					else {
						//record that we've seen this variable.
						set.add(var);
					}
				}
				else if (env.object==null) {
					throw new EvaluationException("Attempt to reference undefined variable "+env.var);
				}
				else {
					hash.put(original, env.object);
					return env.object;
				}
			}
			else {
				env = env.next;
			}
		}
	
		return original;
	}
	
	/**
	 * Getter for size.
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Convenience method for string-ifying objects.
	 * 
	 * @param o
	 * @return
	 */
	private String string(Object o) {
		if (o==null) {
			return null;
		}
		else 
		if (o instanceof Object[] ) {
			return Arrays.asList((Object[]) o).toString();
		}
		
		return o.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb =new StringBuffer();
		sb.append("{");
		Environment env = this;
		if (env!=EMPTY) {
			sb.append(env.var+"="+string(env.object));
		}
		while (env!=EMPTY) {
			sb.append(", ");
			env = env.next;
			sb.append(env.var+"="+string(env.object));
		}
		
		sb.append("}");
		
		return sb.toString();	
	}	
}
