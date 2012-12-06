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

import java.awt.LayoutManager2;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public abstract class SourcePanel<T> extends JPanel implements InputSource<T> {
	private CopyOnWriteArrayList<SourceListener<T>> listeners = new CopyOnWriteArrayList<SourceListener<T>>();
	
	protected SourcePanel() {
		super();
	}
	
	protected SourcePanel(LayoutManager2 m) {
		super(m);
	}
	
	public void addSourceListener(SourceListener<T> l) {
		listeners.add(l);
	}
	public void removeSourceListener(SourceListener<T> l) {
		listeners.remove(l);
	}
	
	protected void fireSourceChangeEvent(final SourceChangeEvent<T> e) {
		for (SourceListener<T> ear : listeners) {
			final SourceListener<T> listener = ear;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					listener.sourceChange(e);
				}
			});
		}
	}
	
	public abstract String getTitle();
	public abstract void clear();
}
