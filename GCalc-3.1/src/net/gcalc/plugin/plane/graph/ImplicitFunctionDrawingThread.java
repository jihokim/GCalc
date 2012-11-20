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

Email: jiho@gcalc.net
Web: http://gcalc.net

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.plugin.plane.graph;





import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Stack;

import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.VariableToken;


class ImplicitFunctionDrawingThread extends LayeredDrawingThread
{
    private boolean DEBUG=false;
    private double[][] gridVal = null;
    private CartesianGraph graph;
   
    ImplicitFunctionDrawingThread(CartesianGraph parent, Graphics g, ModelList m)
    {
        super(parent,g, m);
        graph = parent;
    }
    
    protected void render(int K, Graphics buffer, RenderableModel model)
    {
        //fill with transparent color;
        buffer.setColor(new Color(1,1,1,0f));
      // buffer.setColor(Color.white);
        buffer.fillRect(0,0,graph.getWidth(),graph.getHeight());
        
        //initialized evaluation objects
        ValueTable vt = new ValueTable();
        
        //get the function
        Function f = model.getFunction();
        
        //TODO depending on the variables involved, we can be smart about how we 
        //graph the equation...  But we'll be naive for now and ignore this.
        
        
        
        //compute derivatives
        Function fx = f.derivative(VariableToken.X_VAR);
        Function fy = f.derivative(VariableToken.Y_VAR);
        Function fxx = fx.derivative(VariableToken.X_VAR);
        Function fyy = fy.derivative(VariableToken.Y_VAR);
        Function fxy = fx.derivative(VariableToken.Y_VAR);
        
        
        //stack for backtracking
        Stack S = new Stack();
        
        //divide the screen in to D^2 parts and push them onto the stack.
        int D = 5;
        int height = graph.getHeight();
        int width = graph.getWidth();
        
        int hx = width / D;
        int hy = height / D;
        for (int j = 0; j < height; j += hy)
            for (int i = 0; i < width; i += hx)
                S.add(new Rect(i, j, Math.min(i + hx, width), Math.min(j + hy, height)));
       
        
        //initialized computation variables
        double hess, grad, w;
        
        double dx = Math.abs(graph.screenXtoCartesian(2) - graph.screenXtoCartesian(1));           
        double dy = Math.abs(graph.screenYtoCartesian(2) - graph.screenYtoCartesian(1)); 
        double epsilon = Math.min(dx, dy);
        
        double ne, nw, se, sw;
        double val;
        
        float rnd;
        
        Point[] P = new Point[4];
        
        Rect R = null;
        
        Color color = model.getColor();
        
            
        while (!S.isEmpty() )
        {
            //current rectangle to look at.
            R = (Rect) S.pop();
            
            //approximate the maximum hessian value in the rectangle
            hess = Math.abs(hessianDeterminant(fxx, fyy, fxy, vt, R));
            
            //compute magnitude of the gradient
            vt.setValue(VariableToken.X_VAR, graph.screenXtoCartesian(R.xmid()));
            vt.setValue(VariableToken.Y_VAR, graph.screenYtoCartesian(R.ymid()));
            grad = gradient(fx, fy, vt);
          
            //this is an upper bound on the width/height of the rectangle.
            w = R.w() * Math.max(dx, dy);
            
            //only draw if (1) the box is small enough, (2) Box is too small or 
            //the f(x,y) is flat enough.  The hessian/grad*w*w measure the flatness.
            if (R.w()<20 && (hess/grad*w*w<epsilon || R.w()<3))
            {
                
                if (DEBUG)
                {
                    //gray
                    rnd = (float) (Math.random() / 5 + .66);
                    buffer.setColor(new Color(rnd, rnd, rnd));
                    buffer.fillRect(R.x1, R.y1, R.x2 - R.x1, R.y2 -
                            R.y1);
                }
                
                //okay, draw a line segment;
                
                int i = 0;
                
                nw = getFunctionValue(f, vt, R.x1, R.y1);
                sw = getFunctionValue(f, vt, R.x1, R.y2);
                ne = getFunctionValue(f, vt, R.x2, R.y1);
                se = getFunctionValue(f, vt, R.x2, R.y2);
                
                /*
                 if (Math.abs(R.x1-getWidth()/2)<5) {
                 System.out.println(R.x1+" "+R.y1+" "+R.x2+" "+R.y2);
                 System.out.println(nw+" "+sw+" "+ne+" "+se);
                 }
                 */
                
                if (sw == 0 && ne == 0 && se == 0 && nw == 0)
                {
                    buffer.setColor(color);
                    buffer.fillRect(R.x1, R.y1, R.x2 - R.x1, R.y2 - R.y1);
                    i = -1;
                }
                
                //north
                if (nw == 0 && ne == 0)
                {
                    buffer.setColor(color);
                    buffer.drawLine(R.x1, R.y1, R.x2, R.y1);
                    i = -1;
                }
                
                //west
                if (nw == 0 && sw == 0)
                {
                    buffer.setColor(color);
                    buffer.drawLine(R.x1, R.y1, R.x1, R.y2);
                    i = -1;
                }
                
                //east
                if (ne == 0 && se == 0)
                {
                    buffer.setColor(color);
                    buffer.drawLine(R.x2, R.y1, R.x2, R.y2);
                    i = -1;
                }
                
                //south
                if (sw == 0 && se == 0)
                {
                    buffer.setColor(color);
                    buffer.drawLine(R.x1, R.y2, R.x2, R.y2);
                    i = -1;
                }
                
                //northwest/southeast
                if (nw == 0 && se == 0)
                {
                    buffer.setColor(color);
                    buffer.drawLine(R.x1, R.y1, R.x2, R.y2);
                    i = -1;
                }
                
                //southswest/northeast
                if (sw == 0 && ne == 0)
                {
                    buffer.setColor(color);
                    buffer.drawLine(R.x1, R.y2, R.x2, R.y1);
                    i = -1;
                }
                
                // We know now that none of the corners are zero.
                
                if (i != -1)
                {
                    //check north
                    if (nw * ne <= 0)
                    {
                        val = R.x1 + nw / (nw - ne) * (R.x2
                                - R.x1);
                        P[i] = new Point((int) (val + .5), R.y1);
                        i++;
                    }
                    
                    //check west
                    if (nw * sw <= 0)
                    {
                        val = R.y1 + nw / (nw - sw) * (R.y2
                                - R.y1);
                        P[i] = new Point(R.x1, (int) (val +
                                .5));
                        i++;
                    }
                    
                    //check east
                    if (ne * se <= 0)
                    {
                        val = R.y1 + ne / (ne - se) * (R.y2
                                - R.y1);
                        P[i] = new Point(R.x2, (int) (val +
                                .5));
                        i++;
                    }
                    
                    //check south
                    if (sw * se <= 0)
                    {
                        val = R.x1 + sw / (sw - se) * (R.x2
                                - R.x1);
                        P[i] = new Point((int) (val + .5), R.y2);
                        i++;
                    }
                    
                    if (i >= 2)
                    {
                        if (DEBUG)
                        {
                            //red
                            rnd = (float) (Math.random() / 5 + .8);
                            buffer.setColor(new Color(rnd, .77f, .77f));
                            buffer.fillRect(R.x1, R.y1, R.x2 - R.x1, R.y2 - R.y1);
                        }
                        
                        buffer.setColor(color);
                        for (int k = 0; k < i; k++)
                            for (int j = k; j < i; j++)
                                buffer.drawLine(P[j].x, P[j].y,
                                        P[k].x, P[k].y);
                        
                    }
                    
                }
                
                if (DEBUG)
                {
                    if (i == 0)
                    {
                        //green
                        rnd = (float) (Math.random() / 5 + .8);
                        buffer.setColor(new Color(.77f, rnd, .77f));
                        buffer.fillRect(R.x1, R.y1, R.x2 - R.x1, R.y2 - R.y1);
                    }
                }
                
            }
            else
            {
                addToStack(S, R, R.x1, R.y1, R.xmid(), R.ymid());
                addToStack(S, R, R.xmid(), R.y1, R.x2, R.ymid());
                addToStack(S, R, R.xmid(), R.ymid(), R.x2, R.y2);
                addToStack(S, R, R.x1, R.ymid(), R.xmid(), R.y2);
            }
        }
        
    }
    
    private double getFunctionValue(Function F, ValueTable vt, int x, int y)
    {
        if (gridVal == null)
            {
                gridVal = new double[graph.getWidth()][graph.getHeight()];

                for (int i = 0; i < gridVal.length; i++)
                    for (int j = 0; j < gridVal[i].length; j++)
                        gridVal[i][j] = Double.NaN;
            }

        double val = 0;

        if (x < 0 || y < 0 || x >= gridVal.length || y >= gridVal[x].length
|| Double.isNaN(gridVal[x][y]))
            {

                vt.setValue(VariableToken.X_VAR, graph.screenXtoCartesian(x));
                vt.setValue(VariableToken.Y_VAR, graph.screenYtoCartesian(y));
                val = F.evaluate(vt);
            }
        else
            {
                val = gridVal[x][y];
            }

        return val;
    }

    //add a Rectangle to stack to be processed later, making sure it's smaller than the current one.
    private void addToStack(Stack S, Rect R, int u1, int v1, int u2, int v2)        {
        if (R.x1 != u1 || R.y1 != v1 || R.x2 != u2 || R.y2 != v2)
            {
                S.add(new Rect(u1, v1, u2, v2));
            }
    }

    //approximate the maximum of the determinants in the rectangle
    //R.
    private double hessianDeterminant(Function fxx, Function fyy, Function fxy, ValueTable vt, Rect R)
    {
        double x1 = graph.screenXtoCartesian(R.x1);
        double x2 = graph.screenXtoCartesian(R.x2);
        double y1 = graph.screenXtoCartesian(R.y1);
        double y2 = graph.screenXtoCartesian(R.y2);

        double M = -1;

        vt.setValue(VariableToken.X_VAR, x1);
        vt.setValue(VariableToken.Y_VAR, y1);
        M = Math.max(Math.abs(hessian(fxx, fyy, fxy, vt)), M);

        vt.setValue(VariableToken.Y_VAR, y2);
        M = Math.max(Math.abs(hessian(fxx, fyy, fxy, vt)), M);

        vt.setValue(VariableToken.X_VAR, x2);
        M = Math.max(Math.abs(hessian(fxx, fyy, fxy, vt)), M);

        vt.setValue(VariableToken.Y_VAR, y1);
        M = Math.max(Math.abs(hessian(fxx, fyy, fxy, vt)), M);

        vt.setValue(VariableToken.X_VAR, R.xmid());
        vt.setValue(VariableToken.Y_VAR, R.ymid());
        M = Math.max(Math.abs(hessian(fxx, fyy, fxy, vt)), M);
 
        return M;
    }

    //compute the determinant of the hessian given the second
    //derivatives.
    private double hessian(Function fxx, Function fyy, Function fxy, ValueTable vt)
    {

        double xx = fxx.evaluate( vt);
        double yy = fyy.evaluate( vt);
        double xy = fxy.evaluate( vt);

        return xx * yy - xy * xy;
    }

    //compute the magnitude of the gradient given the first
    //derivatives.
    private double gradient(Function fx, Function fy, ValueTable vt)
    {

        double x = fx.evaluate(vt);
        double y = fy.evaluate(vt);

        return Math.sqrt(x * x + y * y);
    }

           
    protected void finishRun()
    {
        graph.repaint();
    }
}

class Rect
{
    public int x1, y1, x2, y2;

    public Rect(int x1, int y1, int x2, int y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int xmid()
    {
        return (x1 + x2) / 2;
    }

    public int ymid()
    {
        return (y1 + y2) / 2;
    }

    public double w()
    {
        return Math.max(x2 - x1, y2 - y1);
    }

}
