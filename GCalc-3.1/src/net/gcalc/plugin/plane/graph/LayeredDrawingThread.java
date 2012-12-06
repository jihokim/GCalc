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
 * Email: jiho@gcalcul.us
 * Web: http://gcalcul.us
 * 
 * Snail Mail: 
 *   Jiho Kim
 *   1002 Monterey Lane
 *   Tacoma, WA 98466
 */


package net.gcalc.plugin.plane.graph;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.gcalc.calc.main.HackHack;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;



/**
 * @author jkim
 */
public abstract class LayeredDrawingThread extends DrawingThread
{
    protected ModelList modelList;
    
    //hack hack hack vars
    protected int modelIdx = 0;
    protected int modelIdxMax = 1;
       
    
    protected LayeredDrawingThread(Component parent, Graphics g, ModelList m)
    {
        super(parent, g, "Rendering in progress...");
        modelList = m;
    }
    
    public void run()
    {
        
        BufferedImage image = null;
        Object[] notDrawnModel = modelList.getNotDrawnModels();
        modelIdxMax = notDrawnModel.length;
        
        progressMonitor.setMaximum(notDrawnModel.length*100);
        progressMonitor.setMinimum(0);
        
        for (int i=0; i<notDrawnModel.length; i++) {
            RenderableModel model = (RenderableModel) notDrawnModel[i];
            image = model.getImage();
            modelIdx = i;
            
            if (image==null) 
            {
                image = new BufferedImage(parent.getWidth(), parent.getHeight(), BufferedImage.TYPE_INT_ARGB);
           
                render(i, image.getGraphics(), model); 
            }
            
            if (! isDead()) {
                graphics.drawImage(image, 0,0,null);
                model.setDrawn(true);
                model.setImage(image);
                model.setThumbnailImage(HackHack.generateThumbnailImage(image,40,40));
            }
            
            if (isDead()) 
                break;
        }            
     
        progressMonitor.close();
        finishRun();
        
        modelList.poke();

    }
    
    protected abstract void finishRun();
    protected abstract void render(int layer, Graphics g, RenderableModel model);
}

