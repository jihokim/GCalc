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


package net.gcalc.plugin.plane.gui;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.gcalc.plugin.properties.GraphProperties;



/**
 * @author jkim
 */
public class FlagPanel extends JPanel
{
    private GraphProperties properties;

    public FlagPanel(GraphProperties gp)
    {
        this(gp, 5,2, new String[]{
                GraphProperties.H_GRID, 
                GraphProperties.V_GRID, 
                GraphProperties.H_AXIS, 
                GraphProperties.V_AXIS, 
                GraphProperties.H_LABEL, 
                GraphProperties.V_LABEL, 
                GraphProperties.H_SCALE, 
                GraphProperties.V_SCALE, 
                GraphProperties.H_TITLE,
                GraphProperties.V_TITLE
        });
    }
    
    public FlagPanel(GraphProperties gp, int row, int col, String[] keys)
    {
        super(new GridLayout(row, col));
        properties = gp;

        for (int i=0; i<keys.length; i++) {
            if (keys[i]!=null)
                add(makeCheckBox(getLabelString(keys[i]), keys[i]));
            else
                add(new JPanel());
        }
    }

    private JCheckBox makeCheckBox(String title, String key)
    {
        JCheckBox cb = new JCheckBox(new BooleanPropertyAction(title,
                properties, key));
        cb.setSelected(properties.getBooleanProperty(key));

        return cb;
    }
    
    public String getLabelString(String key)
    {
        if (key.equals(GraphProperties.TRACE))
            return "Trace";
        if (key.equals(GraphProperties.THICK_GRAPH))
            return "Thick Graph";
        
        if (key.equals(GraphProperties.H_GRID))
            return "H Grid";
        if (key.equals(GraphProperties.H_AXIS))
            return "H Axis";
        if (key.equals(GraphProperties.H_LABEL))
            return "H Label";
        if (key.equals(GraphProperties.H_SCALE))
            return "H Scale";
        if (key.equals(GraphProperties.H_TITLE))
            return "H Title";

        if (key.equals(GraphProperties.V_GRID))
            return "V Grid";
        if (key.equals(GraphProperties.V_AXIS))
            return "V Axis";
        if (key.equals(GraphProperties.V_LABEL))
            return "V Label";
        if (key.equals(GraphProperties.V_SCALE))
            return "V Scale";
        if (key.equals(GraphProperties.V_TITLE))
            return "V Title";

        if (key.equals(GraphProperties.SHOW_CONCAVITY))
            return "Concavity";
        if (key.equals(GraphProperties.SHOW_MONOTONICITY))
            return "Monotonicity";
        
        return "";
    }

}

