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


package net.gcalc.plugin.plane;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.gcalc.calc.math.functions.BooleanOperation;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.plugin.gui.InputPanel;
import net.gcalc.plugin.gui.TextInputPanel;
import net.gcalc.plugin.plane.graph.CartesianInequalityGraph;
import net.gcalc.plugin.plane.gui.FlagPanel;

/**
 *
 */
public class InequalitiesPlugin extends CartesianGraphPlugin  
{
    public InequalitiesPlugin() {
        super();
        graph = new CartesianInequalityGraph(this);
    }
    
    protected Color firstColor()
    {
        return Color.gray;
    }
    
    protected JPanel makeFlagPanel()
    {
        return new FlagPanel(graph.getProperties());
    }
    
    protected InputPanel getInputPanel()
    {
        return new TextInputPanel("Inequality", new String[] { "" }, null);
    }
     
    public String getDescription() {
        return "<p>Graphs inequalities involving x and y.</p>";
    }
    
    public String getPluginName() {
        return "Inequalities Plugin";
    }
    
    protected String[] getModelLabels()
	{
		return null;
	}
    
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource()==ip)
        {
            String s = ip.getValues()[0].trim();
            Function f = null;
            
            try
            {
                f = FunctionFactory.getFunction(s);
            }
            catch (BadSyntaxException e)
            {
                popupMessageDialog("Bad Syntax in '"+s+"'!\n"
                        +e.getMessage(), JOptionPane.ERROR_MESSAGE);
                // e.printStackTrace();
                return;
            }
            
            if (f!=null)
            {
                if (f instanceof BooleanOperation) {
                    drawGraph(f, s);
                	statusBar.setText("Graphing: "+s);
                }
                else {
                    popupMessageDialog("This plugin only graphs inequalities or boolean functions!", JOptionPane.ERROR_MESSAGE);
      
                }
            }
        }
    }

}


