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


package net.gcalc.calc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.gcalc.calc.gui.BasicFrame;
import net.gcalc.calc.gui.DebugDialog;
import net.gcalc.calc.gui.HtmlDialog;
import net.gcalc.calc.gui.ShutdownDialog;
import net.gcalc.calc.gui.ShutdownWindowAdapter;
import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.AbstractPlugin;
import net.gcalc.calc.main.DuplicatePluginException;
import net.gcalc.calc.main.ExtensionFileFilter;



/**
 * Main class of GCalc application
 * 
 * @author jkim
 */
public class GCalc extends BasicFrame implements ActionListener, TreeSelectionListener
{
    public final static String TITLE = "GCalc 3.1";
    public final static String COPYRIGHT_NOTICE = "Copyright (C) Jiho Kim 2003-2006";
    public final static String ICON_IMAGE = "resources/gicon.png";
    public final static String PLUGIN_LIST = "pluginlist.xml";
    public final static String SPLASH_IMAGE = "resources/splash1.png";
    public final static String LICENSE_AGREEMENT = "resources/GPL.txt";
    public final static String CREDITS = "resources/Credits.txt";
    
   
    /**
     * Boolean value to control overall debugging state.
     */
    private final static boolean DEBUG = true;

    private JMenuItem about, debug;
    private JMenuItem quit, open, load;
    private JMenu plugin;
    private JMenu help;
    private JMenuBar menubar;
    private JEditorPane jep;
    private PluginList pl;

    
    /** 
     * Loads icon from jar file.  If it fails, the exception is ignored.
     *
     */
    private void loadWindowIcon()
    {
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            ImageIcon icon = new ImageIcon(cl.getResource(ICON_IMAGE));
            this.setIconImage(icon.getImage());
        }
        catch (Exception e1)
        {
            //just ignore it. It's not critical.
        }
    }

    /** 
     * Constructor for GCalc.  
     */
    public GCalc()
    {
        super(TITLE);
        AbstractPlugin.setParentFrame(this);

        loadWindowIcon();

        //instantiate menu items
        about = new JMenuItem("About GCalc 3...");
        debug = new JMenuItem("Debug Info...");
      
        load = new JMenuItem("Load Plugin...");
        open = new JMenuItem("Open Plugin...");
        
        quit = new JMenuItem("Quit...");
        
        //construct and initialize menubar
        menubar = new JMenuBar();

        //make the menu
        menubar.add(plugin = new JMenu("GCalc"));
        menubar.add(Box.createHorizontalGlue());
        menubar.add(help = new JMenu("Help"));
        
    
        
        plugin.add(load);
        plugin.add(open);
        plugin.addSeparator();
        plugin.add(quit);
        help.add(about);
        help.add(debug);
        
        quit.addActionListener(this);
        about.addActionListener(this);
        load.addActionListener(this);
        open.addActionListener(this);
        debug.addActionListener(this);

        
        setJMenuBar(menubar);

     
        //set up the gui
        jep = new JEditorPane("text/html", "");
        JScrollPane sp = null;
        
        pl = new PluginList();
        pl.addTreeSelectionListener(this);
        

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, pl, sp = new JScrollPane(jep,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        splitPane.setDividerSize(2);
        splitPane.setDividerLocation(0.5);

        sp.setPreferredSize(new Dimension(250, 200));
        pl.setPreferredSize(new Dimension(250, 200));
        jep.setEditable(false);

        //add everything to the window
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", splitPane);
        
        
        //set up window
        this.pack();

        	//this code makes the main window center in a screen on a multi-head 
        //graphics display
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        
        int W = 600;
        int H = 300;
        
        int x = bounds.width/2-W/2;
        int y = bounds.height/2-H/2;
        
        this.setBounds(x,y,W,H);
        this.setResizable(true);
        this.setVisible(true);
        this.enableOpen(false);
        
       
    }

    
    /**
     * Starts GCalc. 
     * 
     * @param args command line arguments.  If the array is length 1 (one), then try to start the plugin specified.  Otherwise, start GCalc.
     */
    public static void main(String[] args)
    {
        if (args.length==1) {
            PluginStarter starter = new PluginStarter(args[0]);
            try {
                starter.start();
            }
            catch (Exception e) {
                e.printStackTrace();
                SwingGUI.popupFatalError(e.toString(), 3);
                
            }
        }
        else { 
            new GCalc();
        }
    }

   /**
    * Handle ActionEvents from GCalc's menu items.
    * 
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        if (src == quit)
        {
            shutdown();
        }
        else if (src == load)
        {
            try {
                loadFile();
            }
            catch (DuplicatePluginException e2) {
            		JOptionPane.showMessageDialog(this, "There were duplicated plugins.  Some plugins were not loaded.\n"+e2.getMessage(), "Duplicate plugins", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception e1) {
            	e1.printStackTrace();
                JOptionPane.showMessageDialog(this, "Access to filesystem is denied.\n"+e1, "Cannot load plugins!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (src == open)
        {
            pl.openSelectedPlugin();
        }
        else if (src == about)
        {
               new AboutGCalcDialog(this);
        }  
        else if (src == debug)
        {
            new Thread(new DebugDialog(this)).start();
        }

      
    }

    /**
     * Sets the description of the plugin on the right hand panel.
     * 
     * @param s description in HTML format
     */
    private void setDescription(String s)
    {
        jep.setText(s);
        jep.setCaretPosition(0);
    }

    /**
     * Controls if the 'open' menu is enabled or not.  
     * 
     * @param b
     */
   private void enableOpen(boolean b)
    {
        open.setEnabled(b);
    }

    public void shutdown() {
        if (DEBUG)
            System.exit(0);
     
        
        super.shutdown();
    }
    
    /**
     * Open the dialog for selecting a plugin library from the filesystem.
     * 
     * @throws Exception
     */
    private void loadFile() throws Exception
    {
        JFileChooser chooser = new JFileChooser();
       
        ExtensionFileFilter filter = new ExtensionFileFilter("jar", ".jar file");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                loadJar(chooser.getSelectedFile());
        }
    }
    
    /**
     * Load a plugin library (jar file)/
     * 
     * @throws Exception
     */
    private void loadJar(File f) throws Exception
    {   
    	//how do I make this classloader search the url's before delegating to the default classloader?
        URLClassLoader cl = new URLClassLoader(new URL[] {f.toURL()});
       
        PluginListReader helper = new PluginListReader(cl);        
        
        JarFile jarFile = new JarFile(f);
        Enumeration e = jarFile.entries();
   
        JarEntry entry;
        String name;
        
        DefaultMutableTreeNode top = null;
        while (e.hasMoreElements() && top==null) {
            entry = (JarEntry) e.nextElement();
            name = entry.getName();
           
            if (name.equals(PLUGIN_LIST)) {
                InputStream is = jarFile.getInputStream(entry);
                top = helper.getTreeRoot(is);
                pl.graftBranch(top);
            }
        }
    }
    
  
   
    /**
     * React to change in the selection of plugins.  Mostly, change the 
     * description the the right hand panel.
     */
    public void valueChanged(TreeSelectionEvent e)
    {        
        TreePath tp = e.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
                .getLastPathComponent();
        Object obj = node.getUserObject();

        if (obj instanceof AbstractPlugin)
        {
            AbstractPlugin dp = (AbstractPlugin) obj;
            setDescription(dp.getHTMLDescription());
            enableOpen(true);
        }
        else if (obj instanceof Package)
        {
            Package p = (Package) obj;
            String str = "<h1>" + p.getName() + "</h1><p>"
                    + p.getDescription() + "</p></font>";
            setDescription(str);
            enableOpen(false);
        }
        else
        {
            setDescription("");
            enableOpen(false);
        }
    }


    /**
     * Thread to call <code>System.exit()</code> after some time.
     * This is only for debugging. The idea is that applications will
     * die on their own and free me from having to close them.
     * 
     * @author jkim
     */
    private class DoomsdayClock implements ActionListener
    {
    	javax.swing.Timer timer;
        long time = 0;

        public DoomsdayClock(long end)
        {
        	timer = new javax.swing.Timer(1000,this);
            time = end;
        }

        public void actionPerformed(ActionEvent e)
        {
            if (System.currentTimeMillis() < time)
            {
                setTitle((time - System.currentTimeMillis()) / 1000 + " " + TITLE);
            } 
            else {	
            	System.exit(20);
            }
        }
    }

    /**
     * Dialog to show the "About GCalc" info.
     * 
     * @author jkimO
     */
    private class AboutGCalcDialog extends ShutdownDialog implements ActionListener
    {
        JButton licenseButton = null;
        JButton creditsButton = null;
           
        public AboutGCalcDialog(Frame f)
        {
            super(f, "About GCalc", true);

            ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource(SPLASH_IMAGE));

            JLabel label = new JLabel(icon);
            Box box = Box.createHorizontalBox();
            box.add(Box.createHorizontalGlue());
            box.add(licenseButton=new JButton("Show License"));
            box.add(creditsButton=new JButton("Credits"));
            box.add(Box.createHorizontalGlue());
            
            JPanel jp = new JPanel(new BorderLayout());
            jp.add("Center", label);
            jp.add("South", box);

            this.getContentPane().add(jp);

            this.addWindowListener(new ShutdownWindowAdapter(this));
         
            label.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e)
                {
                    shutdown();
                }
            });
            
            licenseButton.addActionListener(this);
            creditsButton.addActionListener(this);
            
            pack();

            center();

            setResizable(false);
            setVisible(true);
        }   
        
        public void actionPerformed(ActionEvent e) {
            if (e.getSource()==licenseButton)
                new HtmlDialog((Frame) this.getParent(), "License Agreement", LICENSE_AGREEMENT);
            else if (e.getSource()==creditsButton)
                new HtmlDialog((Frame) this.getParent(), "Credits", CREDITS);
        }
    
    }
} //end GCalc class
