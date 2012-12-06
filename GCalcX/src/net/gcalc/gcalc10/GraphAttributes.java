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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.gcalc10.util.Util;


public class GraphAttributes {
	private HashMap<Object, List<AttributeChangeListener>> listenerMap = new HashMap<Object,List<AttributeChangeListener>>();
		
	public final Attribute<Boolean> TICKS = new Attribute<Boolean>(true);
	public final Attribute<Boolean> GRID = new Attribute<Boolean>(true);
	public final Attribute<Boolean> AXES = new Attribute<Boolean>(true);
	public final Attribute<Boolean> LABELS = new Attribute<Boolean>(true);

	public final Attribute<Double> STROKE_WIDTH = new Attribute<Double>(3.0) {
		public void setValue(Double x) {
			if (x<=0)
				throw new IllegalArgumentException("xgap must be positive");
			super.setValue(x);
		}		
	};
	
	public final Attribute<DoubleString> GRID_XGAP = new Attribute<DoubleString>(Util.get(".2")) {
		public void setValue(DoubleString x) {
			if (x.value<=0)
				throw new IllegalArgumentException("xgap must be positive");
			super.setValue(x);
		}		
	};
	public final Attribute<DoubleString> GRID_YGAP = new Attribute<DoubleString>(Util.get(".2")) {
		public void setValue(DoubleString x) {
			if (x.value<=0)
				throw new IllegalArgumentException("xgap must be positive");
			super.setValue(x);
		}		
	};
	public final Attribute<DoubleString> TMIN = new Attribute<DoubleString>(Util.get(".0")) {
		public void setValue(DoubleString x) {
			if (x.value>=TMAX.getValue().value)
				throw new IllegalArgumentException("tmin must be lesser than tmax");
			super.setValue(x);
		}		
	};
	public final Attribute<DoubleString> TMAX = new Attribute<DoubleString>(Util.get(".4pi")) {
		public void setValue(DoubleString x) {
			if (x.value<=TMIN.getValue().value)
				throw new IllegalArgumentException("tmax must be greater than tmin");
			super.setValue(x);
		}		
	};
	public final Attribute<Integer> TSEGS = new Attribute<Integer>(100) {
		public void setValue(Integer x) {
			if (x<2)
				throw new IllegalArgumentException("tsegs must be >=2");
			super.setValue(x);
		}		
	};
	

	public GraphAttributes(GCalcInit init) {
		GRID.setValue(init.getBoolean("gui.graph_attributes.grid.value"));
		AXES.setValue(init.getBoolean("gui.graph_attributes.axes.value"));
		TICKS.setValue(init.getBoolean("gui.graph_attributes.ticks.value"));
		LABELS.setValue(init.getBoolean("gui.graph_attributes.labels.value"));
		
		GRID_XGAP.setValue(init.getDoubleString("gui.graph_attributes.xgap.value"));
		GRID_YGAP.setValue(init.getDoubleString("gui.graph_attributes.ygap.value"));
		TMIN.setValue(init.getDoubleString("gui.graph_attributes.tmin.value"));
		TMAX.setValue(init.getDoubleString("gui.graph_attributes.tmax.value"));
		TSEGS.setValue(init.getInteger("gui.graph_attributes.tsegs.value"));
		
		STROKE_WIDTH.setValue(init.getDouble("gui.graph_attributes.stroke.value"));
		
	}
	
	public <T> T getValue(Attribute<T> attribute) {
		return attribute.getValue();
	}
	
	private <T> void fireAttributeChangedEvent(Attribute<T> attribute)
	{
		final AttributeChangeEvent<T> e = new AttributeChangeEvent<T>(attribute);
		
		for (AttributeChangeListener l : listenerMap.get(attribute)) {
			final AttributeChangeListener listener = l;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					listener.attributeChange(e);					
				}
			});
		}
	}
	
	public void addAttributeChangeListener(AttributeChangeListener listener) 
	{
		for (Object x : listenerMap.keySet()) {
			listenerMap.get(x).add(listener);
		}
	}

	public void addAttributeChangeListener(AttributeChangeListener listener, Object ... attributes) 
	{
		for (Object x : attributes) {
			listenerMap.get(x).add(listener);
		}
	}

	public class Attribute<T> 
	{
		private boolean allowNull = false;
		private T value;
		
		public Attribute() {
			this(null, true);
		}
		
		public Attribute(T value) {
			this(value, value==null);
		}
		
		public Attribute(T value, boolean allowNull) {
			this.allowNull = allowNull;
			initValue(value);
			listenerMap.put(this, new ArrayList<AttributeChangeListener>());
		}
		
		private void initValue(T x) {
			if (! allowNull && x==null) 
				throw new IllegalArgumentException("Null is not an allowed attribute value"); 
			value = x;
		}
		
		public void setValue(T x) {
			initValue(x);
			fireAttributeChangedEvent(this);
		}
		
		public T getValue() {
			return value;
		}
		
		public void addAttributeChangeListener(AttributeChangeListener listener) {
			listenerMap.get(this).add(listener);
		}
		
		public void touch() {
			fireAttributeChangedEvent(this);
		}
	}
}
