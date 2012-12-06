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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.gcalc.calc.main.AbstractPlugin;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Reader for plugin list.  
 * @author jkim
 *
 */
public class PluginListReader
{
    private ClassLoader classLoader;
    
    /**
     * Constructor
     * @param cl Classloader to use to load plugins.
     */
    public PluginListReader(ClassLoader cl)
    {
        classLoader = cl;
    }
    
    /**
     * Search through the children nodes of <code>pkg</code> for  tag with the 
     * name <code>tagName</code>.   
     * @param pkg XML node corresponding to a package
     * @param tagName Name of the tag to look for.
     * @return The first string within tag <code>tagName</code>.
     */
    private  String getPackageObject(Node pkg, String tagName)
    {
        NodeList nl = pkg.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node n = nl.item(i);
            if (n.getNodeName() == tagName)
            {
                NodeList children = n.getChildNodes();
                String name = children.item(0).getNodeValue();
    
                return name;
            }
        }
    
        return "No " + tagName;
    }
    
    /**
     * Get the string in the description tag in the package tag.
     * @param pkg XML node corresponding to a package
     * @return Description of package.
     */
    private  String getPackageDescription(Node pkg)
    {
        return getPackageObject(pkg, "description");
    }

    /**
     * Get the string in the name tag in the package tag.
     * @param pkg XML node corresponding to a package
     * @return Name of package.
     */
    private  String getPackageName(Node pkg)
    {
        return getPackageObject(pkg, "name");
    }

    /**
     * Construct and return the AbstractPlugin with the name in <code>className</code>.
     * 
     * @param className String containing the name of the class to be loaded.
     * @return AbstractPlugin which has the name <code>className</code>.
     * @throws Exception
     */
    private  AbstractPlugin getPlugin(String className) throws Exception
    {
        AbstractPlugin dp = null;
        try
        {
            //This will throw an exception if the class is not
            //found. Probably misspelled the class name; or
            //the classpath is weird.
            Class C = Class.forName(className, true, classLoader);
    
            //lots of things can go wrong with this...
            dp = (AbstractPlugin) C.newInstance();
    
        }
        catch (ClassNotFoundException e1)
        {
            throw new Exception("Cannot find "+e1.getMessage());
        }
    
    
        return dp;
    }

    /**
     * Construct and return leaves of the plugin/package tree.  
     * 
     * @param node XML node which denotes a plugin tag.
     * @return Leaf node
     * @throws Exception
     */
    private  DefaultMutableTreeNode getPluginTreeNode(Node node) throws Exception
    {
        String className = node.getChildNodes().item(0).getNodeValue();
        AbstractPlugin dp = getPlugin(className);
    
        if (dp == null)
            return null;
        
        return new DefaultMutableTreeNode(dp);
    }
  

    
    
    /**
     * Recusively construct a plugin package tree from a XML tree node.
     * @param packageNode &lt;package&gt; XML tree node
     * @return Root of the plugin package tree.
     * @throws Exception
     */
    private  DefaultMutableTreeNode getTreeNode(Node packageNode) throws Exception
    {
        String name = getPackageName(packageNode);
        String description = getPackageDescription(packageNode);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new Package(name, description));
    
        NodeList nl = packageNode.getChildNodes();
    
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node n = nl.item(i);
            String nodeName = n.getNodeName();
            if (nodeName.equals("package"))
                top.add(getTreeNode(n));
            else if (nodeName.equals("plugin"))
                top.add(getPluginTreeNode(n));
        }
    
        return top;
    }
    
    /**
     * Construct a tree from the InputStream (which contains a XML-formated data file).
     * 
     * @param stream
     * @return Root node of the constructed branch.
     * @throws Exception
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public synchronized DefaultMutableTreeNode getTreeRoot(InputStream stream) throws IOException, ParserConfigurationException, Exception
    {
        //set up XML parser
    	DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();    	
    	DocumentBuilder db = factory.newDocumentBuilder();
    	InputSource is = new InputSource(new InputStreamReader(stream));
    	
        //parse XML
        Document doc = db.parse(is);
        
        //construct Tree recursively
        DefaultMutableTreeNode branch = getTreeNode(doc.getDocumentElement());
        
        return branch;
    }
    
}

/**
 * Wrapper for a pair of strings that denote the name and an HTML description of a package.
 * @author jkim
 */
class Package
{
    private String name, description;

    public Package(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String toString()
    {
        return getName();
    }
} // end class