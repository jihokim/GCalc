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

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;

import net.gcalc.plugin.properties.GraphProperties;

/**
 * @author jkim
 *
 */
public class BooleanPropertyAction extends AbstractAction
{
    private GraphProperties properties;
    private String key;
    private boolean offset;
   
    public BooleanPropertyAction(String title, GraphProperties gp, String key)
    {
        this(title, gp, key, false);
    }
    
    public BooleanPropertyAction(String title, GraphProperties gp, String key, boolean b)
    {
        super(title);
    
        properties = gp;
        this.key = key;
        offset = b;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        boolean flag =((AbstractButton) e.getSource()).isSelected();
        if (offset)
            flag = !flag;
        properties.put(key, flag?Boolean.TRUE:Boolean.FALSE);
    }
}

