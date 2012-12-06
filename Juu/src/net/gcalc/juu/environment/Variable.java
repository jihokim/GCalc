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

import java.util.HashMap;

public class Variable implements Comparable<Variable> {
	private static Object lock = new Object();
	private static HashMap<String,Variable> map = new HashMap<String,Variable>();
	private static long counter = 0;

	final public String name;

	
	private Variable() {
		this("#fresh"+counter);
	}
	
	public static Variable get(String s) {
		Variable var = null;
		synchronized (map) {
			var = map.get(s);

			if (var==null) 
				map.put(s, var = new Variable(s));
		}
		return var;
	}

	public static Variable fresh() {
		Variable fresh = null;
		synchronized (lock) {
			fresh = new Variable();
		}
		return fresh;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	private Variable(String s) {
		assert(s!=null);
		name = s;
	}
	
	public String toString() {
		return "var("+name+")";
	}

	@Override
	public int compareTo(Variable v) {
		return name.compareTo(v.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
