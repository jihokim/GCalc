/*  
GCalc 1.5 - Graphing calculator applet
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

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/


/*

Notes from the original author:  

I'm sorry about this code.  It's ugly design, but it's functional.
Don't bother complaining too much.

*/



import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class GCalc extends Applet implements ActionListener, ItemListener
{
    String notice = "GCalc\nVersion 1.5\nCopyright 1999-2001 Jiho Kim\n";

	TextField textfield1;
	Graph graph1;

	Button axisButton, scoresButton, gridButton, dotsButton;

	Button standardZoomButton, trigZoomButton, squareZoomButton, graphFitZoomButton;
	Button zoomInButton, zoomOutButton;
	TextField xZoom, yZoom;

	TextField xminTF, xmaxTF, xsclTF,
			  yminTF, ymaxTF, ysclTF;

	Button resetButton, previousButton, nextButton;

	Button regraphButton;

	Choice colorChoice;
	String[] colorString;
	Color[] colorArray;

	StringList sl;

	public static void main(String[] args)
	{
		Frame f = new Frame("GCalc");

		GCalc gc = new GCalc();

		gc.init();
		gc.start();

		f.addWindowListener(f);
		f.add("Center",gc);
		f.setSize(700,430);
		f.setResizable(false);
		f.show();

	}

	public void init()
	{
		initApplet();
		initGUI();

	}

	void initApplet()
	{
		setVisible(true);
		setBackground(new Color(240,240,200));
		setLayout(new BorderLayout(5,5));

		graph1 = new Graph(this,460,330);

		colorString = new String[5];
		colorArray = new Color[5];
		colorString[0]="Blue";			colorArray[0]=Color.blue;
		colorString[1]="Green";			colorArray[1]=Color.green.darker();
		colorString[2]="Purple";		colorArray[2]=Color.magenta.darker();
		colorString[3]="Brown";			colorArray[3]=Color.orange.darker();
		colorString[4]="Black";			colorArray[4]=Color.black;

		sl = new StringList();
	}

	void initGUI()
	{
		Font normalFont = new Font("Courier", Font.PLAIN, 12);

		Panel graphProperties = new Panel();
		graphProperties.setLayout(new GridLayout(1,4,1,0));

		axisButton = new Button("Axis ON");
		scoresButton = new Button ("Scale ON");
		gridButton = new Button("Grid OFF");
		dotsButton = new Button("Continuous");

		axisButton.addActionListener(this);
		scoresButton.addActionListener(this);
		gridButton.addActionListener(this);
		dotsButton.addActionListener(this);

		graphProperties.add(axisButton);
		graphProperties.add(scoresButton);
		graphProperties.add(gridButton);
		graphProperties.add(dotsButton);

	//Center--Graph, input textfield, graph properties
		Panel west=new Panel();

		textfield1 = new TextField("",42);
		textfield1.setFont(new Font("Courier", Font.PLAIN, 12));
		textfield1.setBackground(Color.white);
		textfield1.addActionListener(this);

		Panel westCenter = new Panel();
		westCenter.setLayout(new BorderLayout(5,5));

		westCenter.add("North",textfield1);
		westCenter.add("Center",graph1);
		westCenter.add("South",graphProperties);
		west.add(westCenter);
		add("West",west);

	//East--zoom Buttons7
		standardZoomButton = new Button("Standard Zoom");
		trigZoomButton = new Button ("Trig Zoom");
		squareZoomButton = new Button("Square Zoom");
		graphFitZoomButton = new Button("Graph-Fit Zoom");

		standardZoomButton.addActionListener(this);
		trigZoomButton.addActionListener(this);
		squareZoomButton.addActionListener(this);
		graphFitZoomButton.addActionListener(this);

		Panel zoomButtons = new Panel();
		zoomButtons.setLayout(new GridLayout(4,1));
		zoomButtons.add(standardZoomButton);
		zoomButtons.add(trigZoomButton);
		zoomButtons.add(squareZoomButton);
		zoomButtons.add(graphFitZoomButton);

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

		Panel manualZooms = new Panel();
		manualZooms.setLayout(new GridLayout(4,1));
		manualZooms.add(zoomInButton);
		manualZooms.add(zoomOutButton);
		manualZooms.add(xZoom);
		manualZooms.add(yZoom);

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

		Panel xminPanel = new Panel();
		Panel xmaxPanel = new Panel();
		Panel xsclPanel = new Panel();
		Panel yminPanel = new Panel();
		Panel ymaxPanel = new Panel();
		Panel ysclPanel = new Panel();

		xminPanel.setLayout(new BorderLayout());
		xmaxPanel.setLayout(new BorderLayout());
		xsclPanel.setLayout(new BorderLayout());
		yminPanel.setLayout(new BorderLayout());
		ymaxPanel.setLayout(new BorderLayout());
		ysclPanel.setLayout(new BorderLayout());

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

		Panel xyRange = new Panel();

		xyRange.setLayout(new GridLayout(6,1));
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

		colorChoice = new Choice();
		colorChoice.addItemListener(this);
		for (int i=0; i<colorString.length; i++)
			colorChoice.add(colorString[i]);

		Panel misc = new Panel();
		misc.setLayout(new GridLayout(2,2,5,5));

		misc.add(previousButton);
		misc.add(nextButton);
		misc.add(colorChoice);
		misc.add(resetButton);

	//East
		Panel eastTop=new Panel();
		Panel eastBottom=new Panel();
		Panel zoomPanel = new Panel();
		Panel east=new Panel();
		eastTop.setLayout(new BorderLayout(5,5));
		zoomPanel.setLayout(new GridLayout(1,2,5,0));
		eastBottom.setLayout(new BorderLayout(5,5));
		east.setLayout(new BorderLayout(5,5));

		eastTop.add("North",misc);
		zoomPanel.add(zoomButtons);
		zoomPanel.add(manualZooms);
		eastTop.add("Center",zoomPanel);
		eastTop.add("South",eastBottom);
		eastBottom.add("North",xyRange);

		Panel east2 = new Panel();
		east2.setLayout(new BorderLayout(5,5));
		east2.add("North",eastTop);
		east2.add("Center",eastBottom);
		east.add("Center",east2);
		east.add("East", new Canvas());
		add("East",east);

	//North
		add("North", new Canvas());
	//South
		add("South", new Canvas());

		updateRangeTF();
	}

	public void start()
	{
		System.out.println(notice);
		graph1.clearGraph();
	}

	public void itemStateChanged(ItemEvent event){}

	public void actionPerformed(ActionEvent event)
	{
		Component target=(Component) event.getSource();

		if (target == textfield1)
		{
			processUserInput(textfield1.getText());
		}
		else
		if (target == axisButton)
		{
			graph1.axis = ! graph1.axis;
			if (graph1.axis)
				axisButton.setLabel("Axis ON");
			else
				axisButton.setLabel("Axis OFF");
			graph1.clearGraph();
			graph1.drawPostfix();
		}
		else
		if (target == scoresButton)
		{
			graph1.scores = ! graph1.scores;
			if (graph1.scores)
				scoresButton.setLabel("Scores ON");
			else
				scoresButton.setLabel("Scores OFF");
			graph1.clearGraph();
			graph1.drawPostfix();
		}
		else
		if (target == gridButton)
		{
			graph1.grid = ! graph1.grid;
			if (graph1.grid)
				gridButton.setLabel("Grid ON");
			else
				gridButton.setLabel("Grid OFF");
			graph1.clearGraph();
			graph1.drawPostfix();
		}
		else
		if (target == dotsButton)
		{
			graph1.dots = ! graph1.dots;
			if (graph1.dots)
				dotsButton.setLabel("Discrete");
			else
				dotsButton.setLabel("Continuous");
			graph1.clearGraph();
			graph1.drawPostfix();
		}
		else
		if (target == graphFitZoomButton)
		{
			if (graphFitZoomButton.getLabel().equals("Graph-Fit Zoom"))
			{
				graphFitZoomButton.setLabel("In progress");
				double max = Double.MIN_VALUE;
				double min = Double.MAX_VALUE;
				double yy;

				PostfixList pfl = graph1.getList();

				for (int ind=0; ind<pfl.List.size(); ind++)
				{
					PostfixListNode ptr = (PostfixListNode) pfl.List.elementAt(ind);
					if (ptr.drawn==true && ptr.pf!=null)
					{
						for (double x=0; x<=graph1.xpixels; x+=.25)
						{
							yy = pfl.get(ind).evaluate((graph1.xmax-graph1.xmin)*x/graph1.xpixels+graph1.xmin);
							if (yy<min) min=yy;
							if (yy>max) max=yy;
						}
					}
				}

				if (max!=Double.MIN_VALUE && min!=Double.MAX_VALUE && max!=min &&
					min!=Double.NEGATIVE_INFINITY && max!=Double.POSITIVE_INFINITY)
				{
					graph1.changeRange(graph1.xmin,graph1.xmax,graph1.xscl,
											min,max,graph1.yscl);
					graph1.clearGraph();
					graph1.drawPostfix();
				}
			}
			else
			{
				graphFitZoomButton.setLabel("In progress");
				graph1.fitToZoomBox();
				graph1.drawPostfix();
			}
			updateRangeTF();
			graphFitZoomButton.setLabel("Graph-Fit Zoom");
		}
		else
		if (target == standardZoomButton)
		{
			standardZoomButton.setLabel("In progress");
			graph1.changeRange(-10,10,1,-10,10,1);
			graph1.clearGraph();
			graph1.drawPostfix();
			updateRangeTF();
			standardZoomButton.setLabel("Standard Zoom");
		}
		else
		if (target == trigZoomButton)
		{
			trigZoomButton.setLabel("In progress");
			graph1.changeRange(-2.5*Math.PI,2.5*Math.PI,Math.PI/2,-4,4,1);
			graph1.clearGraph();
			graph1.drawPostfix();
			updateRangeTF();
			trigZoomButton.setLabel("Trig Zoom");
		}
		else
		if (target == squareZoomButton)
		{
			squareZoomButton.setLabel("In progress");
			double d = graph1.ypixels*(graph1.xmax-graph1.xmin)/2/graph1.xpixels;
			graph1.changeRange(graph1.xmin,graph1.xmax,graph1.xscl,
								graph1.yavg-d,graph1.yavg+d,graph1.yscl);
			graph1.clearGraph();
			graph1.drawPostfix();
			updateRangeTF();
			squareZoomButton.setLabel("Square Zoom");
		}
		else
		if (target == zoomInButton)
		{
			zoomInButton.setLabel("In progress");
			setZoomValues();
			double d = (graph1.xmax-graph1.xmin)/graph1.xzoom/2;
			double e = (graph1.ymax-graph1.ymin)/graph1.yzoom/2;
			graph1.changeRange(graph1.xavg-d,graph1.xavg+d,graph1.xscl,
								graph1.yavg-e,graph1.yavg+e,graph1.yscl);
			graph1.clearGraph();
			graph1.drawPostfix();
			updateRangeTF();
			updateZoomTF();
			zoomInButton.setLabel("Zoom In");
		}
		else
		if (target == zoomOutButton)
		{
			zoomOutButton.setLabel("In progress");
			setZoomValues();
			double d = (graph1.xmax-graph1.xmin)*graph1.xzoom/2;
			double e = (graph1.ymax-graph1.ymin)*graph1.yzoom/2;
			graph1.changeRange(graph1.xavg-d,graph1.xavg+d,graph1.xscl,
								graph1.yavg-e,graph1.yavg+e,graph1.yscl);
			graph1.clearGraph();
			graph1.drawPostfix();
			updateRangeTF();
			updateZoomTF();
			zoomOutButton.setLabel("Zoom Out");
		}
		else
		if (target == xmaxTF || target == xminTF || target == ymaxTF ||
			target == yminTF ||	target == xsclTF || target == ysclTF )
		{
			setRangeValues();
			graph1.clearGraph();
			graph1.drawPostfix();
		}
		else
		if (target==resetButton)
		{
			setRangeValues();
			graph1.clearList();
			graph1.clearGraph();
			textfield1.setText("");
			colorChoice.select(0);
			sl.clear();
		}
		else
		if (target == previousButton)
		{
			textfield1.setText(sl.getPrev());
		}
		else
		if (target == nextButton)
		{
			textfield1.setText(sl.getNext());
		}
	}

	void processUserInput(String strInput)
	{
		String mystr = strInput.trim().toLowerCase();
		String postfixText="";

		int commandType = 0;

		if (mystr.equals("clear"))
		{
			commandType = 3;
			graph1.clearList();
			graph1.clearGraph();
			textfield1.setText("");
			colorChoice.select(0);
			sl.clear();

			System.gc();
		}
/*		else if (mystr.equals("hide"))
		{
			commandType = 1;
			graph1.setVisible(! graph1.isVisible());
			graph1.clearGraph();
			graph1.drawPostfix();
			textfield1.setText("");
		}
*/		else if (mystr.equals("axis"))
		{
			commandType = 1;
			graph1.axis = ! graph1.axis;
			graph1.clearGraph();
			textfield1.setText("");
		}
		else if (mystr.equals("tick"))
		{
			commandType = 1;
			graph1.scores = ! graph1.scores;
			graph1.clearGraph();
			textfield1.setText("");
		}
		else if (mystr.equals("dots"))
		{
			commandType = 1;
			graph1.dots = ! graph1.dots;
			graph1.clearGraph();
			graph1.drawPostfix();
			textfield1.setText("");
		}
		else if (mystr.equals("grid"))
		{
			commandType = 1;
			graph1.grid = ! graph1.grid;
			graph1.clearGraph();
			textfield1.setText("");
		}
		else if (mystr.equals("prev"))
		{
			commandType = 1;
			textfield1.setText(sl.getPrev());
		}
		else if (mystr.equals("next"))
		{
			commandType = 1;
			textfield1.setText(sl.getNext());
		}
		else if (mystr.startsWith("~"))
		{
			commandType = 1;
			int val=0;
			try
			{
				Integer d = Integer.valueOf(mystr.substring(1));
				val = d.intValue();
			}
			catch (NumberFormatException exception) {}

			graph1.remove(val);
			graph1.clearGraph();
			graph1.drawPostfix();
			textfield1.setText("");
		}
		else if (mystr.startsWith("list"))
		{
			commandType = 1;

			graph1.getList().showInfix();
			textfield1.setText("");
		}
		else if (mystr.startsWith("#"))
		{
			commandType = 1;
			int val=0;
			try
			{
				Integer d = Integer.valueOf(mystr.substring(1));
				val = d.intValue();
			}
			catch (NumberFormatException exception) {}

			textfield1.setText(graph1.getList().get(val).infixStr);
		}

		Postfix postfix=null;

		if (commandType==0)
		{
			long timer = System.currentTimeMillis();
			commandType = 2;

			sl.add(mystr);
			sl.resetCursor();

			setRangeValues();

			postfix = new Postfix(mystr);
			postfixText = postfix.getStack().showAll();

			graph1.drawPostfix(postfix, colorArray[colorChoice.getSelectedIndex()]);
//			graph1.drawPostfix(postfix.derivative(), new Color(200,200,200));

			graph1.drawPostfix();

			textfield1.setText("");
			colorChoice.select((colorChoice.getSelectedIndex()+1)%colorArray.length);
		}

		if (commandType==2 && !postfixText.equals(""))
		{
			System.out.println("INFIX: "+mystr);
			System.out.println("POSTFIX: "+postfixText);
//			System.out.println("FULLY PARENTHESIZED INFIX: "+postfix.infix());
//			System.out.println("DERIVATIVE:"+postfix.derivative().infix());
			System.out.println();
		}
	}

	public void setZoomValues()
	{
		double xz, yz;

		try
		{
			Double d = Double.valueOf(xZoom.getText());
			xz = d.doubleValue();
		}
		catch (NumberFormatException exception)
		{
			xz = graph1.xzoom;
		}

		try
		{
			Double d = Double.valueOf(yZoom.getText());
			yz = d.doubleValue();
		}
		catch (NumberFormatException exception)
		{
			yz = graph1.yzoom;
		}

		if (xz*yz!=0)
		{
			graph1.yzoom = Math.abs(yz);
			graph1.xzoom = Math.abs(xz);
			updateZoomTF();
		}
	}

	public void setRangeValues()
	{
		double xmax1, xmin1, xscl1;
		double ymax1, ymin1, yscl1;

		Postfix pf;

		pf = new Postfix(xminTF.getText());
		xmin1=pf.isConstant()?pf.evaluate(0):graph1.xmin;

		pf = new Postfix(xmaxTF.getText());
		xmax1=pf.isConstant()?pf.evaluate(0):graph1.xmax;

		pf = new Postfix(xsclTF.getText());
		xscl1=pf.isConstant()?pf.evaluate(0):graph1.xscl;

		pf = new Postfix(yminTF.getText());
		ymin1=pf.isConstant()?pf.evaluate(0):graph1.ymin;

		pf = new Postfix(ymaxTF.getText());
		ymax1=pf.isConstant()?pf.evaluate(0):graph1.ymax;

		pf = new Postfix(ysclTF.getText());
		yscl1=pf.isConstant()?pf.evaluate(0):graph1.yscl;

		if (xmax1>xmin1 && ymax1>ymin1 /*&& yscl1!=0 && xscl1!=0*/)
		{
			graph1.changeRange(xmin1, xmax1, xscl1, ymin1, ymax1, yscl1);
			updateRangeTF();
		}
	}

	void updateRangeTF()
	{
		xmaxTF.setText(""+graph1.xmax);
		xminTF.setText(""+graph1.xmin);
		xsclTF.setText(""+graph1.xscl);
		ymaxTF.setText(""+graph1.ymax);
		yminTF.setText(""+graph1.ymin);
		ysclTF.setText(""+graph1.yscl);
	}

	void updateZoomTF()
	{
		xZoom.setText(""+graph1.xzoom);
		yZoom.setText(""+graph1.yzoom);
	}

}

class Graph extends Canvas implements MouseListener, MouseMotionListener
{
	private GCalc host;
	private Image image;
	private Graphics page;

	public boolean axis, dots, scores, grid;

	public double xmax, xmin, xscl, xscl2, xavg, xfact;
	public double ymax, ymin, yscl, yscl2, yavg, yfact;
	public double xzoom, yzoom;

	public int xpixels, ypixels;

	private PostfixList pfl;

	private float mx, my;
	public int dragstartX, dragstartY;
	public int dragdX, dragdY;

	int preferredWidth;
	int preferredHeight;

	Graph (GCalc gc, int xSize, int ySize)
	{
		this(gc, xSize, ySize, -10, 10, 1, -10, 10, 1);
	}

	Graph (GCalc gc, int xSize, int ySize, double xmin1, double xmax1, double xscl1,
								double ymin1, double ymax1, double yscl1)
	{
		host = gc;
		xpixels=xSize;
		ypixels=ySize;

		setSize(xpixels+1,ypixels+1);

 		changeRange(xmin1, xmax1, xscl1, ymin1, ymax1, yscl1);

		initDefault();
		clearList();

		addMouseListener(this);
		addMouseMotionListener(this);

	}


	private void initDefault()
	{
		axis = true;
		dots = false;
		scores = true;
		grid = false;

		xzoom=2;
		yzoom=2;

		my=0;
		mx=0;

		dragstartX=-1;
		dragstartY=-1;
		dragdX=0;
		dragdY=0;
	}

	public void mouseClicked (MouseEvent event)
	{
		switch (event.getClickCount())
		{
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
		mouseMoved(event);
	}

	public void mouseMoved (MouseEvent event)
	{
		if (page!=null)
		{
			page.setPaintMode();
			referencePt((float) (event.getX()/xfact+xmin),(float) (event.getY()/yfact+ymax));
			drawCoordinate();
		}
		repaint();
	}

	public void referencePt(float xx, float yy)
	{
		mx=xx;
		my=yy;
	}

	public void mouseDragged (MouseEvent event)
	{
		if (dragstartX==-1 && dragstartY==-1)
		{
			dragstartX=event.getX();
			dragstartY=event.getY();
			dragdX=0;
			dragdY=0;
		}
		else
		{
			page.setXORMode(Color.white);
			page.setColor(Color.orange);
			page.drawRect(dragstartX-dragdX, dragstartY-dragdY, 2*dragdX, 2*dragdY);
			dragdX=Math.abs(event.getX()-dragstartX);
			dragdY=Math.abs(event.getY()-dragstartY);
			page.drawRect(dragstartX-dragdX, dragstartY-dragdY, 2*dragdX, 2*dragdY);
			mouseMoved(event);
		}
	}

	public void mouseReleased (MouseEvent event)
	{
		page.setPaintMode();

		if (dragstartX!=-1 && dragstartY!=-1)
			host.graphFitZoomButton.setLabel("Box Zoom");
		else
			host.graphFitZoomButton.setLabel("Graph-Fit Zoom");
	}

	public void mousePressed (MouseEvent event)
	{
		if (dragstartX!=-1 && dragstartY!=-1)
		{
			page.setXORMode(Color.white);
			page.setColor(Color.orange);
			page.drawRect(dragstartX-dragdX, dragstartY-dragdY, 2*dragdX, 2*dragdY);
//			dragstartX=-1;
//			dragstartY=-1;
			dragstartX=event.getX();
			dragstartY=event.getY();
			dragdX=0;
			dragdY=0;
		}
	}

	public void mouseEntered (MouseEvent event)
	{
		mouseMoved(event);
	}

	public void mouseExited (MouseEvent event)
	{
		mx=Float.NaN;
		my=Float.NaN;
		drawCoordinate();
	}

	private void drawCoordinate()
	{
		page.setColor(Color.gray);
		page.fillRect(3,2,120,23);
		page.setColor(Color.lightGray);
		page.fillRect(0,0,120,23);
		page.setColor(Color.black);
		page.drawString("x = "+nonNaNString(mx), 5, 10);
		page.drawString("y = "+nonNaNString(my), 5, 20);

		repaint();
	}
	
	private static String nonNaNString(float a)
	{
		if (Float.isNaN(a)) return "";
		
		return ""+a;
	}

	public void changeRange (double xmin1, double xmax1, double xscl1,
								double ymin1, double ymax1, double yscl1)
	{
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

		xavg = (xmax+xmin)/2;	xfact=xpixels/(xmax-xmin);
		yavg = (ymax+ymin)/2;	yfact=ypixels/(ymin-ymax);

		if (image!=null) clearGraph();
	}

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
		if (image==null)
		{
			image = createImage(xpixels+1,ypixels+1);
			page = image.getGraphics();
			clearGraph();
		}

		g.drawImage(image, 0,0, this);
	}

	public void clearList()
	{
		pfl=new PostfixList();
	}

	public void clearGraph()
	{
		if (image==null) return;

		page.setColor(Color.white);
		page.fillRect(0,0,xpixels+1,ypixels+1);

		if (grid) drawGrid();
		if (axis) drawAxis();
		if (scores) drawScores();

		pfl.deflagAll();
		referencePt(Float.NaN,Float.NaN);
		drawCoordinate();

		dragstartX=-1;
		dragstartY=-1;
		dragdX=0;
		dragdY=0;

		host.graphFitZoomButton.setLabel("Graph-Fit Zoom");

	}

	private void drawAxis()
	{
		int sx = (int) (-xmin*xfact+.5);	//(sx,sy) is where the origin is
		int sy = (int) (-ymax*yfact+.5);

		page.setColor(Color.red);
		if (YonScreen(sy))
			page.drawLine(0,sy,xpixels-1,sy);

		if (XonScreen(sx))
			page.drawLine(sx,0,sx,ypixels-1);
	}

	private void saveScl()
	{
		xscl2 = xscl;
		yscl2 = yscl;
		if (xscl!=0 && 1/xscl>Math.abs(3*xfact))
			xscl = Math.abs(1/xfact);
		if (yscl!=0 && 1/yscl>Math.abs(3*yfact))
			yscl = Math.abs(1/yfact);
	}

	private void restoreScl()
	{
		xscl = xscl2;
		yscl = yscl2;
	}

	private void drawGrid()
	{
		int sx = (int) (-xmin*xfact+.5);	//(sx,sy) is where the origin is
		int sy = (int) (-ymax*yfact+.5);

		saveScl();

		page.setColor(Color.lightGray);
		if (xscl!=0)
		{
			for (double i = xscl*((int) (xmin/xscl+.5)); i<=xmax; i+=xscl)
			{
				int sx1 = (int) ((i-xmin)*xfact+.5);
				int sx2 = (int) ((-i-xmin)*xfact+.5);
				page.drawLine(sx1,0,sx1,ypixels-1);
				page.drawLine(sx2,0,sx2,ypixels-1);
			}
		}

		if (yscl!=0)
		{
			for (double i = yscl*((int) (ymin/yscl+.5)); i<=ymax; i+=yscl)
			{
				int sy1 = (int) ((i-ymax)*yfact+.5);
				int sy2 = (int) ((-i-ymax)*yfact+.5);
				page.drawLine(0,sy1,xpixels-1,sy1);
				page.drawLine(0,sy2,xpixels-1,sy2);
			}
		}

		restoreScl();

	}

	private void drawScores()
	{
		int sx = (int) (-xmin*xfact+.5);	//(sx,sy) is where the origin is
		int sy = (int) (-ymax*yfact+.5);

		saveScl();

		page.setColor(Color.blue);
		if (YonScreen(sy) && xscl!=0)						//Draw X axis
		{
			for (double i = xscl*((int) (xmin/xscl+.5)); i<=xmax; i+=xscl)
			{
				int sx1 = (int) ((i-xmin)*xfact+.5);
				int sx2 = (int) ((-i-xmin)*xfact+.5);
				page.drawLine(sx1,sy-1,sx1,sy+1);
				page.drawLine(sx2,sy-1,sx2,sy+1);
			}
		}
		if (XonScreen(sx) && yscl!=0)						//Draw Y axis
		{
			for (double i = yscl*((int) (ymin/yscl+.5)); i<=ymax; i+=yscl)
			{
				int sy1 = (int) ((i-ymax)*yfact+.5);
				int sy2 = (int) ((-i-ymax)*yfact+.5);
				page.drawLine(sx-1,sy1,sx+1,sy1);
				page.drawLine(sx-1,sy2,sx+1,sy2);
			}
		}

		restoreScl();
	}

	private boolean YoutOfBounds (double y)
	{
		if (Double.isInfinite(y)) return true;
		if (Double.isNaN(y)) return true;
		if (y<ymin) return true;
		if (y>ymax) return true;

		return false;
	}

	private boolean XoutOfBounds (double x)
	{
		if (Double.isInfinite(x)) return true;
		if (Double.isNaN(x)) return true;
		if (x<xmin) return true;
		if (x>xmax) return true;

		return false;
	}

	private boolean YonScreen (double y)
	{
		return (y>=0 && y<ypixels);
	}

	private boolean XonScreen (double x)
	{
		return (x>=0 && x<xpixels);
	}

	boolean isNotReal(double d)
	{
		return Double.isNaN(d) || Double.isInfinite(d);
	}

	public void drawPostfix(Postfix postfix, Color c)
	{
		if (postfix==null) return;
		if (! postfix.getStack().isEmpty())
			pfl.add(postfix, c);

		drawPostfix();
		repaint();
	}

	public void drawPostfix()
	{
		for (int i=0; i<pfl.List.size(); i++)
		{
			PostfixListNode ptr = pfl.getNodeAt(i);

			if (ptr.drawn==false)
			{
				drawGraph(ptr.pf, ptr.color);
				ptr.drawn = true;
			}
		}
		drawCoordinate();
	}


	private void drawGraph(Postfix postfix, Color c)
	{
		if (postfix==null) return;

		int sx=0, sy=0;			//Screen x, y
		int psx=-50, psy=50;	//Previous Screen x,y
		double yy2=Double.NaN;	//last real functional value;
		double yy=0;			//real functional value

		page.setColor(c);

		for (double i=0; i<=xpixels; i+=1)
		{
			yy = postfix.evaluate(i/xfact+xmin);

			sx=(int) i;
			sy=(int) ((yy-ymax)*yfact+.5);

			if (! (YoutOfBounds(yy) && YoutOfBounds(yy2)) )
			{
				if (isNotReal(yy))
				{
					//the current point is not plottable.
				}
				else
				if (dots || isNotReal(yy2))
				{
					// if the dots flag is on or the last y value was not plotted
					// plot a point at (sx,sy)
					page.drawLine(sx,sy,sx,sy);
				}
				else
				{
					// Seems okay to plot a line between the last plotted point and
					// this one.
					page.drawLine(sx,sy,psx,psy);
				}
			}

			yy2=yy;
			psx=sx;
			psy=sy;
		}//for i
	}


	public PostfixList getList()
	{
		return pfl;
	}

	public void remove(int n)
	{
		pfl.remove(n);
	}
}




class Postfix
{
	double x = 0;
	Stack pf;
	String infixStr;
	boolean isConstant;
	
	public Postfix()
	{
		this("");
	}


	protected Postfix(Stack s)
	{
		pf=s;
	}

	public Postfix(String input)
	{
		infixStr = input;

		Stack infix = new Stack();
		String eqn = input.trim();
		boolean okay = true;
		isConstant = true;

		Token tk = null;

		while (eqn.length()!=0 && okay)
		{
			tk = new Token(eqn);
			if (tk.isValid())
			{
				isConstant = isConstant && ! tk.is("x");

				String content = tk.getContent();
				int num = tk.length();
				eqn = eqn.substring(num).trim();

				infix.push(tk);
			}
			else
			{
				infix.clear();
				okay = false;
			}
		}

		pf = infix2postfix(infix.flip());

//		System.out.println(pf.showAll());

		if (isMalformed())
			pf = new Stack();
	}

	private boolean isMalformed()
	{
		boolean b = false;

		char[] ch = infixStr.toCharArray();
		int count=0;

		for (int i=0; i<ch.length; i++)
		{
			if (ch[i]=='(') count++;
			if (ch[i]==')') count--;

			if (count<0) return true;
		}

		try 
		{
			b = (count!=0 || evaluate(0)==Double.MIN_VALUE);
		}
		catch (EmptyStackException e)
		{
			return true;
		}

		return b;
	}

	private Stack infix2postfix(Stack infix)
	{
		Stack pf = new Stack();					//Stack for postfix
		Stack ops = new Stack();				//Stack for operations
		Stack temp = new Stack();

		Token lastTk = new Token("+");

		infix.showAll();

		while (! infix.isEmpty())
		{
			pf.showAll();

			Token tk = infix.pop();

/*NEGATION CODE
	If conditions are right, "-" can mean the negative
	of a number and not subtraction.
*/			if (tk.is("-"))
			{
				if (pf.isEmpty())
				{
					tk = new Token("neg");
				}
				else if (lastTk.isOperation() && ! lastTk.is(")"))
				{
					tk = new Token("neg");
				}
				else if (lastTk.isNumber())
				{
					tk = new Token("-");
				}
			}

/*POSITIVE CODE
	If conditions are right, "+" can mean the positive
	of a number and not addition.
*/			if (tk.is("+"))
			{
				if (pf.isEmpty())
				{
					tk = new Token("");
				}
				else if (lastTk.isOperation() && ! lastTk.is(")"))
				{
					tk = new Token("");
				}
				else if (lastTk.isNumber())
				{
					tk = new Token("+");
				}
			}

			float precedence=tk.precedence();

// IMPLIED MULTIPLICATION CODE
			if (! infix.isEmpty())
			{
				Token nt = infix.peek();
				if ( (tk.is(")") && nt.is("(")) ||
					 (tk.isNumber() && nt.is("(")) ||
					 (tk.is(")") && nt.isNumber()) ||
					 (tk.is(")") && nt.precedence()==1) ||
					 (tk.isNumber() && nt.precedence()==1) ||
					 (tk.isNumber() && nt.isNumber())  )

					 infix.push(new Token("*"));
			}

			if (tk.isNumber())
			{
				pf.push(tk);
			}
			else
			if (tk.isOperation())
			{
				if (ops.isEmpty() || tk.is("("))
				{
					ops.push(tk);
				}
				else
				if (tk.is(")"))
				{
					while (! ops.peek().is("("))
					{
						pf.push(ops.pop());
						if (ops.isEmpty()) break;
					}
					if (! ops.isEmpty()) ops.pop();
				}
				else
				if (tk.isBinary())
				{
					while (ops.peek().precedence()<=precedence)
					{
						pf.push(ops.pop());
						if (ops.isEmpty()) break;
					}
					ops.push(tk);
				}
				else
				{
					while (ops.peek().precedence()<precedence)
					{
						pf.push(ops.pop());
						if (ops.isEmpty()) break;
					}
					ops.push(tk);
				}
			}

			if (! tk.is("")) lastTk = tk;
		}

		while (! ops.isEmpty())
			pf.push(ops.pop());

		Stack t = new Stack();
		while (! pf.isEmpty())
		{

			Token tk = pf.pop();
			if (tk.is("ddx"))
			{
				Stack d = derivative(sub(pf));
				while (d!=null && ! d.isEmpty())
				{
					t.push(d.pop());
				}
			}
			else
			{
				t.push(tk);
			}
		}

		while (! t.isEmpty())
			pf.push(t.pop());

		return pf;
	}

	static Stack add(Stack a,Stack b)  // r = a+b
	{
		Stack r = new Stack();

		if (a.isZero() && b.isZero())
		{
			r.push("0");
		}
		else
		if (a.isZero())
		{
			r.push(b);
		}
		else
		if (b.isZero())
		{
			r.push(a);
		}
		else
		{
			r.push(a);
			r.push(b);
			r.push("+");
		}

		return r;
	}

	static Stack subtract(Stack a,Stack b)  // r = a-b
	{
		Stack r = new Stack();

		if (a.isZero() && b.isZero())
		{
			r.push("0");
		}
		else
		if (a.isZero())
		{
			r.push(b);
			r.push("neg");
		}
		else
		if (b.isZero())
		{
			r.push(a);
		}
		else
		{
			r.push(a);
			r.push(b);
			r.push("-");
		}

		return r;
	}

	static Stack multiply(Stack a,Stack b)  // r = a*b
	{
		Stack r = new Stack();

		if (a.isZero() || b.isZero())
		{
			r.push("0");
		}
		else
		if (a.isOne())
		{
			r.push(b);
		}
		else
		if (b.isOne())
		{
			r.push(a);
		}
		else
		{
			r.push(a);
			r.push(b);
			r.push("*");
		}

		return r;
	}

	static Stack divide(Stack a,Stack b)  // r = a/b
	{
		Stack r = new Stack();

		if (b.isOne())
		{
			r.push(a);
		}
		else
		{
			r.push(a);
			r.push(b);
			r.push("/");
		}

		return r;
	}

	static Stack pow(Stack a,Stack b)  // r = a^b
	{
		Stack r = new Stack();

		if (a.isOne())
		{
			r.push("1");
		}
		else
		if (b.isZero() && ! a.isZero())
		{
			r.push("1");
		}
		else
		if (b.isOne())
		{
			r.push(a);
		}
		else
		{
			r.push(a);
			r.push(b);
			r.push("^");
		}

		return r;
	}

	static Stack ln(Stack a)  // r = ln a
	{
		Stack r = new Stack();

		if (a.isOne())
		{
			r.push("0");
		}
		else
		{
			r.push(a);
			r.push("ln");
		}

		return r;
	}

	static Stack inverse(Stack a)  // r = 1/a
	{
		Stack r = new Stack();

		if (a.isOne())
		{
			r.push("1");
		}
		else
		{
			r.push("1");
			r.push(a);
			r.push("/");
		}

		return r;
	}

	static Stack negate(Stack a)  // r = -a
	{
		Stack r = new Stack();

		if (a.isZero())
		{
			r.push("0");
		}
		else
		{
			r.push(a);
			r.push("neg");
		}

		return r;
	}

	static Stack square(Stack a)  // r = a^2
	{
		Stack r = new Stack();

		if (a.isZero())
		{
			r.push("0");
		}
		else
		if (a.isOne())
		{
			r.push("1");
		}
		else
		{
			r.push(a);
			r.push("2");
			r.push("^");
		}

		return r;
	}


	static Stack sqrt(Stack a)  // r = sqrt a
	{
		Stack r = new Stack();

		if (a.isOne())
		{
			r.push("1");
		}
		else
		{
			r.push(a);
			r.push("sqrt");
		}

		return r;
	}

	static Stack abs(Stack a)  // r = sqrt a
	{
		Stack r = new Stack();

		r.push(a);
		r.push("abs");

		return r;
	}


	public Postfix derivative()
	{
		Stack rec = this.pf.copy();

		Stack der = derivative(rec);

		return new Postfix(der);
	}

	private static Stack derivative(Stack rec)
	{
		if (rec.isEmpty()) return null;

		Stack der=new Stack();
		Token tk = rec.pop();

		if (tk.isNumber())
		{
			if (tk.is("x"))
			{
				der.push("1");
			}
			else
			{
				der.push("0");
			}
		}
		else
		{
			if (tk.is("+"))
			{
				Stack r = derivative(rec);
				Stack l = derivative(rec);
				der.push(add(l,r));
			}
			else
			if (tk.is("-"))
			{
				Stack r = derivative(rec);
				Stack l = derivative(rec);

				der.push(subtract(l,r));
			}
			else
			if (tk.is("*"))
			{
				Stack v = sub(rec);
				Stack dv = derivative(v.copy());
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(add(multiply(v,du),multiply(u,dv)));

			}
			else
			if (tk.is("/"))
			{
				Stack v = sub(rec);
				Stack dv = derivative(v.copy());
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack two = new Stack();
				two.push("2");

				der.push(divide(subtract(multiply(v,du),multiply(u,dv)),pow(v,two)));
			}
			else
			if (tk.is("^"))
			{
				Stack h = sub(rec);
				Stack dh = derivative(h.copy());
				Stack g = sub(rec);
				Stack dg = derivative(g.copy());

				if (! h.isConstant())
				{
					der.push(multiply(add(multiply(h,divide(dg,g)),multiply(dh,ln(g))),pow(g,h)));
				}
				else
				{
					Postfix p = new Postfix(h);
					Stack h2 = new Stack();
					h2.push(""+Math.abs(p.evaluate(0)-1.0));
					if (p.evaluate(0)-1.0<0) h2.push("neg");
					der.push(multiply(multiply(h,pow(g,h2)),dg));
				}
			}
			else
			if (tk.is("neg"))
			{
				Stack d = derivative(sub(rec).copy());

				der.push(d);
				der.push("neg");
			}
			else
			if (tk.is("abs"))
			{
				Stack h = sub(rec);
				Stack dh = derivative(h.copy());

				der.push(multiply(divide(abs(h),h),dh));
			}
			else
			if (tk.is("exp"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				if (! u.isZero())
				{
					der.push(u);
					der.push("exp");
					der = multiply(der,du);
				}
				else
				{
					der.push("1");
				}
			}
			else
			if (tk.is("ln"))
			{
				Stack h = sub(rec);
				Stack dh = derivative(h.copy());

				der.push(divide(dh,h));
			}
			else
			if (tk.is("log"))
			{
				Stack h = sub(rec);
				Stack dh = derivative(h.copy());

				Stack ln10 = new Stack();
				ln10.push("10");
				ln10.push("ln");

				der.push(divide(dh,multiply(ln10,h)));
			}
			else
			if (tk.is("sin"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack p = new Stack();
				p.push(u);
				p.push("cos");

				der.push(multiply(du,p));
			}
			else
			if (tk.is("cos"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack p = new Stack();
				p.push(u);
				p.push("sin");
				p.push("neg");

				der.push(multiply(du,p));
			}
			else
			if (tk.is("tan"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("sec");
				der.push("2");
				der.push("^");

				der = multiply(du,der);
			}
			else
			if (tk.is("sec"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("sec");
				der.push(u);
				der.push("tan");
				der.push("*");

				der = multiply(du,der);
			}
			else
			if (tk.is("csc"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("csc");
				der.push(u);
				der.push("cot");
				der.push("*");
				der.push("neg");

				der = multiply(du,der);
			}
			else
			if (tk.is("cot"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("csc");
				der.push("2");
				der.push("^");
				der.push("neg");

				der = multiply(du,der);
			}
			else
			if (tk.is("sqrt"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(".5");
				der.push(sqrt(u));
				der.push("/");

				der = multiply(du,der);
			}
			else
			if (tk.is("asin"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(sqrt(subtract(one,square(u))))));
			}
			else
			if (tk.is("acos"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(negate(multiply(du,inverse(sqrt(subtract(one,square(u)))))));
			}
			else
			if (tk.is("atan"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(add(one,square(u)))));
			}
			else
			if (tk.is("acot"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(negate(multiply(du,inverse(add(one,square(u))))));
			}
			else
			if (tk.is("asec"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(multiply(square(u),sqrt(subtract(one,inverse(square(u))))))));
			}
			else
			if (tk.is("acsc"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(negate(multiply(du,inverse(multiply(square(u),sqrt(subtract(one,inverse(square(u)))))))));
			}
			else
			if (tk.is("sinh"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("cosh");

				der = multiply(du,der);
			}
			else
			if (tk.is("cosh"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("sinh");

				der = multiply(du,der);
			}
			else
			if (tk.is("tanh"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(u);
				der.push("tanh");

				der = multiply(du,subtract(one,square(der)));
			}
			else
			if (tk.is("coth"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(u);
				der.push("coth");

				der = multiply(du,subtract(one,square(der)));
			}
			else
			if (tk.is("csch"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("csch");
				der.push("neg");
				der.push(u);
				der.push("coth");
				der.push("*");

				der = multiply(der,du);
			}
			else
			if (tk.is("sech"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				der.push(u);
				der.push("sech");
				der.push("neg");
				der.push(u);
				der.push("tanh");
				der.push("*");

				der = multiply(der,du);
			}
			else
			if (tk.is("asinh"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(sqrt(add(one,square(u))))));
			}
			else
			if (tk.is("acosh"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(multiply(sqrt(subtract(u,one)),sqrt(add(u,one))))));
			}
			else
			if (tk.is("atanh"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(subtract(one,square(u)))));
			}
			else
			if (tk.is("acoth"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(multiply(du,inverse(subtract(one,square(u)))));
			}
			else
			if (tk.is("asech"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(negate(multiply(du,inverse(multiply(square(u),sqrt(subtract(inverse(square(u)),one)))))));
			}
			else
			if (tk.is("acsch"))
			{
				Stack u = sub(rec);
				Stack du = derivative(u.copy());

				Stack one = new Stack();
				one.push("1");

				der.push(negate(multiply(du,inverse(multiply(square(u),sqrt(add(inverse(square(u)),one)))))));
			}
			else
			if (tk.is("ddx"))
			{
				Stack d2u = derivative(derivative(sub(rec).copy()));

				der.push(d2u);
			}
			else
			{
				der.push("?");
			}

		}

		return der;

	}

	public static Stack sub(Stack s)
	{
		Stack ret = new Stack();

		if (!s.isEmpty())
		{
			Token tk=s.pop();

			if (tk.isNumber())
			{
				ret.push(tk);
			}
			else
			if (tk.is("+") || tk.is("-") || tk.is("*") || tk.is("/") || tk.is("^") )
			{
				//Binary Operations

				Stack oper2 = sub(s);
				Stack oper1 = sub(s);
				ret.push(oper1);
				ret.push(oper2);
				ret.push(tk);
			}
			else
			if(tk.is("sin") || tk.is("cos") || tk.is("tan") || tk.is("sec") || tk.is("csc") || tk.is("cot") ||
			   tk.is("asin") || tk.is("acos") || tk.is("atan") || tk.is("asec") || tk.is("acsc") || tk.is("acot") ||
			   tk.is("sinh") || tk.is("cosh") || tk.is("tanh") || tk.is("sech") || tk.is("csch") || tk.is("coth") ||
			   tk.is("asinh") || tk.is("acosh") || tk.is("atanh") || tk.is("asech") || tk.is("acsch") || tk.is("acoth") ||
			   tk.is("neg") || tk.is("abs") || tk.is("sqrt") || tk.is("log") || tk.is("ln") || tk.is("exp")	||
			   tk.is("ddx")
			   )
			{
				//Unary Operations

				Stack oper1 = sub(s);
				ret.push(oper1);
				ret.push(tk);
			}
		}

		return ret;
	}

	public double evaluate(double x)
	{
		double a=0;
		Stack rpf = new Stack();
		StackDoub ws = new StackDoub();
		double oper1, oper2;

		rpf=pf.flip();

		try			//to catches malformed inputs.
		{
			while (! rpf.isEmpty())
			{
				Token tk=rpf.pop();

				if (tk.isNumber())
				{
					if (tk.is("x"))
						ws.push(x);
					else
					if (tk.is("E"))
						ws.push(Math.E);
					else
					if (tk.is("PI"))
						ws.push(Math.PI);
					else
					if (tk.is("rnd"))
						ws.push(Math.random());
					else
						ws.push(Double.valueOf(tk.getContent()).doubleValue());
				}
				else
				if (tk.is("+"))
				{
					oper2=ws.pop();
					oper1=ws.pop();
					ws.push(oper1+oper2);
				}
				else
				if (tk.is("-"))
				{
					oper2=ws.pop();
					oper1=ws.pop();
					ws.push(oper1-oper2);
				}
				else
				if(tk.is("*"))
				{
					oper2=ws.pop();
					oper1=ws.pop();
					ws.push(oper1*oper2);
				}
				else
				if(tk.is("/"))
				{
					oper2=ws.pop();
					oper1=ws.pop();
					ws.push(oper1/oper2);
				}
				else
				if(tk.is("^"))
				{
					oper2=ws.pop();
					oper1=ws.pop();
					ws.push(Math.pow(oper1,oper2));
				}
				else
				if(tk.is("sin"))
					ws.push(Math.sin(ws.pop()));
				else
				if(tk.is("cos"))
					ws.push(Math.cos(ws.pop()));
				else
				if(tk.is("tan"))
					ws.push(Math.tan(ws.pop()));
				else
				if(tk.is("csc"))
					ws.push(1.0/Math.sin(ws.pop()));
				else
				if(tk.is("sec"))
					ws.push(1.0/Math.cos(ws.pop()));
				else
				if(tk.is("cot"))
					ws.push(1.0/Math.tan(ws.pop()));
				else
				if(tk.is("asinh"))
				{
					oper1=ws.pop();
					ws.push(Math.log(Math.sqrt(oper1*oper1+1)+oper1));
				}
				else
				if(tk.is("acosh"))
				{
					oper1=ws.pop();
					ws.push(Math.log(Math.sqrt(oper1*oper1-1)+oper1));
				}
				else
				if(tk.is("atanh"))
				{
					oper1=ws.pop();
					ws.push(Math.log((1+oper1)/(1-oper1))/2);
				}
				else
				if(tk.is("acsch"))
				{
					oper1=1/ws.pop();
					ws.push(Math.log(Math.sqrt(oper1*oper1+1)+oper1));
				}
				else
				if(tk.is("asech"))
				{
					oper1=1/ws.pop();
					ws.push(Math.log(Math.sqrt(oper1*oper1-1)+oper1));
				}
				else
				if(tk.is("acoth"))
				{
					oper1=1/ws.pop();
					ws.push(Math.log((1+oper1)/(1-oper1))/2);
				}
				else
				if(tk.is("sinh"))
				{
					oper1=ws.pop();
					ws.push((Math.exp(oper1)-Math.exp(-oper1))/2);
				}
				else
				if(tk.is("cosh"))
				{
					oper1=ws.pop();
					ws.push((Math.exp(oper1)+Math.exp(-oper1))/2);
				}
				else
				if(tk.is("tanh"))
				{
					oper1=ws.pop();
					ws.push((Math.exp(oper1)-Math.exp(-oper1))/(Math.exp(oper1)+Math.exp(-oper1)));
				}
				else
				if(tk.is("csch"))
				{
					oper1=ws.pop();
					ws.push(2.0/(Math.exp(oper1)-Math.exp(-oper1)));
				}
				else
				if(tk.is("sech"))
				{
					oper1=ws.pop();
					ws.push(2.0/(Math.exp(oper1)+Math.exp(-oper1)));
				}
				else
				if(tk.is("coth"))
				{
					oper1=ws.pop();
					ws.push((Math.exp(oper1)+Math.exp(-oper1))/(Math.exp(oper1)-Math.exp(-oper1)));
				}
				else
				if(tk.is("asin"))
					ws.push(Math.asin(ws.pop()));
				else
				if(tk.is("acos"))
					ws.push(Math.acos(ws.pop()));
				else
				if(tk.is("atan"))
					ws.push(Math.atan(ws.pop()));
				else
				if(tk.is("acsc"))
					ws.push(Math.asin(1/ws.pop()));
				else
				if(tk.is("asec"))
					ws.push(Math.acos(1/ws.pop()));
				else
				if(tk.is("acot"))
					ws.push(Math.PI/2-Math.atan(ws.pop()));
				else
				if(tk.is("neg"))
					ws.push(-ws.pop());
				else
				if(tk.is("abs"))
					ws.push(Math.abs(ws.pop()));
				else
				if(tk.is("sqrt"))
					ws.push(Math.sqrt(ws.pop()));
				else
				if(tk.is("log"))
					ws.push(Math.log(ws.pop())/Math.log(10));
				else
				if(tk.is("ln"))
					ws.push(Math.log(ws.pop()));
				else
				if(tk.is("exp"))
					ws.push(Math.exp(ws.pop()));
				else
				if(tk.is("sign"))
				{
				{
					oper1=ws.pop();
					if (oper1>0)
						ws.push(1);
					else
					if (oper1<0)
						ws.push(-1);
					else
						ws.push(0);
				}
				}
			}
		}
		catch (NullPointerException exception)
		{
			//Primative malformed input detection.
			ws = new StackDoub();
			ws.push(Double.MIN_VALUE);
		}

		if (! ws.isEmpty())
			a=ws.pop();
		else
			a=Double.NaN;
		return a;
	}

	public Stack getStack()
	{
		return pf.copy();
	}

	public boolean isConstant()
	{
		return isConstant;
	}

	public String infix()
	{
		if (pf!=null)
			return infix(pf.copy());

		return "?:?";
	}

	private static String infix(Stack s)
	{
		String str = "";

		Token tk = s.pop();

		if (tk.isBinary())
		{
			Stack b = sub(s);
			Stack a = sub(s);

			str = "("+infix(a)+tk.getContent()+infix(b)+")";
		}
		else
		if (tk.isNumber())
		{
			str = tk.getContent();
		}
		else
		{
			Stack a = sub(s);
			str = "("+tk.getContent()+" "+infix(a)+")";
		}

		return str;

	}
}

class Token
{
	//The order of these tokens matter and are often hard coded into the code.
	//take care not to disturb it.
	public static String[] validString =
	{
		"0","1","2","3","4","5","6","7","8","9",".",
		"(", ")", "^", "*", "/", "+", "-",
		"sinh", "cosh", "tanh", "csch", "sech", "coth",
		"asinh", "acosh", "atanh", "acsch", "asech", "acoth",
		"sin", "cos", "tan", "csc", "sec", "cot",
		"asin", "acos", "atan",	"acsc", "asec", "acot",
		"sqrt", "neg",
		"log", "abs", "ln", "exp", "sign",
		"ddx",
		"rnd","x","PI", "E"
	};

	public int index=-1;
	public String content="";
	boolean numerical;
	int length;
	
	
	Token(String str)
	{
		String t = str.trim().toLowerCase();
		numerical = false;

		for (int i = 0; i<validString.length && content.equals(""); i++)
		{
			if (t.startsWith(validString[i].toLowerCase()))
			{
				content = validString[i];
				length = content.length();

				index = i;

				if (i<11)
				{
					content = getValue(t);
					if (content.equals("?")) index =-1;
					numerical = (index!=-1);
				}
				else
				{
					numerical= (content.equals("x") ||
								content.equals("PI") ||
								content.equals("E") ||
								content.equals("rnd")
								);
				}
			}
		}

		if (index>=0 && index<11) index=0;	//Makes sure all numerical values have index 0
	}

	public boolean is(String a)
	{
		return getContent().equalsIgnoreCase(a);
	}

	public boolean isValid()
	{
		return index!=-1;
	}

	public boolean isNumber()
	{
		return (numerical);
	}

	public boolean isOperation()
	{
		return (precedence()>0);
	}

	public boolean isBinary()
	{
		return (precedence()==4 || precedence()==3 || precedence()==2);
	}

	public String getContent()
	{
		return content;
	}

	public float precedence()
	{
		float order=-1;
		Token tk = this;

		String tk_str = tk.getContent().toLowerCase();

		if (tk_str.equals("("))
		{
			order = 5;
		}
		else if (tk_str.equals(")"))
		{
			order = 6;
		}
		else if (tk_str.equals("+"))
		{
			order = 4;
		}
		else if (tk_str.equals("-"))
		{
			order = 4;
		}
		else if (tk_str.equals("*"))
		{
			order = 3;
		}
		else if (tk_str.equals("/"))
		{
			order = 3;
		}
		else if (tk_str.equals("^"))
		{
			order = 2;
		}
		else if (
			tk_str.equals("sin") ||		//Trig functions
			tk_str.equals("cos") ||
			tk_str.equals("tan") ||
			tk_str.equals("csc") ||
			tk_str.equals("sec") ||
			tk_str.equals("cot") ||

			tk_str.equals("asin") ||	//Inverse trig functions
			tk_str.equals("acos") ||
			tk_str.equals("atan") ||
			tk_str.equals("acsc") ||
			tk_str.equals("asec") ||
			tk_str.equals("acot") ||

			tk_str.equals("sinh") ||	//Hyperbolic trig functions
			tk_str.equals("cosh") ||
			tk_str.equals("tanh") ||
			tk_str.equals("csch") ||
			tk_str.equals("sech") ||
			tk_str.equals("coth") ||

			tk_str.equals("asinh") ||	//Inverse hyperbolic trig functions
			tk_str.equals("acosh") ||
			tk_str.equals("atanh") ||
			tk_str.equals("acsch") ||
			tk_str.equals("asech") ||
			tk_str.equals("acoth") ||

			tk_str.equals("ddx") ||		//Derivative

			tk_str.equals("neg") ||		//Negation
			tk_str.equals("sqrt") ||	//Square root
			tk_str.equals("exp") ||		//Exponential base e
			tk_str.equals("ln") ||		//Natural log
			tk_str.equals("log") ||		//Common log
			tk_str.equals("abs") ||		//Absolute value
			tk_str.equals("sign")		//sign
			)
		{
			order=1;
		}
		else if (tk.isNumber())
		{
			order = 0;
		}
		else
		{
			order =-1;
		}

		return order;
	}
	
	public String getValue(String s)
	{
		String ret = "";
		char[] input = s.toLowerCase().toCharArray();
		int state = 1;
		int i = 0;
		
		StackDoub st = new StackDoub();
		
		while (state<30)
		{
			st.push(state);
//			System.out.println(state+" "+input[i]);
			
			switch (state)
			{
				case 1:
					if (Character.isDigit(input[i]))
						state = 2;
					else
					if (input[i]=='.')
						state = 6;
					else
						state = 99;
					break;

				case 2:
					if (Character.isDigit(input[i]))
						state = 2;
					else
					if (input[i]=='e')
						state = 3;
					else
					if (input[i]=='.')
						state = 7;
					else
					{
						i = i - 1;
						state = 99;
					}
					break;

				case 3:
					if (Character.isDigit(input[i]))
						state = 5;
					else
					if (input[i]=='e')
					{
						i = i - 2;
						st.pop();
						state = 99;
					}
					else
					if (input[i]=='.')
						state = 8;
					else
					if (input[i]=='+' || input[i]=='-')
						state = 4;
					else
						state = 99;
					break;

				case 4:
					if (Character.isDigit(input[i]))
						state = 5;
					else
					if (input[i]=='e')
					{
						i = i - 3;
						st.pop();
						st.pop();
						state = 99;
					}
					else
					if (input[i]=='.')
					{
						i = i - 3;
						st.pop();
						st.pop();
						state = 99;
					}
					else
					if (input[i]=='+' || input[i]=='-')
					{
						i = i - 3;
						st.pop();
						st.pop();
						state = 99;
					}
					else
						state = 99;
					break;

				case 5:
					if (Character.isDigit(input[i]))
						state = 5;
					else
					if (input[i]=='.')
						state = 8;
					else
					{
						i = i - 1;
						state = 99;
					}
					break;

				case 6:
					if (Character.isDigit(input[i]))
						state = 7;
					else
					if (input[i]=='e')
						state = 8;
					else
					if (input[i]=='.')
						state = 8;
					else
					if (input[i]=='+' || input[i]=='-')
						state = 8;
					else
						state = 99;
					break;

				case 7:
					if (Character.isDigit(input[i]))
						state = 7;
					else
					if (input[i]=='e')
						state = 3;
					else
					if (input[i]=='.')
						state = 8;
					else
					{
						i = i - 1;
						state = 99;
					}
					break;

				case 8:
					break;
			}
			
			if (i==(input.length-1) || state==99)
			{
				if (state == 99)
					state = (int) st.pop();
			
				if (state!=2 && state!=7 && state != 5)
					state=8;
				else
					state = 99;
			}
			
			if (state == 8)
			{
				s = "?";
				i=0; 
				state = 99;
			}

			i++;
		}
		
		length = i;
		
		ret = s.substring(0,length);
		
		return ret;
		
	}

	public int length()
	{
		return length;
	}
}


class Stack
{
	StackCell top;

	Stack()
	{
		top = null;
	}

	public void clear()
	{
		top = null;
	}

	public boolean isEmpty()
	{
		return top == null;
	}

	public void push(Token s)
	{
		StackCell newCell =  new StackCell(s, top);
		top = newCell;
	}

	public void push(String s)
	{
		push(new Token(s));
	}

	public void push(Stack s)
	{
		Stack t = new Stack();
		if (s==null) return;
		while (! s.isEmpty())
			t.push(s.pop());

		while (! t.isEmpty())
		{
			push(t.peek());
			s.push(t.pop());
		}
	}

	public Token pop()
	{
		if (top!=null)
		{
			Token result = top.now;
			top = top.next;
			return result;
		}

		return new Token("?");
	}

	public Token peek()
	{
		return top.now;
	}
/*
	public static void showAll(Stack s0)
	{
		Stack s1 = new Stack();
		while (! s0.isEmpty())
		{
			Token tk = s0.pop();
			s1.push(tk);
		}

		while (! s1.isEmpty())
		{
			s0.push(s1.pop());
		}
	}
*/
	public String showAll()
	{
		Stack s1 = new Stack();
		String ret="";
		while (! this.isEmpty())
		{
			Token tk = this.pop();
			ret = tk.getContent()+" "+ret;
			s1.push(tk);
		}

		while (! s1.isEmpty())
		{
			this.push(s1.pop());
		}

		return ret;
	}

	public Stack copy ()
	{
		Stack s1 = new Stack();
		Stack s2 = new Stack();
		while (! this.isEmpty())
		{
			s1.push(this.pop());
		}
		while (! s1.isEmpty())
		{
			s2.push(new Token(s1.peek().getContent()));
			this.push(s1.pop());
		}

		return s2;
	}

	public Stack flip ()
	{
		Stack s1 = new Stack();
		Stack s2 = new Stack();
		while (! this.isEmpty())
		{
			s1.push(this.peek());
			s2.push(this.pop());
		}
		while (! s1.isEmpty())
		{
			this.push(s1.pop());
		}

		return s2;
	}

	public boolean isConstant()
	{
		Stack s1 = new Stack();
		boolean isconstant = true;

		while (! isEmpty())
		{
			Token tk = pop();
			s1.push(tk);

			if (tk.is("x")) isconstant = false;
		}

		while (! s1.isEmpty())
			push(s1.pop());

		return isconstant;
	}

	public boolean isZero()
	{
		Postfix p = new Postfix(this);
		return (this.isConstant() && Math.abs(p.evaluate(10))<1e-20);
	}

	public boolean isOne()
	{
		Postfix p = new Postfix(this);
		return (this.isConstant() && Math.abs(p.evaluate(10)-1)<1e-20);
	}

	public boolean isNegativeOne()
	{
		Postfix p = new Postfix(this);
		return (this.isConstant() && Math.abs(p.evaluate(10)+1)<1e-20);
	}

}

	class StackCell
	{
		Token now;
		StackCell next;

		StackCell(Token a, StackCell b)
		{
			now = a;
			next = b;
		}
	}



class StackDoub
{
	java.util.Stack stack;

	StackDoub()
	{
		stack = new java.util.Stack();
	}

	public boolean isEmpty()
	{
		return stack.empty();
	}

	public void push(double s)
	{
		stack.push(new Double(s));
	}

	public double pop()
	{
		return ((Double) stack.pop()).doubleValue();
	}

	public double peek()
	{
		return ((Double) stack.peek()).doubleValue();
	}
}

class PostfixList
{
	Vector List;
	int listSize;

	PostfixList()
	{
		List = new Vector();
		listSize=0;
	}

	void showInfix()
	{
		System.out.println("\n-----------------");
		System.out.println("Current Functions");
		System.out.println("-----------------");

		if (listSize==0)
		{
			System.out.println("None");
		}
		else
		{
			int i=0;
			for (int k=listSize; k>0; i++)
			{
				String s = get(i).infixStr.trim();

				if (s.length()>0)
				{
					System.out.println("F"+i+"(x)="+s);
					k--;
				}

			}
		}
	}

	void add(Postfix pf, Color c)
	{
		boolean placed=false;

		if (listSize<List.size())
		{
			int i=0;
			for (int k=listSize; ! placed; i++)
			{
				String s = get(i).infixStr.trim();

				if (s.length()==0)
				{
					placed=true;
					List.setElementAt(new PostfixListNode(pf, c),i);
				}
				else
				{
					k--;
				}
			}
		}
		else
		{
			List.addElement(new PostfixListNode(pf,c));

		}

		listSize++;
	}

	Postfix get(int ind)
	{
		Postfix temp = getNodeAt(ind).pf;

		if (temp==null)
			temp = new Postfix("");

		return temp;
	}

	PostfixListNode getNodeAt(int ind)
	{
		return (PostfixListNode) List.elementAt(ind);
	}

	void remove(int n)
	{
		try
		{
			List.setElementAt(new PostfixListNode(null, null),n);
			listSize--;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{ }
	}

	void deflagAll()
	{
		PostfixListNode temp = null;
		for (int i=0; i<List.size(); i++)
		{
			temp = getNodeAt(i);
			temp.drawn = false;
		}
	}
}

class PostfixListNode
{
	public Postfix pf;
	public boolean drawn;
	public Color color;

	PostfixListNode(Postfix now, Color color2)
	{
		pf=now;
		drawn=false;
		color=color2;
	}
}




class StringList
{
	Vector v;
	int cursor;

	StringList()
	{
		clear();
	}

	void resetCursor()
	{
		cursor = v.size()-1;
	}

	void clear()
	{
		v = new Vector();
		v.addElement("");
		resetCursor();
	}

	void add(String str)
	{
		v.insertElementAt(str, 0);

		if (v.size()>100)
			v.removeElementAt(100);
	}

	String getPrev()
	{
		cursor = (cursor+1)%v.size();

		return (String) v.elementAt(cursor);
	}

	String getNext()
	{
		cursor = (cursor-1+v.size())%v.size();

		return (String) v.elementAt(cursor);
	}

}


class Frame extends java.awt.Frame implements WindowListener
{
	Frame()
	{
		super();
	}

	Frame(String title)
	{
		super(title);
	}


	public void windowActivated(WindowEvent e) {}

	public void windowClosed(WindowEvent e) {}

	public void windowClosing(WindowEvent e)
	{
		dispose();
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e) {}

	public void windowDeiconified(WindowEvent e) {}

	public void windowIconified(WindowEvent e) {}

	public void windowOpened(WindowEvent e) {}
}


