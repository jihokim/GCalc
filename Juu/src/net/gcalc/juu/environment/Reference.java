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


public class Reference {
	private Variable variable;
	
	public Reference(Variable v) {
		variable = v;
	}
	
	public Reference(Reference ref) {
		variable = ref.getVariable();
	}
	
	public Variable getVariable()
	{
		return variable;
	}
	
	public String toString() {
		return "ref("+variable.toString()+")";
	}
}
