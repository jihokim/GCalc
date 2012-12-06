/** 
GCalc 3.0
Copyright (C) 2005 Jiho Kim 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Email: jiho@gcalcul.us
Web: http://gcalcul.us

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.plugin.properties;

public class GraphProperties extends Properties
{
	public final static String PLUGIN = Unique.string();
	public final static String GRAPH_CANVAS = Unique.string();
	public final static String GRAPH_PROPERTIES_DIALOG = Unique.string();
	public final static String MODEL_LIST = Unique.string();
	public final static String SCREEN_DIMENSION = Unique.string();
	public final static String SCREEN_DIMENSION_UPDATED = Unique.string();
	public final static String VIEW=Unique.string();
	public final static String H_AXIS=Unique.string();
	public final static String H_SCALE=Unique.string();
	public final static String H_GRID=Unique.string();
	public final static String H_TITLE=Unique.string();
	public final static String H_LABEL=Unique.string();
	public final static String V_AXIS=Unique.string();
	public final static String V_SCALE=Unique.string();
	public final static String V_GRID=Unique.string();	
	public final static String V_TITLE=Unique.string();
	public final static String V_LABEL=Unique.string();
	public final static String H_AXIS_COLOR=Unique.string();
	public final static String H_SCALE_COLOR=Unique.string();
	public final static String H_GRID_COLOR=Unique.string();
	public final static String H_TITLE_COLOR=Unique.string();
	public final static String H_LABEL_COLOR=Unique.string();
	public final static String V_AXIS_COLOR=Unique.string();
	public final static String V_SCALE_COLOR=Unique.string();
	public final static String V_GRID_COLOR=Unique.string();
	public final static String V_TITLE_COLOR=Unique.string();
	public final static String V_LABEL_COLOR=Unique.string();
	
	//?
	public final static String H_TITLE_STRING=Unique.string();
	public final static String V_TITLE_STRING=Unique.string();
	
	
	public final static String SHOW_CONCAVITY=Unique.string();
	public final static String SHOW_MONOTONICITY=Unique.string();
	public final static String SYMBOL_TABLE=Unique.string();
	public final static String VALUE_TABLE=Unique.string();
	public final static String AXES_LABEL_FONTS=Unique.string();
	public final static String TRACE=Unique.string();	
	public final static String ZOOMS=Unique.string();
	public final static String INTERACTIVE_ZOOM=Unique.string();
	public final static String THICK_GRAPH=Unique.string();
	public final static String INCREASING_COLOR=Unique.string();
	public final static String DECREASING_COLOR=Unique.string();
	public final static String CONCAVITY_COLOR=Unique.string();
	
	public final static String TOLERANCE=Unique.string();
	public final static String MAX_ITER=Unique.string();
	

	public GraphProperties()
	{
		super();	
	}
	
	public GraphProperties(String filename)
	{
		super(filename);
	}	
}

