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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.gcalc.calc.main.ExtensionFileFilter;
import net.gcalc.calc.main.HackHack;
import net.gcalc.calc.main.ImageSelection;
import net.gcalc.calc.main.PrintUtilities;


/**
 * Implements a BufferedCanvas with clipboard, printing, and saving
 * capabilities.
 */
public abstract class ExtendedBufferedCanvas extends BufferedCanvas
{

    protected ExtendedBufferedCanvas()
    {
		this(false);
	}
	
	protected ExtendedBufferedCanvas(boolean antialiased)
	{
		super(antialiased);
	}
	

	/**
	 * Copies the image into the clipboard
	 * @throws UnavailableServiceException
	 */
	public void copyToClipboard()
	{
	    try {
	        //try the normal way first...
	        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(new ImageSelection(this.image), null);
	    }
	    catch (AccessControlException w) {
	        //if the above doesn't work, try JNLP. 
	        copyToClipboardJNLP();
	    }	    
	}
	
    /** Uses JNLP to copy the image into the clipboard.
     */
    private void copyToClipboardJNLP() 
    {
          
    }
    
    /** Prints the Image on this canvas.
     */
    public void printImage() 
    {
        try {
	        //try the normal way first...
            PrintUtilities.printComponent(this);
	    }
	    catch (AccessControlException w) {
	        //if the above doesn't work, try JNLP. 
	        printImageJNLP();   
	    }	    
         
    }
    
    /** Uses JNLP to print this component to paper.
     */
    private void printImageJNLP() {
        
    }
    
    /**
     * Saves the Image on this canvas to the filesystem. This method
     * prompts the user for the filename.
     */
    public void saveImage() throws IOException
    {
        try {
            //try the non-JNLP way...
            JFileChooser chooser = new JFileChooser();
            
            ExtensionFileFilter filter = new ExtensionFileFilter("png", "Portable Network Graphics (png)");
            chooser.setFileFilter(filter);
            
            int returnVal = chooser.showSaveDialog(this);
            
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                
                System.out.println(file.getAbsolutePath());
                if (! file.getName().endsWith(".png"))
                    file = new File(file.getAbsolutePath()+".png");
                
                if (! file.exists()) {
                    writeImageToFile(file);
                   
                }
                else {
                    //file does exists... confirm overwrite...
                    if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(this, "Do you want to overwrite?", "File "+file.getName()+" already exists!", JOptionPane.YES_NO_OPTION))
                        writeImageToFile(file);
                }
                System.out.println(file.getAbsolutePath());

            }
        }
        catch (AccessControlException w) {
            //if the above doesn't work, try JNLP. 
            saveImageJNLP();   
        }	 
    }
    
    /**
     * Saves the image to a file.
     * 
     * @param image
     * @param file
     * @throws IOException
     */
    private void writeImageToFile(File file) throws IOException
    {
        FileOutputStream os = new FileOutputStream(file);
        ByteArrayInputStream bais = HackHack.getByteArrayInputStream(image);
        byte[] buf = new byte[4096];
        int read=0;
        
        while ((read=bais.read(buf))!=-1)
        {	
            os.write(buf,0,read);
        }
        os.flush();
        os.close();
        bais.close();
    }
    
    /**
     * Uses JNLP to save the image to the file system.
     */
    private void saveImageJNLP() throws IOException
    {
      
    }
}

