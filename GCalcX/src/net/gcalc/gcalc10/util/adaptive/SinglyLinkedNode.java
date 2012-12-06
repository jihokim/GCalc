/** 
GCalcX
Copyright (C) 2010 Jiho Kim 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/
package net.gcalc.gcalc10.util.adaptive;


public class SinglyLinkedNode<N extends SinglyLinkedNode<N>> {
	public N next;
	
	public SinglyLinkedNode(N right) {
		this.next = right;
	}
	
	public String getName()
	{
		return super.toString();
	}

	public String toString() {
		SinglyLinkedNode<N> cursor = this;
		StringBuilder sb = new StringBuilder();
		
		while (cursor!=null) {
			sb.append(cursor.getName());
			cursor = cursor.next;
			if (cursor!=null) {
				sb.append("->");
			}
		}
		
		return sb.toString();
	}
	
	public int size() {
		SinglyLinkedNode<N> cursor = this;
		int count = 0;
		
		while (cursor!=null) {
			count++;
			cursor = cursor.next;
		}
		
		return count;
	}

}
