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


package net.gcalc.plugin.plane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.gui.CopyGraphAction;
import net.gcalc.plugin.gui.InputPanel;
import net.gcalc.plugin.gui.PrintGraphAction;
import net.gcalc.plugin.gui.PropertiesPanel;
import net.gcalc.plugin.gui.SaveGraphAction;
import net.gcalc.plugin.gui.SimpleColorChooser;
import net.gcalc.plugin.gui.TextInputPanel;
import net.gcalc.plugin.plane.graph.CartesianGraph;
import net.gcalc.plugin.plane.gui.AboutPluginAction;
import net.gcalc.plugin.plane.gui.ClosePluginAction;
import net.gcalc.plugin.plane.gui.CoordinatePanel;
import net.gcalc.plugin.plane.gui.FlagPanel;
import net.gcalc.plugin.plane.gui.GraphDimensionPanel;
import net.gcalc.plugin.plane.gui.ModelListPanel;
import net.gcalc.plugin.plane.gui.ShowPropertiesPanelAction;
import net.gcalc.plugin.plane.gui.ViewPanel;
import net.gcalc.plugin.plane.gui.ZoomPanel;
import net.gcalc.plugin.properties.GraphProperties;


/**
 * Graph Plugin
 */
public class CartesianGraphPlugin extends AbstractCartesianGraphPlugin implements ActionListener
{
    protected InputPanel ip;
    protected CartesianGraph graph;
    protected SimpleColorChooser colorChooser;
    protected JLabel statusBar;

    public CartesianGraphPlugin()
    {
        super();
        graph = new CartesianGraph(this);       
    }
    
    protected JPanel makeGraphDimensionPanel()
    {
        return new GraphDimensionPanel(graph.getProperties());
    }

    protected JPanel makeFlagPanel() {
		return new FlagPanel(graph.getProperties(), 7, 2, new String[] {
				GraphProperties.TRACE, GraphProperties.THICK_GRAPH,
				GraphProperties.H_GRID, GraphProperties.V_GRID,
				GraphProperties.H_AXIS, GraphProperties.V_AXIS,
				GraphProperties.H_LABEL, GraphProperties.V_LABEL,
				GraphProperties.H_SCALE, GraphProperties.V_SCALE,
				GraphProperties.H_TITLE, GraphProperties.V_TITLE,
				GraphProperties.SHOW_CONCAVITY,	GraphProperties.SHOW_MONOTONICITY
		});
	}
    
    protected JPanel makeViewPanel()
    {
        return new ViewPanel(graph.getProperties());
    }
    
    protected JPanel makeZoomPanel()
    {       
        return new ZoomPanel(graph.getProperties());
    } 
    
    protected JPanel makeModelListPanel()
    { 
       return new ModelListPanel(graph.getProperties());
    }
    
    protected Color firstColor()
    {
        return Color.black;
    }
        
    protected void initPropertiesPanel()
    {
        //make various auxillery panels.
        JPanel flagPanel = makeFlagPanel();
        Box viewPanel = SwingGUI.wrap(makeViewPanel());
        JPanel functionListPanel = makeModelListPanel();
        colorChooser = new SimpleColorChooser(firstColor());
        
        PropertiesPanel propertiesPanel = getPropertiesPanel();
        
        //propertiesDialog.addTab("Color", colorChooser);
        propertiesPanel.addTab("Graphs", functionListPanel);    
        propertiesPanel.addTab("Properties", flagPanel);
        propertiesPanel.addTab("View", viewPanel);    
        propertiesPanel.addTab("Screen", makeGraphDimensionPanel());    
 //    	propertiesPanel.pack();
    }
    
    protected void initMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu plugin = new JMenu("Plugin");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        JMenuItem save = new JMenuItem(new SaveGraphAction(this, graph));
        JMenuItem print = new JMenuItem(new PrintGraphAction(this, graph));
        JMenuItem quit = new JMenuItem(new ClosePluginAction(this));
        JMenuItem showProperties = new JMenuItem(new ShowPropertiesPanelAction(this));
        JMenuItem about = new JMenuItem(new AboutPluginAction(this));
        JMenuItem copy = new JMenuItem(new CopyGraphAction(this,graph));

        menuBar.add(plugin);
        menuBar.add(edit);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(help);
        
        plugin.add(save);
        plugin.addSeparator();
        plugin.add(print);
        plugin.addSeparator();
        plugin.add(quit);
        
        help.add(about);
        
        edit.add(copy);
        edit.addSeparator();
        edit.add(showProperties);
          
        this.setJMenuBar(menuBar);
    }
    
    protected InputPanel getInputPanel()
    {
        return new TextInputPanel("Input", getModelLabels(), null);
    }
    
    protected Component getCenter()
    {
        ip = getInputPanel();
        if (ip!=null) {
            ip.addActionListener(this);
        }

        CoordinatePanel cp = new CoordinatePanel(2);
        graph.setCoordinatePanel(cp);

        Box graphBox = Box.createVerticalBox();
        graphBox.add(graph);
        graphBox.add(cp);
      

        Box mainBox = Box.createVerticalBox();
        
        if (ip!=null) {
            mainBox.add(ip);
        }
        
        mainBox.add(SwingGUI.wrapTitledBorder(graphBox, "Graph"));
        
        
        
        JPanel uberBox =new JPanel(new BorderLayout());
        uberBox.add(mainBox, BorderLayout.NORTH);
        uberBox.add(new JPanel(), BorderLayout.CENTER);
        
        
        return uberBox;
    }

    public void init()
    {
        initPropertiesPanel();
        initMenuBar();
        
        statusBar = new JLabel(" ");
        statusBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        
        getContentPane().setLayout(new BorderLayout(5,5));
        getContentPane().add("Center", getCenter());
        getContentPane().add("South", statusBar);
        
        this.showPropertiesPanel(true);
        
        
        this.setTitle(getPluginName());
        this.pack();

        this.setInitialized(true);
    }
    
	
    public String getCreatorName()
    {
        return "Jiho Kim (jiho@gcalcul.us)";
    }

    public String getDescription()
    {
        return "<p>Simple Graph plugin.</p>";
    }

    public String getPluginName()
    {
        return "Graph Plugin";
    }
    
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource()==ip)
        {
            String s = ip.getValues()[0].trim();
            Function f = null;

            try
            {
                f = FunctionFactory.getFunction(s);
            }
            catch (BadSyntaxException e)
            {
                this.popupMessageDialog("Bad Syntax in '"+s+"'!\n"
                        +e.getMessage(), JOptionPane.ERROR_MESSAGE);
                // e.printStackTrace();
                return;
            }

            if (f!=null)
            {
                drawGraph(f, s);
                statusBar.setText("Graphing: "+s);
            }
        }
    }

    protected void drawGraph(Function f, String s)
    {
        Color color = getNewColor();
        
        graph.draw(new ColoredModel(f,s, color));

        ip.addCurrentValuesToHistory();
        ip.clear();
    }
    
    protected Color getNewColor()
    {
        Color color = colorChooser.getColor();

        //change the hue of the next color.
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(),
                color.getBlue(), null);
        hsb[0] += 2.0f/7; // 2/7 of the way across the rainbow

        if (hsb[0]>1)
            hsb[0] -= 1;
        
        colorChooser.setColor(Color.getHSBColor(hsb[0], .9f, .8f));
        
        return color;
    }
}
