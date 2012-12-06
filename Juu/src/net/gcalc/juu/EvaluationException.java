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

import net.gcalc.juu.parser.Node;

public class EvaluationException extends RuntimeException
{
	public Node node;
	public Object object;

	public EvaluationException(String s, Node n, Object o) {
		super(s);
		node = n;
		object = o;
	}

	public EvaluationException(String s, Node n, Object o, Throwable t) {
		super(s,t);
		node = n;
		object = o;
	}
	
	public EvaluationException(String s) {
		super(s);
	}
}

