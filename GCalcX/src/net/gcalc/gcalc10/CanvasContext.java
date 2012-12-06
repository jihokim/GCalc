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
package net.gcalc.gcalc10;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import net.gcalc.gcalc10.util.Util;

public class CanvasContext {
	private AffineTransform transform;
	private AffineTransform inverse;
	private Rectangle pixelBound;
	private Rectangle2D cartesianBound;
	private Rectangle2D loose;
	private Graphics2D buffer;
	public final GraphAttributes attributes;
	
	public CanvasContext(Graphics2D g, AffineTransform t, AffineTransform inv, Rectangle pb, Rectangle2D cb, GraphAttributes ga) {
		this.attributes = ga;
		buffer = g;
		transform = t;
		inverse = inv;
		pixelBound = pb;
		cartesianBound = cb;

		loose = new Rectangle2D.Double(cartesianBound.getMinX()-cartesianBound.getWidth(), cartesianBound.getMinY()-cartesianBound.getHeight(), cartesianBound.getWidth()*3, cartesianBound.getHeight()*3);
	}
	
	
	public Graphics2D getGraphics()
	{
		return buffer;
	}
	
	public Rectangle2D getLooseCartesianBounds() {
		return loose;
	}
	
	public Rectangle2D getCartesianBounds() {	
		return cartesianBound;
	}

	public AffineTransform getInverse() {
		if (inverse==null)
			inverse = Util.invert(transform);
		return inverse;
	}

	public Rectangle getPixelBounds() {
		return pixelBound;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	
}