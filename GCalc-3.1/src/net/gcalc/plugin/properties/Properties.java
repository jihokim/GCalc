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


package net.gcalc.plugin.properties;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
0) make some unified infrastructure for storing/changing properties
1) strip the dialog of all the gui stuff
2) make a separate class of each tab, which individually interacts with the property storage class from step 0.
3) make a way for the plugin to specify the tabs to put into its properties dialog.
*/

public class Properties extends Observable {

	private Hashtable table;
	private Hashtable defaultTable;

	public String toString() {
		Enumeration E = defaultTable.keys();
		String s = "*\n";

		while (E.hasMoreElements()) {
			String key = (String) E.nextElement();
			s += key + "=";
			s += defaultTable.get(key) + "\n";
		}

		return s + "*";
	}

	public Properties() {
		table = new Hashtable();
		defaultTable = new Hashtable();
	}

	public Properties(String filename) {
		try {
			init(filename);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void init(String filename) throws IOException {
		ClassLoader cl = this.getClass().getClassLoader();

		try {
			DocumentBuilder db =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is =
				new InputSource(
					new InputStreamReader(cl.getResourceAsStream(filename)));

			Document doc = db.parse(is);

			System.out.println(doc);

			initProperty(defaultTable = new Hashtable(), doc.getFirstChild());

		} catch (ParserConfigurationException e1) {
			System.err.println(e1);
		} catch (SAXException e2) {
			System.err.println(e2);
		}

		notifyObservers();
	}

	private Object[] initChildElements(Node node, String[] childtypes) {
		Vector vector = new Vector();

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			String s = n.getNodeName();

			if (childtypes != null) {
				for (int j = 0; j < childtypes.length; j++)
					if (s.equals(childtypes[j]))
						vector.add(initPropertyElement(n));
			} else if (!s.equals("#text")) {
				vector.add(initPropertyElement(n));
			}

			//ignore any tag that's not in childtypes.
		}

		return vector.toArray();
	}

	private String getAttributeValue(Node node, String attr) {
		NamedNodeMap nnm = node.getAttributes();
		Node n = nnm.getNamedItem(attr);
		String name = (n == null) ? "" : n.getNodeValue();

		return name;
	}

	/*
	private String getTextValue(Node node) {
		NodeList list = node.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeName() == "#text")
				return n.getNodeValue();
		}
		return "";
	}
	*/

	private Element initPropertyElement(Node node) {
		String name = node.getNodeName();
		System.out.println("Processing " + name);
		Element element = null;

		if (name.equals("view"))
			element = initViewNode(node);
		else if (name.equals("range"))
			element = initRangeNode(node);
		else if (name.equals("boolean"))
			element = initBooleanNode(node);
		else if (name.equals("color"))
			element = initColorNode(node);
		else if (name.equals("string"))
			element = initStringNode(node);
		else if (name.equals("dimension"))
			element = initDimensionNode(node);
		else if (name.equals("double"))
			element = initDoubleNode(node);

		return element;
	}

	private void initProperty(Hashtable H, Node node) {
		Object[] list = initChildElements(node, null);

		for (int i = 0; i < list.length; i++) {
			Element e = (Element) list[i];
			H.put(e.getName(), e.getContent());
		}
	}

	private Element initViewNode(Node node) {
		String name = this.getAttributeValue(node, "name");
		String[] tag = { "range" };
		Object[] list = initChildElements(node, tag);
		Range[] ranges = new Range[list.length];

		for (int i = 0; i < list.length; i++) {
			Element e = (Element) list[i];
			ranges[i] = (Range) e.getContent();
		}

		return new Element(name, new View(ranges));
	}

	private Element initRangeNode(Node node) {
		String name = this.getAttributeValue(node, "name");
		String[] tag = { "double" };
		Object[] list = initChildElements(node, tag);

		double min = 0, max = 0, scale = 0;

		for (int i = 0; i < list.length; i++) {
			Element e = (Element) list[i];

			String n = e.getName();
			double val = ((Double) e.getContent()).doubleValue();

			if (n.equals("min"))
				min = val;
			else if (n.equals("max"))
				max = val;
			else if (n.equals("scale"))
				scale = val;
		}

		return new Element(name, new Range(min, max, scale));
	}

	private Element initBooleanNode(Node node) {
		String name = getAttributeValue(node, "name");
		String bool = getAttributeValue(node, "value");

		return new Element(name, new Boolean(bool.equals("true")));
	}

	private float tryParseFloat(String s) {
		double n = 0;

		try {
			n = Double.parseDouble(s);
		} catch (NumberFormatException e) {
		}

		return (float) n;
	}

	private int tryParseInt(String s) {
		int n = 0;

		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException e) {
		}

		return n;
	}

	private Element initColorNode(Node node) {
		String name = getAttributeValue(node, "name");
		String red = getAttributeValue(node, "red");
		String green = this.getAttributeValue(node, "green");
		String blue = this.getAttributeValue(node, "blue");

		float r = tryParseFloat(red);
		float g = tryParseFloat(green);
		float b = tryParseFloat(blue);

		return new Element(name, new Color(r, g, b));
	}

	private Element initStringNode(Node node) {
		String name = getAttributeValue(node, "name");
		String str = getAttributeValue(node, "value");
		return new Element(name, str);
	}

	private Element initDimensionNode(Node node) {
		String name = getAttributeValue(node, "name");
		String width = getAttributeValue(node, "width");
		String height = getAttributeValue(node, "height");

		int w = tryParseInt(width);
		int h = tryParseInt(height);

		return new Element(name, new Dimension(w, h));
	}

	private Element initDoubleNode(Node node) {
		String name = getAttributeValue(node, "name");
		String num = getAttributeValue(node, "value");

		if (num != null)
			return new Element(name, new Double(Double.parseDouble(num)));
		return null;
	}

	public synchronized void put(String key, Object obj) {
		table.put(key, obj);
		setChanged();
		notifyObservers(key);
	}

	public void initDefault(String key, boolean b) {
		initDefault(key, new Boolean(b));
	}

	public synchronized void initDefault(String key, Object obj) {
		defaultTable.put(key, obj);
	}

	public synchronized Object get(String key) {
		return table.get(key);
	}

	public synchronized void revertToDefault() {
		Enumeration E = defaultTable.keys();

		while (E.hasMoreElements())
			revertToDefault((String) E.nextElement());

		setChanged();
		notifyObservers();
	}

	public synchronized void revertToDefault(String key) {
		Object obj = defaultTable.get(key);

		if (obj != null) {

			table.put(key, obj);
			setChanged();

			notifyObservers(key);
		}
	}
	
	public Color getColorProperty(String key)
	{
		return (Color) get(key);
	}
	
	public String getStringProperty(String key)
	{
		return (String) get(key);
	}
	
	public View getViewProperty(String key)
	{
	    return (View) get(key);
	}
	
	public boolean getBooleanProperty(String key)
	{
		Object o = get(key);
		
		if (o==null)
			return false;
		
		return ((Boolean) o).booleanValue();
	}
	
	public void setPropertyChanged(Object key)
	{
	    setChanged();
	    notifyObservers(key);
	}
}

