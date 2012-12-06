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

package net.gcalc.plugin.plane.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.ColoredModel;


/**
 * @author jkim
 *
 */
public class IVPModel extends ColoredModel
{
    private double x,y;
    
    public IVPModel(Function[] f, String[] s, double x, double y, Color c)
    {
        super(f,s, c);
        this.x = x;
        this.y = y;
    }
    
    public String toString()
    {
        return "("+x+","+y+")";
    }
    
    public Point2D.Double getPoint()
    {
        return new Point2D.Double(x,y);
    }
  
}
