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


package net.gcalc.plugin.plane.graph;



import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Observer;

import net.gcalc.calc.models.Model;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.plugin.gui.ExtendedBufferedCanvas;
import net.gcalc.plugin.plane.gui.CoordinatePanel;
import net.gcalc.plugin.properties.GraphProperties;

/**
 * An abstract GraphCanvas, a gui element that displays graphs. This
 * class is just an convenience abstraction. If there is only 2D
 * graphs, this class probably should be merged with its immediate
 * child, CartesianGraphCanvas, but I think this class is a good
 * foundation to start for 3D graphing.
 */

public abstract class GraphCanvas extends ExtendedBufferedCanvas implements Observer
{
    protected GraphProperties properties;
    protected CoordinatePanel coordinatePanel;

    /**
     * Default constructor.
     */
    public GraphCanvas(GraphProperties gp)
    {
        super();

        properties = gp;

        setDefaultProperties();
        properties.addObserver(this);

        resetSize();

        this.setCursor(defaultCursor());
    }
    
    public GraphProperties getProperties()
    {
        return properties;
    }

    private void setDefaultProperties()
    {
        properties.put(GraphProperties.MODEL_LIST, new ModelList());

        setDefaultScreenDimension();
        setDefaultView();
        setDefaultGraphElements();
        setDefaultColors();
        setDefaultFonts();

        properties.revertToDefault();
    }

    protected void setDefaultScreenDimension()
    {
        properties.put(GraphProperties.SCREEN_DIMENSION, new Dimension(325, 325));
    }

    abstract void setDefaultFonts();
    abstract void setDefaultView();

    abstract void setDefaultGraphElements();

    abstract void setDefaultColors();
    
    protected ModelList getModelList()
    {
        return (ModelList) properties.get(GraphProperties.MODEL_LIST);
    }

    /**
     * The default cursor for graph canvas is the
     * Cursor.CROSSHAIR_CURSOR.
     */
    public Cursor defaultCursor()
    {
        return new Cursor(Cursor.CROSSHAIR_CURSOR);
    }

    /**
     * Resets size of the canvas to the default specified in
     * GraphProperties.SCREEN_DIMENSION.
     */
    public void resetSize()
    {
        properties.revertToDefault(GraphProperties.SCREEN_DIMENSION);
        resetSize((Dimension) properties.get(GraphProperties.SCREEN_DIMENSION));
    }

    /**
     * Removes any plots from the graph canvas.
     */
    public void clear()
    {
        ModelList ml = (ModelList) properties.get(GraphProperties.MODEL_LIST);
        ml.removeAllModels();
        redrawAll();
    }
    
    public boolean isOnScreen(int x, int y)
    {
        return 0<=x && x<=getWidth() && 0<=y && y<=getHeight();
    }
    
    protected void drawBackgroundComponents()
    {
        drawGrid();
        drawAxes();
        drawScale();
        drawLabel();
        drawAxesTitle();
    }
    

    /**
     * The implementation of this method specifies the order in which
     * various graph components (grid, axes, scale, plots) are drawn
     * and shown.
     */
    protected void drawGraphComponents()
    {
        drawBackgroundComponents();
        
        getModelList().flagAll(false);
        drawModelList();

        draw();
        
    }

    protected void draw()
    {
    // this is so that you can bypass the model system completely.
    // If you don't need a list of models, implement this
    // function.
    }

    /**
     * Calling this method causes the canvas to redraw everything from
     * the beginning. It's useful when you've changed a graph
     * property.
     */
    public void redrawAll()
    {
       redrawAll(true);
    }
    
    /**
     * Calling this method causes the canvas to redraw everything from
     * the beginning. It's useful when you've changed a graph
     * property.
     */
    public void redrawAll(boolean useCache)
    {
        super.clear();
        
        if (!useCache)
            getModelList().deleteCache();
        
        drawGraphComponents();
        repaint();
    }

    /**.1
     * Draws the axes
     */
    abstract protected void drawAxes();

    /**
     * Draws the grid
     */
    abstract protected void drawGrid();

    /**
     * Draws the scale
     */
    abstract protected void drawScale();

    /**
     * Draws the label
     */
    abstract protected void drawLabel();

    /**
     * Draws the axes title
     */
    abstract protected void drawAxesTitle();

    /**
     * A public interface to draw a Model on this GraphCanvas.
     * 
     * @see Model
     */
    public void draw(Model fg)
    {
        getModelList().add(fg);

        drawModelList();

        properties.setPropertyChanged(GraphProperties.MODEL_LIST);
        
       repaint();
    }

    /**
     * Draws a single Model. Each subclass should override this method
     * to do its specific drawing.
     */
    abstract protected void draw(RenderableModel F);

    /**
     * Iterates through the Models and draws any that are not drawn.
     */
    protected void drawModelList()
    {
        ModelList modelList = getModelList();
        RenderableModel model;

        for (int i = 0; i<modelList.getSize(); i++)
        {
            model = (RenderableModel) modelList.getModelAt(i);

            if (model!=null && !model.isDrawn())
            {
                draw(model);
                model.setDrawn(true);
            }
        }
        
        modelList.poke();
    }
    
    public void clearModelList() {
    		getModelList().removeAllModels();
    }
}


