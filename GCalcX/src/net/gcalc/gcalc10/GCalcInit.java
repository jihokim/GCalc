/** 
GCalcX
Copyright (C) 2010 Jiho Kim 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/
package net.gcalc.gcalc10;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.gcalc.gcalc10.util.DoubleString;
import net.gcalc.gcalc10.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class GCalcInit
{
	private final static String ROOT = "root";
	
	private Map<String,String> map ;

	public static String getStringAttribute(Node node, String attribute) {
		if (! node.hasAttributes())
			return null;

		NamedNodeMap map = node.getAttributes();
		node = map.getNamedItem(attribute);
		return node==null ? null : node.getNodeValue();
	}
	
	public GCalcInit(String filename) throws ParserConfigurationException, SAXException, IOException {
		
		map = new TreeMap<String,String>();

		Document doc = null;
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputStream is = this.getClass().getResourceAsStream(filename);

		doc = builder.parse(is);

		Node dataNode = null;

		NodeList children = doc.getChildNodes();
		for (int i=0; i<children.getLength() && dataNode==null; i++) {
			Node child = children.item(i);
			if (ROOT.equals(child.getNodeName()))
				dataNode = children.item(i);
		}

		readInitFile("", dataNode, map);
		
//		System.out.println(map);
	}

	private static void readInitFile(String prefix, Node node, Map<String,String> map) {
		NodeList nodeList = node.getChildNodes();
		String nodeName = node.getNodeName();

		String next = prefix;

		if (! ROOT.equals(nodeName)) {
			if (prefix.isEmpty()) {
				next = nodeName;
			}
			else {
				next = prefix+"."+nodeName;
			}
		}

		for (int i=0; i<nodeList.getLength(); i++) {
			readInitFile(next, nodeList.item(i), map);
		}

		String link = getStringAttribute(node, "link");

		if (link!=null) {
			map.put(next, map.get(link));
		}
		else {
			short type = node.getNodeType();
			if (type==Node.TEXT_NODE) {
					String text = node.getTextContent();
					map.put(prefix, text.trim());
			}
		}
	}

	public String getString(String attribute) {
		return map.get(attribute);
	}
	
	public boolean getBoolean(String attribute) {
		String value = getString(attribute);
		
		if (value==null)
			return false;
		
		value = value.trim();
		
		return "true".equalsIgnoreCase(value) || "1".equals(value);
	}
	
	public DoubleString getDoubleString(String attribute) {
		return Util.get(getString(attribute));
	}


	public Integer getInteger(String attribute) {
		try {
			int value = Integer.parseInt(getString(attribute));
			return value;
		}
		catch (NumberFormatException e) {

		}

		return null;
	}

	
	public Double getDouble(String attribute) {
		try {
			double value = Double.parseDouble(getString(attribute));
			return value;
		}
		catch (NumberFormatException e) {

		}

		return null;
	}


	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		GCalcInit mapping = new GCalcInit("/resource/gcalcx.xml");
		System.out.println(mapping.getString("application.name"));
	}
}
