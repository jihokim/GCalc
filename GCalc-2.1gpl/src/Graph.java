/*  
GCalc 2.0 - Graphing calculator applet
Copyright (C) 2001 Jiho Kim

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

Email: gcalc@humblestar.net
Web: http://www.humblestar.net/GCalc

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/

import java.awt.*;
import java.awt.event.*;


/**
 *  Encapsulation of a graphing screen
 */
public class Graph extends Canvas implements MouseListener, MouseMotionListener
{
    private GCalc host;
    private Image image;
    private Graphics page;

    boolean axis, dots, scores, grid;
    boolean trace;

    double xmax, xmin, xscl, xscl2, xavg, xfact;
    double ymax, ymin, yscl, yscl2, yavg, yfact;
    double xzoom, yzoom;


    private PostfixList pfl;

    private double mx, my;
    private int dragstartX, dragstartY;
    private int dragdX, dragdY;

    private int tracestate=0;

    private LabelPair lp;

    private long mouseClickedTime=0;
    private int direction=1;

    private boolean tracedrawn=false;
    private int xtrace=-1;
    private int ytrace=-1;

    private Dimension size=null;

    /**
     * Basic Graph Constructor
     * 
     * @param gc GCalc object.  The Graph sometimes needs to make things happen to the GUI on the graphing calculator
     * @param xSize width of the graph
     * @param ySize height of the graph
     */
    public Graph (GCalc gc, int xSize, int ySize)
    {
	this(gc, xSize, ySize, -10, 10, 1, -10, 10, 1);
    }

    /**
     * Graph Constructor
     * 
     * @param gc GCalc object.  The Graph sometimes needs to make things happen to the GUI on the graphing calculator
     * @param xSize width of the graph
     * @param ySize height of the graph
     * @param xmin1 minimum x value
     * @param xmax1 maximum x value
     * @param xscl1 scale along the x axis
     * @param ymin1 minimum y value
     * @param ymax1 maximum y value
     * @param yscl1 scale along the y axis
     */
    public Graph (GCalc gc, int xSize, int ySize,
	   double xmin1, double xmax1, double xscl1,
	   double ymin1, double ymax1, double yscl1) {
	host = gc;


	size = new Dimension(xSize,ySize);
	setSize(size);

	changeRange(xmin1, xmax1, xscl1, ymin1, ymax1, yscl1);

	initDefault();
	clearList();

	addMouseListener(this);
	addMouseMotionListener(this);

    }

    /**
     * Initializes object to defaults. Used in the constructor.
     */
    private void initDefault()
    {
	axis = true;
	dots = false;
	scores = true;
	grid = false;
	trace = false;

	xzoom=2;
	yzoom=2;

	my=0;
	mx=0;

	dragstartX=-1;
	dragstartY=-1;
	dragdX=0;
	dragdY=0;
    }

    /**
     * Returns the graph image.  Used to pass the image to the Image Canvas
     */
    Image getImage()
    {
	return image;
    }

    /**
     * Registers the LabelPair, which will contain the position of the
     * mouse relative to the origin.
     */
    public void setLabelPair(LabelPair lp)
    {
	this.lp = lp;
    }

    /**
     * Toggles the trace option on or off
     */
    public void trace()
    {
	trace = ! trace;
    }

    /**
     * Handles MOUSE_CLICKED events.
     */
    public void mouseClicked(MouseEvent event)
    {
	if (trace && pfl.List.size()>0) {
	    direction = (event.getModifiers()==event.BUTTON3_MASK)?-1:1;
	    tracestate = (tracestate+direction+pfl.List.size())%pfl.List.size();
	}
	else {
	    switch (event.getClickCount()) {
	    case 1:
		break;
	    default:
		double x=event.getX()/xfact+xmin;
		double y=event.getY()/yfact+ymax;
		changeRange(x-(xmax-xmin)/2, x+(xmax-xmin)/2, xscl,
			    y-(ymax-ymin)/2, y+(ymax-ymin)/2, yscl);

		host.updateRangeTF();
		clearGraph();
		drawPostfix();
		break;
	    }
	}
	mouseMoved(event);
    }

    /**
     * Handles MOUSE_MOVED events.  Also other handler call this to
     * make sure the screen gets updated.
     */
    public void mouseMoved (MouseEvent event)
    {
	if (page!=null) {
	    page.setPaintMode();
	    referencePt((event.getX()/xfact+xmin),(event.getY()/yfact+ymax));

	    if (trace && pfl.listSize>0) {
		Postfix pf = null;
		boolean stop = false;
		int temp = tracestate;
		do {
		    try {
			pf = pfl.get(temp);
		    }
		    catch (ArrayIndexOutOfBoundsException e) {

		    }

		    stop = (pf!=null &&  pf.isValid());

		    //    System.out.println(temp+" "+pfl.List.size());

		    if (!stop) {
			temp = (temp+direction+pfl.List.size())%pfl.List.size();
			if (temp==tracestate) {
			    stop = true;
			    pf = null;
			}
		    }
		} while (!stop);
		tracestate = temp;

		int x = event.getX();
		double xx = x/xfact+xmin;
		double yy = (pf==null)?Double.NaN:pf.evaluate(xx);

		//		System.out.println(pf);

		int y=(Double.isNaN(yy) || pf==null)?-10:(int) ((yy-ymax)*yfact+.5);

		if (pf!=null)
		    host.setStatusString("Tracing "+pf.infix());
		host.repaint();
		drawTrace(x, y);
		referencePt(xx, yy);
	    }

	    drawCoordinate();
	}
	repaint();
    }

   
    /**
     * Handles MOUSE_DRAGGED events.  Used during dragging a zoom box.
     */
    public void mouseDragged (MouseEvent event)
    {
	if (trace)
	    return;

	if (dragstartX==-1 && dragstartY==-1) {
	    dragstartX=event.getX();
	    dragstartY=event.getY();
	    dragdX=0;
	    dragdY=0;
	}
	else {
	    page.setXORMode(GCalc.graphBgColor);
	    page.setColor(GCalc.zoomBoxColor);
	    page.drawRect(dragstartX-dragdX, dragstartY-dragdY, 2*dragdX, 2*dragdY);
	    dragdX=Math.abs(event.getX()-dragstartX);
	    dragdY=Math.abs(event.getY()-dragstartY);
	    page.drawRect(dragstartX-dragdX, dragstartY-dragdY, 2*dragdX, 2*dragdY);
	    mouseMoved(event);
	}
    }

    /**
     * Handles MOUSE_RELEASED events.
     */
    public void mouseReleased (MouseEvent event)
    {
	if (trace) {
	    if (System.currentTimeMillis()-mouseClickedTime>2000) {
		new ImageDialog(host, image, getWidth(), getHeight());
		mouseExited(event);
	    }

	    return;
	}
	else {
	    page.setPaintMode();

	    if (dragstartX!=-1 && dragstartY!=-1)
		host.boxZoomSetEnabled(true);
	    else
		host.boxZoomSetEnabled(false);
	}
    }

    /**
     * Handles MOUSE_PRESSED events.
     */
    public void mousePressed (MouseEvent event)
    {
	if (trace) {
	    mouseClickedTime = System.currentTimeMillis();
	    return;
	}

	if (dragstartX!=-1 && dragstartY!=-1) {
	    page.setXORMode(GCalc.graphBgColor);
	    page.setColor(GCalc.zoomBoxColor);
	    page.drawRect(dragstartX-dragdX, dragstartY-dragdY, 2*dragdX, 2*dragdY);
	    //			dragstartX=-1;
	    //			dragstartY=-1;
	    dragstartX=event.getX();
	    dragstartY=event.getY();
	    dragdX=0;
	    dragdY=0;
	}
    }

    /**
     * Handles MOUSE_ENTERED events.
     */
     public void mouseEntered (MouseEvent event)
    {
	this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	if (trace) {
	    xtrace = -1;
	    ytrace = -1;
	}
	mouseMoved(event);
    }

    /**
     * Handles MOUSE_EXITED events.
     */
    public void mouseExited (MouseEvent event)
    {
	this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	if (trace)
	    eraseTrace();

	mx=Double.NaN;
	my=Double.NaN;
	drawCoordinate();
    }

    /**
     * Sets the points for the LabelPair to contain
     *
     * @param xx the x value
     * @param yy the y value
     */
    private void referencePt(double xx, double yy)
    {
 	mx=xx;
 	my=yy;
    }

    /**
     * Draws the trace crosshairs at (x,y) on the screen.  
     *
     * @param x the x value relative to the screen
     * @param y the y value relative to the screen
     */
    private void drawTrace(int x, int y)
    {
	page.setColor(GCalc.traceColor);

	if (tracedrawn)
	    eraseTrace();

	if (x>-10 && x<getWidth()+10 && y>-10 && y<getHeight()+10) {
	    page.setXORMode(GCalc.graphBgColor);
	    if (y!=-1)
		page.drawLine(0,y,getWidth()-1, y);
	    
	    if (x!=-1 && y!=-1)
		page.drawOval(x-5,y-5, 11,11);
	}

	if (x>-10 && x<getWidth()+10) {
	    page.setXORMode(GCalc.graphBgColor);
	    page.drawLine(x,0,x, getHeight()-1);
	}

	tracedrawn = true;
	
	repaint();
	
	xtrace = x;
	ytrace = y;

    }

    /**
     * Erases the trace crosshairs.
     */
    private void eraseTrace()
    {
	tracedrawn = false;
	drawTrace(xtrace, ytrace);

	repaint();
    }

    /**
     * Draws Coordinates on the screen. Since this is no longer done,
     * this method is left to make code not break.
     */
    private void drawCoordinate()
    {
	if (lp!=null)
	    lp.setText(nonNaNString(mx), nonNaNString(my));
	repaint();
    }

    /**
     * Method returns the strings to feed to the LabelPanel.
     * Appropriate rounding and blanking of NaN is done.
     *
     * @param val The value to return as a string.
     */
    private static String nonNaNString(double val)
    {
	if (Double.isNaN(val)) 
	    return "";

	/*
	if ((""+val).indexOf('E')<0)
	    val = Math.round(val*1.0e13)/1.0e13;
	*/

	return ""+val;
    }

    /**
     * Change the window dimensions
     *
     * @param xmin1 minimum x value
     * @param xmax1 maximum x value
     * @param xscl1 scale along the x axis
     * @param ymin1 minimum y value
     * @param ymax1 maximum y value
     * @param yscl1 scale along the y axis
     */
    public void changeRange (double xmin1, double xmax1, double xscl1,
			     double ymin1, double ymax1, double yscl1) {
	if (xmax1>xmin1 && ymax1>ymin1 &&
	    !isNotReal(xmin1) && !isNotReal(xmax1) && !isNotReal(xscl1) &&
	    !isNotReal(ymin1) && !isNotReal(ymax1) && !isNotReal(yscl1)	)
	    {
		xscl = Math.abs(xscl1);
		yscl = Math.abs(yscl1);
		xmax = xmax1;
		xmin = xmin1;
		ymax = ymax1;
		ymin = ymin1;
	    }

	xavg = (xmax+xmin)/2;
	yavg = (ymax+ymin)/2;

	xfact=(getWidth()-1)/(xmax-xmin);
	yfact=(getHeight()-1)/(ymin-ymax);

	if (image!=null)
	    clearGraph();
    }

    /**
     * Changes the ranges to the that of the box dragged out.
     */
    public void fitToZoomBox()
    {
	double xmin2=(dragstartX-dragdX)/xfact+xmin;
	double xmax2=(dragstartX+dragdX)/xfact+xmin;
	double ymax2=(dragstartY-dragdY)/yfact+ymax;
	double ymin2=(dragstartY+dragdY)/yfact+ymax;

	changeRange(xmin2,xmax2,xscl,
		    ymin2,ymax2,yscl);
    }

    public void paint(Graphics g)
    {
	update(g);
    }

    public void update(Graphics g)
    {
	if (image==null) {
	    image = createImage(getWidth()+1,getHeight()+1);
	    page = image.getGraphics();
	    clearGraph();
	}

	g.drawImage(image, 0,0, this);
    }

    /**
     * Clears the Postfix list.
     */
    public void clearList()
    {
	pfl=new PostfixList();
    }

    /**
     * Resets the graph to a clean state.  Does everything except draw
     * the functions.
     */
    public void clearGraph()
    {
	System.out.println("graph cleared.");

	if (image==null) return;

	page.setPaintMode();
	page.setColor(GCalc.graphBgColor);
	page.fillRect(0,0,getWidth()+1,getHeight()+1);

	if (grid) drawGrid();
	if (axis) drawAxes();
	if (scores) drawScores();

	pfl.deflagAll();
	referencePt(Double.NaN,Double.NaN);
	drawCoordinate();

	dragstartX=-1;
	dragstartY=-1;
	dragdX=0;
	dragdY=0;

	tracedrawn=false;

	host.boxZoomSetEnabled(false);
    }


    /**
     * Draw the Axes on the screen.
     */
    private void drawAxes()
    {
	int sx = (int) (-xmin*xfact+.5);	//(sx,sy) is where the origin is
	int sy = (int) (-ymax*yfact+.5);


	if (YonScreen(sy)) {
	    page.setColor(GCalc.h_axisColor);
	    page.drawLine(0,sy,getWidth()-1,sy);
	}

	if (XonScreen(sx))
	    page.setColor(GCalc.v_axisColor);
	    page.drawLine(sx,0,sx,getHeight()-1);
    }

    
    /**
     * Saves the scale values.  Used when drawing the grid and scales,
     * when the scale values are changed to reasonable numbers.
     */
    private void saveScl()
    {
	xscl2 = xscl;
	yscl2 = yscl;
	if (xscl!=0 && 1/xscl>Math.abs(3*xfact))
	    xscl = Math.abs(1/xfact);
	if (yscl!=0 && 1/yscl>Math.abs(3*yfact))
	    yscl = Math.abs(1/yfact);
    }
    
    /**
     * Restore the scale values.  Used when done drawing the grid and
     * scales
     */
    private void restoreScl()
    {
	xscl = xscl2;
	yscl = yscl2;
    }

    /**
     * Draws the grid onto the screen.
     */
    private void drawGrid()
    {
	int sx = (int) (-xmin*xfact+.5);	//(sx,sy) is where the origin is
	int sy = (int) (-ymax*yfact+.5);

	saveScl();

	page.setColor(GCalc.gridColor);
	double s;
	if (xscl!=0) {
	    s =  xscl*((int) (xmin/xscl+.5));
	    if (((xmax-s)/xscl)<=getWidth()) {
		for (double i = s; i<=xmax; i+=xscl) {
		    int sx1 = (int) ((i-xmin)*xfact+.5);
		    int sx2 = (int) ((-i-xmin)*xfact+.5);
		    page.drawLine(sx1,0,sx1,getHeight()-1);
		    page.drawLine(sx2,0,sx2,getHeight()-1);
		}
	    }
	}
	if (yscl!=0) {
	    s =  yscl*((int) (ymin/xscl+.5));
	    if (((ymax-s)/yscl)<=getHeight()) {
		for (double i = s; i<=ymax; i+=yscl) {
		    int sy1 = (int) ((i-ymax)*yfact+.5);
		    int sy2 = (int) ((-i-ymax)*yfact+.5);
		    page.drawLine(0,sy1,getWidth()-1,sy1);
		    page.drawLine(0,sy2,getWidth()-1,sy2);
		}
	    }
	}

	restoreScl();

    }

    /**
     * Draws the scale marks on the axes.
     */
    private void drawScores()
    {
	int sx = (int) (-xmin*xfact+.5);	//(sx,sy) is where the origin is
	int sy = (int) (-ymax*yfact+.5);

	saveScl();

	page.setColor(GCalc.scaleColor);
	double s;
	if (YonScreen(sy) && xscl!=0) {

	    s =  xscl*((int) (xmin/xscl+.5));
	    if (((xmax-s)/xscl)<=getWidth()) {
		for (double i = s; i<=xmax; i+=xscl) {

		    int sx1 = (int) ((i-xmin)*xfact+.5);
		    int sx2 = (int) ((-i-xmin)*xfact+.5);
		    page.drawLine(sx1,sy-1,sx1,sy+1);
		    page.drawLine(sx2,sy-1,sx2,sy+1);
		}
	    }
	}
	if (XonScreen(sx) && yscl!=0) {

	    s =  yscl*((int) (ymin/xscl+.5));
	    if (((ymax-s)/yscl)<=getHeight()) {
		for (double i = s; i<=ymax; i+=yscl) {
		    int sy1 = (int) ((i-ymax)*yfact+.5);
		    int sy2 = (int) ((-i-ymax)*yfact+.5);
		    page.drawLine(sx-1,sy1,sx+1,sy1);
		    page.drawLine(sx-1,sy2,sx+1,sy2);
		}
	    }
	}

	restoreScl();
    }

    /**
     * Test to determine the value is out of bounds vertically on the
     * plane.
     *
     * @param y value
     * @return <code>true</true> if in , else <code>false</code>.
     */
    private boolean YoutOfBounds (double y)
    {
	int buffer=0;

	if (Double.isInfinite(y)) return true;
	if (Double.isNaN(y)) return true;
	if (y<ymin-buffer) return true;
	if (y>ymax+buffer) return true;

	return false;
    }

    /**
     * Test to determine the value is out of bounds horizontally on the
     * plane.
     *
     * @param x value
     * @return <code>true</true> if in , else <code>false</code>.
     */
    private boolean XoutOfBounds (double x)
    {
	if (Double.isInfinite(x)) return true;
	if (Double.isNaN(x)) return true;
	if (x<xmin) return true;
	if (x>xmax) return true;

	return false;
    }

    /**
     * Test to determine the value is out of bounds horizontally on the
     * screen.
     *
     * @param y value
     * @return <code>true</true> if in , else <code>false</code>.
     */
    private boolean YonScreen (double y)
    {
	return (y>=0 && y<=getHeight()-1);
    }

    /**
     * Test to determine the value is out of bounds horizontally on the
     * screen
     *
     * @param x value
     * @return <code>true</true> if in , else <code>false</code>.
     */
    private boolean XonScreen (double x)
    {
	return (x>=0 && x<=getWidth()-1);
    }

    /**
     * Test to determine the value is a NaN or Infinite.
     *
     * @param x value
     * @return <code>true</true> if NaN or Infinite, else <code>false</code>
     */
    private static boolean isNotReal(double d)
    {
	return Double.isNaN(d) || Double.isInfinite(d);
    }

    /**
     * Adds a single postfix expression (and a corresponding color) to
     * the Postfix list.
     *
     * @param postfix the postfix object
     * @param c the color
     */
    public void drawPostfix(Postfix postfix, Color c)
    {
	if (postfix==null) return;
	if (postfix.isValid())
	    pfl.add(postfix, c);

	drawPostfix();
	repaint();
    }

    /**
     * Draws all the functions marked as not drawn already.
     */
    public void drawPostfix()
    {
	for (int i=0; i<pfl.List.size(); i++) {
	    PostfixListNode ptr = pfl.getNodeAt(i);

	    if (ptr.drawn==false) {
		System.out.println("drawing "+ptr.pf);
		drawGraph(ptr.pf, ptr.color);
		ptr.drawn = true;
	    }
	}
	drawCoordinate();
    }

    /**
     * Draws a specific function onto the screen with a given color.
     *
     * @param postfix the postfix object
     * @param c the color
     */
    private void drawGraph(Postfix postfix, Color c)
    {
	if (postfix==null) return;

	int sx=0, sy=0;			//Screen x, y
	int psx=-50, psy=50;	//Previous Screen x,y
	double yy2=Double.NaN;	//last real functional value;
	double yy=0;			//real functional value

	page.setColor(c);
	page.setPaintMode();

	for (double i=0; i<=getWidth(); i+=1) {
	    yy = postfix.evaluate(i/xfact+xmin);

	    sx=(int) i;
	    sy=(int) ((yy-ymax)*yfact+.5);

	    if (! (YoutOfBounds(yy) && YoutOfBounds(yy2)) ) {
		if (isNotReal(yy)) {
		    //the current point is not plottable.
		}
		else if (dots || isNotReal(yy2)) {
		    /* if the dots flag is on or the last y value was
		     * not plotted plot a point at (sx,sy)
		     */

		    page.drawLine(sx,sy,sx,sy);
		}
		else {
		    /* Seems okay to plot a line between the last
		     * plotted point and this one, if it won't be a bad point
		     */

		    if (Math.abs(sy)<3*getHeight() && sy!=Integer.MIN_VALUE)
			if (Math.abs(psy)<3*getHeight() && psy!=Integer.MIN_VALUE)
			    page.drawLine(sx,sy,psx,psy);
			else
			    page.drawLine(sx,sy,sx,sy);
		}
	    }

	    yy2=yy;
	    psx=sx;
	    psy=sy;

	}//for i
    }

    /**
     * Returns the internal postfix list object.
     *
     * @return postfix list
     */
    public PostfixList getList()
    {
	return pfl;
    }

   

    /**
     * Returns the pixel width of the screen.  It is saves us from
     * allocating a Dimension object every time we want to know the
     * size of the screen.
     *
     * @return the width.
     */
    public int getWidth()
    {
	return size.width;
    }

    /**
     * Returns the pixel height of the screen.  It is saves us from
     * allocating a Dimension object every time we want to know the
     * size of the screen.
     *
     * @return the height.
     */
    public int getHeight()
    {
	return size.height;
    }
}
