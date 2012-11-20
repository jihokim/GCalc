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


package net.gcalc.calc.gui;


import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Implementation of a frame that shutdown when it receives a window
 * closing event.
 * 
 * @author jkim
 */
public class BasicFrame extends JFrame implements Shutdown
{
	private boolean shutdown = false;
	
    public BasicFrame(String s)
    {
        super(s);
        this.addWindowListener(new ShutdownWindowAdapter(this));
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public void shutdown()
    {
    	//makes sure that there is only one call to this method at a time.
    	if (shutdown)
    		return;
    	
    	shutdown = true;
        this.toFront();
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure?",
                "Quitting...", JOptionPane.YES_NO_OPTION);

		if (ans == JOptionPane.YES_OPTION)
			System.exit(0);
		else {
			this.setVisible(true);
			this.requestFocus();
		}
		shutdown = false;
    }
}

