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
package net.gcalc.gcalc10.input;

import java.util.EventObject;



public class SourceChangeEvent<T> extends EventObject
{
	private T value;
	
	public SourceChangeEvent(Object source, T value) {
		super(source);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
}
