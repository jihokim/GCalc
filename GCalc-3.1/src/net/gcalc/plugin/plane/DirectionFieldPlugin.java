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

Email: jiho@gcalc.net
Web: http://gcalc.net

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/


package net.gcalc.plugin.plane;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.plugin.gui.InputPanel;
import net.gcalc.plugin.gui.TextInputPanel;
import net.gcalc.plugin.plane.graph.DirectionFieldGraph;
import net.gcalc.plugin.plane.gui.FlagPanel;
import net.gcalc.plugin.plane.gui.ModelListPanel;
import net.gcalc.plugin.properties.GraphProperties;

/**
 * 
 */
public class DirectionFieldPlugin extends CartesianGraphPlugin  
{
    public DirectionFieldPlugin() {
        super();
        graph = new DirectionFieldGraph(this);
    }
  
    
    protected JPanel makeFlagPanel()
    {
        return new FlagPanel(graph.getProperties(), 6,2, 
                new String[]{
                GraphProperties.H_GRID, 
                GraphProperties.V_GRID, 
                GraphProperties.H_AXIS, 
                GraphProperties.V_AXIS, 
                GraphProperties.H_LABEL, 
                GraphProperties.V_LABEL, 
                GraphProperties.H_SCALE, 
                GraphProperties.V_SCALE, 
        });
    }
    
    protected InputPanel getInputPanel()
    {
        return new TextInputPanel("Direction Field", getModelLabels(), null);
    }
    
    protected JPanel makeModelListPanel()
    { 
       return new ModelListPanel(graph.getProperties(), false, true);
      }
    
    protected String[] getModelLabels() {
    		return new String[] { "dx/dt=f(x,y)=","dy/dt=g(x,y)=" }	;
    }

    public String getDescription() {
        return "<p>Draws the direction field given by dx/dt=f(x,y) and dy/dt=g(x,y).</p>"
        +"<p>Useful for investigating planar autonomous systems.</p>";
    }
    
    public String getPluginName() {
        return "Direction Field Plugin";
    }
    
    public void actionPerformed(ActionEvent event)
    {
       // System.out.println(event);
        
        if (event.getSource()==ip)
        {
            String s1 = ip.getValues()[0].trim();
            String s2 = ip.getValues()[1].trim();
            
            Function f = null;
            Function g = null;
            
            try
            {
                f = FunctionFactory.getFunction(s1);
            }
            catch (BadSyntaxException e)
            {
                this.popupMessageDialog("Bad Syntax in '"+s1+"'!\n"
                        +e.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try
            {
                g = FunctionFactory.getFunction(s2);
            }
            catch (BadSyntaxException e)
            {
                this.popupMessageDialog("Bad Syntax in '"+s2+"'!\n"
                        +e.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (f!=null)
            {
                ((DirectionFieldGraph) graph).drawSystem(new RenderableModel(new Function[] { f,g}, new String[] {s1, s2}));
        //        statusBar.setText("Graphing: "+s1+"/"+s2);
            }
        }
    }
}