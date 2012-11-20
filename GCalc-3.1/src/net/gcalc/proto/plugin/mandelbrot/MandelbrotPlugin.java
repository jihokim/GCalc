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


package net.gcalc.proto.plugin.mandelbrot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.gui.gradient.CompoundGradient;
import net.gcalc.calc.gui.gradient.GradientCanvas;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.plane.CartesianGraphPlugin;
import net.gcalc.plugin.plane.graph.CartesianGraph;
import net.gcalc.plugin.plane.gui.CoordinatePanel;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Range;
import net.gcalc.plugin.properties.View;


/**
 *
 */
public class MandelbrotPlugin extends CartesianGraphPlugin implements Observer
{
	private MandelbrotGraph mgraph;
	private JProgressBar progressbar;
		
    
    public MandelbrotPlugin() {
        super();
        graph = mgraph = new MandelbrotGraph(this);
      
    }
    
    public void setVisible(boolean b)
    {
    	super.setVisible(b);
    	 if (b)
    	 	graph.redrawAll();	
    }
    
    protected void initPropertiesPanel()
    {   
    	propertiesPanel.addTab("Screen", makeGraphDimensionPanel());
    	propertiesPanel.addTab("Gradient", new GradientChooser(mgraph.getGradient()));
	}
    
    public void init()
    {
    	initPropertiesPanel();
        initMenuBar();
        
        CoordinatePanel cp = new CoordinatePanel(2);
        graph.setCoordinatePanel(cp);

        Box graphBox = Box.createVerticalBox();
        graphBox.add(graph);
        graphBox.add(cp);
        
        getContentPane().setLayout(new BorderLayout(5,5));
        getContentPane().add("Center", graphBox);
        
        getContentPane().add("South", progressbar = new JProgressBar(0,graph.getHeight()*graph.getWidth()));
        
        graph.getProperties().addObserver(this);
        
        this.setTitle(getPluginName());
        this.pack();

        this.setInitialized(true);
    }
    
    public void update(Observable src, Object key)
    {
    	GraphProperties properties = (GraphProperties) src;
    	
    	if (key.equals("progress")) {    		
        	progressbar.setValue(((Integer) properties.get("progress")).intValue());
    	}
    }
   
    public String getDescription()
    {
        return "<p>Show the really cool Mandelbrot Set fractal.</p>";
    }
   
    public String getPluginName()
    {
        return "Mandelbrot Set Plugin";
    }
    
    public void shutdown()
    {
    	mgraph.killThread();
    	super.shutdown();
    }
    
  
    class GradientChooser extends JPanel implements ActionListener
	{
		private GradientCanvas gradientCanvas;
		private JButton apply, ok;
		 
	    public GradientChooser(CompoundGradient cg) {
	    	super();
	    	
	    	Box vBox = Box.createVerticalBox();
	    	
	    	Box buttonBox = Box.createHorizontalBox();
	    	buttonBox.add(Box.createHorizontalGlue());
	    	buttonBox.add(apply=new JButton("Apply"));
	    //	buttonBox.add(ok=new JButton("Ok"));
	    //	buttonBox.add(cancel=new JButton("Cancel"));
	    	
	    	apply.addActionListener(this);
	    //	ok.addActionListener(this);
	    //    cancel.addActionListener(this);
	        
	        
	        gradientCanvas = new GradientCanvas(mgraph.getGradient());
	    	vBox.add(SwingGUI.wrapTitledBorder(gradientCanvas, "Gradient"));
	    	vBox.add(buttonBox);
	    	vBox.add(Box.createVerticalGlue());
	    	
	    	add(vBox);
		
	    }
	    
	    
	    
	    public void actionPerformed(ActionEvent e)
	    {
	    	if (e.getSource()==apply) {
	    		mgraph.setGradient(gradientCanvas.getGradient());
	    		mgraph.redrawAll();
	    		return;
	    	}
	    	
	    	if (e.getSource()==ok) {
	    		mgraph.setGradient(gradientCanvas.getGradient());
	    		mgraph.redrawAll();
	    	}
	    	
	    	setVisible(false);
	    }

	}
}



class Mandelbrot
{
    private int maxiter=100;
    
    public Mandelbrot(int maxiter)
    {
        this.maxiter = maxiter;
    }
    
    public int escape(double a, double b)
    {
    	//see if the point is in the main cardiod
        double p=(a-.25)*(a-.25)+b*b;
        double t=.5*(1-Math.cos(Math.atan2(b,a-.25)));
        if (p<t*t)
            return maxiter;
        
        int i=0;
        
        double x=0,y=0; 
        double nx,ny;
        
        for (i=0; i<maxiter; i++) {
            if (x*x+y*y>4)
                return i;
            
            nx=x*x-y*y+a;
            ny=2*x*y+b;
            
            x = nx;
            y = ny;
        }
        
        return i;
    }
    
    public int maxIteration()
    {
        return maxiter;
    }
}



class MandelbrotGraph extends CartesianGraph
{
    private MandelbrotDrawingThread drawingThread;
    private Mandelbrot mandel;
    private CompoundGradient gradient;
    
    public MandelbrotGraph(AbstractCartesianGraphPlugin plugin)
    {
        super(plugin);
        this.removeMouseListener(this.normalMouseAdapter);
        addMouseListener(new MandelbrotMouseAdapter());
        
        gradient = new CompoundGradient(Color.blue, Color.red);
        mandel = new Mandelbrot(20000);  
    }
    
    public void killThread()
    {
    	drawingThread.die();
    }
    
    protected void setDefaultView()
    {
        properties.initDefault(GraphProperties.VIEW, new View(new Range(-2, 2,
                1), new Range(-2, 2, 1)));
        properties.initDefault(GraphProperties.TRACE, false);
        properties.revertToDefault(GraphProperties.VIEW);
    }
    
    protected void setDefaultScreenDimension()
    {
        properties.put(GraphProperties.SCREEN_DIMENSION,
                new Dimension(400, 400));
    }
    
    public void redrawAll(boolean b)
    {
        if (drawingThread!=null)
            drawingThread.die();
        
        drawingThread = new MandelbrotDrawingThread();           
        drawingThread.start();
    }
    
    public CompoundGradient getGradient()
    {
    	return gradient;
    }
    
    public void setGradient(CompoundGradient g)
    {
    	gradient =  g;
    }

    
    class MandelbrotMouseAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            double factor = 1/1.5f;
            
            if (e.getButton()==MouseEvent.BUTTON3)
                factor = 1/factor;
            
            Range xRange = getXRange();
            Range yRange = getYRange();
            
            double w = (double) getWidth();
            double h = (double) getHeight();
            
            double aspect = h/w;
            
            
            double x = screenXtoCartesian(e.getX());
            double y = screenYtoCartesian(e.getY());
           
            
            if (drawingThread!=null)
                drawingThread.die();
            
            double size = xRange.getWidth(); 
            
            xRange = new Range(x-factor*e.getX()/w*size, x+factor*(1-e.getX()/w)*size);
            yRange = new Range(y-factor*(1-e.getY()/h)*size*aspect, y+factor*(e.getY()/h)*size*aspect);
            
            setXYRange(xRange, yRange);
            redrawAll();
        }
    }
    
    class MandelbrotDrawingThread extends Thread {
        boolean die = false;
        
        public void die()
        {
            die = true;
        }
        
        private void drawSquare(int i, int j, int size)
        {
            
            double x = screenXtoCartesian(i);
            double y = screenYtoCartesian(j);
            int c = mandel.escape(x,y);
            if (c==mandel.maxIteration())
                gr.setColor(Color.black);
            else {
                float h = ((2*c)%1000)/1000f;
                gr.setColor(gradient.getColor(h));
            }
            gr.fillRect(i,j,size,size);
        }
        
        public void run()
        {
        	if (! MandelbrotGraph.this.isShowing())
        		return;
        		
            int N = Math.max(getWidth(), getHeight());
            int n=1;
            while (n<N)
                n*=2;
            
            drawSquare(0,0, 16);
            
            int pow2=1;
            
            
            //This is an ugly set of loops to iterate through a rectangle at increasing 
            //degree of resolution, visiting each pixel once.          
            int size=n;
            int x=0, y=0;
            int x2=0, y2=0;
            int count=0;
            int max = getHeight()*getWidth();
           
            for (int g=0; g<(int) (Math.log(n)/Math.log(2)) ;g++) {
            	//count = max*g/gmax;
                for (int i=0; i<pow2 && !die ; i++) {
                    x = i*size;
                    
                    if (x<getWidth()) {
                        x2 = x+size/2;
                        for (int j=0; j<pow2 && !die; j++) {
                            y = j*size;
                            
                            if (y<getHeight()) {
                                y2 = y+size/2;
                                
                                if (y2<getHeight()) {
                                    drawSquare(x, y2, size);
                                    if (x2<getWidth())
                                        drawSquare(x2, y2, size);
                                }
                                if (x2<getHeight())
                                    drawSquare(x2, y, size);
                                
                                count+=3;
                            }
                            else {
                                j = pow2;
                            }
                        }  
                    }
                    getProperties().put("progress", new Integer(count));
                }
                
                if (die) {
              //      System.out.println(this);
                    return;
                }
                
                if (size<16) {
                    Thread.yield();
                    repaint();
                }

                pow2 *=2;
                size = n/pow2;
            }
            
            getProperties().put("progress", new Integer(max));
            Toolkit.getDefaultToolkit().beep();
        }
    }
}


