/** 
 * GCalc 3.0
 * Copyright (C) 2005 Jiho Kim 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * Email: jiho@gcalc.net
 * Web: http://gcalc.net
 * 
 * Snail Mail: 
 *   Jiho Kim
 *   1002 Monterey Lane
 *   Tacoma, WA 98466
 */

package net.gcalc.plugin.plane.graph;

import java.awt.image.BufferedImage;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.SymbolTableException;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.properties.GraphProperties;
import net.gcalc.plugin.properties.Range;



public class PolarGraph extends ParametricGraph 
{   
    private SymbolTable st ;
    private Function x;
    private Function y;
    
    public PolarGraph(AbstractCartesianGraphPlugin plugin)
    {
        super(plugin);
        this.getProperties().put(GraphProperties.V_TITLE_STRING, "y");
        st = new SymbolTable();
        try {
            x = FunctionFactory.getFunction("cos(t)*r");
            y = FunctionFactory.getFunction("sin(t)*r");
        }
        catch (BadSyntaxException e1)
        {
            //shouldn't happen
        }
    }
      
    public void setDefaultGraphElements()
    {
        boolean b = true;

        properties.initDefault(GraphProperties.H_AXIS, b);
        properties.initDefault(GraphProperties.H_GRID, !b);
        properties.initDefault(GraphProperties.H_SCALE, !b);
        properties.initDefault(GraphProperties.V_AXIS, b);
        properties.initDefault(GraphProperties.V_GRID, !b);
        properties.initDefault(GraphProperties.V_SCALE, !b);
        properties.initDefault(GraphProperties.V_TITLE, b);
        properties.initDefault(GraphProperties.H_TITLE, b);
        properties.initDefault(GraphProperties.V_LABEL, b);
        properties.initDefault(GraphProperties.H_LABEL, b);
        properties.initDefault(GraphProperties.INTERACTIVE_ZOOM, b);
    }
    
    protected void draw(RenderableModel model)
    {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Function r = model.getFunction(0);
        
        try {
            st.setVariable(VariableToken.R_VAR, r);
        }
        catch (SymbolTableException e)
        {
            //this'll happen when the function refers to r.
            System.err.println(e);
            return;
        }
        Range T = properties.getViewProperty(GraphProperties.VIEW).getRange(2);

        draw(x,y, T, model.getColor(), buffer.getGraphics(), st);
        gr.drawImage(buffer,0,0,null);
        model.setImage(buffer);
    }
    
  
}




