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
package net.gcalc.gcalc10.editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.gcalc.gcalc10.SafeTable;
import net.gcalc.gcalc10.drawable.ColoredDrawable;
import net.gcalc.gcalc10.drawable.DrawableSketcher.SketchingAlgorithm;
import net.gcalc.gcalc10.script.Function;
import net.gcalc.gcalc10.util.DoubleString;

public class HeteroTable extends SafeTable {
	protected ColorEditor colorEditor = new ColorEditor();
	protected BooleanEditor booleanEditor = new BooleanEditor();
	protected FunctionEditor functionEditor = new FunctionEditor();
	protected DoubleStringEditor doubleStringEditor = new DoubleStringEditor();
	protected DrawableFunctionEditor drawableFunctionEditor = new DrawableFunctionEditor();

	private List<Class<?>> tceClasses = new ArrayList<Class<?>>();
	private List<TableCellEditor> tces = new ArrayList<TableCellEditor>();

	private List<Class<?>> tcrClasses = new ArrayList<Class<?>>();
	private List<TableCellRenderer> tcrs = new ArrayList<TableCellRenderer>();

	private Map<Class<?>, TableCellEditor> tceMap = new HashMap<Class<?>, TableCellEditor>();
	private Map<Class<?>, TableCellRenderer> tcrMap = new HashMap<Class<?>, TableCellRenderer>();

//	private int sigFigs = 6;

	public HeteroTable() {
		super();
		init();
	}

	public HeteroTable(TableModel tm) {
		super(tm);
		init();
	}

	private void putEditor(Class<?> c, TableCellEditor tce) {
		tceMap.clear();
		
		int index = tceClasses.indexOf(c);
		
		if (index>=0) {
			tces.set(index, tce);
		}
		else {		
			tceClasses.add(c);
			tces.add(tce);
		}
	}

	public void putRenderer(Class<?> c, TableCellRenderer tcr) {
		tcrMap.clear();
		
		int index = tcrClasses.indexOf(c);
		
		if (index>=0) {
			tcrs.set(index, tcr);
		}
		else {		
			tcrClasses.add(c);
			tcrs.add(tcr);
		}
	}

	private void init() 
	{
		putRenderer(Color.class, new ColorRenderer());
		putRenderer(Boolean.class, new DefaultTableCellRenderer());	

		putEditor(Color.class, colorEditor);	
		putEditor(Boolean.class, booleanEditor);	
		putEditor(Function.class, functionEditor);
		putEditor(DoubleString.class, doubleStringEditor);
		putEditor(ColoredDrawable.class, drawableFunctionEditor);
		putEditor(SketchingAlgorithm.class, new EnumEditor(SketchingAlgorithm.ADAPTIVE));			
		colorEditor.setClickCountToStart(1);
	}

	public TableCellRenderer getDefaultRenderer(Class<?> classVal) {
		TableCellRenderer renderer = tcrMap.get(classVal);

		if (renderer!=null) {
			return renderer;
		}

		for (int i=0; i<tcrClasses.size(); i++) {
			if (tcrClasses.get(i).isAssignableFrom(classVal)) {
				tcrMap.put(classVal, tcrs.get(i));
				return tcrs.get(i);
			}
		}

		return (TableCellRenderer) super.getDefaultRenderer(Object.class);
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		Class<?> classVal = getValueAt(row, column).getClass();

		return getDefaultRenderer(classVal);
	}

	public TableCellEditor getCellEditor(int row, int column) {
		Class<?> classVal = getValueAt(row, column).getClass();
		TableCellEditor editor = tceMap.get(classVal);

		if (editor!=null) {
			return editor;
		}

		for (int i=0; i<tceClasses.size(); i++) {
			if (tceClasses.get(i).isAssignableFrom(classVal)) {
				tceMap.put(classVal, tces.get(i));
				return tces.get(i);
			}
		}

		return (TableCellEditor) defaultEditorsByColumnClass.get(Object.class);
	}
}
