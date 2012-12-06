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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Zoom;



/**
 * @author jkim
 */
public class ZoomPanel extends JPanel implements ActionListener
{
    private GraphProperties properties;
    private JComboBox cb;

    public ZoomPanel(GraphProperties gp)
    {
        super();
        properties = gp;

        cb = new JComboBox(getZooms());
        JButton button = new JButton("Apply");

        Box box = Box.createHorizontalBox();

        box.add(cb);
        box.add(button);
        button.addActionListener(this);
        this.add(box);
    }

    public void actionPerformed(ActionEvent e)
    {
        int n = cb.getSelectedIndex();
        properties.put(GraphProperties.VIEW,
                ((Zoom) getZooms().elementAt(n)).getView());
    }
    
    public Vector getZooms()
    {
        return (Vector) properties.get(GraphProperties.ZOOMS);
    }
    
   

}

