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
 *  The GCalc user interface and global constraints.
 */
public class GCalc extends Frame implements ActionListener, WindowListener, ItemListener
{
    final static int RADIAN = 1;
    final static int DEGREE = 2;
    static int angle = RADIAN;

    public static Color bgColor = new Color(240,240,200);
    public static Color statusbarColor = bgColor.darker();

    public static Color scaleColor = Color.blue;
    public static Color gridColor = new Color(230,230,230);
    public static Color traceColor = Color.orange;
    public static Color h_axisColor = Color.red;
    public static Color v_axisColor = Color.red;
    public static Color graphBgColor = Color.white;
    public static Color zoomBoxColor = Color.orange;
 
    protected TextField textfield1;
    protected Graph graphCanvas;

    protected Button axisButton, scoresButton, gridButton, dotsButton, traceButton;

    protected Button standardZoomButton, trigZoomButton, squareZoomButton, graphFitZoomButton, boxZoomButton;
    protected Button zoomInButton, zoomOutButton;
    protected TextField xZoom, yZoom;

    protected TextField xminTF, xmaxTF, xsclTF, yminTF, ymaxTF, ysclTF;
    protected Label xLabel, yLabel;

    protected Button resetButton, previousButton, nextButton;

    protected StringList sl;

    protected Panel graphPanel;

    protected Button justGraphButton, aboutButton, listButton;

    protected ColorLabel colorLabel;

    protected Label status;

    protected Checkbox radian, degree;
    protected CheckboxGroup angleCBG;
	
    protected Button integrationButton, rootLocatorButton;


    /**
     * Starts GCalc from the command line
     *
     * @param args command line arguments, but are not used!
     */
    public static void main(String[] args)
    {
	GCalc G = new GCalc();
	System.out.println(G);
    }

    /**
     * GCalc Constructor.  Sets up the GUI components, layout, event
     * listeners, etc...
     */
     public GCalc()
    {
	super("GCalc");
	init();
	addWindowListener(this);
	pack();
	setResizable(false);

	setVisible(true);
    }


    /**
     * Sets the status message with a default color.
     * 
     * @param s the message
     */
    public void setStatusString(String s)
    {
	setStatusString(s, Color.black);
    }

    /**
     * Sets the status message with a particular color.  Useful for
     * warning messages.
     * 
     * @param s message
     * @param c color
     */
    public void setStatusString(String s, Color c)
    {
	status.setForeground(c);
	status.setText(s);
    }

    /**
     * Initializes an array of colors, to pass to the ColorChooser
     *
     * @return array of colors
     */
    protected static  Color[] initColorArray()
    {
	int n=20;

	Color[] colorArray = new Color[n];

	int idx = 1;

	for (int j=0; j<colorArray.length-1; j++) {
	    float h = j/((float) n-1);
	    float s=1f;

	    colorArray[j+1]=Color.getHSBColor(h,s,1f).darker();

	    idx++;
	}

	colorArray[0]=Color.black;

	return colorArray;
    }

    /**
     * Initializes the GUI elements for GCalc.  Only called by the
     * constructor.
     */
    protected void init()
    {
	Font normalFont = new Font("monospaced", Font.PLAIN, 12);

	setVisible(true);
	setBackground(GCalc.bgColor);
	setLayout(new BorderLayout(2,2));

	Panel statusbar = new Panel(new GridLayout(1,1));
	status = new Label();
	status.setBackground(GCalc.statusbarColor);
	status.setFont(new Font("Monospaced", Font.BOLD, 11));
	statusbar.add(status);
	add("South", statusbar);

	int width = 501;
	int height = (int) (width/((1+Math.sqrt(5))/2)+.5);

	//	System.out.println(width+" "+height);
	graphCanvas = new Graph(this,width, height);

	xLabel = new Label();
	yLabel = new Label();
	xLabel.setBackground(Color.lightGray);
	yLabel.setBackground(Color.lightGray);
	xLabel.setFont(normalFont);
	yLabel.setFont(normalFont);
	LabelPair lp = new LabelPair(xLabel, yLabel);
	graphCanvas.setLabelPair(lp);

	Panel lpPanel= new Panel(new GridLayout(1,2));
	lpPanel.add(xLabel);
	lpPanel.add(yLabel);

	graphPanel = new Panel(new BorderLayout(0,2));
	graphPanel.add("Center", graphCanvas);
	graphPanel.add("South",lpPanel);

	sl = new StringList();

	Panel graphProperties = new Panel(new GridLayout(2,4,0,0));

	axisButton = new Button("Axis ON");
	scoresButton = new Button ("Scale ON");
	gridButton = new Button("Grid OFF");
	dotsButton = new Button("Continuous");
	traceButton = new Button("Trace OFF");
	justGraphButton = new Button("Graph Window");
	aboutButton = new Button("About GCalc");
	listButton = new Button("Function List");
	integrationButton = new Button("Numeric Integration");
	rootLocatorButton = new Button("Root Locator");

	Font buttonFont = new Font("Sans-Serif", Font.PLAIN, 11);


	axisButton.setFont(buttonFont);
	scoresButton.setFont(buttonFont);
	gridButton.setFont(buttonFont);
	dotsButton.setFont(buttonFont);
	traceButton.setFont(buttonFont);
	justGraphButton.setFont(buttonFont);
	aboutButton.setFont(buttonFont);
	listButton.setFont(buttonFont);
	integrationButton.setFont(buttonFont);
	rootLocatorButton.setFont(buttonFont);

	axisButton.addActionListener(this);
	scoresButton.addActionListener(this);
	gridButton.addActionListener(this);
	dotsButton.addActionListener(this);
	traceButton.addActionListener(this);
	justGraphButton.addActionListener(this);
	aboutButton.addActionListener(this);
	listButton.addActionListener(this);
	integrationButton.addActionListener(this);
	rootLocatorButton.addActionListener(this);

	graphProperties.add(axisButton);
	graphProperties.add(scoresButton);
	graphProperties.add(gridButton);
	graphProperties.add(dotsButton);
	graphProperties.add(traceButton);
	graphProperties.add(justGraphButton);
	graphProperties.add(listButton);
	graphProperties.add(aboutButton);

	/*
	graphProperties.add(new Panel());

	graphProperties.add(integrationButton);
	graphProperties.add(rootLocatorButton);
	graphProperties.add(new Panel());
	*/

	//Center--Graph, input textfield, graph properties
	Panel west=new Panel();

	textfield1 = new TextField("");
	textfield1.setFont(new Font("monospaced", Font.BOLD, 14));
	textfield1.setBackground(Color.white);
	textfield1.addActionListener(this);

	Panel westCenter = new Panel(new BorderLayout(0,2));

	westCenter.add("North",textfield1);
	westCenter.add("Center",graphPanel);
	westCenter.add("South",graphProperties);
	west.add(westCenter);
	add("West",west);

	//East--zoom Buttons7
	standardZoomButton = new Button("Standard Zoom");
	trigZoomButton = new Button ("Trig Zoom");
	squareZoomButton = new Button("Square Zoom");
	graphFitZoomButton = new Button("Graph-Fit Zoom");
	boxZoomButton = new Button("Box Zoom");

	boxZoomButton.setEnabled(false);

	standardZoomButton.addActionListener(this);
	trigZoomButton.addActionListener(this);
	squareZoomButton.addActionListener(this);
	graphFitZoomButton.addActionListener(this);
	boxZoomButton.addActionListener(this);

	Panel zoomButtons = new Panel(new GridLayout(5,1));
	zoomButtons.add(standardZoomButton);
	zoomButtons.add(trigZoomButton);
	zoomButtons.add(squareZoomButton);
	zoomButtons.add(graphFitZoomButton);
	zoomButtons.add(boxZoomButton);

	zoomInButton = new Button("Zoom In");
	zoomOutButton = new Button("Zoom Out");
	xZoom = new TextField("2.0");
	yZoom = new TextField("2.0");
	xZoom.setFont(normalFont);
	yZoom.setFont(normalFont);
	xZoom.setBackground(Color.white);
	yZoom.setBackground(Color.white);

	zoomInButton.addActionListener(this);
	zoomOutButton.addActionListener(this);
	xZoom.addActionListener(this);
	yZoom.addActionListener(this);

	Panel manualZooms = new Panel(new GridLayout(5,1));
	Panel xZoomPanel = new Panel(new GridLayout(1,2));
	Panel yZoomPanel = new Panel(new GridLayout(1,2));
	xZoomPanel.add(new Label("XZoom"));
	xZoomPanel.add(xZoom);
	yZoomPanel.add(new Label("YZoom"));
	yZoomPanel.add(yZoom);
	manualZooms.add(zoomInButton);
	manualZooms.add(zoomOutButton);
	manualZooms.add(xZoomPanel);
	manualZooms.add(yZoomPanel);
	manualZooms.add(new Panel());

	//East--range fields
	xminTF = new TextField("",20);
	xmaxTF = new TextField("",20);
	xsclTF = new TextField("",20);
	yminTF = new TextField("",20);
	ymaxTF = new TextField("",20);
	ysclTF = new TextField("",20);

	xminTF.addActionListener(this);
	xmaxTF.addActionListener(this);
	xsclTF.addActionListener(this);
	yminTF.addActionListener(this);
	ymaxTF.addActionListener(this);
	ysclTF.addActionListener(this);

	xminTF.setFont(normalFont);
	xmaxTF.setFont(normalFont);
	xsclTF.setFont(normalFont);
	yminTF.setFont(normalFont);
	ymaxTF.setFont(normalFont);
	ysclTF.setFont(normalFont);

	xminTF.setBackground(Color.white);
	xmaxTF.setBackground(Color.white);
	xsclTF.setBackground(Color.white);
	yminTF.setBackground(Color.white);
	ymaxTF.setBackground(Color.white);
	ysclTF.setBackground(Color.white);

	Panel xminPanel = new Panel(new BorderLayout());
	Panel xmaxPanel = new Panel(new BorderLayout());
	Panel xsclPanel = new Panel(new BorderLayout());
	Panel yminPanel = new Panel(new BorderLayout());
	Panel ymaxPanel = new Panel(new BorderLayout());
	Panel ysclPanel = new Panel(new BorderLayout());

	xminPanel.add("Center",new Label ("XMin", Label.LEFT));
	xminPanel.add("East",xminTF);
	xmaxPanel.add("Center",new Label ("XMax", Label.LEFT));
	xmaxPanel.add("East",xmaxTF);
	xsclPanel.add("Center",new Label ("XScale", Label.LEFT));
	xsclPanel.add("East",xsclTF);
	yminPanel.add("Center",new Label ("YMin", Label.LEFT));
	yminPanel.add("East",yminTF);
	ymaxPanel.add("Center",new Label ("YMax", Label.LEFT));
	ymaxPanel.add("East",ymaxTF);
	ysclPanel.add("Center",new Label ("YScale", Label.LEFT));
	ysclPanel.add("East",ysclTF);

	Panel xyRange = new Panel(new GridLayout(6,1));
	xyRange.add(xminPanel);
	xyRange.add(xmaxPanel);
	xyRange.add(xsclPanel);
	xyRange.add(yminPanel);
	xyRange.add(ymaxPanel);
	xyRange.add(ysclPanel);

	//East--misc buttons
	resetButton = new Button ("RESET");
	previousButton = new Button ("Prev");
	nextButton = new Button("Next");

	previousButton.addActionListener(this);
	nextButton.addActionListener(this);
	resetButton.addActionListener(this);

	PaletteColorChooser.setColorArray(initColorArray());
	colorLabel = new ColorLabel(this,PaletteColorChooser.getColor());

	Panel misc = new Panel(new GridLayout(2,2));

	misc.add(previousButton);
	misc.add(nextButton);
	//	misc.add(colorChoice);
	misc.add(colorLabel);
	misc.add(resetButton);

	Panel zoomPanel = new Panel(new GridLayout(1,2));
	zoomPanel.add(zoomButtons);
	zoomPanel.add(manualZooms);


	// radian/degree checkboxes
	Panel anglePanel = new Panel(new GridLayout(1,2));
	angleCBG = new CheckboxGroup();
	anglePanel.add(radian = new Checkbox("Radian", angleCBG, true));
	anglePanel.add(degree = new Checkbox("Degree", angleCBG, false));
	radian.addItemListener(this);
	degree.addItemListener(this);


	//integration/root location
	
	/*
	intChoice = new Choice();
	intChoice.add("Left Riemann");
	intChoice.add("Right Riemann");
	intChoice.add("Midpoint Rule");
	intChoice.add("Trapezoidal Rule");
	intChoice.add("Simpson's Rule");
	intChoice.add("Adaptive Quadrature");
	intChoice.add("Monte Carlo Method");

	rootChoice = new Choice();
	rootChoice.add("Bisection");
	rootChoice.add("Newton's Method");
	*/



	//East
	Panel east=new Panel(new BorderLayout(2,2));
	Panel east1=new Panel(new BorderLayout(2,2));
	Panel east2=new Panel(new BorderLayout(2,2));
	Panel east3=new Panel(new BorderLayout(2,2));
	Panel east4=new Panel(new BorderLayout(2,2));
	Panel east5=new Panel(new BorderLayout(2,2));

	east1.add("North", new Canvas());
	east2.add("North", misc);
	east3.add("North", zoomPanel);
	east4.add("North", xyRange);
	east5.add("North", anglePanel);

	east1.add("South", east2);
	east2.add("South", east3);
	east3.add("South", east4);
	east4.add("South", east5);

	east.add("North",east1);
	east.add("East",new Panel());

	add("East",east);

	updateRangeTF();

	textfield1.requestFocus();

    }

    /**
     * Implements a single exit point for GCalc.  Simplifies code, if
     * there are more than one exit points.
     */
    protected void exit()
    {	
	dispose();

	try {
	    System.exit(0);
	}
	catch (Exception exception) {}
    }

    /**
     * Does nothing.
     */
    public void windowActivated(WindowEvent e) {}

    /**
     * Does nothing.
     */
    public void windowClosed(WindowEvent e) {}

    /**
     * Does nothing.
     */
    public void windowDeactivated(WindowEvent e) {}

    /**
     * Does nothing.
     */
    public void windowDeiconified(WindowEvent e) {}

    /**
     * Does nothing.
     */
    public void windowIconified(WindowEvent e) {}

    /**
     * Does nothing.
     */
    public void windowOpened(WindowEvent e) {}

    /**
     * Exits the GCalc program when the window is closing.
     */
    public void windowClosing(WindowEvent e)
    {
	exit();
    }

    /**
     * Handles events from the radian/degree checkboxes.
     */
    public void itemStateChanged(ItemEvent e) 
    {
	Object src = e.getSource();

	if (src==radian)
	    GCalc.angle = GCalc.RADIAN; 
	else if (src==degree)
	    GCalc.angle = GCalc.DEGREE; 

	graphCanvas.clearGraph();
	graphCanvas.drawPostfix();
    }

    /**
     * Handles events from various GUI components that produce
     * ActionEvents.
     */
    public void actionPerformed(ActionEvent event)
    {
	Object target = event.getSource();
	this.setStatusString("");

	if (target == textfield1) {
	    processUserInput(textfield1.getText());
	}
	else if (target == axisButton) {
	    graphCanvas.axis = ! graphCanvas.axis;
	    if (graphCanvas.axis)
		axisButton.setLabel("Axis ON");
	    else
		axisButton.setLabel("Axis OFF");
	    graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();
	}
	else if (target == traceButton) {
	    graphCanvas.trace();

	    if (graphCanvas.trace) {
		traceButton.setLabel("Trace ON");
		setStatusString("Hold down the mouse button on the screen for 2 seconds to get graph window with the trace crosshairs.");
	    }
	    else
		traceButton.setLabel("Trace OFF");

	    
	    //graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();

	}
	else if (target == scoresButton) {
	    graphCanvas.scores = ! graphCanvas.scores;
	    if (graphCanvas.scores)
		scoresButton.setLabel("Scores ON");
	    else
		scoresButton.setLabel("Scores OFF");
	    graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();
	}
	else if (target == gridButton) {
	    graphCanvas.grid = ! graphCanvas.grid;
	    if (graphCanvas.grid)
		gridButton.setLabel("Grid ON");
	    else
		gridButton.setLabel("Grid OFF");
	    graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();
	}
	else if (target == dotsButton) {
	    graphCanvas.dots = ! graphCanvas.dots;
	    if (graphCanvas.dots)
		dotsButton.setLabel("Discrete");
	    else
		dotsButton.setLabel("Continuous");
	    graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();
	}
	else if (target == graphFitZoomButton) {
	    graphFitZoomButton.setLabel("In progress");
	    double max = Double.MIN_VALUE;
	    double min = Double.MAX_VALUE;
	    double yy;

	    PostfixList pfl = graphCanvas.getList();

	    for (int ind=0; ind<pfl.List.size(); ind++) {
		PostfixListNode ptr = (PostfixListNode) pfl.List.elementAt(ind);
		if (ptr.drawn==true && ptr.pf!=null) {
		    for (double x=0; x<=graphCanvas.getWidth(); x+=.25) {
			yy = pfl.get(ind).evaluate((graphCanvas.xmax-graphCanvas.xmin)*x/graphCanvas.getWidth()+graphCanvas.xmin);
			if (yy<min) min=yy;
			if (yy>max) max=yy;
		    }
		}
	    }

	    if (max!=Double.MIN_VALUE && min!=Double.MAX_VALUE && max!=min &&
		min!=Double.NEGATIVE_INFINITY && max!=Double.POSITIVE_INFINITY) {
		graphCanvas.changeRange(graphCanvas.xmin,graphCanvas.xmax,graphCanvas.xscl,
					min,max,graphCanvas.yscl);
       		graphCanvas.drawPostfix();
	    }

	    updateRangeTF();
	    graphFitZoomButton.setLabel("Graph-Fit Zoom");
	}
	else if (target == boxZoomButton) {
	    boxZoomButton.setLabel("In progress");
	    graphCanvas.fitToZoomBox();
	    graphCanvas.drawPostfix();
	    updateRangeTF();
	    boxZoomButton.setLabel("Box Zoom");
	}
	else if (target == standardZoomButton) {
	    graphCanvas.changeRange(-10,10,1,-10,10,1);
	    graphCanvas.drawPostfix();
	    updateRangeTF();
	    standardZoomButton.setLabel("Standard Zoom");
	}
	else if (target == trigZoomButton) {
	    trigZoomButton.setLabel("In progress");
	    double af = 0;
	    
	    if (GCalc.angle == GCalc.RADIAN)
		af = Math.PI;
	    else if (GCalc.angle == GCalc.DEGREE)
		af = 180;

	    graphCanvas.changeRange(-2.5*af,2.5*af,af/2,-4,4,1);
	    graphCanvas.drawPostfix();
	    updateRangeTF();
	    trigZoomButton.setLabel("Trig Zoom");
	}
	else if (target == squareZoomButton) {
	    squareZoomButton.setLabel("In progress");
	    double d = graphCanvas.getHeight()*(graphCanvas.xmax-graphCanvas.xmin)/2/graphCanvas.getWidth();
	    graphCanvas.changeRange(graphCanvas.xmin,graphCanvas.xmax,graphCanvas.xscl,
				    graphCanvas.yavg-d,graphCanvas.yavg+d,graphCanvas.yscl);
	    graphCanvas.drawPostfix();
	    updateRangeTF();
	    squareZoomButton.setLabel("Square Zoom");
	}
	else if (target == zoomInButton) {
	    zoomInButton.setLabel("In progress");
	    setZoomValues();
	    double d = (graphCanvas.xmax-graphCanvas.xmin)/graphCanvas.xzoom/2;
	    double e = (graphCanvas.ymax-graphCanvas.ymin)/graphCanvas.yzoom/2;
	    graphCanvas.changeRange(graphCanvas.xavg-d,graphCanvas.xavg+d,graphCanvas.xscl,
				    graphCanvas.yavg-e,graphCanvas.yavg+e,graphCanvas.yscl);
	    graphCanvas.drawPostfix();
	    updateRangeTF();
	    updateZoomTF();
	    zoomInButton.setLabel("Zoom In");
	}
	else if (target == zoomOutButton) {
	    zoomOutButton.setLabel("In progress");
	    setZoomValues();
	    double d = (graphCanvas.xmax-graphCanvas.xmin)*graphCanvas.xzoom/2;
	    double e = (graphCanvas.ymax-graphCanvas.ymin)*graphCanvas.yzoom/2;
	    graphCanvas.changeRange(graphCanvas.xavg-d,graphCanvas.xavg+d,graphCanvas.xscl,
				    graphCanvas.yavg-e,graphCanvas.yavg+e,graphCanvas.yscl);
	    graphCanvas.drawPostfix();
	    updateRangeTF();
	    updateZoomTF();
	    zoomOutButton.setLabel("Zoom Out");
	}
	else if (target == xmaxTF || target == xminTF || target == ymaxTF ||
		 target == yminTF || target == xsclTF || target == ysclTF ) {
	    setWindowValues();
	    graphCanvas.drawPostfix();
	}
	else if (target==resetButton) {
	    boolean yes = ConfirmDialog.getConfirmation(this, "Do you really want to reset?", true);

	    if (yes) {
		setWindowValues();
		graphCanvas.clearList();
		graphCanvas.clearGraph();
		textfield1.setText("");
		sl.clear();
	    }
	}
	else if (target == previousButton) {
	    textfield1.setText(sl.getPrev());
	}
	else if (target == nextButton) {
	    textfield1.setText(sl.getNext());
	}
	else if (target == justGraphButton) {
	    new ImageDialog(this, graphCanvas.getImage(), graphCanvas.getWidth(), graphCanvas.getHeight() );
	}
	else if (target == aboutButton) {
	    new AboutDialog(this);
	}
	else if (target == listButton) {
	    new PostfixListDialog(this, graphCanvas.getList());
	    graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();

	}
    }

    /**
     * Handles input from the main textfield.  
     */
    protected void processUserInput(String strInput)
    {
	String mystr = strInput.trim().toLowerCase();
	String postfixText="";

	int commandType = 0;

	if (mystr.equals("exit")) {
	    exit();
	}
	else if (mystr.equals("clear")) {
	    commandType = 3;
	    graphCanvas.clearList();
	    graphCanvas.clearGraph();
	    textfield1.setText("");
	    sl.clear();

	    System.gc();
	}
	else if (mystr.equals("prev")) {
	    commandType = 1;
	    textfield1.setText(sl.getPrev());
	}
	else if (mystr.equals("next")) {
	    commandType = 1;
	    textfield1.setText(sl.getNext());
	}
	else if (mystr.startsWith("list")) {
	    commandType = 1;

	    textfield1.setText("");

	    new PostfixListDialog(this, graphCanvas.getList());

	    //need to clear in case the dialog removed some functions.
	    graphCanvas.clearGraph();
	    graphCanvas.drawPostfix();
	}
	else if (mystr.startsWith("#")) {
	    commandType = 1;
	    int val=0;
	    try {
		Integer d = Integer.valueOf(mystr.substring(1));
		val = d.intValue();
	    }
	    catch (NumberFormatException exception) {}

	    textfield1.setText(graphCanvas.getList().get(val).infixString());
	}

	Postfix postfix=null;

	if (commandType==0) {
	    long timer = System.currentTimeMillis();
	    commandType = 2;

	    sl.add(mystr);
	    sl.resetCursor();

	    setWindowValues();

	    postfix = new Postfix(mystr);
	    postfixText = postfix.toPostfixString();

	    System.out.println(postfixText);

	    textfield1.setText("");


	    if (!postfixText.equals("")) {
		long tt = System.currentTimeMillis();
		graphCanvas.drawPostfix(postfix, colorLabel.getColor());
		PaletteColorChooser.advanceColorIndex();
		colorLabel.setColor(PaletteColorChooser.getColor());

		System.out.println("tt2 "+(System.currentTimeMillis()-tt));

		System.out.println("INFIX: "+mystr);
		System.out.println("POSTFIX: "+postfixText);
		//			System.out.println("FULLY PARENTHESIZED INFIX: "+postfix.infix());
		//			System.out.println("DERIVATIVE:"+postfix.derivative().infix());

		setStatusString("Graphed '"+postfix.infix()+"'.");

	    }
	    else {
		setStatusString("Don't understand '"+mystr+"'.", Color.red);
	    }

	    long tt = System.currentTimeMillis();
	    graphCanvas.drawPostfix();

	    System.out.println("tt "+(System.currentTimeMillis()-tt));

	}

    }

    /**
     * Sets the zoom values on the graph given the values in the
     * textfields.
     */
    protected void setZoomValues()
    {
	double xz, yz;

	try {
	    Double d = Double.valueOf(xZoom.getText());
	    xz = d.doubleValue();
	}
	catch (NumberFormatException exception) {
	    xz = graphCanvas.xzoom;
	}

	try {
	    Double d = Double.valueOf(yZoom.getText());
	    yz = d.doubleValue();
	}
	catch (NumberFormatException exception) {
	    yz = graphCanvas.yzoom;
	}

	if (xz*yz!=0) {
	    graphCanvas.yzoom = Math.abs(yz);
	    graphCanvas.xzoom = Math.abs(xz);
	    updateZoomTF();
	}
    }

    /**
     * Sets the window values on the graph given the values in the
     * textfields.
     */
    protected void setWindowValues()
    {
	double xmax1, xmin1, xscl1;
	double ymax1, ymin1, yscl1;

	Postfix pf;

	pf = new Postfix(xminTF.getText());
	xmin1=pf.isConstant()?pf.evaluate(0):graphCanvas.xmin;

	pf = new Postfix(xmaxTF.getText());
	xmax1=pf.isConstant()?pf.evaluate(0):graphCanvas.xmax;

	pf = new Postfix(xsclTF.getText());
	xscl1=pf.isConstant()?pf.evaluate(0):graphCanvas.xscl;

	pf = new Postfix(yminTF.getText());
	ymin1=pf.isConstant()?pf.evaluate(0):graphCanvas.ymin;

	pf = new Postfix(ymaxTF.getText());
	ymax1=pf.isConstant()?pf.evaluate(0):graphCanvas.ymax;

	pf = new Postfix(ysclTF.getText());
	yscl1=pf.isConstant()?pf.evaluate(0):graphCanvas.yscl;

	if (xmax1>xmin1 && ymax1>ymin1) {
	    if (graphCanvas.ymax!=ymax1 || graphCanvas.ymin!=ymin1 || graphCanvas.yscl!=yscl1 ||
		graphCanvas.xmax!=xmax1 || graphCanvas.xmin!=xmin1 || graphCanvas.xscl!=xscl1 )
		graphCanvas.changeRange(xmin1, xmax1, xscl1, ymin1, ymax1, yscl1);
	    updateRangeTF();
	}
    }

    /**
     * Sets the xzoom/yzoom in the textfields given the values in the
     * graph object
     */
    protected void updateZoomTF()
    {
	xZoom.setText(""+graphCanvas.xzoom);
	yZoom.setText(""+graphCanvas.yzoom);
    }

    /**
     * Sets the window range values in the textfields given the values
     * in the graph object.
     */
    void updateRangeTF()
    {
	xmaxTF.setText(""+graphCanvas.xmax);
	xminTF.setText(""+graphCanvas.xmin);
	xsclTF.setText(""+graphCanvas.xscl);
	ymaxTF.setText(""+graphCanvas.ymax);
	yminTF.setText(""+graphCanvas.ymin);
	ysclTF.setText(""+graphCanvas.yscl);
    }

    /**
     * Enables/Disables the boxZoom feature
     *
     * @param b <code>true</code> makes it enabled.
     */
    void boxZoomSetEnabled(boolean b)
    {
	boxZoomButton.setEnabled(b);
    }

}


