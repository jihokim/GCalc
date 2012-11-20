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
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.plugin.gui.InputPanel;
import net.gcalc.plugin.gui.TextInputPanel;
import net.gcalc.plugin.plane.graph.ImplicitFunctionGraph;
import net.gcalc.plugin.plane.gui.FlagPanel;

/**
 * 
 */
public class ImplicitFunctionPlugin extends CartesianGraphPlugin  
{
    public ImplicitFunctionPlugin() {
        super();
        graph = new ImplicitFunctionGraph(this);
    }
  
    
    protected JPanel makeFlagPanel()
    {
        return new FlagPanel(graph.getProperties());
    }
    
    protected InputPanel getInputPanel()
    {
        return new TextInputPanel("Function", getModelLabels(), null);
    }
     
    public String getDescription() {
        return "<p>Graphs the zero set of functions of x and y.  </p>"+
    "<p>The <i>zero set</i> of a function f(x,y) is the set of points where f(x,y)=0.</p>";
}
    
    public String getPluginName() {
        return "Implicit Function Plugin";
    }
    
    protected String[] getModelLabels()
    	{
    		return new String[] { "f(x,y)=" };
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
                this.popupMessageDialog("Bad Syntax in '"+s+"'!\n"
                        +e.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (f!=null)
            {
                drawGraph(f, s);
                statusBar.setText("Graphing: "+s);
            }
        }
    }

    
 
    
}


