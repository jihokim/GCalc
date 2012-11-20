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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.gcalc.calc.GCalc;



/**
 * A dialog to show rendered HTML.
 * @author jkim
 *
 */
public class HtmlDialog extends ShutdownDialog
{
	public HtmlDialog(Frame f, String title, String resource)
    {
        super(f, title, true);
        
        ClassLoader cl = this.getClass().getClassLoader();
        BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(resource)));

        StringBuffer sb = new StringBuffer();
        
        try {
            String s = null;
            while ((s=br.readLine())!=null) {
                sb.append(s);
                sb.append("\n");
            }
        }
        catch(IOException e) {}
                    
        JEditorPane jep = new JEditorPane("text/html", "<div style=\"font-size: x-small;\"><pre>"+sb.toString()+"</pre></div>");
        jep.setEditable(false);
        jep.setCaretPosition(0);
        
        JScrollPane sp = new JScrollPane(jep,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel jp = new JPanel(new BorderLayout());
        Box notices = Box.createVerticalBox();
        notices.add(new JLabel(GCalc.TITLE));
        notices.add(new JLabel(GCalc.COPYRIGHT_NOTICE));

        jp.add("Center", sp);
        this.getContentPane().add(jp);
        
        this.addWindowListener(new ShutdownWindowAdapter(this));

        this.setSize(450,400);
        center();

        setResizable(true);
        setVisible(true);
    }
}