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

package net.gcalc.calc.models;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import net.gcalc.calc.main.HackHack;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.gui.ModelEditingDialog;

/**
 * @author jkim
 *
 */
public class RenderableModel extends Model
{
    private BufferedImage image;
    private BufferedImage thumbnailImage;
	private boolean drawn = false;

    /**
     * @param f
     */
    public RenderableModel(Function f)
    {
        super(f);
 
    }
    /**
     * @param f
     * @param s
     */
    public RenderableModel(Function f, String s)
    {
        super(f, s);
   
    }
    /**
     * @param f
     */
    public RenderableModel(Function[] f, String[] s)
    {
        super(f, s);

    }

    public void setImage(BufferedImage im)
	{
	    image = im;
	}

    public void setThumbnailImage(BufferedImage im)
	{
	    thumbnailImage = im;
	}
	
	public BufferedImage getImage()
	{
	    return image;
	}
	
	public synchronized Image getThumbnailImage(int w, int h)
	{
	    if (thumbnailImage==null) {
	        if (getImage()!=null)
	            return HackHack.generateThumbnailImage(getImage(), w, h);
	        return null;
	    }
	    
	    return thumbnailImage.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
	}
	
	public ModelEditingDialog getEditingDialog(AbstractCartesianGraphPlugin plugin)
	{
	    return new ModelEditingDialog(plugin, this);
	}
	
	public  Color getColor()
	{
	    return Color.black;
	}
	
	public void setDrawn(boolean t)
	{
	    drawn = t;
	}
	
	public boolean isDrawn()
	{
	    return drawn;
	}
}
