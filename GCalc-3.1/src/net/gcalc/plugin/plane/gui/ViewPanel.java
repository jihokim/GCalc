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

Email: jiho@gcalcul.us
Web: http://gcalcul.us

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.plugin.plane.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.plugin.plane.graph.CartesianGraph;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Range;
import net.gcalc.plugin.properties.View;
import net.gcalc.plugin.properties.Zoom;



/**
 * @author jkim
 */
public class ViewPanel extends JPanel implements ActionListener
{
    private GraphProperties properties;
    private ViewTextField[][] textfields;

    public ViewPanel(GraphProperties gp) {
    	this(gp, true);
    }
    
    public ViewPanel(GraphProperties gp, boolean zooms)
    {
        super();
        properties = gp;

        //Create "Range" portion of the view panel
        
        View view = gp.getViewProperty(GraphProperties.VIEW);

        int n = view.getDimension();
        String[] varnames = view.getVarNames();

        JPanel rangePanel = new JPanel(new GridLayout(3*n, 2));
        
        textfields = new ViewTextField[n][3];
        
        for (int i = 0; i<n; i++)
        {
            rangePanel.add(new JLabel(varnames[i]+" Min"));
            rangePanel.add(textfields[i][0]=this.makeViewTextField(i, Range.MIN));
            rangePanel.add(new JLabel(varnames[i]+" Max"));
            rangePanel.add(textfields[i][1]=this.makeViewTextField(i, Range.MAX));
            rangePanel.add(new JLabel(varnames[i]+" Scale"));
            rangePanel.add(textfields[i][2]=this.makeViewTextField(i, Range.SCALE));
        }
        
        JButton set = new JButton("Set");
        JButton revert = new JButton("Restore");
             Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(set);
        buttonBox.add(revert);
        set.addActionListener(this);

        for (int i = 0; i<n; i++)
              for (int j=0; j<3; j++)
                  revert.addActionListener(textfields[i][j]);
        
        Box rangeBox = Box.createVerticalBox();
        rangeBox.add(rangePanel);
        rangeBox.add(buttonBox);
        
        
         Box box = Box.createVerticalBox();
        
        box.add(SwingGUI.wrapTitledBorder(rangeBox, "Ranges"));
     
        
        
        if (zooms) {
        //make the zoom portion
        if (properties.get(GraphProperties.ZOOMS)!=null)
        	box.add(SwingGUI.wrapTitledBorder(new ZoomPanel(properties), "Predefined Zooms"));
        
        box.add(SwingGUI.wrapTitledBorder(new ZoomIOPanel(properties), "Zoom In/Out"));
        
        }
        box.add(Box.createVerticalGlue());
        
        this.add(box);
        
        properties.setPropertyChanged(GraphProperties.VIEW);
    }

    private ViewTextField makeViewTextField(int var, int field)
    {
        ViewTextField tf = new ViewTextField(properties, var, field);
        tf.addActionListener(this);
        return tf;
    }
    
    public void actionPerformed(ActionEvent event)
    {
        double[][] values = new double[textfields.length][3];
        Range[] ranges = new Range[textfields.length];
        
        //check for bad values.
        for (int i=textfields.length-1; i>=0; i--) {
            for (int j=0; j<3; j++) {
                values[i][j]=textfields[i][j].getValue();
                if (Double.isNaN(values[i][j])) {
                    textfields[i][j].requestFocus();
                    return;
                }
            }
            
            //construct range to make sure the numbers make sense
            //together.
            try {
                ranges[i] = new Range(values[i][0], values[i][1], values[i][2]);
            }
            catch (IllegalArgumentException e)
            {
                textfields[i][0].requestFocus();
                return;
            }
        }
        
        //all checks pass, update properties.
        properties.put(GraphProperties.VIEW, new View(ranges));
    }
}

class ZoomIOPanel extends JPanel implements ActionListener
{
	private JComboBox factor;
	private JButton in, out;
	private GraphProperties properties;
	
	public ZoomIOPanel(GraphProperties gp) {
		super(new BorderLayout(5,5));
		
		properties = gp;
		in = new JButton("Zoom in");
		out = new JButton("Zoom out");
		factor = new JComboBox(new String[] {"1000000:1","1000:1","100:1","10:1","5:1", "3:1", "2:1", "4:3", "3:2"});
		factor.setSelectedIndex(6);
		
		
		JPanel buttons = new JPanel(new GridLayout(1,2));
		buttons.add(in);
		buttons.add(out);
		in.addActionListener(this);
		out.addActionListener(this);
		
		add(factor, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		int[] a = getFactor();
		
		if (src==out) {
			zoom(a[1], a[0]);
		}
		else if (src==in) {
			zoom(a[0], a[1]);
		}
	}
	
	private int[] getFactor() {
		String item = (String) factor.getSelectedItem();
		StringTokenizer st = new StringTokenizer(item, ":");
		int[] a = new int[2];
		
		a[0] = Integer.parseInt(st.nextToken());
		a[1] = Integer.parseInt(st.nextToken());
	
		return a;
	}
	
	private void zoom(int s, int d) {
		CartesianGraph graph = (CartesianGraph) properties.get(GraphProperties.GRAPH_CANVAS);
		
		ZoomIO zio = new ZoomIO(s,d);
		
		properties.put(GraphProperties.VIEW, graph.zoomWrapper(zio).getView());
	}
	
	 public class ZoomIO extends Zoom {
	 	private int s,d;
	 	
	 	public ZoomIO(int a, int b) {
	 		s = a;
	 		d = b;
	 	}

	 	public View getView() {
			View view = properties.getViewProperty(GraphProperties.VIEW);
			Range x = view.getRange(0);
			Range y = view.getRange(1);
			
			
			double r = x.getWidth()*d/s/2;
			Range x2 = new Range(x.getCenter()-r, x.getCenter()+r, x.getScale());

			r = y.getWidth()*d/s/2;
			Range y2 = new Range(y.getCenter()-r, y.getCenter()+r, y.getScale());
			
			return new View(x2,y2);
		}
	}
}

class ViewTextField extends JTextField implements Observer, FocusListener, ActionListener
{
    private GraphProperties properties;
    private int variable;
    private int field;

    public ViewTextField(GraphProperties gp, int var, int field)
    {
        super("", 7);
        properties = gp;
        variable = var;
        this.field = field;
        gp.addObserver(this);
        this.addFocusListener(this);
    }

    public void update(Observable o, Object obj)
    {
        if (obj==null)
            return;
        
        if (obj.equals(GraphProperties.VIEW))
        {
            updateValue();
        }

    }
    
    public void actionPerformed(ActionEvent e)
    {
        updateValue();
    }

    public void updateValue()
    {
        View view = properties.getViewProperty(GraphProperties.VIEW);
        Range range = view.getRange(variable);
        
        double val = 0;

        switch (field)
        {
        case Range.MIN:
            val = range.getMin();
            break;

        case Range.MAX:
            val = range.getMax();
            break;

        case Range.SCALE:
            val = range.getScale();

            break;

        default:
        //do nothing
        }

        this.setText(""+val);
        this.setCaretPosition(0);
    }

    public double getValue()
    {
        double val = 0;

        try
        {
            val = Double.parseDouble(this.getText());
        }
        catch (NumberFormatException exception)
        {
           val= Double.NaN;
        }

        return val;
    }
    
    public void focusLost(FocusEvent f) {}

    public void focusGained(FocusEvent f)
    {
        setCaretPosition(this.getText().length());
        select(0,this.getText().length());
    }    
}

