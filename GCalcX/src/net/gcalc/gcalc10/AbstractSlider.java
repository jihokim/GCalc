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

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractSlider<T> extends JPanel {
	protected T value;
	protected final JSlider slider;
	protected final JTextField textfield;
	protected boolean init = true;
	
	private List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();
	
	public AbstractSlider(JSlider s, JTextField t, T val) {
		super(new BorderLayout());
		
		this.slider = s;
		this.textfield = t;
		this.value = val;
		
		add(BorderLayout.CENTER, slider);
		add(BorderLayout.EAST, textfield);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setValue(getValueFromSlider());
				syncTextfieldToValue();
				fireChangeEvent(new ChangeEvent(AbstractSlider.this));
			}
		});
		
		textfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setValue(getValueFromTextfield());
				syncSliderToValue();
				fireChangeEvent(new ChangeEvent(AbstractSlider.this));
			}
		});
	}
	
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}
	
	protected void fireChangeEvent(final ChangeEvent event) {
		for (ChangeListener l : listeners) {
			final ChangeListener listener = l;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					listener.stateChanged(event);
				}
			});
		}
	}
	
	protected void syncViewToValue() {
		syncTextfieldToValue();
		syncSliderToValue();
	}
	
	public void paintComponent(Graphics g) {
		if (init) {
			syncViewToValue();
			init = false;
		}
		super.paintComponent(g);
	}

	public void setValue(T x) {
		value = x;
		syncViewToValue();
	}
	
	public T getValue() {
		return value;
	}
	
	protected abstract T getValueFromSlider();
	protected abstract T getValueFromTextfield();
	protected abstract void syncTextfieldToValue();
	protected abstract void syncSliderToValue();
}
