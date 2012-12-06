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


package net.gcalc.proto.plugin.space;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;

import net.gcalc.calc.main.AbstractPlugin;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.BadSyntaxRuntimeException;
import net.gcalc.plugin.gui.TextInputPanel;
 
public class CartesianGraph3DPlugin
	extends AbstractPlugin
	implements ActionListener
{
	protected Graph3DCanvas gc;

	protected JButton inputButton;

	private JMenuItem close;
	private JMenuItem clearGraph;
	private JMenuItem clearHistory;
	private JMenuItem saveImage;
	private JMenuItem printImage;
	private JMenuItem copyImage;
	private JMenuItem aboutPlugin;
	private JMenuItem showHelp;
	
	private TextInputPanel ip;

	
	/** Return the plugin-specific JMenu.
	 */
	protected JMenu initPluginMenu()
	{
		return null;
	}

	/** Build and return the MenuBar for the plugin.
	 */
	protected JMenuBar initMenuBar()
	{
		JMenuBar jmb = new JMenuBar();
		JMenu graphMenu = new JMenu("Graph");
		JMenu editMenu = new JMenu("Edit");
		JMenu helpMenu = new JMenu("Help");
		JMenu pluginMenu = initPluginMenu();

		graphMenu.add(clearGraph = new JMenuItem("Clear Graph..."));
		graphMenu.add(clearHistory = new JMenuItem("Clear Function History..."));
		graphMenu.addSeparator();
		graphMenu.add(saveImage = new JMenuItem("Save Graph Image..."));
		graphMenu.add(printImage = new JMenuItem("Print Image..."));
		graphMenu.addSeparator();
		graphMenu.add(close = new JMenuItem("Close Plugin"));

		editMenu.add(copyImage = new JMenuItem("Copy"));

		helpMenu.add(showHelp = new JMenuItem("Help..."));
		helpMenu.add(aboutPlugin = new JMenuItem("About Plugin..."));
	
		saveImage.addActionListener(this);
		printImage.addActionListener(this);
		aboutPlugin.addActionListener(this);
		showHelp.addActionListener(this);
		close.addActionListener(this);
		clearGraph.addActionListener(this);
		clearHistory.addActionListener(this);
		copyImage.addActionListener(this);

		jmb.add(graphMenu);
		jmb.add(editMenu);
		if (pluginMenu != null)
			jmb.add(initPluginMenu());
		jmb.add(Box.createHorizontalGlue());
		jmb.add(helpMenu);

		return jmb;
	}

	/** The box shows which way to layout the graphcanvas with respect
	 *  to the input panel.
	 */
	protected Box frameBox()
	{
		return Box.createVerticalBox();
	}

	Thread T;

	public void init()
	{

		String[] s = { "f(x,y)=" };
		init(new Graph3DCanvas(), s);

		if (T == null)
		{
			T = new Thread(gc);
			T.start();
		}
		pack();

	}

	/** Constructs most of the gui elements for the plugin.  This
	 *  method should not be called from the constructor to delay the
	 *  bulk of the overhead of building each plugin until it's
	 *  actually shown.
	 *  @param gc Graphing Canvas
	 *  @param functionLabel labels for the textareas in the input
	 *  
	 */
	protected void init(Graph3DCanvas gc, String[] labels)
	{
		if (this.gc != null)
			return;

//		System.out.println("Initializing " + this.getPluginName() + "...");

		this.gc = gc;

		ip = new TextInputPanel("Function", labels);
		ip.addActionListener(new AbstractAction() {
		    public void actionPerformed(ActionEvent e)
		    {
		        handleTextInput();
		    }
		});

		Box graphBox = Box.createVerticalBox();
		graphBox.add(gc);

		graphBox.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				"Graph"));

//		gc.addComponentListener(new CustomComponentAdapter());

		Box frame = frameBox();

		frame.add(ip);
		frame.add(graphBox);

		this.getContentPane().add(frame);

		this.setJMenuBar(initMenuBar());

		pack();
		//System.out.println(this.getClass()+" "+gc.getWidth()+" "+gc.getHeight());
		//	gc.resetSize();
		//System.out.println(this.getClass()+" "+gc.getWidth()+" "+gc.getHeight());
	}

	/** Default plugin.  Pretty much does nothing, leaving everything
	 *  until the init() method is called.  This enables us to quickly
	 *  make a instance of a plugin, but delaying the bulk of the
	 *  object construction until it is absolutely required to show
	 *  the plugin.  
	 */
	public CartesianGraph3DPlugin()
	{
		super();
		gc = null;
	}

	/*
	class CustomComponentAdapter extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e)
		{
			pack();
			gc.redrawAll();
		}
	}
	*/

	/** Process the user input from the plugin and graph is possible.
	 */
	protected void handleTextInput()
	{
		try
		{
			String input = ip.getValues()[0].trim();
			gc.setModel(new RenderableModel(FunctionFactory.getFunction(input)));
			ip.addCurrentValuesToHistory();
			ip.clear();
		}
		catch (BadSyntaxRuntimeException ex2)
		{
			//can be more friendly....
			JOptionPane.showMessageDialog(
				this,
				ex2.toString(),
				"Syntax Error",
				JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/** Handle the ActionEvents from menus and input panel components, 
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();

		if (src == close)
		{
			shutdown();
		}
		else if (src == aboutPlugin)
		{
			JOptionPane.showMessageDialog(
				this,
				getDescription(),
				"About Plugin",
				JOptionPane.INFORMATION_MESSAGE);
		}
		else if (src == showHelp)
		{
			JOptionPane.showMessageDialog(
				this,
				"Sorry, the help feature is not yet implemented.",
				"Help!",
				JOptionPane.ERROR_MESSAGE);
		}
		else if (src == clearGraph)
		{
			if (JOptionPane
				.showConfirmDialog(
					this,
					"Do you really want to clear the graph?",
					"Purge Graph Data?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.YES_OPTION)
				gc.clear();

		}
		else if (src == clearHistory)
		{
			if (JOptionPane
				.showConfirmDialog(
					this,
					"Do you really want to clear the input history?",
					"Purge Input History?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.YES_OPTION)
				ip.clearHistory();

		}
	}

	public String getHTMLDescription()
	{
		return getStandardHeading()
			+ "<p>Graphs standard 3D graphs near the origin.</p>"
			+ "<p>Color legend: <font color=red>x-axis</font>,<font color=green>y-axis</font>, and <font color=blue>z-axis</font>";
	}

	public String getCreatorName()
	{
		return "Jiho Kim";
	}

	public String getPluginName()
	{
		return "Cartesian 3D Graph";
	}

	public String getDescription()
	{
		return "Graphs standard 3D graphs near origin.";
	}

}

