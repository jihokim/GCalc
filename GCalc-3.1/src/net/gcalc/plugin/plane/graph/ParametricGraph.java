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
 * Email: jiho@gcalcul.us
 * Web: http://gcalcul.us
 * 
 * Snail Mail: 
 *   Jiho Kim
 *   1002 Monterey Lane
 *   Tacoma, WA 98466
 */

package net.gcalc.plugin.plane.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Range;
import net.gcalc.plugin.properties.View;
import net.gcalc.plugin.properties.Zoom;



public class ParametricGraph extends CartesianGraph 
{
    public ParametricGraph(AbstractCartesianGraphPlugin plugin)
    {
        super(plugin);
        this.getProperties().put(GraphProperties.V_TITLE_STRING, "y");
    }
    
    protected Vector makeZoomsVector()
    {
       Vector v = super.makeZoomsVector();
      
       //get rid of fit zooms
       for (int i=v.size()-1; i>=0; i--) 
           if (v.elementAt(i) instanceof FitZoom)
               v.removeElementAt(i);
           
       //add the a parameter to all the zooms...
       for (int i=0; i<v.size(); i++)
           v.setElementAt(zoomWrapper((Zoom) v.elementAt(i)),i);
      
        return v;
    }
    
    public Zoom zoomWrapper(Zoom z) {
    	return new ParametricZoomWrapper(z);
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
    
    public void setDefaultView()
    {
        super.setDefaultView();
        
        View view = new View(new Range(-5, 5, 1), new Range(-5, 5, 1), new Range(-10,10,.01));
        view.setNames(new String[] {"x", "y", "t"});
        
        properties.initDefault(GraphProperties.VIEW,view); 
    }
    
    protected void draw(RenderableModel model)
    {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Function f = model.getFunction(0);
        Function g = model.getFunction(1);
        
        Range T = properties.getViewProperty(GraphProperties.VIEW).getRange(2);

        draw(f,g, T, model.getColor(), buffer.getGraphics(), new SymbolTable());
        gr.drawImage(buffer,0,0,null);
        model.setImage(buffer);
    }
    
    
    
    protected void draw(Function F, Function G, Range T, Color c, Graphics g, SymbolTable st)
    {
        ValueTable vt = new ValueTable();
        
        int steps = (int) Math.min((T.getWidth()/T.getScale()),1e6);
        double tscl = T.getScale();
        double tmin = T.getMin();
        
        double t;
        double x,y;
        int sx,sy,rx,ry;
        
        //init to bogus numbers
        rx = ry = Integer.MAX_VALUE;
        
        int thickness = properties.getBooleanProperty(GraphProperties.THICK_GRAPH)?3:1;
      
        for (int i=0; i<steps; i++) {
            t = i*tscl + tmin;
            vt.setValue(VariableToken.T_VAR, t);
            x = F.evaluate(st, vt);
            y = G.evaluate(st, vt);
            
            sx = cartesianXtoScreen(x);
            sy = cartesianYtoScreen(y);
            
            if (isOnScreen(sx,sy) && isOnScreen(rx, ry)) {
            		drawThickLine(g,sx,sy,rx,ry,c,thickness);
            		rx = sx;
            		ry = sy;
            }
            else if (isOnScreen(sx,sy)) {                
            		drawThickLine(g,sx,sy,sx,sy,c,thickness);
            		rx = sx;
            		ry = sy;
            }
            else {
            		rx = ry = Integer.MAX_VALUE;
            }
            

            /*
            if (rx!=Integer.MAX_VALUE) {
                if (isOnScreen(sx,sy) && isOnScreen(rx, ry)) {
                    drawThickLine(g,sx,sy,rx,ry,c,thickness);
                    rx = sx;
                    ry = sy;
                }
                else if (isOnScreen(sx,sy)) {                
                    drawThickLine(g,sx,sy,sx,sy,c,thickness);
                    rx = sx;
                    ry = sy;
                }
                else {
                    rx = ry = Integer.MAX_VALUE;
                }
            }
            else if (0<=sx&&sx<=getWidth()&&0<=sy&&sy<=getHeight())
            {
                g.drawLine(sx, sy, sx, sy);
                rx = sx;
                ry = sy;
            }
            */

        }
    }
    
    /**
     * Wrapper for zooms which adds a parameter <code>t</code> to
     * the underlying zoom. This wrapper exists so that we don't have
     * to redefine the zooms from the CartesianGraph class.
     */
    class ParametricZoomWrapper extends Zoom
    {
        private Zoom zoom;
        public ParametricZoomWrapper(Zoom z)
        {
            zoom = z;
        }
        
        public String getName()
        {
            return zoom.getName();
        }
        
        public View getView()
        {
            View view = zoom.getView();
            int n = view.getDimension(); 
            Range[] range = new Range[n+1];
            String[] varName = new String[n+1];
                  
            for (int i=0; i<n; i++) {
                range[i]=view.getRange(i);
                varName[i]=view.getVarNames()[i];
            }
            
            range[n]=properties.getViewProperty(GraphProperties.VIEW).getRange(2);
            varName[n]=properties.getViewProperty(GraphProperties.VIEW).getVarNames()[2];
        
           	view= new View(range);
           	view.setNames(varName);
           	
           	return view;
        }
    }

}


