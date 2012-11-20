/** 
 * GCalc 3.0
 * Copyright (C) 2005 Jiho Kim 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * Email: jiho@gcalc.net
 * Web: http://gcalc.net
 * 
 * Snail Mail: 
 *   Jiho Kim
 *   1002 Monterey Lane
 *   Tacoma, WA 98466
 */

package net.gcalc.plugin.plane.graph;

import java.util.Vector;

import net.gcalc.calc.models.ModelList;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.properties.GraphProperties;



public class ImplicitFunctionGraph extends CartesianGraph
{
    private DrawingThread thread = null;
   
    public ImplicitFunctionGraph(AbstractCartesianGraphPlugin plugin)
    {
        super(plugin);
        this.getProperties().put(GraphProperties.V_TITLE_STRING, "y");
        
    }
    
    protected Vector makeZoomsVector()
    {
        //removes FitZoom from the list from superclass since it doesn't make sense in this context.
        
       Vector v = super.makeZoomsVector();
      
       for (int i=v.size()-1; i>=0; i--)
           if (v.elementAt(i) instanceof FitZoom)
               v.removeElementAt(i);
      
        return v;
    }

    public void setDefaultGraphElements()
    {
        boolean b = true;

        properties.initDefault(GraphProperties.H_AXIS, b);
        properties.initDefault(GraphProperties.H_GRID, b);
        properties.initDefault(GraphProperties.H_SCALE, b);
        properties.initDefault(GraphProperties.V_AXIS, b);
        properties.initDefault(GraphProperties.V_GRID, b);
        properties.initDefault(GraphProperties.V_SCALE, b);
        properties.initDefault(GraphProperties.V_TITLE, b);
        properties.initDefault(GraphProperties.H_TITLE, b);
        properties.initDefault(GraphProperties.V_LABEL, b);
        properties.initDefault(GraphProperties.H_LABEL, b);
        properties.initDefault(GraphProperties.INTERACTIVE_ZOOM, b);
    }

    public void drawModelList()
    {
        ModelList modelList = getModelList();
        
        if (thread!=null) {
            //this should make the old thread stop running....
            thread.kill();
        }
        
        thread = new ImplicitFunctionDrawingThread(this, gr, modelList);
        thread.start();
   
    }
  
   
}



