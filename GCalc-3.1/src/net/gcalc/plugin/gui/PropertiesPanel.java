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

Email: jiho@gcalc.net
Web: http://gcalc.net

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/

package net.gcalc.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @author jkim
 *
 */
public class PropertiesPanel extends JPanel implements ActionListener
{
    private JTabbedPane tabbedPane;
    private AbstractCartesianGraphPlugin plugin;
    
    
    public PropertiesPanel(AbstractCartesianGraphPlugin s)
    {
    	super(new BorderLayout());
    	
    	JButton button = new JButton("Hide Properties");
        plugin = s;
        this.add(tabbedPane = new JTabbedPane(), BorderLayout.CENTER);
        
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(button);
        
        this.add(box, BorderLayout.SOUTH);
        button.addActionListener(this);
     }
    
    public void addTab(String title, Component c)
    {
        tabbedPane.add(c,title);
    }
    
    public void actionPerformed(ActionEvent e) {
    		plugin.showPropertiesPanel(false);
    }
}