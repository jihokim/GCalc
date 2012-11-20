

package net.humblestar.gcalcdemo.calculus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.Token;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.gui.PropertiesPanel;
import net.gcalc.plugin.gui.SimpleColorChooser;
import net.gcalc.plugin.plane.CartesianGraphPlugin;
import net.gcalc.plugin.plane.graph.CartesianGraph;
import net.gcalc.plugin.plane.gui.FlagPanel;
import net.gcalc.plugin.plane.gui.ModelListPanel;
import net.gcalc.plugin.properties.GraphProperties;

/**
 *
 */
public class FunctionGymnastics extends CartesianGraphPlugin {

	private Function f = null;
	private FunctionGymnasticsPanel fgp;
	private ValueTable vt = new ValueTable();
	
	public FunctionGymnastics() {
		super();
		graph = new FunctionGymnasticsGraph(this);
		fgp = new FunctionGymnasticsPanel();
	}
	
	  protected void initPropertiesPanel()
	    {
	        //make various auxillery panels.
	        JPanel flagPanel = makeFlagPanel();
	        Box viewPanel = SwingGUI.wrap(makeViewPanel());
	    //    JPanel functionListPanel = makeModelListPanel();
	        colorChooser = new SimpleColorChooser(firstColor());
	        
	        PropertiesPanel propertiesPanel = getPropertiesPanel();
	        
	        //propertiesDialog.addTab("Color", colorChooser);
	  //      propertiesPanel.addTab("Graphs", functionListPanel);    
	        propertiesPanel.addTab("Properties", flagPanel);
	        propertiesPanel.addTab("View", viewPanel);    
	        propertiesPanel.addTab("Screen", makeGraphDimensionPanel());    
	        propertiesPanel.addTab("Parameters", makeParametersPanel());    
	 //    	propertiesPanel.pack();
	    }

	protected JPanel makeFlagPanel() {
		return new FlagPanel(graph.getProperties(), 5, 2, new String[] { GraphProperties.H_GRID,
				GraphProperties.V_GRID, GraphProperties.H_AXIS,
				GraphProperties.V_AXIS, GraphProperties.H_LABEL,
				GraphProperties.V_LABEL, GraphProperties.H_SCALE,
				GraphProperties.V_SCALE, GraphProperties.H_TITLE,
				GraphProperties.V_TITLE });
	}

	protected JPanel makeModelListPanel() {
		return new ModelListPanel(graph.getProperties(), false, true);
	}
	protected JPanel makeParametersPanel() {
		return fgp;
	}

	public String getDescription() {
		return "<p>Shows the effects of various parameters on a function.</p>"+
		"<p>Examples:</p><p>f(x)=m*x+b<br>f(x)=A sin(B*(x-C))<br><p>";
	}

	public String getPluginName() {
		return "Function Gymnastics";
	}
	
		public void actionPerformed(ActionEvent event) {
		
		if (event.getSource() == ip) {
			String[] values = ip.getValues();
			
			String s1 = values[0].trim(); //function
			
			f = null;
			
			try {
				f = FunctionFactory.getFunction(s1);
			} catch (BadSyntaxException e) {
				this.popupMessageDialog("Bad Syntax in '" + s1 + "'!\n"
						+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}

			fgp.reset();
			graph.clearModelList();
			graph.redrawAll();
			drawGraph(new ColoredModel(f, s1, getNewColor()));
			statusBar.setText("Graphing: " + s1);
		}
	}

	protected void drawGraph(ColoredModel model) {
		graph.draw(model);

		ip.addCurrentValuesToHistory();
	//	ip.clear();
	}
	
	class FunctionGymnasticsGraph extends CartesianGraph {
		public FunctionGymnasticsGraph(AbstractCartesianGraphPlugin plugin) {
			super(plugin);
		}
		
		public Dimension defaultDimension() {
			return new Dimension(425, 425);
		}
		
		protected void draw(Function F, Color color, Graphics gr)
		{   
			draw (F, color, gr, vt);
		}
	} //end class
	
	
	class FunctionGymnasticsPanel extends JPanel
	{		
		public FunctionGymnasticsPanel() {
			super(new BorderLayout());
			reset();
		}
		
		public void reset() {
			//start over
			removeAll();
			
			Box box = Box.createHorizontalBox();
			JScrollPane jsp = new JScrollPane(box, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			Vector v = new Vector();
			
			if (f!=null)
				v = f.getVars();
			
			System.out.println(v);
			
			vt = new ValueTable();
			int count=0;
			
			for (int i=0; i<v.size(); i++) {
				Token t = (Token) v.elementAt(i); 
				
				if (! vt.containsVariable(t) && ! t.getName().equals("x")) {
					count++;
					vt.setValue(t,0);
					box.add(new DoubleScale(t,-10,10));
				}
			}
			box.add(Box.createVerticalGlue());
			jsp.setPreferredSize(new Dimension(250,350));
			

			this.add(BorderLayout.NORTH,jsp);
			
			System.out.println(this);
			
			this.repaint();
		}
	}
	
	

	class DoubleScale extends JPanel implements AdjustmentListener, ActionListener
	{
		JLabel value;
		JTextField minTF, maxTF;
		JScrollBar scrollbar;
		Token tok;
		
		
		public DoubleScale(Token s, double min, double max) {
			super(new BorderLayout());
			
			tok = s;
			
			value = new JLabel(" ");
			
			minTF = new JTextField(""+min);
			maxTF = new JTextField(""+max);
			minTF.addActionListener(this);
			maxTF.addActionListener(this);
			
			scrollbar = new JScrollBar(JScrollBar.VERTICAL, 500, 0, 0, 1000);
			scrollbar.addAdjustmentListener(this);
			
			Box sbp = Box.createHorizontalBox();
//			scrollbar.setPreferredSize(new Dimension(20,200));
			sbp.add(Box.createHorizontalGlue());
			sbp.add(BorderLayout.EAST, scrollbar);
			sbp.add(Box.createHorizontalGlue());

			JPanel panel = new JPanel(new BorderLayout());
			panel.add(BorderLayout.NORTH, maxTF);
			panel.add(BorderLayout.CENTER, sbp);
			panel.add(BorderLayout.SOUTH, minTF);

			add(BorderLayout.NORTH,new JLabel(tok.getName(), JLabel.CENTER));
			add(BorderLayout.CENTER,panel);
			add(BorderLayout.SOUTH,value);
			setPreferredSize(new Dimension(70,200));
			
			somethingChanged();
		}
		
		public void adjustmentValueChanged(AdjustmentEvent e) {
			somethingChanged();
		}
		public void actionPerformed(ActionEvent e) {
			somethingChanged();
		}
		
		public void somethingChanged() {
			double min = Double.parseDouble(minTF.getText());
			double max = Double.parseDouble(maxTF.getText());
			
			double val =  max-scrollbar.getValue()/1000.0*(max-min);
			value.setText(tok+"="+(float) val);
			vt.setValue(tok,val);
			scrollbar.setToolTipText(""+val);
			graph.redrawAll();
		}
		
	}
	
}
