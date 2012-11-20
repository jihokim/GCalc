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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Vector;

import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.plane.gui.CoordinatePanel;
import net.gcalc.plugin.plane.gui.ExtendedMouseAdapter;
import net.gcalc.plugin.plane.gui.ResizeAndPackAdapter;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Range;
import net.gcalc.plugin.properties.StaticZoom;
import net.gcalc.plugin.properties.View;
import net.gcalc.plugin.properties.Zoom;



public class CartesianGraph extends GraphCanvas
{
    //Mouse Motion mode
    public final static int VANILLA_MODE = 0;
    public final static int TRACE_MODE = 1;
    public final static int DRAW_MODE = 2;

    private String[] labels = new String[2];
 
    protected ExtendedMouseAdapter normalMouseAdapter = new NormalMouseAdapter();
    protected ExtendedMouseAdapter traceMouseAdapter = new TraceMouseAdapter();    
    protected ExtendedMouseAdapter tangentMouseAdapter = new TangentMouseAdapter();
    
    protected Rectangle highlightRectangle = null;
    private final Color LIGHT_YELLOW = new Color(255,255,200);
    
  
    
    public CartesianGraph(AbstractCartesianGraphPlugin plugin)
    {
        this(new GraphProperties());
        getProperties().put(GraphProperties.PLUGIN, plugin);
        new ResizeAndPackAdapter(plugin, this);
    }

    private CartesianGraph(GraphProperties gp)
    {
        super(gp);
        addExtendedMouseAdapter(normalMouseAdapter);
        gp.put(GraphProperties.ZOOMS, makeZoomsVector());
        gp.put(GraphProperties.GRAPH_CANVAS, this);
        gp.put(GraphProperties.V_TITLE_STRING, "f(x)");
        gp.put(GraphProperties.H_TITLE_STRING, "x");
   
    }

    protected Vector makeZoomsVector()
    {
        Vector z = new Vector();
        z.add(new StaticZoom("Standard Zoom", new View(new Range(-6, 6, 1),
                new Range(-6, 6, 1))));
        z.add(new StaticZoom("Quadrant I Zoom", new View(new Range(-1,11,1), new Range(-1, 11, 1))));
        z.add(new StaticZoom("Trig Zoom", new View(new Range(-4*Math.PI,
                4*Math.PI, Math.PI/2), new Range(-4, 4, 1))));
        z.add(new FitZoom());
        z.add(new SquareZoom());

        return z;
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
        properties.initDefault(GraphProperties.TRACE, !b);
        properties.initDefault(GraphProperties.SHOW_CONCAVITY, !b);
        properties.initDefault(GraphProperties.SHOW_MONOTONICITY, !b);
        properties.initDefault(GraphProperties.THICK_GRAPH, !b);
        properties.initDefault(GraphProperties.INTERACTIVE_ZOOM, b);

    }

    public void setDefaultColors()
    {
        properties.initDefault(GraphProperties.H_AXIS_COLOR, Color.red);
        properties.initDefault(GraphProperties.V_AXIS_COLOR, Color.red);
        properties.initDefault(GraphProperties.H_GRID_COLOR, Color.lightGray);
        properties.initDefault(GraphProperties.V_GRID_COLOR, Color.lightGray);
        properties.initDefault(GraphProperties.H_SCALE_COLOR, Color.blue);
        properties.initDefault(GraphProperties.V_SCALE_COLOR, Color.blue);
        properties.initDefault(GraphProperties.V_LABEL_COLOR, Color.black);
        properties.initDefault(GraphProperties.H_LABEL_COLOR, Color.black);
        properties.initDefault(GraphProperties.V_TITLE_COLOR, Color.black);
        properties.initDefault(GraphProperties.H_TITLE_COLOR, Color.black);
        properties.initDefault(GraphProperties.INCREASING_COLOR,  new Color(255,128,128));
        properties.initDefault(GraphProperties.DECREASING_COLOR,  new Color(128,128,255));
        properties.initDefault(GraphProperties.CONCAVITY_COLOR,  new Color(128,128,128));
        

        this.setBackground(Color.white);
    }

    protected void setDefaultFonts()
    {
        properties.initDefault(GraphProperties.AXES_LABEL_FONTS, new Font("SansSerif", Font.PLAIN, 9));
    }

    protected void setDefaultView()
    {
        properties.initDefault(GraphProperties.VIEW, new View(new Range(-6, 6,
                1), new Range(-6, 6, 1)));
        properties.initDefault(GraphProperties.TRACE, false);
        properties.revertToDefault(GraphProperties.VIEW);
    }
    
    /**
     * Convert the horizontal coordinate from the screen system to the
     * cartesian system.
     */
    public double screenXtoCartesian(int x)
    {
        return getXRange().getWidth()*x/(getWidth()-1)+getXRange().getMin();
    }

    /**
     * Convert the vertical coordinate from the screen system to the
     * cartesian system.
     */
    public double screenYtoCartesian(int y)
    {
        return -getYRange().getWidth()*y/(getHeight()-1)+getYRange().getMax();
    }

    protected void setXYRange(Range x, Range y)
    {
        View view = properties.getViewProperty(GraphProperties.VIEW);
        int n = view.getDimension();
        
        if (n==2) {
            view= new View(x, y);
        }
        else if (view.getDimension()>2) {
            Range[] r = new Range[n];
            r[0]=x;
            r[1]=y;
            for (int i=2; i<n; i++)
                r[i]=view.getRange(i);
            view = new View(r);
        }
        
        properties.put(GraphProperties.VIEW, view);
        // properties.put(GraphProperties.VIEW, new View(x, y));
    }

    /**
     * Return the range in x direction.
     */
    public Range getXRange()
    {    
    	View view = (View) properties.get(GraphProperties.VIEW);
        return view.getRange(0);
    }

    /**
     * Return the range in y direction.
     */
    public Range getYRange()
    {
        View view = (View) properties.get(GraphProperties.VIEW);
        return view.getRange(1);
    }

    /**
     * Convert the horizontal coordinate from the cartesian system to
     * the screen system.
     */
    public int cartesianXtoScreen(double x)
    {
        if (Double.isNaN(x))
            return Integer.MAX_VALUE;

        x = (x-getXRange().getMin())/getXRange().getWidth()*(getWidth()-1)+.5;

        return (int) x;
    }

    /**
     * Convert the vertical coordinate from the cartesian system to
     * the screen system.
     */
    public int cartesianYtoScreen(double y)
    {
        if (Double.isNaN(y))
            return Integer.MAX_VALUE;

        y = (getYRange().getMax()-y)/getYRange().getWidth()*(getHeight()-1)+.5;

        return (int) y;
    }
    
    public void redrawAll(boolean b)
    {
       super.redrawAll(b);
       
       drawHighlightRectangle(highlightRectangle);
    }
    
    
    protected void drawHighlightRectangle(Rectangle R)
    {
        if (R==null)
            return;
        
        synchronized(gr) {
            gr.setXORMode(Color.white);
            gr.setColor(LIGHT_YELLOW);
            gr.fillRect(R.x, R.y, R.width, R.height);       
            gr.setColor(Color.orange);
            gr.drawRect(R.x, R.y, R.width, R.height);  
            gr.setColor(Color.BLACK);
            //TODO make this cleaner on the screen... like clear out the background...
          //  gr.drawString("Double right-click out/in-side box to zoom out/in...", 1,getHeight()-2);
            gr.setPaintMode();
        }
    }

    protected void drawAxes()
    {
        int x = cartesianXtoScreen(0);
        int y = cartesianYtoScreen(0);

        if (properties.getBooleanProperty(GraphProperties.H_AXIS)&&y>=0
                &&y<=getHeight())
        {
            gr.setColor(properties.getColorProperty(GraphProperties.H_AXIS_COLOR));
            gr.drawLine(0, y, getWidth(), y);
        }

        if (properties.getBooleanProperty(GraphProperties.V_AXIS)&&x>=0
                &&x<=getWidth())
        {
            gr.setColor(properties.getColorProperty(GraphProperties.V_AXIS_COLOR));
            gr.drawLine(x, 0, x, getHeight());
        }

    }

    protected void drawGrid()
    {
        if (properties.getBooleanProperty(GraphProperties.V_GRID))
        {
            gr.setColor(properties.getColorProperty(GraphProperties.V_GRID_COLOR));

            int min = (int) (screenXtoCartesian(0)/getXRange().getScale()-1);
            int max = (int) (screenXtoCartesian(getWidth())
                    /getXRange().getScale()+1);
            int j = 0;

            if (max-min<getWidth()/getXRange().getScale()*3)
            {
                for (int u = min; u<max; u++)
                {
                    j = cartesianXtoScreen(u*getXRange().getScale());
                    if (j>=0&&j<=getWidth())
                        gr.drawLine(j, 0, j, getHeight());
                }
            }
            else
            {
                gr.fillRect(0, 0, getWidth(), getHeight());
            }

        }

        if (properties.getBooleanProperty(GraphProperties.H_GRID))
        {
            gr.setColor(properties.getColorProperty(GraphProperties.H_GRID_COLOR));

            int min = (int) (screenYtoCartesian(getHeight())
                    /getYRange().getScale()-1);
            int max = (int) (screenYtoCartesian(0)/getYRange().getScale()+1);
            int j = 0;

            if (max-min<getHeight()/getYRange().getScale()*3)
            {
                for (int u = min; u<max; u++)
                {
                    j = cartesianYtoScreen(u*getYRange().getScale());
                    if (j>=0&&j<=getHeight())
                        gr.drawLine(0, j, getWidth(), j);
                }
            }
            else
            {
                gr.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    /**
     * Draws the labels on axes at regular intervals. This
     * implementation could be smarted about what looks good.
     * 
     * @see net.gcalc.plugin.plane.graph.GraphCanvas#drawLabel()
     */
    protected void drawLabel()
    {
        int x = cartesianXtoScreen(0);
        int y = cartesianYtoScreen(0);

        Font font = new Font("SansSerif", Font.PLAIN, 9);
        gr.setFont(font);
        FontMetrics fm = gr.getFontMetrics();

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);
        nf.setMinimumFractionDigits(0);
        nf.setGroupingUsed(false);

        Rectangle R = new Rectangle(0, 0, getWidth(), getHeight());

        if (properties.getBooleanProperty(GraphProperties.H_LABEL)&&y>=-1
                &&y<=getHeight())
        {
            Color color = properties.getColorProperty(GraphProperties.H_LABEL_COLOR);

            Range xRange = getXRange();

            int u0 = (int) (screenXtoCartesian(getWidth()/2)/xRange.getScale());
            int max = (int) (u0+xRange.getWidth()/getXRange().getScale()+1);
            int min = (int) (u0-xRange.getWidth()/getXRange().getScale()-1);
            int j = 0;

            if ((max-min)<3*getWidth())
            {
                double xval;
                String label;

                for (int u = min; u<max; u++)
                {
                    xval = u*getXRange().getScale();
                    label = nf.format(xval);

                    int offset = fm.stringWidth(label);

                    j = cartesianXtoScreen(xval);

                    if (R.contains(new Rectangle(j-offset/2-1, y+1, offset, 10)))
                    {
                        gr.setColor(getBackground());
                        gr.fillRect(j-offset/2-1, y+1, offset, 10);

                        gr.setColor(color);

                        gr.drawString(label, j-offset/2, y+10);
                    }
                }
            }

        }

        if (properties.getBooleanProperty(GraphProperties.V_LABEL)&&x>=-1
                &&x<=getWidth())
        {
            Color color = properties.getColorProperty(GraphProperties.V_LABEL_COLOR);

            int u0 = (int) (screenYtoCartesian(getHeight()/2)/getYRange().getScale());
            int max = (int) (u0+getYRange().getWidth()/getYRange().getScale()+1);
            int min = (int) (u0-getYRange().getWidth()/getYRange().getScale()-1);
            int j = 0;

            if ((max-min)<getHeight())
            {
                double yval;
                String label;
                for (int u = min; u<max; u++)
                {
                    yval = u*getYRange().getScale();
                    label = nf.format(yval);

                    int offset = fm.stringWidth(label);

                    j = cartesianYtoScreen(yval);

                    if (R.contains(new Rectangle(x+1, j-4, offset+1, 9)))
                    {
                        gr.setColor(getBackground());
                        gr.fillRect(x+1, j-5, offset+1, 10);
                        gr.setColor(color);
                        gr.drawString(label, x+1, j+3);
                    }
                }
            }
        }
    }

    protected void drawAxesTitle()
    {
        int x = cartesianXtoScreen(0);
        int y = cartesianYtoScreen(0);

        Font font = new Font("SansSerif", Font.PLAIN, 9);
        gr.setFont(font);
        FontMetrics fm = gr.getFontMetrics();
     
        if (properties.getBooleanProperty(GraphProperties.H_TITLE)&&y>=-1
                &&y<=getHeight())
        {
            String label = properties.getStringProperty(GraphProperties.H_TITLE_STRING);
            int offset = fm.stringWidth(label);

            Color color = properties.getColorProperty(GraphProperties.H_TITLE_COLOR);
            gr.setColor(color);
            gr.drawString(label, getWidth()-offset-2, y-1);

        }

        if (properties.getBooleanProperty(GraphProperties.V_TITLE)&&x>=-1
                &&x<=getWidth())
        {
            String label = properties.getStringProperty(GraphProperties.V_TITLE_STRING);
                  int offset = fm.stringWidth(label);

            Color color = properties.getColorProperty(GraphProperties.V_TITLE_COLOR);
            gr.setColor(color);
            gr.drawString(label, x-offset-1, 10);
        }
    }

    protected void drawScale()
    {
        int x = cartesianXtoScreen(0);
        int y = cartesianYtoScreen(0);

        if (properties.getBooleanProperty(GraphProperties.H_SCALE)&&y>=-1
                &&y<=getHeight())
        {
            Range xRange = getXRange();
            gr.setColor(properties.getColorProperty(GraphProperties.H_SCALE_COLOR));

            int u0 = (int) (screenXtoCartesian(getWidth()/2)/xRange.getScale());
            int max = (int) (u0+xRange.getWidth()/xRange.getScale()+1);
            int min = (int) (u0-xRange.getWidth()/xRange.getScale()-1);
            int j = 0;

            //            System.out.println(max+" "+min);

            if (max-min<getWidth())
            {
                for (int u = min; u<max; u++)
                {
                    j = cartesianXtoScreen(u*getXRange().getScale());
                    gr.drawLine(j, y-1, j, y+1);
                }
            }
            else
            {
                gr.drawLine(0, y-1, getWidth(), y-1);
                gr.drawLine(0, y, getWidth(), y);
                gr.drawLine(0, y+1, getWidth(), y+1);
            }

        }

        if (properties.getBooleanProperty(GraphProperties.V_SCALE)&&x>=-1
                &&x<=getWidth())
        {
            gr.setColor(properties.getColorProperty(GraphProperties.V_SCALE_COLOR));

            int u0 = (int) (screenYtoCartesian(getHeight()/2)/getYRange().getScale());
            int max = (int) (u0+getYRange().getWidth()/getYRange().getScale()+1);
            int min = (int) (u0-getYRange().getWidth()/getYRange().getScale()-1);
            int j = 0;

            if (max-min<getWidth())
            {
                for (int u = min; u<max; u++)
                {
                    j = cartesianYtoScreen(u*getYRange().getScale());
                    gr.drawLine(x-1, j, x+1, j);
                }
            }
            else
            {
                gr.drawLine(x-1, 0, x-1, getHeight());
                gr.drawLine(x, 0, x, getHeight());
                gr.drawLine(x+1, 0, x+1, getHeight());
            }
        }
    }

   
    protected void draw(RenderableModel model)
    {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Function F = model.getFunction();

        draw(F, model.getColor(), buffer.getGraphics());
        gr.drawImage(buffer,0,0,null);
        model.setImage(buffer);
      //  System.out.println(model);
    }

    protected void draw(Function F, Color color, Graphics gr, ValueTable vt) {
    	  int a = 0;
          int b = getWidth();

          int n = getWidth();

          if (n<=0)
              return;

          int[] j = new int[n];

         
          //calculate points and draw dots
          double y = 0;
          for (int i = a; i<b; i++)
          {
              vt.setValue(VariableToken.X_VAR, screenXtoCartesian(i));
              y = F.evaluate(vt);
              j[i] = cartesianYtoScreen(y);

              gr.setColor(color);

              if (j[i]>=0&&j[i]<=getHeight())
                  gr.drawLine(i, j[i], i, j[i]);
          }
          
          int x0, y0, x1, y1;
          
          //refactored to another method for readability sake...
          if (properties.getBooleanProperty(GraphProperties.SHOW_MONOTONICITY) ||
                  properties.getBooleanProperty(GraphProperties.SHOW_CONCAVITY)) 
          {
              drawMonoticityAndConcavity(F, j, a, b, vt, color);
          }
          
          //connect the dots
          int thickness = properties.getBooleanProperty(GraphProperties.THICK_GRAPH)?3:1;
          for (int i = a; i<b-1; i++)
          {
              x0 = i;
              x1 = i+1;
              y0 = j[x0];
              y1 = j[x1];
              
              if (Math.abs(y1-y0)<getHeight()*5&&Math.abs(y1)<getHeight()*5)
              {
                  if (y0>=0&&y0<getHeight())
                      drawThickLine(gr, x0, y0, x1, y1, color, thickness);
                  else if (y1>=0&&y1<getHeight())
                      drawThickLine(gr, x0, y0, x1, y1, color, thickness);
              }
          }
          	
    }
   
    protected void draw(Function F, Color color, Graphics gr)
    {   
    		draw (F, color, gr, new ValueTable());
    }
    
    private void drawMonoticityAndConcavity(Function F, int[] j, int a, int b, ValueTable vt, Color color)
    {
        int k = 10;  //length of concavity hairs
        int x0, y0, x1, y1;
        double dy = 0, d2y = 0;
        
        Color up = properties.getColorProperty(GraphProperties.INCREASING_COLOR);
        Color down = properties.getColorProperty(GraphProperties.DECREASING_COLOR);
        Color concavity = properties.getColorProperty(GraphProperties.CONCAVITY_COLOR);

        Function D1 = F.derivative(VariableToken.X_VAR);
        Function D2 = D1.derivative(VariableToken.X_VAR);
        
        for (int i = a; i<b-1; i++)
        {
            x0 = i;
            x1 = i+1;
            y0 = j[x0];
            y1 = j[x1];
            
            if (Math.abs(y1-y0)<getHeight()*5&&Math.abs(y1)<getHeight()*5)
            {
                vt.setValue(VariableToken.X_VAR, screenXtoCartesian(i));
                dy = D1.evaluate(vt);
                d2y = D2.evaluate(vt);
                
                if (! Double.isNaN(dy)) {
                    
                    if (properties.getBooleanProperty(GraphProperties.SHOW_MONOTONICITY) && y0>=0&&y0<getHeight())
                    {
                        Color c = null;
                        
                        if (dy>0)
                            gr.setColor(c = up);
                        else if (dy<0)
                            gr.setColor(c = down);
                        else
                            gr.setColor(c = color);
                        
                        drawThickLine(gr, x0, y0, x1, y1, c, 7);
                    }
                    
                    if (properties.getBooleanProperty(GraphProperties.SHOW_CONCAVITY) && y0>=0&&y0<getHeight())
                    {
                        //TODO be smarter about this when dy is very close to 0.
                        double t = Math.atan(-1/dy);
                       
                       if (dy*d2y>0)
                            t += Math.PI;
                        
                        if (d2y!=0)
                            drawThickLine(gr, x0, y0, (int) (x0+k*Math.cos(t)),
                                    (int) (y0-k*Math.sin(t)), concavity, 3);
                    }
                }    
            }
        }    
    }
    
    protected void drawThickLine(Graphics g, int x0, int y0, int x1, int y1,
            Color c, int thickness)
    {
        if (thickness<0)
            return;
        
        g.setColor(c);
        
        if (thickness==1)
        {
            g.drawLine(x0, y0, x1, y1);
            return;
        }

        int dx = Math.abs(x0-x1);
        int dy = Math.abs(y0-y1);

        if (dx==0)
        {
            g.fillRect(x0-thickness/2, Math.min(y0, y1)-thickness/2, thickness,
                    dy+thickness);
            return;
        }
        if (dy==0)
        {
            g.fillRect(Math.min(x0, x1)-thickness/2, y0-thickness/2, dx
                    +thickness, thickness);
            return;
        }
        
        int x2 = (x0+x1)/2;
        int y2 = (y0+y1)/2;
        
        if (dx==1) {
            drawThickLine(g, x0, y0, x0, y2, c, thickness);
            drawThickLine(g, x1, y2, x1, y1, c, thickness);
            return;
        }

        if (dy==1) {
            drawThickLine(g, x0, y0, x2, y0, c, thickness);
            drawThickLine(g, x2, y1, x1, y1, c, thickness);
            return;
        }

        drawThickLine(g, x0, y0, x2, y2, c, thickness);
        drawThickLine(g, x2, y2, x1, y1, c, thickness);

    }

    public void update(Observable observable, Object obj)
    {
        String key = (String) obj;

        if (key==null)
            return;

        if (key.equals(GraphProperties.SCREEN_DIMENSION))
        {
            resetSize();
            redrawAll(false);
        }

        if (key.equals(GraphProperties.VIEW))
        {
            traceMouseAdapter.reset();
            
            redrawAll(false);
        }

        if (key.equals(GraphProperties.TRACE))
        {
            removeExtendedMouseAdapter(normalMouseAdapter);
            removeExtendedMouseAdapter(traceMouseAdapter);
            traceMouseAdapter.reset();
            boolean trace = properties.getBooleanProperty(key);

            if (trace)
                addExtendedMouseAdapter(traceMouseAdapter);
            else
                addExtendedMouseAdapter(normalMouseAdapter);
        }

        if (key.equals(GraphProperties.H_GRID)
                ||key.equals(GraphProperties.V_GRID)
                ||key.equals(GraphProperties.H_AXIS)
                ||key.equals(GraphProperties.V_AXIS)
                ||key.equals(GraphProperties.H_LABEL)
                ||key.equals(GraphProperties.V_LABEL)
                ||key.equals(GraphProperties.H_SCALE)
                ||key.equals(GraphProperties.V_SCALE)
                ||key.equals(GraphProperties.H_TITLE)
                ||key.equals(GraphProperties.V_TITLE)
                ||key.equals(GraphProperties.SHOW_MONOTONICITY)
                ||key.equals(GraphProperties.SHOW_CONCAVITY)
                ||key.equals(GraphProperties.THICK_GRAPH)
        )
        {
            this.redrawAll();
        }
    }

    public void setCoordinatePanel(CoordinatePanel c)
    {
        coordinatePanel = c;
    }

    private void setCoordinates(double x, double y)
    {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance();
        
      //  System.out.println(nf+"\t"+x+"\t"+y);
        nf.setMaximumFractionDigits(11);
        nf.setMinimumFractionDigits(0);
        nf.setGroupingUsed(false);
        
        String hlabel = properties.getStringProperty(GraphProperties.H_TITLE_STRING);
        String vlabel = properties.getStringProperty(GraphProperties.V_TITLE_STRING);
        
        labels[0] = hlabel+"="+nf.format(x);
        labels[1] = vlabel+"="+nf.format(y);

        if (coordinatePanel!=null)
            coordinatePanel.setLabels(labels);
    }

    private void addExtendedMouseAdapter(ExtendedMouseAdapter a)
    {
        addMouseMotionListener(a);
        addMouseListener(a);
    }

    protected void removeExtendedMouseAdapter(ExtendedMouseAdapter a)
    {
        removeMouseMotionListener(a);
        removeMouseListener(a);
    }
    
    public Zoom zoomWrapper(Zoom z) 
    {
    	return z;
    }

    protected class FitZoom extends Zoom
    {
        private ValueTable vt = new ValueTable();

        public View getView()
        {
            Range xRange = getXRange();
            Range yRange = getYRange();

            ModelList modelList = getModelList();
            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;
            double val = 0;
            for (int i = 0; i<modelList.getSize(); i++)
            {
                Function f = modelList.getModelAt(i).getFunction();

                for (int j = 0; j<getWidth(); j++)
                {
                    vt.setValue(VariableToken.X_VAR, screenXtoCartesian(j));
                    val = f.evaluate(vt);
                    if (val>max)
                        max = val;
                    if (val<min)
                        min = val;
                }
            }

            View newView = null;
            try
            {
                newView = new View(xRange, new Range(min, max,
                        yRange.getScale()));
            }
            catch (IllegalArgumentException e)
            {
                return new View(xRange, yRange);
            }

            return newView;
        }

        public String getName()
        {
            return "Fit Zoom";
        }
    }

    protected class SquareZoom extends Zoom
    {
        public View getView()
        {
            Range xRange = getXRange();
            Range yRange = getYRange();

            double height = xRange.getWidth()/getWidth()*getHeight();
            double min = yRange.getCenter()-height/2;
            double max = yRange.getCenter()+height/2;

            return new View(xRange, new Range(min, max, yRange.getScale()));
        }

        public String getName()
        {
            return "Square Zoom";
        }
    }

    private class TangentMouseAdapter extends TraceMouseAdapter {
        //TODO put something here... 
    }
    
    private class TraceMouseAdapter extends ExtendedMouseAdapter {
        protected final static int BOGUS = -20;
        protected int n = 0;
        protected int x = BOGUS;
        protected int y = BOGUS;
        
        public void mouseMoved(MouseEvent e)
        {
            double cx = screenXtoCartesian(e.getX());
            double cy = screenYtoCartesian(e.getY());
                  
            try
            {
                Function f = getModelList().getModelAt(n).getFunction();
                vt.setValue(VariableToken.X_VAR, cx);
                cy = f.evaluate(vt);
                if (!Double.isNaN(y))
                {
                    int sy = cartesianYtoScreen(cy);
                    
                    if (sy>=0&&sy<=getHeight())
                        moveTraceTo(e.getX(), sy);
                    else {
                        moveTraceTo(e.getX(), BOGUS);
                    }
                }
                else
                {
                    moveTraceTo(e.getX(), BOGUS);
                }
                
            }
            catch (Exception exception)
            {
                moveTraceTo(e.getX(), e.getY());
            }
            
            setCoordinates(cx, cy);
        }
        
        public void mouseClicked(MouseEvent e)
        {
            n = (n+1)%getModelList().getSize();
            mouseMoved(e);
        }
        
        public void mouseEntered(MouseEvent e)
        {
            mouseMoved(e);
        }
        
        public void mouseExited(MouseEvent e)
        {
            reset();
        }
        
        private void moveTraceTo(int nx, int ny)
        {
            drawTrace(x, y);
            drawTrace(nx, ny);
            x = nx;
            y = ny;
            repaint();
        }
        
        private void drawTrace(int x, int y)
        {
            gr.setXORMode(Color.white);
            gr.setColor(Color.orange);
            gr.drawRect(x-4, y-4, 8, 8);
            gr.drawLine(0, y, getWidth(), y);
            gr.drawLine(x, 0, x, getHeight());
            gr.setPaintMode();
        }
        
        public void reset()
        {
            moveTraceTo(BOGUS, BOGUS);
        }
    };
    
    private class NormalMouseAdapter extends ExtendedMouseAdapter {
        Point p1=null;
        Point p2=null;
        Rectangle R = null;
        
        public void mouseMoved(MouseEvent e)
        {
            setCoordinates(screenXtoCartesian(e.getX()),
                    screenYtoCartesian(e.getY()));
        }
        
        public void mousePressed(MouseEvent e)
        {
            if (e.getButton()!=MouseEvent.BUTTON1)
                return;
            
            p1 = e.getPoint();
            
            if (R!=null) {
                    drawHighlightRectangle(R);
                    repaint();
            }
            
            highlightRectangle = R = null;
            
        }
        
       

        public void mouseDragged(MouseEvent e)
        {
            if (p1==null)
                return;
           
            if (R!=null)
                drawHighlightRectangle(R);
            
            p2 = e.getPoint();
            
            int x1=Math.min(p1.x, p2.x);
            int x2=Math.max(p1.x, p2.x);
            int y1=Math.min(p1.y, p2.y);
            int y2=Math.max(p1.y, p2.y);
            R = new Rectangle(x1,y1, x2-x1, y2-y1);
            drawHighlightRectangle(R);
            repaint();
            //System.out.println("p2:"+p2);
        }
        
        public void mouseReleased(MouseEvent e)
        {
             highlightRectangle = R;
             
             if (R!=null)
             applyBoxZoom(new Point((int) R.getCenterX(), (int) R.getCenterY()));
        }
        
        private void applyBoxZoom(Point p)
        {
            Rectangle rect = highlightRectangle;
            if (rect==null)
                return;
            double xmin,xmax, ymin,ymax;
            double xscl=getXRange().getScale();
            double yscl=getYRange().getScale();
            
            if (rect.contains(p))
            {
                //do box zoom in

                xmin = screenXtoCartesian(rect.x);
                ymax = screenYtoCartesian(rect.y);
                xmax = screenXtoCartesian(rect.x+rect.width);
                ymin = screenYtoCartesian(rect.y+rect.height);

            }
            else
            {
                //do box zoom out
                
            	 xmin = screenXtoCartesian(-rect.x * getWidth() / rect.width);
        		 ymax = screenYtoCartesian(-rect.y * getHeight() / rect.height);
        		 xmax = screenXtoCartesian((getWidth() - rect.x) * getWidth() / rect.width);
        		 ymin = screenYtoCartesian((getHeight() - rect.y) * getHeight() / rect.height);

            }
            
            setXYRange(new Range(xmin, xmax, xscl), new Range(ymin, ymax, yscl));
            highlightRectangle = R= null;
            
            redrawAll();
            
        }
        
        private void applyTranslationZoom(Point p)
        {
            double x = screenXtoCartesian(p.x);
            double y = screenYtoCartesian(p.y);
            
            double factor = 1;
            
            double min,max;
            
            min = x-factor*getXRange().getWidth()/2;
            max = x+factor*getXRange().getWidth()/2;
            Range xRange = new Range(min,max, getXRange().getScale());
            
            min = y-factor*getYRange().getWidth()/2;
            max = y+factor*getYRange().getWidth()/2;
            Range yRange = new Range(min,max, getYRange().getScale());
            
            setXYRange(xRange, yRange);
        }
        
        public void mouseClicked(MouseEvent e)
        {
            if (! properties.getBooleanProperty(GraphProperties.INTERACTIVE_ZOOM))
                return;
            
            
            if (e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON3)
            {
                if (R==null)
                    applyTranslationZoom(e.getPoint());
                else
                    applyBoxZoom(e.getPoint());
            }
             
        }
    };
    
  
}

