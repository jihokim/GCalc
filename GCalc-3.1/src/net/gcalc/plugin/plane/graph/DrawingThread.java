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

import javax.swing.ProgressMonitor;



/**
 * @author jkim
 */
public class DrawingThread extends Thread
{
    private boolean die;

    //should these have access methods?
    protected Graphics graphics;
  
    protected long beginTime;
    protected ProgressMonitor progressMonitor;
    protected Component parent;

    protected DrawingThread(Component parent, Graphics g, String message)
    {
        graphics = g;
        die = false;
        this.parent = parent;
        progressMonitor = new ProgressMonitor(parent, message, "", 0, 100);
        progressMonitor.setMillisToDecideToPopup(1000);
   
       // graphics.setComposite(AlphaComposite.SrcOver);
    }

    protected DrawingThread(Component parent, Graphics g)
    {
        this(parent, g, "Rendering in progress...");
    }

    public void kill()
    {
        die = true;
    }

    public boolean isDead()
    {
        return die || progressMonitor.isCanceled();
    }

    public void start()
    {
        beginTime = System.currentTimeMillis();

        super.start();
    }
}

