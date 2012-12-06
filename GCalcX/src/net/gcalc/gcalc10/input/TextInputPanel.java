/** 
GCalcX
Copyright (C) 2010 Jiho Kim 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/
package net.gcalc.gcalc10.input;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.gcalc.gcalc10.util.SpringUtilities;
import net.gcalc.gcalc10.util.Util;

public abstract class TextInputPanel<T> extends SourcePanel<T>
{
	protected JTextField[] fields;
	
	public TextInputPanel(String ... labels) {
		super(new BorderLayout());
		
	    int numPairs = labels.length;
		fields = new JTextField[numPairs];
		
	    //Create and populate the panel.
	    JPanel p = new JPanel(new SpringLayout());
	    for (int i = 0; i < numPairs; i++) {
	        JLabel l = new JLabel(labels[i], JLabel.TRAILING);
	        p.add(l);
	        fields[i] = new JTextField(10);
	        l.setLabelFor(fields[i]);
	        p.add(fields[i]);
	        
	        fields[i].addActionListener(new Actor(i));
	    }

	    //Lay out the panel.
	    SpringUtilities.makeCompactGrid(p,
	                                    numPairs, 2, //rows, cols
	                                    0, 0,        //initX, initY
	                                    2, 2);       //xPad, yPad

	    add(BorderLayout.NORTH, p);
	}
	
	public void setText(String ... x) {
		for (int i=0; i<fields.length; i++) {
			fields[i].setText(x[i]);
		}
	}
	
	public void clear() {
		for (JTextField jtf : fields) {
			jtf.setText("");
		}
	}
	
	class Actor implements ActionListener {
		int index;
		
		public Actor(int i) {
			index = i;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (index==fields.length-1) {
				T value = null;
				try {
					value = getValue();
				} catch (InputException e1) {
					Util.issueErrorDialog(e1,TextInputPanel.this);
				}
				fireSourceChangeEvent(new SourceChangeEvent<T>(TextInputPanel.this, value));
				return;
			}
			
			JTextField next = fields[index+1];
			
			next.requestFocus();
			next.setSelectionStart(0);
			next.setSelectionStart(next.getText().length());
			next.setCaretPosition(next.getText().length());
		}
	}
}

