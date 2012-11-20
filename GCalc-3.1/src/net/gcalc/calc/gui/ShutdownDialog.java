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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JDialog;

/**
 * @author jkim
 *
 */
public class ShutdownDialog extends JDialog implements Shutdown 
{
    public ShutdownDialog(Dialog arg0, String arg1, boolean arg2)
    {
        super(arg0, arg1, arg2);
        this.addWindowListener(new ShutdownWindowAdapter(this));
    }
   
    
    public ShutdownDialog(Frame arg0, String arg1, boolean arg2)
    {
        super(arg0, arg1, arg2);
    }
    
    public void shutdown()
    {
        setVisible(false);
    }
    
    public void center()
    {
        Rectangle r = getOwner().getBounds();
        setLocation(r.x+(r.width-this.getWidth())/2, r.y+(r.height-this.getHeight())/2);
    }
}