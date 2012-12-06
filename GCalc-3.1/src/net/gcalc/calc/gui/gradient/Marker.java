/** 
GCalc 3.0
Copyright (C) 2005 Jiho Kim 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Email: jiho@gcalcul.us
Web: http://gcalcul.us

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.calc.gui.gradient;

import java.awt.Color;
import java.util.Observable;


public class Marker extends Observable implements Comparable
{
	private double position;
	private Color left, center, right;
	
	public Marker(double pos)
	{
		setPosition(pos);
	}
	
	public Marker(double pos, Color c) 
	{
		this(pos,c,c,c);		
	}
	
	public Marker(double pos, Color l, Color c, Color r) 
	{
		this(pos);
		left = l;
		center = c;
		right = r;
	}
	
	public void setPosition(double p)
	{
		position = p;
		setChanged();
		notifyObservers();
	}
	
	public double getPosition()
	{
		return position;
	}
	
	public void setColor(Color c)
	{
		left = center = right = c;
		setChanged();
		notifyObservers();
	}
	
	public Color getLeft() {
		return left;
	}
	public void setLeft(Color left) {
		this.left = left;
		setChanged();
		notifyObservers();
	}
	public Color getRight() {
		return right;
	}
	public void setRight(Color right) {
		this.right = right;
		setChanged();
		notifyObservers();
	}
	
	public int compareTo(Object o)
	{
		Marker m = (Marker) o;
		
		if (getPosition()<m.getPosition())
			return -1;
		if (getPosition()>m.getPosition())
			return 1;
		
		return 0;
	}
	/**
	 * @return Returns the center.
	 */
	public Color getCenter() {
		return center;
	}
	/**
	 * @param center The center to set.
	 */
	public void setCenter(Color center) {
		this.center = center;
		setChanged();
		notifyObservers();
	}
	
	public String toString()
	{
		return ""+getPosition();
	}
}

