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
/**
 *  GCalc 10 - Copyright Jiho Kim 2010
 *
 *  Do not redistribute.
 */

package net.gcalc.gcalc10.drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public class DrawableList<T extends Drawable> implements Drawable, Iterable<T> {
	private ArrayList<T> list = new ArrayList<T>();
	
	private DrawableListModel listModel = new DrawableListModel();
	
	public void add(T drawable) {
		list.add(drawable);
		int idx = list.size()-1;

		listModel.fireItemAdded(idx);
	}
	
	public void remove(int idx) {
		list.remove(idx);
		listModel.fireItemRemoved(idx);
	}
	
	public int size() {
		return list.size();
	}
	
	public List<Callable<Runnable>> getTasks(final DrawableVisitor<Runnable> drawer) {
		List<Callable<Runnable>> children = new ArrayList<Callable<Runnable>>(list.size());
		for (Drawable d : list) {
			final Drawable drawable = d;
			children.add(new Callable<Runnable>() {
				public Runnable call() {
					return drawable.visit(drawer);
				}
			});			
		}
		
		return children;
	}
	
	public DrawableListModel getListModel() {
		return listModel;
	}
	
	public <R> R visit(DrawableVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Iterator<T> iterator() {
		return Collections.unmodifiableList(list).iterator();
	}
	
	public T get(int idx) {
		return list.get(idx);
	}

	public void set(int idx, T x) {
		list.set(idx, x);
	}
	
	class DrawableListModel extends AbstractListModel
	{
		public int getSize() {
			return list.size();
		}

		public Object getElementAt(int index) {
			return list.get(index);
		}

		public void fireItemAdded(final int idx) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DrawableListModel.this.fireIntervalAdded(DrawableList.this, idx, idx);
				}
			});
		}

		public void fireItemRemoved(final int idx) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DrawableListModel.this.fireIntervalRemoved(DrawableList.this, idx, idx);
				}
			});
		}
	}
}

