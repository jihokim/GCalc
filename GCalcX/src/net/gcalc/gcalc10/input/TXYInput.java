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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.gcalc.gcalc10.drawable.ColoredDrawable;

public class TXYInput extends SourcePanel<ColoredDrawable> {
	
	private void add(Component component, GridBagLayout gridbag, GridBagConstraints constraints)
	{
		gridbag.setConstraints(component, constraints);
		add(component);
	}

	public TXYInput() {
		super();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		add(new JLabel("x(t)="), gridbag, c); 
		c.weightx = 1.0;
		add(new JTextField(), gridbag, c); 
		c.weightx = 0.0;
		add(new JLabel("tmin", JLabel.CENTER), gridbag, c); 
		add(new JLabel("tmax", JLabel.CENTER), gridbag, c); 
		add(new JLabel("tsegs", JLabel.CENTER), gridbag, c); 

		c.gridx = 0;
		c.gridy = 1;
		add(new JLabel("y(t)="), gridbag, c); 
		c.gridx = GridBagConstraints.RELATIVE;
		add(new JTextField(), gridbag, c); 
		c.weightx = 0.0;
		add(new JTextField(5), gridbag, c); 
		add(new JTextField(5), gridbag, c); 
		add(new JTextField(5), gridbag, c); 
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new TXYInput());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public void clear() {
		
	}

	public String getTitle() {
		return "(x(t),y(t))";
	}

	public ColoredDrawable getValue() {
		return null;
	}
	
}
