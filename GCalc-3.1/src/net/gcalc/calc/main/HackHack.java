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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @author jkim
 *
 */
public class HackHack
{
    public static Color randomColor()
    {
        Random random = new Random();
        return new Color(random.nextFloat(),random.nextFloat(),random.nextFloat());
    }
    
    public static BufferedImage generateThumbnailImage(BufferedImage image, int width, int height) {
	    BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) thumbnail.getGraphics();
        g2.setColor(new Color(1,1,1,0f));
        g2.fillRect(0,0,width,height);
    
        int w = image.getWidth()/width+1; 
        int h = image.getHeight()/height+1; 
        
        int pixel=0;
        int alpha;
        ColorModel colorModel = image.getColorModel();
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++) {
                int min = Integer.MAX_VALUE;
                for (int u=i*w; u<(i+1)*w && u<image.getWidth(); u++)
                    for (int v=j*h; v<(j+1)*h && v<image.getHeight(); v++) {
                        pixel = image.getRGB(u,v);  
                        alpha = colorModel.getAlpha(pixel);

                        if (pixel<min && alpha!=0)
                            min = pixel;
                    }

                Color c = new Color(min);
                if (! c.equals(Color.white)) {
                    g2.setColor(c);
                    g2.drawLine(i,j,i,j);
                }
            }
          return thumbnail;
	}
    
    public static BufferedImage checkeredImage(Color fore, Color back, int w, int h, int s)
    {
        BufferedImage image =new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.white);
        g.clearRect(0,0,w,h);
        
        for (int i=0; i<w; i+=s)
            for (int j=0; j<h; j+=s) {
                if ((i/s+j/s)%2==0)
                    g.setColor(fore);
                else
                    g.setColor(back);
                g.fillRect(i,j,s,s);
            }
        
        return image;
    }
    
    /*
    
    private static final Class[] parameters = new Class[]{URL.class};

    
    public static void addStringToRuntimeClasspath(String s) throws IOException {
        File f = new File(s);
        addFileToRuntimeClasspath(f);
    }
    
    public static void addFileToRuntimeClasspath(File f) throws IOException {
        addURLToRuntimeClasspath(f.toURL());
    }
    
    public static void addURLToRuntimeClasspath(URL u) throws IOException {
        	URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        	Class sysclass = URLClassLoader.class; 
        try {
            Method method = sysclass.getDeclaredMethod("addURL",parameters);
            method.setAccessible(true);
            method.invoke(sysloader,new Object[]{ u });
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
    */
    
    
    public static ByteArrayInputStream getByteArrayInputStream(BufferedImage image) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.setUseCache(false);
        ImageIO.write(image, "png", baos);
        baos.close();
        return new ByteArrayInputStream(baos.toByteArray());
    
    }
}
