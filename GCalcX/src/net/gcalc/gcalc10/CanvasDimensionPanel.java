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
package net.gcalc.gcalc10;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.gcalc.gcalc10.util.SpringUtilities;
import net.gcalc.gcalc10.util.Util;

public class CanvasDimensionPanel extends JPanel implements RecomputationListener {
	JTextField xmin,xmid,xmax;
	JTextField ymin,ymid,ymax;
	JTextField xpix, ypix;
	
	public CanvasDimensionPanel() {
		super(new SpringLayout());
		
		add(new JPanel());
		add(new JLabel("x", JLabel.CENTER));
		add(new JLabel("y", JLabel.CENTER));

		add(new JLabel("min", JLabel.RIGHT));
		add(xmin = new JTextField(15));
		add(ymin= new JTextField(15));

		add(new JLabel("mid", JLabel.RIGHT));
		add(xmid = new JTextField(15));
		add(ymid = new JTextField(15));
		
		add(new JLabel("max", JLabel.RIGHT));
		add(xmax = new JTextField(15));
		add(ymax = new JTextField(15));
		
		add(new JLabel("pixels", JLabel.RIGHT));
		add(xpix = new JTextField(15));
		add(ypix = new JTextField(15));
		
		xmin.setEditable(false);
		xmid.setEditable(false);
		xmax.setEditable(false);
		xpix.setEditable(false);
		ymin.setEditable(false);
		ymid.setEditable(false);
		ymax.setEditable(false);
		ypix.setEditable(false);

		
		SpringUtilities.makeCompactGrid(this, 5, 3, 0, 0, 2, 2);
	}

	@Override
	public void computationPerformed(CanvasContext context) {
		Rectangle2D rect = context.getCartesianBounds();
		
		setValue(xmin,rect.getMinX());
		setValue(xmid,rect.getCenterX());
		setValue(xmax,rect.getMaxX());
		setValue(ymin,rect.getMinY());
		setValue(ymid,rect.getCenterY());
		setValue(ymax,rect.getMaxY());

		Rectangle bounds = context.getPixelBounds();
		
		xpix.setText(Integer.toString(bounds.width));
		ypix.setText(Integer.toString(bounds.height));

	}
	
	private void setValue(JTextField field, double value) {
		field.setText(Util.sigfig(value,10));
	}
}
