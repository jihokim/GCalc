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
package net.gcalc.gcalc10.drawable;


public interface DrawableVisitor<T> {
	public T visit(XY f);
	public T visit(TR f);
	public T visit(TXY f);
	public T visit(Points f);
	public <L extends Drawable> T visit(DrawableList<L> f);
	public T visit(DrawableShape f);
	public T visit(Grid f);
	public T visit(Labels f);

}
