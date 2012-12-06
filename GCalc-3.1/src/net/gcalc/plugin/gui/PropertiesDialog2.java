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

package net.gcalc.plugin.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JTabbedPane;

import net.gcalc.calc.gui.ShutdownDialog;
import net.gcalc.calc.gui.ShutdownWindowAdapter;
import net.gcalc.calc.main.AbstractPlugin;

/**
 * @author jkim
 *
 */
public class PropertiesDialog2 extends ShutdownDialog implements ComponentListener
{
    private JTabbedPane tabbedPane;
    
    public PropertiesDialog2(AbstractPlugin s)
    {
        super(s, s.getTitle(), false);
        this.getContentPane().add(tabbedPane = new JTabbedPane());
        
        this.addWindowListener(new ShutdownWindowAdapter(this));
        moveToOwnerRight();
        this.setResizable(false);
    }
    
    public void addTab(String title, Component c)
    {
        tabbedPane.add(c,title);
    }
    
    public void moveToOwnerRight()
    {
        Rectangle R = this.getOwner().getBounds();
        
        this.setLocation(R.x+R.width+1, R.y);
    }
    
    public void componentMoved(ComponentEvent e) {
     
        
//        System.out.println(e);
    }
    public void componentResized(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {
        componentMoved(e);
      // moveToOwnerRight();
      //  this.setVisible(true);
        
    }

}
