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


package net.gcalc.calc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.AbstractPlugin;
import net.gcalc.calc.main.DuplicatePluginException;



/**
 * GUI Wrapper for the plugin list/tree on the left-hand panel.
 * 
 * @author jkim
 */
class PluginList extends JPanel
{
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;

    /**
     * Constructor. Mostly does error handling for <code>init()</code>
     * which does most of the work.
     */
    public PluginList()
    {
        super(new BorderLayout());

        try
        {
            init();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        catch (ClassNotFoundException e)
        {
            System.out.println(e);
        }

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setMinimumSize(new Dimension(dim.width/2, dim.height/2));
    }
    
    /**
     * Refactoring of the constructor.  Called by constructor.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void init() throws IOException, ClassNotFoundException
    {
        ClassLoader cl = this.getClass().getClassLoader();
        PluginListReader plh = new PluginListReader(cl);
        DefaultMutableTreeNode top = null;

        try
        {
            top = plh.getTreeRoot(cl.getResourceAsStream(GCalc.PLUGIN_LIST));
        }
        catch (Exception e)
        {
            SwingGUI.popupFatalError("Error reading builtin plugin list.\n("+e.getMessage()+")", 2);
        }

        root = new DefaultMutableTreeNode("Plugins");
        root.add(top);

        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
   
        DefaultMutableTreeNode node = top;
        while (node.getChildCount()>0)
        {
            node = (DefaultMutableTreeNode) node.getChildAt(0);
        }
        node = (DefaultMutableTreeNode) node.getParent();

        tree.expandPath(new TreePath(node.getPath()));

        JScrollPane treeView = new JScrollPane(tree);
        this.add(treeView);

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow!=-1)
                {
                    if (e.getClickCount()==2)
                        doubleClicked(selPath);
                }
            }
        };

        tree.addMouseListener(ml);

    } //end init


    public void addTreeSelectionListener(TreeSelectionListener l)
    {
        //do what's expected.
        tree.addTreeSelectionListener(l);

        //This line makes sure that something is selected at the
        //beginning. Otherwise the description panel
        //would be empty
        tree.addSelectionRow(1);
    }

    /**
     * Handle a double click on the tree.
     * @param selPath The TreePath to the node which was selected
     */  
    private void doubleClicked(TreePath selPath)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (node.getUserObject() instanceof AbstractPlugin)
            openSelectedPlugin();
    }

    /**
     * Open (or initialize/start) the selected plugin.  The method 
     * is public to allow other gui elements to start plugins.
     *
     */
    public void openSelectedPlugin()
    {
        AbstractPlugin dp = getSelectedPlugin();
        if (!dp.isInitialized()) {
            dp.init();
        }

        dp.setVisible(true);
    }
    
    /**
     * Get the plugin that's selected.  Called by <code>openSelectedPlugin</code>
     * 
     * @return
     */
    private AbstractPlugin getSelectedPlugin()
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        AbstractPlugin dp = (AbstractPlugin) node.getUserObject();
        return dp;
    }

    
    /**
     * Finds all the leaves of on the list
     * @param root
     * @return
     */
    private Vector getPluginNodes(DefaultMutableTreeNode root)
    {
    	Enumeration enumeration = root.depthFirstEnumeration();
    	
    	Vector list = new Vector();
    	
    	DefaultMutableTreeNode node;
    	
    	while (enumeration.hasMoreElements())
    	{
    		node = (DefaultMutableTreeNode) enumeration.nextElement();
    		if (node.isLeaf()) {
    			list.add(node);
    		}
    	}
    
    	return list;
    }
 
   
    /**
     * Add a branch to the root of the tree.  Called by <code>loadJar()</code>
     * @param node Root of the branch.
     */
    public void graftBranch(DefaultMutableTreeNode node) throws DuplicatePluginException
    {
    	int badplugin = 0;
    	
    	//look for duplicate plugins...
    	Vector existingNodes = getPluginNodes(root);
    	Vector newNodes = getPluginNodes(node);
    	
      	for (int i=0; i<existingNodes.size(); i++) {
      		DefaultMutableTreeNode nd = (DefaultMutableTreeNode) existingNodes.elementAt(i);
          	
          	for (int j=0; j<newNodes.size(); j++) {
          		DefaultMutableTreeNode iternode = (DefaultMutableTreeNode) newNodes.elementAt(j);
          		if (nd.getUserObject().equals(iternode.getUserObject())) {
          			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) iternode.getParent();
          			DefaultMutableTreeNode grandparent =null;
          			iternode.removeFromParent();
          			badplugin++;
          			while (parent!=null && parent.getChildCount()==0) {
          				grandparent = (DefaultMutableTreeNode) parent.getParent();
          				parent.removeFromParent();
          				parent = grandparent;
          			}
          			
          			System.out.println(iternode);
          		}
          	}
      	}
      	
      	if (! node.isLeaf())
      		treeModel.insertNodeInto(node, root, root.getChildCount());
      	
      	if (badplugin>0)
      		 throw new DuplicatePluginException(badplugin+"/"+newNodes.size()+" plugin(s) ignored.");
      	
    }
} //end class
