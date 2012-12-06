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

import java.awt.geom.Point2D;


public abstract class PointNode<P extends PointNode<P>> extends SinglyLinkedNode<P>
{
	private final double t;
	protected Point2D cartesianPt;
	protected Point2D screenPt;
	
	public PointNode(P next, double t) {
		super(next);
		this.t = t;
	}

	public double getT() {
		return t;
	}
	
	public double getX()
	{
		if (cartesianPt==null) {
			lazyCalculate();
		}

		return cartesianPt.getX();
	}
	
	public double getY()
	{
		if (cartesianPt==null) {
			lazyCalculate();
		}

		return cartesianPt.getY();
	}
	
	public Point2D getCartesianPoint() {
		if (cartesianPt==null) {
			lazyCalculate();
		}
		return cartesianPt;
	}
	
	public Point2D getScreenPoint() {
		if (screenPt==null) {
			lazyCalculate();
		}
		return screenPt;
	}
	
	protected abstract Point2D getPoint(double t);
	protected abstract Point2D getScreenPoint(Point2D p);
	
	public String getName() {
		return cartesianPt+""+screenPt;
	}
	
	private void lazyCalculate() {
		cartesianPt = getPoint(t);
		screenPt = getScreenPoint(cartesianPt);
	}
} 
