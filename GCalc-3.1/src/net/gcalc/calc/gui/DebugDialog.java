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


package net.gcalc.calc.gui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextArea;




/**
 * Dialog to show the runtime memory usage in real time.
 * @author jkim
 *
 */
public class DebugDialog extends ShutdownDialog implements Runnable
{
    private JTextArea textArea;
    private Runtime runtime = Runtime.getRuntime();
    
    public DebugDialog(JFrame frame)
    {
        super(frame, "Debug Dialog", false);
        this.getContentPane().add(textArea = new JTextArea());
        
        memory();
        setSize(300,200);
        center();
        setVisible(true);
        
    } 
   
    public void run()
    {
        while (true) {
            
            memory();
            
            try {
                Thread.sleep(1000);
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    public void memory()
    {
        String free = "Free Mem:  "+runtime.freeMemory();
        String max = "\nMax Mem:   "+runtime.maxMemory();
        String total = "\nTotal Mem: "+runtime.totalMemory();
        textArea.setText(free+max+total);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
    }
}