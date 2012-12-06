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
package net.gcalc.gcalc10.vt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import net.gcalc.gcalc10.script.Function;
import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.juu.environment.Variable;


public class ValueTableModel
{
	public static int MAX_COLUMNS = 100;
	
	public static class Range
	{
		private DoubleString low;
		private DoubleString high;
		private int subdivisions;
		private double width ;

		public Range(Range range) {
			this(range.low, range.high, range.subdivisions);
		}

		public Range(double low, double high, int subdivisions) {
		}

		public Range(DoubleString low, DoubleString high, int subdivisions) {
			this.low = low;
			this.high = high;
			this.subdivisions = subdivisions;
			width = high.value-low.value;
		}

		public void setLow(DoubleString low) {
			this.low = low;
			width = high.value-low.value;
		}

		public void setHigh(DoubleString high) {
			this.high = high;
			width = high.value-low.value;
		}

		public void setSubdiv(int subdiv) {
			this.subdivisions = subdiv;
		}

		public double compute(int n) {
			return low.value+width*n/subdivisions;
		}
	}

	private List<Variable> independentVariables = new ArrayList<Variable>();
	private Map<Variable,Range> variableRanges = new TreeMap<Variable,Range>();
	private List<String> expressions = new ArrayList<String>();
	private List<Function> functions = new ArrayList<Function>();

	public Variable getVariable(int index) {
		return independentVariables.get(index);
	}

	public int getVariableCount() {
		return independentVariables.size();
	}

	public int getExpressionCount() {
		return expressions.size();
	}

	public boolean hasVariable(Variable v) {
		return variableRanges.containsKey(v);
	}

	public void removeVariables(int[] indices) {
		Arrays.sort(indices);
		List<Variable> list = new ArrayList<Variable>();
		for (int i=0; i<getVariableCount(); i++) {
			Variable v = getVariable(i);
			if (Arrays.binarySearch(indices, i)<0)
				list.add(v);
			else {
				variableRanges.remove(v);
			}
		}

		independentVariables = list;
		redefineFunctions();

		variableModel.fireTableDataChanged();
		valueModel.fireTableStructureChanged();
	}

	
	public void addVariable(Variable var, Range range)
	{
		int n = independentVariables.indexOf(var);

		if (n<0) {
			independentVariables.add(var);
		}
		
		variableRanges.put(var, range);			
		redefineFunctions();
		variableModel.fireTableDataChanged();
		valueModel.fireTableStructureChanged();
	}

	public void addExpression(String expr) {
		expressions.add(expr);

		redefineFunctions();

		exprModel.fireTableDataChanged();
		valueModel.fireTableStructureChanged();
	}
	
	public void removeExpressions(int[] indices) {
		Arrays.sort(indices);
		List<String> stringList = new ArrayList<String>();
		List<Function> funcList = new ArrayList<Function>();
		for (int i=0; i<getExpressionCount(); i++) {
			if (Arrays.binarySearch(indices, i)<0) {
				stringList.add(expressions.get(i));
				funcList.add(functions.get(i));
			}
		}
		expressions = stringList;
		functions = funcList;
		exprModel.fireTableDataChanged();
		valueModel.fireTableStructureChanged();
	}


	private void redefineFunctions() {
		functions.clear();
		Variable[] vars = independentVariables.toArray(new Variable[0]);
		for (String expr : expressions) {
			functions.add(Function.createFunction(expr, vars));
		}
	}

	private final AbstractTableModel valueModel = new AbstractTableModel() {

		@Override
		public int getColumnCount() {
			return MAX_COLUMNS+getVariableCount()+getExpressionCount();
		}

		@Override
		public int getRowCount() {
			if (variableRanges.isEmpty())
				return 0;

			int count = 1;

			for (Variable v : independentVariables) {
				Range range = variableRanges.get(v);
				count *= (range.subdivisions+1);
			}

			return count;
		}

		@Override
		public Object getValueAt(int row, int col) {
			int variableCount = getVariableCount();
			if (col>=variableCount+getExpressionCount()) {
				return "";
			}

			double[] vvec = new double[variableCount];

			int m = row;
			int i = 0;

			for (Variable v: independentVariables) {
				Range range = variableRanges.get(v);
				int n = m % (range.subdivisions+1);
				m /= (range.subdivisions+1);
				vvec[i] = range.compute(n);
				i++;
			}

			if (col>=variableCount) {
				Function f = functions.get(col-variableCount);
				return f.eval(vvec);
			}

			return vvec[col];
		}

		@Override
		public String getColumnName(int col) {
			int variableCount = getVariableCount();
			if (col>=variableCount+getExpressionCount()) {
				return null;
			}
			else if (col>=variableCount) {
				return expressions.get(col-variableCount);
			}

			return independentVariables.get(col).name;
		}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return Double.class;
		}		
	};

	private final AbstractTableModel variableModel = new AbstractTableModel() {
		String[] colNames = { "Variable", "Low", "High", "Subdivisions" };
		Class<?>[] classes = new Class<?>[] {
				String.class,
				DoubleString.class,
				DoubleString.class,
				Integer.class
		};

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return getVariableCount();
		}

		@Override
		public Object getValueAt(int row, int col) {

			Variable var = independentVariables.get(row);
			Range range = variableRanges.get(var);
			switch (col) {
			case 0: 
				return var.name;

			case 1: 
				return range.low;

			case 2: 
				return range.high;

			case 3: 
				return range.subdivisions;
			}

			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return classes[columnIndex];
		}

		@Override
		public String getColumnName(int columnIndex) {
			return colNames[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object val, int row, int col) {
			Variable var = independentVariables.get(row);
			Range range = variableRanges.get(var);

			switch (col) {
			case 0: {
				Variable v = Variable.get((String) val);
				if (! variableRanges.containsKey(v)) {
					independentVariables.set(row, Variable.get((String) val));
					variableRanges.put(independentVariables.get(row), new Range(variableRanges.get(var)));
					variableRanges.remove(var);
					redefineFunctions();
					fireChange(true);
				}
				else {
					fireChange(false);
				}

			}
			break;

			case 1: 
				range.setLow((DoubleString) val);
				fireChange(false);
				break;

			case 2: 
				range.setHigh((DoubleString) val);
				fireChange(false);
				break;

			case 3: 
				range.setSubdiv((Integer) val);
				fireChange(false);
				break;
			}
		}

		private void fireChange(boolean structure)
		{
			fireTableDataChanged();
			if (structure) 
				valueModel.fireTableStructureChanged();
			else
				valueModel.fireTableDataChanged();
		}
	};

	private final AbstractTableModel exprModel = new AbstractTableModel() {

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return expressions.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			return functions.get(row);
		}

		@Override
		public String getColumnName(int col) {
			return "Expression";
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return true;
		}

		@Override
		public void setValueAt(Object val, int row, int col) {
			functions.set(row, (Function) val);
			expressions.set(row, val.toString());

			this.fireTableDataChanged();
			valueModel.fireTableStructureChanged();
		}
	};
	
	

	public AbstractTableModel getValueTableModel() {
		return valueModel;
	}

	public AbstractTableModel getExpressionTableModel() {
		return exprModel;
	}

	public AbstractTableModel getVariableModel() {
		return variableModel;
	}	
}
