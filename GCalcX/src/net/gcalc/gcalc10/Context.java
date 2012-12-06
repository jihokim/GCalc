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
/**
 *  GCalc 10 - Copyright Jiho Kim 2010
 *
 *  Do not redistribute.
 */

package net.gcalc.gcalc10;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

 interface Context2{
	public Graphics2D getGraphics();
	public AffineTransform getTransform();
	public AffineTransform getInverse();
	public Rectangle2D getCartesianBounds();
	public Rectangle getPixelBounds();
	public Rectangle2D getLooseCartesianBounds();
}
