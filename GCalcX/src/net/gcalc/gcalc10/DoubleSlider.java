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

import java.awt.Color;

import javax.swing.JSlider;
import javax.swing.JTextField;

public class DoubleSlider extends AbstractSlider<Double>
{
	double min,max;

	public DoubleSlider(int n, double min, double val, double max) {
		super(new JSlider(), new JTextField(n), val);
		this.min = min;
		this.max = max;
	}

	@Override
	protected Double getValueFromSlider() {
		double val = .01*slider.getValue()*(max-min)+min;
		return val;
	}

	@Override
	protected Double getValueFromTextfield() {
		try {
			double v = Double.parseDouble(textfield.getText());
			
			if (v<min)
				v = min;
			if (v>max)
				v = max;
			
			return v;
		}
		catch (NumberFormatException e) {
			System.out.println(e);
		}
		return null;
	}

	@Override
	protected void syncSliderToValue() {
		if (value!=null) {
			double t = (value-min)/(max-min)*100;
			slider.setValue((int) (t+.5));
		}
		slider.repaint();
	}

	@Override
	protected void syncTextfieldToValue() {
		if (value!=null) {
			textfield.setText(""+getValue().floatValue());
			textfield.setBackground(Color.white);
		}
		else {
			textfield.setBackground(Color.orange);
		}
		textfield.repaint();
	}
	
}
