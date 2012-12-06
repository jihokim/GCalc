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


public class DrawableTableModelVisitor extends AbstractDrawableVisitor<AbstractDrawableTableModel<? extends ColoredDrawable>> {

	@Override
	public AbstractDrawableTableModel<XY> visit(XY f) {
		return new XYTableModel(new XY(f));
	}

	@Override
	public AbstractDrawableTableModel<TR>  visit(TR f) {
		return new TRTableModel(new TR(f));
	}

	@Override
	public AbstractDrawableTableModel<TXY>  visit(TXY f) {
		return new TXYTableModel(new TXY(f));
	}

	@Override
	public AbstractDrawableTableModel<? extends ColoredDrawable> visit(
			Points f) {
		// TODO Auto-generated method stub
		return null;
	}
}

