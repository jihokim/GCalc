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

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.plugin.gui.InputPanel;
import net.gcalc.plugin.gui.TextInputPanel;
import net.gcalc.plugin.plane.graph.PolarGraph;
import net.gcalc.plugin.plane.gui.ModelListPanel;
 
/**
 *
 */
public class PolarGraphPlugin extends ParametricGraphPlugin  
{
    public PolarGraphPlugin() {
        super();
        graph = new PolarGraph(this);
    }
    
    protected InputPanel getInputPanel()
    {
        return new TextInputPanel(null, getModelLabels(), null);
    }
    
    protected String[] getModelLabels()
    {
    		return new String[] { "r(t)="};
    }
    
    protected JPanel makeModelListPanel()
    { 
       return new ModelListPanel(graph.getProperties(), true, true);
      }

    public String getDescription() {
        return "<p>Draws a polar graph on the plane</p>";
    }
    
    public String getPluginName() {
        return "Polar Graph Plugin";
    }
    
    public void actionPerformed(ActionEvent event)
    {
       // System.out.println(event);
        
        if (event.getSource()==ip)
        {
            String s1 = ip.getValues()[0].trim();
             
            Function r = null;
            
            try
            {
                r = FunctionFactory.getFunction(s1);
            }
            catch (BadSyntaxException e)
            {
                this.popupMessageDialog("Bad Syntax in '"+s1+"'!\n"
                        +e.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            drawGraph(new ColoredModel(r, s1, getNewColor()));
            statusBar.setText("Graphing: "+s1);
        }
    }
}