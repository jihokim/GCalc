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


package net.gcalc.plugin.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;



/**
 * @author jkim
 */
public class SimpleColorChooser extends JPanel
{
    private PalettePanel palette;
    private ColorPanel s1, s2;

    public SimpleColorChooser()
    {
        this(Color.black);
    }
    
    public SimpleColorChooser(Color firstColor)
    {
       this(firstColor, 200,100);
    }
    
    
    public SimpleColorChooser(Color firstColor, int w, int h)
    {
        super();
        Box hbox = Box.createHorizontalBox();
        
        Box box = Box.createVerticalBox();
        box.add(s1 = new ColorPanel(Color.white));
        box.add(s2 = new ColorPanel(firstColor));
        
        hbox.add(palette=new PalettePanel());
        hbox.add(box);

        s1.setPreferredSize(new Dimension(w/3, h/2));
        s2.setPreferredSize(new Dimension(w/3, h/2));
        palette.setPreferredSize(new Dimension(2*w/3, h));
        
        this.add(hbox);
        
        palette.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e)
            {
                handleMouseEvent(s1,e);
            } 
            public void mouseClicked(MouseEvent e)
            {
                handleMouseEvent(s2,e);
                palette.setCoordinates(e.getX(), e.getY());
            }
        });
        palette.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) 
            {
                handleMouseEvent(s1,e);
            }
            
            public void mouseDragged(MouseEvent e) 
            {
                handleMouseEvent(s1,e);
            } 
        });

    }
    
    protected void handleMouseEvent(ColorPanel s, MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        
        s.setColor(palette.getColorAt(x,y));
    }
    
    public Color getColor()
    {
        return s2.getColor();
    }
    
    public void setColor(Color c)
    {
        s2.setColor(c);
        palette.clearCursors();
    }
}



class ColorPanel extends JComponent
{
    private Color color = Color.black;

    public ColorPanel()
    {
        this(Color.white);
    }

    public ColorPanel(Color c)
    {
        super();
        setColor(c);
     //   setPreferredSize(new Dimension(50, 50));
    }

    protected void paintComponent(Graphics g)
    {
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void setColor(Color c)
    {
        color = c;
        repaint();
    }

    public Color getColor()
    {
        return color;
    }
}



class PalettePanel extends ColorPanel
{
    private final static int BOGUS = -10;
    private int x = BOGUS;
    private int y = BOGUS;
    
    private int cx,cy;
    
    public PalettePanel()
    {
        super(null);
       // setPreferredSize(new Dimension(100, 100));
        
        cx=cy=BOGUS;
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent evt) {
                setCursor(evt.getX(), evt.getY());
            } 
            public void mouseDragged(MouseEvent evt) {
                setCursor(evt.getX(), evt.getY());
            }
        }
        );
        
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    protected void paintComponent(Graphics g)
    {
        int W = getWidth();
        int H = getHeight();
        int z = getHeight()/2;
        
        g.setColor(Color.white);
        g.fillRect(0,0,W,H);
        
        
        for (int i = 0; i<W; i++)
        {
            for (int j = 0; j<z; j++)
            {
                g.setColor(getColorAt(i,j));
                g.drawLine(i, j, i, j);
                g.setColor(getColorAt(i,j+z));
                g.drawLine(i, j+z, i, j+z);
            }
        }
        
        
        g.drawOval(x-2,y-2,5,5);
         g.drawLine(0,cy, getWidth(), cy);
        g.drawLine(cx,0, cx, getHeight());
    }
    
    public void setCoordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
        repaint();
    }
    
    public void clearCursors()
    {
        cx=cy=x=y=BOGUS;
        repaint();
    }
    
    public void setCursor(int x, int y)
    {
        cx = x;
        cy = y;
        repaint();
    }
    
    public Color getColorAt(int x, int y)
    {
        float hue = x/(float) getWidth();
        float v = 2*(y%(getHeight()/2))/(float) getHeight();
        
        if (y>=getHeight()/2) {
            return Color.getHSBColor(hue, 1-v, 1);
        }
        
     
        return Color.getHSBColor(hue, 1, v);
    }

}

