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


package net.gcalc.plugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


/** Implements a double-buffered canvas.  
 * 
 *  (Can the double-buffering be done a better way?  This codes is a
 *  port from Java 1.1 code.)
 */
public abstract class BufferedCanvas extends JPanel
{
	private int maxImgWidth = 0, maxImgHeight = 0;

	protected BufferedImage image = null;
	protected Graphics2D gr;
	
	private boolean antialiased;
	
	/**
	 * Default constructor.  Sets the color to transparent white.
	 *
	 */
	protected BufferedCanvas()
	{
		this(false);
	}
	
	/**
	 * Default constructor.  Sets the color to transparent white.
	 *
	 */
	protected BufferedCanvas(boolean antialiased)
	{
		super();
		setBackground(new Color(1,1,1,0f));
		this.antialiased = antialiased;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		update(g);
	}

	/**
	 * Draws the BufferedImage onto the specified graphics.
	 */
	public void update(Graphics g)
	{
		if (image == null)
		{
			initImage();

			clear();
		}

		g.drawImage(image, 0, 0, this);
	}

	/** Clears canvas with background color.
	 */
	public void clear()
	{
		if (gr == null)
			return;

		gr.setColor(getBackground());
		gr.fillRect(0, 0, this.getWidth(), this.getHeight());

		repaint();
	}

	/**
	 * Create a new buffer.
	 *
	 */
	public void initImage()
	{
		image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		gr = image.createGraphics();

		if (antialiased) {
		    gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	/** 
	 * 	Resizes the canvas.  If the canvas becomes to big for the
	 *  buffer, create a bigger buffer.
	 */
	public void resetSize(Dimension d)
	{
		int x = d.width;
		int y = d.height;

		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setSize(d);

		if (x > maxImgWidth || y > maxImgHeight)
		{
			Image oldimg = image;
			initImage();

			if (oldimg != null)
				gr.drawImage(oldimg.getScaledInstance(x, y, Image.SCALE_DEFAULT), 0, 0, this);
		}

		//	System.out.println("size = "+getWidth()+"\t"+getHeight()+"\t"+d);
	}
	
	

}

