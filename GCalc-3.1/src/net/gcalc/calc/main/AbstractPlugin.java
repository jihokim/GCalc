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


package net.gcalc.calc.main;

import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.JDialog;

import net.gcalc.calc.gui.Shutdown;
import net.gcalc.calc.gui.ShutdownWindowAdapter;
import net.gcalc.calc.gui.SwingGUI;

/**
 * The abstract class to be extended by any GCalc plugin.
 */
public abstract class AbstractPlugin extends JDialog implements Shutdown {

	/**
	 * Extra record keeping in order to require that the plugins constructors
	 * have no parameters.
	 */
	private static Frame owner;

	/**
	 * Lets all instances of plugins know what the main JFrame is. This
	 * information could be passed in through the constructor, but I couldn't
	 * figure out how to require that a plugin have a certain formal parameters.
	 * This mechanism is to get around having to pass anything to the
	 * constructor so all plugins can be instantiated in a uniform way.
	 * 
	 * @param f
	 *            Parent frame
	 */
	public static void setParentFrame(Frame f) {
		owner = f;
	}

	/**
	 * Boolean value set by dieOnClose() method.
	 */
	private boolean dieOnClose;

	/**
	 * Boolean value to make sure initialization happens only once.
	 */
	private boolean initialized;
	
	
	
	/**
	 * Constructs an abstract plugin. Sets the name of the frame and makes it
	 * unresizeable.
	 */
	protected AbstractPlugin() {
		super(owner);
		setResizable(false);
		setTitle(getPluginName());
		addWindowListener(new ShutdownWindowAdapter(this));
		
		System.out.println(owner);
		
	//	setPropertiesPanel(new PropertiesPanel(this));
	}

	public Icon getIcon()
	{
        return null;
	}

	
	/**
	 * Return the string containing the name of the author.
	 */
	public abstract String getCreatorName();

	/**
	 * Return the string containing the <code>text/plain</code> description of
	 * the plugin.
	 */
	public abstract String getDescription();

	/**
	 * Return the string containing the <code>text/html</code> description of
	 * the plugin.
	 */
	public String getHTMLDescription() {
		return getStandardHeading() + " " + getDescription();
	}

	/**
	 * Return the string containing the plugin's name.
	 * 
	 * @return String name of plugin
	 */
	public abstract String getPluginName();

	/**
	 * Returns canned html header with copyright information
	 * 
	 * @return String
	 */
	protected String getStandardHeading() {
		return "<b>" + getPluginName() + " </b><br>\n Author: " + getCreatorName();
	}

	/**
	 * Implemented mostly for debugging purposes
	 *  
	 */
	public String toString() {
		return getPluginName();
	}

	/**
	 * Ask the user for confirmation on hiding the plugin; and hide the plugin
	 * is confirmed.
	 */
	public void shutdown() {
		setVisible(false);
		
		if (owner!=null)
		    owner.toFront();

		if (dieOnClose)
			System.exit(0);
	}

	/**
	 * Sets if the application should call System.exit(0) on shutdown. This is
	 * useful if you're just running the plugin itself as an application.
	 */
	public void setDieOnClose(boolean b) {
		dieOnClose = b;
	}

	/**
	 * Should construct most of the gui elements for the plugin. This method
	 * should not be called from the constructor to delay the bulk of the
	 * overhead of building each plugin until it's actually shown.
	 */
	public abstract void init();
	
    /**
     * Wrapper method for JOptionPane.showMessageDialog, this is so
     * that we can change the message system easily if needed.
     * 
     * @param message
     *            Message String
     * @param messageType
     *            JOptionPane message type (the type of message to be
     *            displayed: ERROR_MESSAGE, INFORMATION_MESSAGE,
     *            WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE)
     * 
     */
	public void popupMessageDialog(String message, int messageType)
	{
	   SwingGUI.popupMessage(message,this,messageType);
	}
	
	/**
	 * 
	 * @param b
	 */
	public void setInitialized(boolean b)
	{
	    initialized = b;
	}
	
	public boolean isInitialized()
	{
	    	return initialized;
	}
	
	/*
	public void setVisible(boolean b)
	{
		if (b==true) {
			setLocationRelativeTo(owner);
		}
		
		super.setVisible(b);
	}*/
	
	
	/**
	 * Returns true if and only if <code>this</code> and <code>o</code> are object of the same class.
	 * This functionality exists so that users know if they're trying to load plugins with the same
	 * class name twice.  This is not possible, of course.
	 */
	public boolean equals(Object o)
	{
		return (o==null)?false:this.getClass().equals(o.getClass());
	}
}

