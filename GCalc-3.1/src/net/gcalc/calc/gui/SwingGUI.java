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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;

import net.gcalc.calc.main.AbstractPlugin;



/**
 * Class contains many static convenience methods dealing with Swing.
 * The idea is to use these to enforce some uniformity on gui
 * presentation.
 * 
 * @author jkim
 */
public final class SwingGUI
{
    /**
     * Wrap a component inside a box which is padded on top and bottom
     * with some glue.
     * @param c component
     */
    public static Box wrap(Component c)
    {
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(c);
        box.add(Box.createVerticalGlue());

        return box;
    }
    
    /**
     * Decorate a component with a (lowered) EtchedBorder, and Wrap
     * that with a TitleBorder with title <code>title</code>
     */
    public static Component wrapTitledBorder(Component c, String title)
    {
        Box box = Box.createHorizontalBox();
        box.add(c);
        box.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title));
        return box;
    }

    /**
     * Wrapper method for JOptionPane.showMessageDialog, this is so
     * that we can change the message system easily if needed.
     * 
     * @param message
     *            Message String
     * @param plugin
     *            Originating plugin/component
     * @param messageType
     *            JOptionPane message type (the type of message to be
     *            displayed: ERROR_MESSAGE, INFORMATION_MESSAGE,
     *            WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE)
     */
    public static void popupMessage(String message, AbstractPlugin plugin,
            int messageType)
    {
        JOptionPane.showMessageDialog(plugin, message, plugin.getTitle(),
                messageType);
    }
    
    /**
     * Show fatal error and then kills application.
     */
    public static void popupFatalError(String message, int exitcode)
    {
        JOptionPane.showMessageDialog(null, message, "Fatal Error!", JOptionPane.ERROR_MESSAGE);
        
        System.exit(exitcode);
    }
}

