import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.EmptyStackException;

public class GCalcApplet2 extends Applet implements ActionListener
{
    String notice = "GCalc\nVersion 2.1\nCopyright 1999-2003 Jiho Kim\n";
    GCalc gcalc;
    Button b = new Button("Click to Start GCalc");

    public void init()
    {
	this.setLayout(new BorderLayout());
	add("Center",b);
	b.addActionListener(this);
    }

    public void start()
    {
	//	gcalc = new GCalc();
    }

    public void destroy()
    {
	gcalc=null;
	b = null;
    }

    public void actionPerformed(ActionEvent e)
    {
	Object o = e.getSource();

	if (o==b) {
	    gcalc = new GCalc();

	}
    }
}

