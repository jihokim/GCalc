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


package net.gcalc.proto.plugin.space;

import java.awt.Color;


class Segment3D implements Comparable
{
	private DoubleTriple dt0, dt1, dt0p, dt1p;
	private Color cfar, cnear;
	private boolean arrow;


	public Segment3D(DoubleTriple p0, DoubleTriple p1, Color far, Color near) 
	{
		this(p0, p1, far, near, false);
	}
	
	public Segment3D(DoubleTriple p0, DoubleTriple p1, Color far, Color near, boolean arrow)
	{
		dt0 = p0;
		dt1 = p1;
		dt0p = new DoubleTriple(dt0);
		dt1p = new DoubleTriple(dt1);
		cfar = far;
		cnear = near;
		this.arrow = arrow;
	}

	public DoubleTriple getP1()
	{
		return dt0p;
	}

	public DoubleTriple getP2()
	{
		return dt1p;
	}
	
	public Color farColor()
	{
		return cfar;
	}

	public Color nearColor()
	{
		return cnear;
	}

	public void transform(double s1, double c1, double s2, double c2, double x0, double y0, double z0, double rh, double sf)
	{
		transform(dt0, dt0p, s1, c1, s2, c2, x0,y0,z0,rh, sf);
		transform(dt1, dt1p, s1, c1, s2, c2, x0,y0,z0,rh, sf);
	}

	private void transform(
		DoubleTriple p0,
		DoubleTriple p1,
		double s1,
		double c1,
		double s2,
		double c2,
		double x0, double y0, double z0,
		double rh,
		double sf)
	{
		double x = p0.getX()-x0;
		double y = p0.getY()-y0;
		double z = p0.getZ()-z0;

		double xx = c1 * x - s1 * y;
		double yy = s1 * c2 * x + c1 * c2 * y + s2 * z;
		double zz = s2 * s1 * x + c1 * s2 * y - c2 * z + rh;

		p1.setTriple(sf * xx, sf * yy, zz);
	}

	public double averageDistance()
	{
		return .5 * (dt0p.getZ() + dt1p.getZ());
	}

	public int compareTo(Object obj)
	{
		Segment3D s = (Segment3D) obj;

		if (this.averageDistance() < s.averageDistance())
			return -1;
		else if (this.averageDistance() > s.averageDistance())
			return 1;

		return 0;
	}
	
	public boolean isArrow()
	{
		return arrow;	
	}
}

