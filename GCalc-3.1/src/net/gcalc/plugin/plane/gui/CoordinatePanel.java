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

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author jkim
 *
 */
public class CoordinatePanel extends JPanel
{
    private JLabel[] label;
    private Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 12);
    
    public CoordinatePanel()
    {
        this(2);
    }
    
    public CoordinatePanel(int n)
    {
        super(new GridLayout(1,n));
        label = new JLabel[n];
       
        for (int i=0; i<n; i++) {
            add(label[i] = new JLabel(" "));
            label[i].setFont(DEFAULT_FONT);
        }
    }
    
    public void clear()
    {
        setLabels(null);
    }
    
    public void setLabels(String[] s)
    {
        if (s==null) {
            for (int i=0; i<label.length; i++)
                label[i].setText("");
            return;
        }
        
        int M = Math.max(s.length, label.length);
        
        for (int i=0; i<M; i++)
            label[i].setText(s[i]);
    }
}

