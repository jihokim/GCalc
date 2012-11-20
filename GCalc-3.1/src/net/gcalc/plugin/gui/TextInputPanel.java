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


package net.gcalc.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.gcalc.calc.gui.SwingGUI;


/**
 * @author jkim
 *
 */
public class TextInputPanel extends InputPanel implements ActionListener, KeyListener
{
    
    protected JTextField inputTF;
    protected JButton inputButton;
    protected Vector history = new Vector();
    private int cursor;
    
    /** These RecallJTextFields point to the textfields in the input
	 *  panel of the plugin.  
	 */
	protected JTextField[] inputTFs;
	
	protected String title;
	
	
	public TextInputPanel(String title, String[] labels)
	{
	    this(title,labels,null);
	}
	public TextInputPanel(String title, String[] labels, String[] values)
	{
	    this(title, "Graph!", labels,values);
	}
    
	public TextInputPanel(String title, String buttonText, String[] labels, String[] values)
	{
	    super(new BorderLayout());
	    
	    Box inputBox = createInputBox(buttonText, labels, values);
	    
	    this.add(SwingGUI.wrapTitledBorder(inputBox, title));
	    
	}
	
	/**
     * Creates and returns an undecorated (non-bordered) box with
     * all the input components. 
     */
    protected Box createInputBox(String buttonText, String[] labels, String[] values)
	{
		Box inputBox = Box.createHorizontalBox();
		int n = -1;

		if (values==null && labels==null) {
			n=0;
		}
		else if (values==null) {
			n = labels.length;
		}
		else {
			n = values.length;
		}
		
		inputTFs = new RecallJTextField[n];
		
		if (buttonText!=null) {
		    inputButton = new JButton(buttonText);
		    inputButton.addActionListener(this);
		}

		switch (n)
		{
			case 0 :
				//this should never happen
				return null;

			case 1 :
				if (labels!=null)
					inputBox.add(new JLabel(labels[0]));
			
				inputBox.add(inputTFs[0] = inputTF = new RecallJTextField(values==null?"":values[0]));
				if (inputButton!=null)
				    inputBox.add(inputButton);
			
				inputTF.addActionListener(this);
				inputTF.addKeyListener(this);
				break;

			default :
				
				Box box = Box.createVerticalBox();
				box.add(createInputSubPanel(n, labels, values));
				box.add(Box.createVerticalGlue());
				
				inputBox.add(box);
				
				if (inputButton!=null) {
				    Box b = Box.createVerticalBox();
				    b.add(inputButton);
				    b.add(Box.createVerticalGlue());
				    
				    inputBox.add(b);
				}
				break;
		}
		
		return inputBox;
	}
    
    protected JPanel createInputSubPanel(int n, String[] labels, String[] values)
	{
    		JLabel[] lhs = new JLabel[n];
		
    		GridBagLayout gb = new GridBagLayout();
		JPanel jp = new JPanel(gb);

		for (int i = 0; i < n; i++)
		{
			if (labels!=null) { 
				lhs[i] = new JLabel(labels[i], JLabel.RIGHT);

				addLeftSide(jp, gb, lhs[i]);
			}
			
			inputTFs[i] = new RecallJTextField(values==null?"":values[i]);
			inputTFs[i].addActionListener(this);
			inputTFs[i].addKeyListener(this);
			addRightSide(jp, gb, inputTF=inputTFs[i]);
		}
		
		return jp;
	}
    
    //helper method for createInputSubPanel
    protected void addLeftSide(JPanel panel, GridBagLayout gb, Component component) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		gb.setConstraints(component, c);
		panel.add(component);
	}

    	//helper method for createInputSubPanel
	protected void addRightSide(JPanel panel, GridBagLayout gb, Component component) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 10;
		
		
		gb.setConstraints(component, c);
		panel.add(component);
	}

    /**
	 * This returns the current contents of the TextFields.
	 * 
	 * @return array of strings
	 */
	public String[] getValues()
	{
	    String[] ret = new String[inputTFs.length];
	    for (int i=0; i<inputTFs.length; i++)
	        ret[i] = inputTFs[i].getText();
	    
	    return ret;
	}
		
	public void clear()
	{
	    for (int i=0; i<inputTFs.length; i++)
	       inputTFs[i].setText("");
	}
	
	public void actionPerformed(ActionEvent e)
	{
	    fireActionEvent();
	}
	
	
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		int size = history.size();
		
		if (size==0)
		    	return;

		if (code == KeyEvent.VK_PAGE_UP)
		{
			cursor = (cursor + size - 1) % size;
			this.setValues((String[]) history.elementAt(cursor));
		}
		else if (code == KeyEvent.VK_PAGE_DOWN)
		{
			cursor = (cursor + 1) % size;
			this.setValues((String[]) history.elementAt(cursor));
		}
	}
	public void keyReleased(KeyEvent e)
	{}
	public void keyTyped(KeyEvent e)
	{}
	
	public void resetRecallCursor()
	{
		cursor = 0;
	}

	public void addCurrentValuesToHistory()
	{
	    String[] s = getValues();
		if (history.isEmpty() || ! Arrays.equals((String[]) history.lastElement(), s))
		{
			history.add(s);
		}
		resetRecallCursor();
	}

	public void clearHistory()
	{
	    clear(); //clears the content of text fields
		history = new Vector();
		history.add(getValues());
		resetRecallCursor();
	}
	
	public void setValues(String[] val)
	{
	    for (int i=0; i<inputTFs.length; i++)
	        inputTFs[i].setText(val[i]);
	
	}

	public void incrementFocusedTextField()
	{
	    //this loop stop short of the last one so that the focus doesn't loop around
	    for (int i = 0; i< inputTFs.length - 1; i++)
		{
			if (inputTFs[i].hasFocus()) {
				inputTFs[(i + 1) % inputTFs.length].requestFocusInWindow();
				return;
			}
		}
	}
}

