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

import java.util.HashMap;
import java.util.Iterator;

import net.gcalc.juu.parser.Node;

class AbstractNode implements Node
{
	private static HashMap<Integer, AbstractNode> hashmap = new HashMap<Integer,AbstractNode>();
	public static AbstractNode get(int id) {
		assert(id<0);
		if (hashmap.containsKey(id))
			return hashmap.get(id);
		
		AbstractNode node = new AbstractNode(id);
		hashmap.put(id, node);
		
		return node;
	}

	
	private int id;
	protected AbstractNode(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public Object getValue() {
		return this;
	}

	public void jjtAddChild(Node n, int i) {}

	public void jjtClose() {}

	public Node jjtGetChild(int i) { return this; }

	public int jjtGetNumChildren() {return 0;}

	public Node jjtGetParent() { return this; }

	public void jjtOpen() {}

	public void jjtSetParent(Node n) {}

	public Iterator<Node> iterator() { return null; }
	
}
