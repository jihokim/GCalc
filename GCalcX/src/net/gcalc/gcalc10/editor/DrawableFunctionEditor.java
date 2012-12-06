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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import net.gcalc.gcalc10.drawable.AbstractDrawableVisitor;
import net.gcalc.gcalc10.drawable.ColoredDrawable;
import net.gcalc.gcalc10.drawable.Points;
import net.gcalc.gcalc10.drawable.TR;
import net.gcalc.gcalc10.drawable.TXY;
import net.gcalc.gcalc10.drawable.XY;
import net.gcalc.gcalc10.input.InputException;


public class DrawableFunctionEditor extends AbstractCellEditor implements TableCellEditor {
	private Component root;
	private final JPanel panel = new JPanel(new GridLayout(1,2));
	private final JTextField x,y;
	private final JTextField single;

	private final Revisor revisor = new Revisor();
	private final ComponentPopulator componentPopulator = new ComponentPopulator();

	private ColoredDrawable drawable = null;
	
	private FocusAdapter focusAdapter = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent event) {
			Component c = event.getOppositeComponent();
			if (c!=x && c!=y)		
				stopCellEditing();
		}

		@Override
		public void focusGained(FocusEvent e) {
			x.setForeground(null);
			y.setForeground(null);
			single.setForeground(null);
		}

	};
	
	private class TwistedFocusTextField extends JTextField {
		TwistedFocusTextField() {
			addFocusListener(focusAdapter);
		}
	}
	
	public DrawableFunctionEditor() {
		x = new TwistedFocusTextField();
		y = new TwistedFocusTextField();
		single = new TwistedFocusTextField();
		panel.add(x);
		panel.add(y);
	}
	
	public boolean stopCellEditing() {

		if (drawable!=null) {
			try {
				drawable.visit(revisor);
			}
			catch (InputException e) {
				x.setForeground(Color.red);
				y.setForeground(Color.red);
				single.setForeground(Color.red);
				return false;
			}
		}

		return super.stopCellEditing();
	}

	@Override
	public Object getCellEditorValue() {
		ColoredDrawable f = null;
		
		try {
			f = drawable.visit(revisor);
		}
		catch (InputException e) {
			return drawable;
		}

		if (f!=null) {
			f.setColor(drawable.getColor());
		}
		
		return f;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, final Object value,
			boolean isSelected, int row, int column) {
		
		drawable = (ColoredDrawable) value;
		
		root = drawable.visit(componentPopulator);
		return root;
	}
	
	@Override
	public boolean isCellEditable(EventObject e) {
		if (e instanceof MouseEvent) {
			return ((MouseEvent) e).getClickCount() >= 2;
		}
		
		return true;
	}

	class Revisor extends AbstractDrawableVisitor<ColoredDrawable>
	{
		public ColoredDrawable visit(XY f) {
			XY xy = new XY(f);
			xy.setY(single.getText());
			return xy;
		}
		public ColoredDrawable visit(TR f) {
			TR tr = new TR(f);
			tr.setR(single.getText());
			return tr;
		}
		public ColoredDrawable visit(TXY f) {
			TXY txy = new TXY(f);
			txy.setXY(x.getText(), y.getText());
			return txy;
		}

		public ColoredDrawable visit(Points f) {
			return new Points(f);
		}
	}
	
	class ComponentPopulator extends AbstractDrawableVisitor<Component> {
		
		@Override		
		public Component visit(XY f) {
			single.setText(f.toString());
			return single;
		}

		@Override
		public Component visit(TR f) {
			single.setText(f.toString());
			return single;
		}

		@Override
		public Component visit(TXY f) {
			x.setText(f.getX().toString());
			y.setText(f.getY().toString());
			return panel;
		}

		@Override
		public Component visit(Points f) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
