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


 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.RungeKutta;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.models.Model;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.plane.model.IVPModel;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.View;



public class DirectionFieldGraph extends CartesianGraph implements MouseListener
{
   private Function F, G;
   
   
    public DirectionFieldGraph(AbstractCartesianGraphPlugin plugin)
    {
        super(plugin);
        this.getProperties().put(GraphProperties.V_TITLE_STRING, "y");
          this.addMouseListener(this);        
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
    
    public void drawBackgroundComponents()
    {
        //the superclass redrawAll will draw all the graph components
        super.drawBackgroundComponents();
        
        //draw the current system
        if (F!=null && G!=null)
            drawSystem(F,G);
    }
    
    protected void drawSystem(Function f, Function g)
	{
        //TODO move this to a property...
	    Color lightBlue = new Color(200, 200, 255);

	    ValueTable vt = new ValueTable();
		
		//get the screen dimension in cartesian units
		View view = properties.getViewProperty(GraphProperties.VIEW);
		double xw = view.getRange(0).getWidth();
		double yw = view.getRange(1).getWidth();
		
		int step = 15;
		int sx, sy, dx, dy;
		double x0, y0, t1, t2, d;
		for (int i = 0; i < getWidth(); i += step)
		{
			for (int j = 0; j < getHeight(); j += step)
			{
				sx = i + step / 2;
				sy = j + step / 2;
				x0 = screenXtoCartesian(sx);
				y0 = screenYtoCartesian(sy);

				vt.setValue(VariableToken.X_VAR, x0);
				vt.setValue(VariableToken.Y_VAR, y0);

				//get vector
				t1 = f.evaluate(vt);
				t2 = g.evaluate(vt);
				
				//adjust vector to screen...
				t1 = t1*getWidth()*yw;
				t2 = t2*getHeight()*xw;
				

				//compute length of vector
				d = Math.sqrt(t1 * t1 + t2 * t2);
				
				//normalize vectors
				t1 = t1 / d;
				t2 = t2 / d;
				
				dx = (int) ((step - 2) * t1 / 2);
				dy = - (int) ((step - 2) * t2 / 2);

				gr.setColor(lightBlue);
				gr.drawLine(sx + dx, sy + dy, sx - dx, sy - dy);
				gr.fillRect(sx + dx - 1, sy + dy - 1, 3, 3);
			}
		}
	}

    
    public void drawSystem(Model model)
    {
        F = model.getFunction(0);
        G = model.getFunction(1);
        ModelList modelList = (ModelList) properties.get(GraphProperties.MODEL_LIST);
        modelList.removeAllModels();
        
        redrawAll();
    }
    
    public void draw(RenderableModel model)
    {
         if (! (model instanceof IVPModel))
            return;
        
        IVPModel ivpModel = (IVPModel) model;
        
        if (ivpModel.getImage()!=null) {
            gr.drawImage(ivpModel.getImage(),0,0,null);
        }
        else {
            Point2D p = ivpModel.getPoint();
            
            BufferedImage modelImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            
            rungeKuttaMethod(p.getX(), p.getY(), modelImage.getGraphics(), ivpModel.getColor());
            gr.drawImage(modelImage, 0,0, null);
            ivpModel.setImage(modelImage);
        }
        
        ivpModel.setDrawn(true);
    }
    
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    public void mouseClicked(MouseEvent e)
    { 
        if (e.getButton()!=MouseEvent.BUTTON1)
            return;
        
        if (F != null && G != null)
        {
            double x0 = screenXtoCartesian(e.getX());
            double y0 = screenYtoCartesian(e.getY());
            
            ColoredModel model = new IVPModel(new Function[] {F,G}, new String[] {F.toInfix(), G.toInfix()},x0, y0, Color.BLACK);
            ModelList modelList = (ModelList) properties.get(GraphProperties.MODEL_LIST);
            modelList.add(model);
            
            redrawAll();
        }
    }
    
	
	private void rungeKuttaMethod(double x, double y, double h0, double tmax, Graphics g, Color color)
	{
		double xn, yn, dx, dy, error, h;
		int sx, sy, rx, ry;
		ValueTable vt = new ValueTable();
		SymbolTable st = new SymbolTable();
		rx = cartesianXtoScreen(x);
		ry = cartesianYtoScreen(y);
		xn = x;
		yn = y;
		h = h0;

		double[] u = new double[2];
		double[] v = new double[2];

		g.setColor(Color.orange);
		g.fillRect(rx - 2, ry - 2, 5, 5);

		RungeKutta rk = new RungeKutta();
	
		for (int i = 0; i < tmax; i++)
		{
			//bogus number
			error = -4;

			//reset step size
			h = h0;

			do
			{
				//one step
				u = rk.oneStep(F, G, xn, yn, h, vt, st, u);

				//two half steps
				v = rk.oneStep(F, G, xn, yn, h / 2, vt, st, v);
				v = rk.oneStep(F, G, xn, yn, h / 2, vt, st, v);

				//estimate the trucation error
				dx = Math.abs(u[0] - v[0]);
				dy = Math.abs(u[1] - v[1]);
				error = Math.max(dx, dy);

				if (Math.abs(h) < 1e-2)
					break;

				if (error > 1e-7)
					h = h * .1;

			}
			while (error > 1e-15);

			//the numbers from the half-steps are more accurate.  Use them.
			xn = v[0];
			yn = v[1];

			//	System.out.println(error+" "+h);

			sx = cartesianXtoScreen(xn);
			sy = cartesianYtoScreen(yn);

			g.setColor(color);
			if (rx > 0 && ry > 0)
				g.drawLine(rx, ry, sx, sy);

			rx = sx;
			ry = sy;

			if (rx < 0 || rx >= getWidth() || ry < 0 || ry >= getHeight())
				break;
		}

	}

	private synchronized void rungeKuttaMethod(double x, double y, Graphics gr, Color color)
	{
		double h = 1;
		double tmax = 10000;

		rungeKuttaMethod(x, y, h, tmax, gr, color);
		rungeKuttaMethod(x, y, -h, tmax, gr, color);

		repaint();
	}

	
	

   
}



