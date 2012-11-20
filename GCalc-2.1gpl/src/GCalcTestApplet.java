import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.EmptyStackException;

public class GCalcTestApplet extends Applet 
{

    Frame gcalc;
    //    Button b = new Button("Click to Start GCalc");

    public void init()
    {
	this.setLayout(new BorderLayout());
	//	add("Center",b);

	//	b.addActionListener(this);
    }

    public void start()
    {
	gcalc = new GCalcTest();
    }

    public void destroy()
    {
	gcalc=null;
    }
}

