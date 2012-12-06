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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.gcalc.plugin.properties.GraphProperties;



/**
 * @author jkim
 */
public class GraphDimensionPanel extends JPanel implements ActionListener
{
    private GraphProperties properties;
    private JTextField width, height;
    private JButton apply, reset;

    public GraphDimensionPanel(GraphProperties gp)
    {
        super();
        properties = gp;
        
        Dimension dimension = (Dimension) properties.get(GraphProperties.SCREEN_DIMENSION);
    
        width = new JTextField(""+dimension.width);
        height = new JTextField(""+dimension.height);
        
        JPanel dimBox = new JPanel(new GridLayout(2,2));
        
        dimBox.add(new JLabel("Width"));
        dimBox.add(new JLabel("Height"));
        dimBox.add(width);
        dimBox.add(height);
        
        Box buttonBox = Box.createHorizontalBox();
      //  buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(apply = new JButton("Apply"));
        buttonBox.add(reset = new JButton("Restore"));
        
        apply.addActionListener(this);
        reset.addActionListener(this);
        width.addActionListener(this);
        height.addActionListener(this);
        
        Box mainBox = Box.createVerticalBox();
        mainBox.add(dimBox);
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(buttonBox);
             
        this.add(mainBox);
    }
    
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if (src==width) {
            height.requestFocus();
            return;
        }
        
        if (src==reset)
            reset();

        if (src==apply)
            apply();

        
    }
    
    private void reset()
    {
        Dimension dimension = (Dimension) properties.get(GraphProperties.SCREEN_DIMENSION);
        width.setText(""+dimension.width);
        height.setText(""+dimension.height);
    }
    
    private void apply()
    {
   //     Dimension dimension = (Dimension) properties.get(GraphProperties.SCREEN_DIMENSION);
      
        int w, h;
        
        String wstr = width.getText();
        String hstr = height.getText();
        
        try {
            w = Integer.parseInt(wstr);
        }
        catch (NumberFormatException e)
        {
            width.setSelectionStart(0);
            width.setSelectionEnd(wstr.length());
            width.requestFocus();
            return;
        }
        try {
            h = Integer.parseInt(hstr);
        }
        catch (NumberFormatException e)
        {
            height.setSelectionStart(0);
            height.setSelectionEnd(hstr.length());
            height.requestFocus();
            return;
        }
        
        if (w>=150&& h>=150 && w*h<1e6)
            properties.put(GraphProperties.SCREEN_DIMENSION, new Dimension(w, h));
    }
}

