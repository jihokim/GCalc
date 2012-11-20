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
import java.util.*;
import java.awt.event.*;

/**
 * Dialog that implements the WindowListener interface.  This is a
 * convenience class so that I don't have to include the same code in
 * all the dialog classes.
 */
abstract public class JiDialog extends Dialog implements WindowListener
{
    private Window parent;

    /**
     * Dialog Constructor
     *
     * @param f Parent frame
     * @param s Title
     * @param b modal
     */
    JiDialog(Frame f, String s, boolean b)
    {
	super(f);
	init(f, s,b);
    }

    /**
     * Dialog Constructor.  
     *
     * The implementation here is a hack given the fact that the Java
     * 1.1 API doesn't let Dialog be children of other dialogs.
     *
     * @param d Parent Dialog
     * @param s Title
     * @param b modal
     */
    JiDialog(Dialog d, String s, boolean b)
    {
	super((Frame) d.getParent());
	init(d, s,b);
    }

    /**
     * Does this commont to all constructors
     *
     * @param w the parent Window
     * @param s Title
     * @param b modal
     */
    private void init(Window w, String s, boolean b)
    {
	parent = w;

	setTitle(s);
	setModal(b);

	setBackground(parent.getBackground());	
	this.addWindowListener(this);
	setLayout(new BorderLayout());
    }

    public void pack()
    {
	super.pack();
	Point p = parent.getLocation();
	Dimension d = parent.getSize();
	Dimension d2 = this.getSize();

	int xpos = p.x+(d.width-d2.width)/2;
	int ypos = p.y+(d.height-d2.height)/2;

	setLocation(xpos, ypos);
    }

    /**
     * Disposes the dialog object when the dialog is closing.
     */
    public void windowClosing(WindowEvent e)
    {
	dispose();
    }

    /**
     * Does nothing.  This method should be implemented in a subclass
     * if reasonable.
     */
    public void windowActivated(WindowEvent e) {}

    /**
     * Does nothing.  This method should be implemented in a subclass
     * if reasonable.
     */
    public void windowClosed(WindowEvent e) {}

    /**
     * Does nothing.  This method should be implemented in a subclass
     * if reasonable.
     */
    public void windowDeactivated(WindowEvent e) {}

    /**
     * Does nothing.  This method should be implemented in a subclass
     * if reasonable.
     */
    public void windowDeiconified(WindowEvent e) {}

    /**
     * Does nothing.  This method should be implemented in a subclass
     * if reasonable.
     */
    public void windowIconified(WindowEvent e) {}

    /**
     * Does nothing.  This method should be implemented in a subclass
     * if reasonable.
     */
    public void windowOpened(WindowEvent e) {}
}

class FunctionModifyDialog extends JiDialog implements ActionListener
{
    Postfix pf;
    Color color;

    PostfixList pfl;
    PostfixListNode pfln;
    int idx;

    TextField input;
    ColorLabel cl;

    Button cancel, done, revert;

    FunctionModifyDialog(Dialog d, PostfixList pfl, int i)
    {
	super(d, "Modifying Function", true);

	setLayout(new BorderLayout(5,5));

	Panel p = new Panel(new GridLayout(2,2,3,3));

	pfln = pfl.getNodeAt(i);
	pf = pfln.pf;
	color=pfln.color;

	Font normalFont = new Font("Monospaced", Font.PLAIN, 12);
	input = new TextField(pf.infixString());
	cl = new ColorLabel(d, color);
	input.setBackground(Color.white);
	input.setFont(normalFont);

	p.add(new Label("Function", Label.RIGHT));
	p.add(input);
	p.add(new Label("Color", Label.RIGHT));
	p.add(cl);

	add("Center", p);

	Panel buttonPanel = new Panel(new FlowLayout());
	buttonPanel.add(revert = new Button("Revert"));
	buttonPanel.add(done = new Button("Done"));
	buttonPanel.add(cancel = new Button("Cancel"));

	done.addActionListener(this);
	cancel.addActionListener(this);
	revert.addActionListener(this);

	add("South", buttonPanel);

	pack();
	setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
	Object src = e.getSource();

	if (src==cancel) {
	    input.setText(pf.infixString());
	    cl.setColor(color);
	    dispose();
	}
	else if (src==done) {

	    Postfix temp = new Postfix(input.getText());
	    
	    if (temp.isValid()) {
		pfln.pf = temp;
		pfln.color = cl.getColor();
		dispose();
	    }
	    else {
		input.setBackground(color.orange.brighter());
	    }

	}
	else if (src==revert) {
	    input.setText(pf.infixString());
	    cl.setColor(color);
	    input.setBackground(Color.white);

	}
    }
}


class ImageDialog extends JiDialog
{
    Image image;

    ImageDialog(Frame f, Image i, int x, int y)
    {
	super(f, "GCalc Graph", true);

	add(new ImageCanvas(i, x, y));
	pack();
	setResizable(false);
	this.addWindowListener(this);

	setVisible(true);
    }

}


class ConfirmDialog extends JiDialog implements ActionListener
{
    Button yes, no;
    boolean answer;

    private ConfirmDialog(Frame f, String question, boolean affirm)
    {
	super(f, "Confirm Dialog", true);

	yes = new Button("Yes");
	no = new Button("No");

	Panel buttonPanel = new Panel(new FlowLayout());
	buttonPanel.add(yes);
	buttonPanel.add(no);

	yes.addActionListener(this);
	no.addActionListener(this);


	Label l = new Label(question, Label.CENTER);
	l.setFont(new Font("SansSerif", Font.ITALIC, 14)); 

 	add("South", buttonPanel);
	add("Center", l);


	pack();
	setVisible(true);

	if (affirm) 
	    yes.requestFocus();
       	else
	    no.requestFocus();
    }


    public void actionPerformed(ActionEvent e)
    {
	Object src = e.getSource();
	
	if (src==yes)
	    answer= true;
	else if (src==no)
	    answer= false;
	else
	    return;

	dispose();

    }

    static boolean getConfirmation(Frame f, String s, boolean defaultAnswer)
    {
	ConfirmDialog d = new ConfirmDialog(f,s, defaultAnswer);

	return d.answer;
    }

}

class AboutDialog extends JiDialog
{

    public AboutDialog(Frame c)
    {
	super(c, "About GCalc", true);
	
	this.add("North", new Label("GCalc Version 2.0"));
	this.add("Center", new Label("(C) Copyright 1998-2003 Jiho Kim"));

	pack();
	show();
    }




}
