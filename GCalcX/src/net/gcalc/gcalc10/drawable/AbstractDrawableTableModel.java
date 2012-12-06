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
package net.gcalc.gcalc10.drawable;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;

import net.gcalc.gcalc10.drawable.DrawableSketcher.SketchingAlgorithm;
import net.gcalc.gcalc10.input.InputException;
import net.gcalc.gcalc10.input.InputSource;
import net.gcalc.gcalc10.script.Function;
import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.juu.parser.ParseException;

public abstract class AbstractDrawableTableModel<T extends Drawable> extends AbstractTableModel implements InputSource<T>
{
	protected String[] name;
	protected T f;

	protected AbstractDrawableTableModel(T f, String[] name) {
		this.f = f;
		this.name = name;
	}

	public T getValue() {
		return f;
	}

	public int getRowCount() {
		return name.length;
	}

	@Override
	public String getColumnName(int col) {
		return col==0 ? "Property" : "Value";
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col==1;
	}
}


class XYTableModel extends AbstractDrawableTableModel<XY>
{
	public XYTableModel(XY f) {
		super(f, new String[] { "y(x)", "Color", "Sketch Algorithm"});
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object object = null;

		if (column==0) 
			return name[row];


		switch(row) {
		case 0:
			object = f.getY();
			break;

		case 1:
			object = f.getColor();
			break;

		case 2:
			object = f.getAlgorithm();
			break;
		}

		return object;
	}

	@Override
	public void setValueAt(Object object, int row, int column) 
	{
		if (column==1) {
			switch(row) {
			case 0:
				f.setY((Function) object);
				break;

			case 1:
				f.setColor((Color) object);
				break;

			case 2:
				f.setAlgorithm((SketchingAlgorithm) object);
				break;
			}
		}
	}
}

class TRTableModel extends AbstractDrawableTableModel<TR> 
{
	public TRTableModel(TR f) {
		super(f,new String[] { "r(t)", "Color", "Default domain", "tmin", "tmax", "tsegs" , "Sketch Algorithm"});
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object object = null;

		if (column==0) 
			return name[row];


		switch(row) {
		case 0:
			object = f.getR();
			break;

		case 1:
			object = f.getColor();
			break;

		case 2:
			object = f.useDefaultDomain();
			break;

		case 3:
			object = f.getTmin();
			break;

		case 4:
			object = f.getTmax();
			break;

		case 5:
			object = f.getTsegs();
			break;

		case 6:
			object = f.getAlgorithm();
			break;

		}

		return object;
	}

	@Override
	public void setValueAt(Object object, int row, int column) 
	{
		try {
			if (column==1) {
				switch(row) {
				case 0:
					f.setR((Function) object);
					break;

				case 1:
					f.setColor((Color) object);
					break;

				case 2:
					f.setUseDefaultDomain((Boolean) object);
					break;

				case 3: 
					f.setTmin(DoubleString.getInstance((String) object));
					break;

				case 4:
					f.setTmax(DoubleString.getInstance((String) object));
					break;

				case 5:
					f.setTsegs(Integer.parseInt((String) object));
					break;

				case 6:
					f.setAlgorithm((SketchingAlgorithm) object);
					break;
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




class TXYTableModel extends AbstractDrawableTableModel<TXY> 
{
	public TXYTableModel(TXY f) {
		super(f,new String[] { "x(t)", "y(t)", "Color", "Default domain", "tmin", "tmax", "tsegs" , "Sketch Algorithm"});
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object object = null;

		if (column==0) 
			return name[row];


		switch(row) {
		case 0:
			object = f.getX();
			break;

		case 1:
			object = f.getY();
			break;

		case 2:
			object = f.getColor();
			break;

		case 3: 
			object = f.useDefaultDomain();
			break;

		case 4:
			object = f.getTmin();
			break;

		case 5:
			object = f.getTmax();
			break;

		case 6:
			object = f.getTsegs();
			break;

		case 7:
			object = f.getAlgorithm();
			break;

		}

		return object;
	}

	@Override
	public void setValueAt(Object object, int row, int column) 
	{
		try {
			if (column==1) {
				switch(row) {
				case 0:
					f.setX((Function) object);
					break;

				case 1:
					f.setY((Function) object);
					break;

				case 2:
					f.setColor((Color) object);
					break;

				case 3:
					f.setUseDefaultDomain((Boolean) object);
					break;

				case 4:
					f.setTmin(DoubleString.getInstance((String) object));
					break;

				case 5:
					f.setTmax(DoubleString.getInstance((String) object));
					break;

				case 6:
					f.setTsegs(Integer.parseInt((String) object));
					break;
				case 7:
					f.setAlgorithm((SketchingAlgorithm) object);
					break;
				}
			}
		}
		catch (ParseException e) {
			throw new InputException(object.toString(), e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

