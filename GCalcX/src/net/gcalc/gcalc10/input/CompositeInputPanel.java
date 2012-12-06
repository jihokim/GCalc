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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.gcalc.gcalc10.util.Util;

public class CompositeInputPanel<T> extends SourcePanel<T> implements SourceListener<T>
{
	private JTabbedPane jtp = new JTabbedPane();
	private List<SourcePanel<T>> sourceList = new ArrayList<SourcePanel<T>>();
	private JButton plot = new JButton(new AbstractAction("Plot") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				plot();
			} catch (InputException e1) {
				Util.issueErrorDialog(e1, CompositeInputPanel.this);
			}
		}
	});
	
	public CompositeInputPanel() {
		super(new BorderLayout());
		
		this.add(BorderLayout.CENTER, jtp);
		this.add(BorderLayout.EAST, plot);
	}
	
	private void plot() {
		fireSourceChangeEvent(new SourceChangeEvent<T>(this, getValue()));		
	}
	
	public void sourceChange(SourceChangeEvent<T> e) {
		fireSourceChangeEvent(new SourceChangeEvent<T>(this, e.getValue()));
	}
	
	public void addSource(SourcePanel<T> source) {
		source.setPreferredSize(new Dimension(1,1));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(BorderLayout.CENTER, source);
		panel.setBorder(new EmptyBorder(10,10,10,10));
		
		source.addSourceListener(this);
		sourceList.add(source);
		jtp.addTab(source.getTitle(), panel);
	}
	
	public void removeSource(int n) {
		jtp.removeTabAt(n);
		sourceList.remove(n).remove(this);
	}

	public String getTitle() {
		return "Composite";
	}

	public T getValue() {
		int n = jtp.getSelectedIndex();
		InputSource<T> source = sourceList.get(n);
		return source.getValue();
	}	
	
	public void clear() {
		int n = jtp.getSelectedIndex();
		SourcePanel<T> source = sourceList.get(n);
		source.clear();
	}
}

