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

Email: jiho@gcalcul.us
Web: http://gcalcul.us

*/
import java.awt.*;
import java.util.*;
import java.awt.event.*;

abstract class ColorChooser extends JiDialog implements MouseListener, MouseMotionListener, ActionListener
{
    Color original;

    ColorCanvas ccOrig;
    ColorCanvas ccNew;
    ColorCanvas ccTemp;
    Button cancel, done;

    protected ColorChooser(Frame f, String s, Color c)
    {
	super(f, "Color Chooser", true);
	init(c);
    }

    protected ColorChooser(Dialog d, String s, Color c)
    {
	super(d, "Color Chooser", true);
	init(c);
    }

    private void init(Color c)
    {
	setLayout(new BorderLayout(5,5));

	original = c;

    }

    private Color getColor()
    {
	return original;
    }

    public void actionPerformed(ActionEvent e)
    {
	Object src = e.getSource();

	if (src==cancel) {
	    dispose();
	}
	else if (src==done) {
	    original = ccNew.getColor();
	    dispose();
	}

    }

    public void mouseEntered(MouseEvent e)
    {
	mouseMoved(e);
    }

    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    protected void buttonPanel()
    {
	Panel buttonPanel = new Panel(new FlowLayout());
	buttonPanel.add(done = new Button("Done"));
	buttonPanel.add(cancel = new Button("Cancel"));

	done.addActionListener(this);
	cancel.addActionListener(this);

	add("South", buttonPanel);

    }

    protected void ccPanel()
    {
	Panel cc = new Panel(new GridLayout(3,2,0,10));
	ccOrig = new ColorCanvas(original,50,33);
	ccNew = new ColorCanvas(original,50,33);
	ccTemp = new ColorCanvas(original,50,33);
	cc.add(new Label("Old", Label.RIGHT));
	cc.add(ccOrig);
	cc.add(new Panel());
	cc.add(ccTemp);
	cc.add(new Label("New", Label.RIGHT));
	cc.add(ccNew);

	ccOrig.addMouseListener(this);
	ccOrig.addMouseMotionListener(this);

	add("East", cc);
    }

    public static Color getColor(Frame f, Color c)
    {
	return getColor(f,c,true);
    }

    public static Color getColor(Frame f, Color c, boolean palette)
    {
	ColorChooser CC = null;

	if (palette)
	    CC = new PaletteColorChooser(f, c);
	else
	    CC = new SwatchColorChooser(f, c);

	return CC.getColor();
    }

    public static Color getColor(Dialog f, Color c)
    {
	return getColor(f,c,true);
    }

    public static Color getColor(Dialog f, Color c, boolean palette)
    {
	ColorChooser CC = null;

	if (palette)
	    CC = new PaletteColorChooser(f, c);
	else
	    CC = new SwatchColorChooser(f, c);

	return CC.getColor();
    }

}

class SwatchColorChooser extends ColorChooser implements AdjustmentListener
{
    Scrollbar sb;
    HSBCanvas hsbCanvas;
    ColorCanvas bottom, top;

    public SwatchColorChooser(Frame f, Color c)
    {
	super(f, "Color Chooser", c);
	init();
    }

    public SwatchColorChooser(Dialog d, Color c)
    {
	super(d, "Color Chooser", c);
	init();
    }

    private void init()
    {
	Panel hsb = new Panel(new BorderLayout(5,5));
	Panel sbPanel = new Panel(new BorderLayout());

	hsbCanvas = new HSBCanvas(original,300,200);
	hsbCanvas.drawColor();

	sb = new Scrollbar(Scrollbar.HORIZONTAL, (int) (hsbCanvas.getSaturation()*100), 1, 0, 101);
	sb.addAdjustmentListener(this);
	sb.setBlockIncrement(10);
	sb.setUnitIncrement(1);

	sbPanel.add("Center", sb);
	sbPanel.add("East", top=new ColorCanvas(fullSaturation(original),10,10));
	sbPanel.add("West", bottom=new ColorCanvas(zeroSaturation(original),10,10));

	hsb.add("Center", hsbCanvas);
	hsb.add("South", sbPanel);

	add("Center", hsb);

	hsbCanvas.addMouseListener(this);
	hsbCanvas.addMouseMotionListener(this);

	buttonPanel();
	ccPanel();

	pack();

	setVisible(true);
    }

    private Color zeroSaturation(Color c)
    {
	float[] f = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	return Color.getHSBColor(f[0], 0, f[2]);
    }

    private Color fullSaturation(Color c)
    {
	float[] f = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	return Color.getHSBColor(f[0], 1, f[2]);
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
	Object src = e.getSource();

	if (src!=sb)
	    return;

	int val = sb.getValue();

	float s = val/100f;

	Color c = ccNew.getColor();
	float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	c=Color.getHSBColor(hsb[0], s, hsb[2]);
	ccTemp.setColor(c);
	ccNew.setColor(c);
	ccTemp.repaint();

	hsbCanvas.setSaturation(s);
    }

    public void mouseEntered(MouseEvent e)
    {
	mouseMoved(e);
    }

    public void mouseExited(MouseEvent e)
    {
	Object src = e.getSource();

	if (src==hsbCanvas) {
	    ccTemp.setColor(null);
	}
	else if (src==ccOrig) {
	    ccTemp.setColor(null);
	}

    }

    public void mouseMoved(MouseEvent e)
    {
	Object src = e.getSource();

	if (src==hsbCanvas) {
	    ccTemp.setColor( hsbCanvas.getColorAt(e.getX(), e.getY()));

	}
	else if (src==ccOrig) {
	    ccTemp.setColor(ccOrig.getColor());
	}

    }

    public void mouseClicked(MouseEvent e)
    {
	Object src = e.getSource();

	if (src==hsbCanvas) {
	    ccNew.setColor( hsbCanvas.getColorAt(e.getX(), e.getY()));
	}
	else if (src==ccOrig) {
	    ccNew.setColor( ccOrig.getColor());
	}

	top.setColor(fullSaturation(ccNew.getColor()));
	bottom.setColor(zeroSaturation(ccNew.getColor()));
    }

}

class PaletteColorChooser extends ColorChooser
{
    ColorCanvas[] preCC;
    int currentOld=-1;

    static int current;
    static Color[] colorArray = new Color[10];

    static void setColorArray(Color[] c)
    {
	colorArray = c;
	current=0;
    }

    static Color getColor()
    {
	return colorArray[current];
    }

    static void advanceColorIndex()
    {
	current= (current+7)%colorArray.length;
    }

    PaletteColorChooser(Frame f, Color c)
    {
	super(f, "Palette Color Chooser", c);
	init();
    }

    PaletteColorChooser(Dialog d, Color c)
    {
	super(d, "Palette Color Chooser", c);
	init();
    }

    public void init()
    {
	Color c;
	Panel prePanel = new Panel(new GridLayout(4, colorArray.length/4, 10,10));
	preCC = new ColorCanvas[colorArray.length];
	for (int i=0; i<colorArray.length; i++) {
	    c = colorArray[i];

	    if (c==null)
		c = Color.white;

	    preCC[i] = new ColorCanvas(c,50,25);
	    preCC[i].addMouseListener(this);
	    preCC[i].addMouseMotionListener(this);
	    prePanel.add(preCC[i]);

	}

	ccPanel();
	buttonPanel();

	add("Center", prePanel);

	pack();
	setVisible(true);

	currentOld = current;
    }

    public void mouseEntered(MouseEvent e)
    {
	mouseMoved(e);
    }

    public void mouseMoved(MouseEvent e)
    {
	Object src = e.getSource();

	for (int i=0; i<preCC.length; i++) {
	    if (src==preCC[i]) {
		ccTemp.setColor(preCC[i].getColor());
		return;
	    }

	}

	if (src==ccOrig) {
	    ccTemp.setColor(ccOrig.getColor());
	}

    }

    public void mouseClicked(MouseEvent e)
    {
	Object src = e.getSource();

	switch(e.getClickCount()) {
	case 1:
	    singleClicked(src);
	    break;

	case 2:
	    doubleClicked(src);
	    break;

	default:

	}
    }

    private void singleClicked(Object src)
    {
	for (int i=0; i<preCC.length; i++) {
	    if (src==preCC[i]) {
		ccNew.setColor(preCC[i].getColor());
		current = i;

		return;
	    }
	}

	if (src==ccOrig) {
	    ccNew.setColor(ccOrig.getColor());
	    current = currentOld;

	}
    }

    private void doubleClicked(Object src)
    {
	for (int i=0; i<preCC.length; i++) {
	    if (src==preCC[i]) {
		preCC[i].setColor(ColorChooser.getColor(this,preCC[i].getColor(), false));
		colorArray[i] = preCC[i].getColor();
		singleClicked(src);
		return;
	    }

	}

    }
}

