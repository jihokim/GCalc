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
import java.awt.Graphics2D;
import java.util.Vector;

import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.BooleanOperation;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.properties.GraphProperties;



public class CartesianInequalityGraph extends CartesianGraph
{
    private DrawingThread thread = null;
   
    public CartesianInequalityGraph(AbstractCartesianGraphPlugin plugin)
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
        properties.initDefault(GraphProperties.TRACE, b);
        properties.initDefault(GraphProperties.INTERACTIVE_ZOOM, b);
    }

    public void drawModelList()
    {
        ModelList modelList = getModelList();
        
        if (thread!=null) {
            //this should make the old thread stop running....
            thread.kill();
        }
        
        thread = new InequalityDrawingThread(this, gr, modelList);
        thread.start();
    }
    
   
    class InequalityDrawingThread extends LayeredDrawingThread
    {
        CartesianGraph graph;
        
        InequalityDrawingThread(CartesianGraph parent, Graphics2D g, ModelList m)
        {
            super(parent,g, m);
            graph = parent;
        }
        
        protected void render(int k, Graphics buffer, RenderableModel model)
        {
            buffer.setColor(new Color(1,1,1,0f));
            buffer.fillRect(0,0,1000,1000);
            
            Function inequality = model.getFunction();
          
            Vector vars = inequality.getVars();
           // System.out.println(vars);
            double x,y,z;
            
            ValueTable vt = new ValueTable();
            
            float[] rgba = model.getColor().getRGBColorComponents(null);
            buffer.setColor(new Color(rgba[0], rgba[1], rgba[2], .5f));
            
            if (vars.contains(VariableToken.X_VAR) && vars.contains(VariableToken.Y_VAR))
            {
                for (int i=0; i<getWidth(); i++) {
                    x = screenXtoCartesian(i);
                    vt.setValue(VariableToken.X_VAR, x);
                    for (int j=0; j<getHeight(); j++)
                    {
                        y = screenYtoCartesian(j);
                        vt.setValue(VariableToken.Y_VAR, y);
                        
                        z = inequality.evaluate(vt);
                        
                        if (!Double.isNaN(z)) {
                            buffer.drawLine(i,j,i,j);
                        }
                    }
                    
                    int progress = modelIdx*100+100*i/getWidth();
                    progressMonitor.setProgress(progress);
                    progressMonitor.setNote(100*progress/progressMonitor.getMaximum()+"% complete");
                    
                    if (isDead()) 
                        break;
                }
            }
            else if (vars.contains(VariableToken.X_VAR)) {
                for (int i=0; i<getWidth(); i++) {
                    x = screenXtoCartesian(i);
                    vt.setValue(VariableToken.X_VAR, x);
                    z= inequality.evaluate(vt);
                    if (!Double.isNaN(z)) {
                        buffer.drawLine(i,0,i,getHeight());
                    }
                    
                    int progress = modelIdx*100+100*i/getWidth();
                    progressMonitor.setProgress(progress);
                    progressMonitor.setNote(100*progress/progressMonitor.getMaximum()+"% complete");
                    
                    if (isDead()) 
                        break;
                }   
            }   
            else if (vars.contains(VariableToken.Y_VAR)) {
                for (int i=0; i<getHeight(); i++) {
                    y = screenYtoCartesian(i);
                    vt.setValue(VariableToken.Y_VAR, y);
                    z= inequality.evaluate(vt);
                    if (!Double.isNaN(z)) {
                        buffer.drawLine(0,i, getWidth(),i);
                    }
                    
                    int progress = modelIdx*100+100*i/getHeight();
                    progressMonitor.setProgress(progress);
                    progressMonitor.setNote(100*progress/progressMonitor.getMaximum()+"% complete");
                    
                   if (isDead()) 
                        break;
                }   
            }
                   
            try {
                //can throw classException, but ignore it if it does.
                BooleanOperation g = (BooleanOperation) inequality;
                
                if (! g.isStrict()) {
                    Function F = g.getZeroSetFunction();
                    ModelList modelList = new ModelList(new ColoredModel(F,Color.black));
                    Thread thread = new ImplicitFunctionDrawingThread(graph, buffer, modelList);
                    
                    thread.start();
                    //don't die until this thread is done.
                    thread.join();
                }
            }
            catch (InterruptedException e0) {}
            catch (ClassCastException e1) {}
        }
               
        protected void finishRun()
        {
            repaint();
        }
    }
}



