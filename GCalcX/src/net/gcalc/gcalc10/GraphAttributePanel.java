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

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import net.gcalc.gcalc10.util.SpringUtilities;
import net.gcalc.gcalc10.util.Util;

public class GraphAttributePanel extends JPanel 
{
	private final GCalcInit init;
	
	public GraphAttributePanel(GCalcInit init, GraphAttributes ga) {
		super(new SpringLayout());
		
		this.init = init;
		
		AbstractButton gridButton = Util.createAttributeCheckBox(ga.GRID);
		AbstractButton axesButton = Util.createAttributeCheckBox(ga.AXES);
		AbstractButton labelsButton = Util.createAttributeCheckBox(ga.LABELS);
		AbstractButton ticksButton = Util.createAttributeCheckBox(ga.TICKS);
		
		DoubleSlider strokeWidth = Util.createDoubleSlider(ga.STROKE_WIDTH, 4, 
				init.getDouble("gui.graph_attributes.stroke.minValue"), 
				ga.getValue(ga.STROKE_WIDTH), 
				init.getDouble("gui.graph_attributes.stroke.maxValue"));
		JTextField gridxgap = Util.createDoubleField(ga.GRID_XGAP);
		JTextField gridygap = Util.createDoubleField(ga.GRID_YGAP);

		JTextField tmin = Util.createDoubleField(ga.TMIN);
		JTextField tmax = Util.createDoubleField(ga.TMAX);
		JTextField tsegs = Util.createIntegerField(ga.TSEGS);

		add(getLabel("gui.graph_attributes.grid.title"));
		add(gridButton);
		add(getLabel("gui.graph_attributes.axes.title"));
		add(axesButton);
		add(getLabel("gui.graph_attributes.ticks.title"));
		add(ticksButton);
		add(getLabel("gui.graph_attributes.labels.title"));
		add(labelsButton);
		add(getLabel("gui.graph_attributes.stroke.title"));
		add(strokeWidth);
		add(getLabel("gui.graph_attributes.xgap.title"));
		add(gridxgap);
		add(getLabel("gui.graph_attributes.ygap.title"));
		add(gridygap);
		add(getLabel("gui.graph_attributes.tmin.title"));
		add(tmin);
		add(getLabel("gui.graph_attributes.tmax.title"));
		add(tmax);
		add(getLabel("gui.graph_attributes.tsegs.title"));
		add(tsegs);
		SpringUtilities.makeCompactGrid(this, 10, 2, 0, 0, 2, 2);
		setBorder(new TitledBorder(init.getString("gui.graph_attributes.title")));
	}
	
	private JLabel getLabel(String attribute) {
		return new JLabel(init.getString(attribute), JLabel.TRAILING);
	}
}
