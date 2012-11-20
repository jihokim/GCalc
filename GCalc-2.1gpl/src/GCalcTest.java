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

import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;

public class GCalcTest extends Frame implements ActionListener, WindowListener
{
    TextField tf = new TextField("Type the expression (in x) in here... and press enter.");
    TextArea ta = new TextArea("", 10, 10, TextArea.SCROLLBARS_VERTICAL_ONLY);
    Label l = new Label();

    String[] c = {"Differentiation", "Parenthesized Infix", "Postfix"};
    Choice choose = new Choice();

    public GCalcTest()
    {
	super("GCalc Test");
	init();
	addWindowListener(this);
	pack();
	setResizable(false);
	setVisible(true);
    }

    
    public void init()
    {
	
	for (int i=0; i<c.length; i++)
	    choose.add(c[i]);


	this.setLayout(new BorderLayout());
	add("North", tf);
	add("Center",ta);
	add("South", choose);

	ta.setEditable(false);
	
	tf.addActionListener(this);
    }

    public void start()
    {
	tf.requestFocus();
    }

    public void actionPerformed(ActionEvent e)
    {
	Object o = e.getSource();

	if (o==tf) {
	    
	    l.setText("");
	    ta.setText("");

	    try {
		String s = tf.getText();
		String t = "";
		Postfix p = new Postfix(s);

		switch(choose.getSelectedIndex()) {
		case 0:
		    Postfix d = p.derivative();
		    t = d.infix();
		    break;

		case 1:
		    t = p.infix();
		    break;

		case 2:
		    t = p.toPostfixString();
		    break;
		
		}


		ta.setText(t);
	    }
	    catch (Exception exception) {
		l.setForeground(Color.red);
		l.setText("Error");
	    }
		
	}
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


}

