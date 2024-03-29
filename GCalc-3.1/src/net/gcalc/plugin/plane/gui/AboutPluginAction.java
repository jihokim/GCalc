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


import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import net.gcalc.calc.gui.ShutdownDialog;
import net.gcalc.calc.main.AbstractPlugin;


public class AboutPluginAction extends AbstractAction
{
    private AbstractPlugin s;
    public AboutPluginAction(AbstractPlugin plugin)
    {
        putValue(Action.NAME, "About Plugin");
        s = plugin;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Component dialog = new AboutDialog(s);
        dialog.setVisible(true);
    }
}

class AboutDialog extends ShutdownDialog
{
    public AboutDialog(AbstractPlugin plugin)
    {
        super(plugin, "About "+plugin.getTitle(), true);
        
        JEditorPane editorPane = new JEditorPane("text/html", "");
        JScrollPane scrollPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
        editorPane.setText(plugin.getHTMLDescription());
        editorPane.setEditable(false);
        
        getContentPane().add(scrollPane);
       
        pack();
        center();
    }
}
