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


public abstract class Adaptive<N extends SinglyLinkedNode<N>> {
	
	public final static double PHI = (Math.sqrt(5)-1)/2;
	
	public abstract void insertNearerNext(N node);
	
	public int recurse(N node, int x) {
		return recurseHelp(node,x)+2;
	}
	
	public int recurseHelp(N node, int x) {
		if (x==0)
			return 0;
		
		insertNearerNext(node);
		int count = 0;
		
		if (keepRecursing(node.next))
			count+=recurseHelp(node.next,x-1);

		if (keepRecursing(node)) 
			count+=recurseHelp(node,x-1);
		
		
		return count+1;
	}
	
	public boolean keepRecursing(N node) {
		return true;
	}
}
