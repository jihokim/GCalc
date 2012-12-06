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



package net.gcalc.calc.main;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;



/** A simple utility class that lets you very simply print
 *  an arbitrary component.
 *
 *  Adapted from:
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */
public class PrintUtilities implements Printable
{
    private Component componentToBePrinted;
    int dpi;

    //non JNLP printing
    
    public static void printComponent(Component c)
    {
        new PrintUtilities(c).print();
    }
    

   
    protected PrintUtilities(Component componentToBePrinted)
    {
        this(componentToBePrinted, -1);
    }

    protected PrintUtilities(Component componentToBePrinted, int dpi)
    {
        this.componentToBePrinted = componentToBePrinted;
        this.dpi = dpi;
    }

  

    
    public void print()
    {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try
            {
                printJob.print();
            }
            catch (PrinterException pe)
            {
                System.out.println("Error printing: " + pe);
            }
        }
    }
    

    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
    {
        if (pageIndex>0)
        {
            return (NO_SUCH_PAGE);
        }
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.translate(pageFormat.getImageableX(),
                pageFormat.getImageableY());
        
        if (dpi!=-1)
        {
            double scale = 72.0/dpi;
            g2d.scale(scale, scale);
        }
        
        disableDoubleBuffering(componentToBePrinted);
        componentToBePrinted.paint(g2d);
        enableDoubleBuffering(componentToBePrinted);
        return (PAGE_EXISTS);
    }

    /** The speed and quality of printing suffers dramatically if
     *  any of the containers have double buffering turned on.
     *  So this turns if off globally.
     */
    public static void disableDoubleBuffering(Component c)
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /** Re-enables double buffering globally. */
    public static void enableDoubleBuffering(Component c)
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}