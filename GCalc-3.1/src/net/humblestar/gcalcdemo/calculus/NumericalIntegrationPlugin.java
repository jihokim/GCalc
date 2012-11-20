package net.humblestar.gcalcdemo.calculus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.Token;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.gui.InputPanel;
import net.gcalc.plugin.gui.PropertiesPanel;
import net.gcalc.plugin.gui.SimpleColorChooser;
import net.gcalc.plugin.gui.TextInputPanel;
import net.gcalc.plugin.plane.CartesianGraphPlugin;
import net.gcalc.plugin.plane.graph.CartesianGraph;
import net.gcalc.plugin.plane.gui.FlagPanel;
import net.gcalc.plugin.plane.gui.ModelListPanel;
import net.gcalc.plugin.properties.GraphProperties;

/**
 *
 */
public class NumericalIntegrationPlugin extends CartesianGraphPlugin {

	//these constants are coupled with the index in the methods array.
	public final static int LH_RIEMANN = 0;
	public final static int RH_RIEMANN = 1;
	public final static int MIDPOINT = 2;
	public final static int TRAPEZOIDAL = 3;
	public final static int SIMPSON = 4;
			
	public NumericalIntegrationPlugin() {
		super();
		graph = new NumericalIntegrationGraph(this);
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

	protected InputPanel getInputPanel() {
		return new NumericalIntegrationInputPanel();
	}

	protected JPanel makeModelListPanel() {
		return new ModelListPanel(graph.getProperties(), false, true);
	}

	public String getDescription() {
		return "<p>Demonstrates numerical integration methods.</p>";
	}

	public String getPluginName() {
		return "Numerical Integration Plugin";
	}

	public void actionPerformed(ActionEvent event) {
		
		if (event.getSource() == ip) {
			String[] values = ip.getValues();
			
			String s1 = values[0].trim(); //function
			String s2 = values[1].trim(); //x0 lower bound
			String s3 = values[2].trim(); //x1 upper bound
			String s4 = values[3].trim(); //subdivision
			String s5 = values[4].trim(); //method

			Function f = null;
			double x0,x1;
			int subdiv, method;

			
			
			try {
				x0 = Double.parseDouble(s2);
			} catch (NumberFormatException e) {
				this.popupMessageDialog("'"+s2 + "' is not a number!\n"
						+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				x1 = Double.parseDouble(s3);
			} catch (NumberFormatException e) {
				this.popupMessageDialog("'"+s3 + "' is not a number!\n"
						+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				subdiv = Integer.parseInt(s4);
			} catch (NumberFormatException e) {
				this.popupMessageDialog("'"+s4 + "' is not a number!\n"
						+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				method = Integer.parseInt(s5);
			} catch (NumberFormatException e) {
				this.popupMessageDialog("Bad Method ("+s5+")\n"
						+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				f = FunctionFactory.getFunction(s1);
			} catch (BadSyntaxException e) {
				this.popupMessageDialog("Bad Syntax in '" + s1 + "'!\n"
						+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}

			double sum = ((NumericalIntegrationGraph) graph).numericalIntegration(f, x0, x1, subdiv, method);
			((NumericalIntegrationInputPanel) ip).setAnswer(sum);

			drawGraph(new ColoredModel(f, s1, getNewColor()));
			statusBar.setText("Graphing: " + s1);
		}
	}

	protected void drawGraph(ColoredModel model) {
		graph.draw(model);

		ip.addCurrentValuesToHistory();
	//	ip.clear();
	}
	
	class NumericalIntegrationGraph extends CartesianGraph {
		public NumericalIntegrationGraph(AbstractCartesianGraphPlugin plugin) {
			super(plugin);
		}

		public Dimension defaultDimension() {
			return new Dimension(425, 425);
		}

		protected double calculateAndDrawSimpleRule(Function f, double min,
				double max, int n, int rule, SymbolTable st, ValueTable vt,
				boolean draw) {
			Token X = new Token("x", Token.VARIABLE);

			double x1, x2, yy;

			double sum = 0;

			for (int i = 0; i < n; i++) {
				x1 = min + (max - min) * i / n;
				x2 = min + (max - min) * (i + 1) / n;

				switch (rule) {
				case LH_RIEMANN:
					vt.setValue(X, x1);
					break;
				case RH_RIEMANN:
					vt.setValue(X, x2);
					break;
				case MIDPOINT:
					vt.setValue(X, (x1 + x2) / 2);
					break;

				default:
					throw new IllegalArgumentException(
							"Bad Integration Rule for this method.");

				}

				yy = f.evaluate(st, vt);

				sum += yy * (x2 - x1);

				if (draw) {
					int sx1 = cartesianXtoScreen(x1);
					int sx2 = cartesianXtoScreen(x2);
					int y1 = cartesianYtoScreen(yy);
					int y2 = cartesianYtoScreen(0);
					int sy1 = Math.max(y1, y2);
					int sy2 = Math.min(y1, y2);

					gr.setColor(new Color(200, 200, 255));
					gr.fillRect(sx1, sy2, sx2 - sx1, sy1 - sy2);

					gr.setColor(new Color(00, 0, 255));
					gr.drawRect(sx1, sy2, sx2 - sx1, sy1 - sy2);
				}

			}

			return sum;
		}

		protected double calculateAndDrawTrapezoidal(Function f, double min,
				double max, int n, SymbolTable st, ValueTable vt, boolean draw) {
			Token X = new Token("x", Token.VARIABLE);

			double x1, x2, yy1, yy2;

			double sum = 0;

			for (int i = 0; i < n; i++) {
				x1 = min + (max - min) * i / n;
				x2 = min + (max - min) * (i + 1) / n;

				vt.setValue(X, x1);
				yy1 = f.evaluate(st, vt);
				vt.setValue(X, x2);
				yy2 = f.evaluate(st, vt);

				sum += (x2 - x1) * .5 * (yy1 + yy2);

				if (draw) {
					int sx1 = cartesianXtoScreen(x1);
					int sx2 = cartesianXtoScreen(x2);
					int h1 = cartesianYtoScreen(yy1);
					int h2 = cartesianYtoScreen(yy2);
					int h0 = cartesianYtoScreen(0);

					gr.setColor(new Color(200, 200, 255));

					if (sx1 != sx2) {
						for (int j = sx1; j <= sx2; j++) {
							gr.drawLine(j, h0, j, h1 + (h2 - h1) * (j - sx1)
									/ (sx2 - sx1));
						}
					} else {
						gr.drawLine(sx1, h0, sx1, h1);
					}

					gr.setColor(Color.blue);
					gr.drawLine(sx1, h0, sx1, h1);
					gr.drawLine(sx2, h0, sx2, h2);
					gr.drawLine(sx1, h1, sx2, h2);
					gr.drawLine(sx1, h0, sx2, h0);
				}
			}

			return sum;
		}

		protected double calculateAndDrawSimpson(Function f, double min,
				double max, int n, SymbolTable st, ValueTable vt, boolean draw) {
			Token X = new Token("x", Token.VARIABLE);

			double x1, x2, x3, yy1, yy2, yy3;

			double sum = 0;

			for (int i = 0; i < n; i++) {
				x1 = min + (max - min) * i / n;
				x3 = min + (max - min) * (i + 1) / n;
				x2 = (x3 + x1) / 2;

				vt.setValue(X, x1);
				yy1 = f.evaluate(st, vt);
				vt.setValue(X, x2);
				yy2 = f.evaluate(st, vt);
				vt.setValue(X, x3);
				yy3 = f.evaluate(st, vt);

				sum += (x2 - x1) * (yy1 + 4 * yy2 + yy3) / 3;

				int sx1 = cartesianXtoScreen(x1);
				int sx3 = cartesianXtoScreen(x3);
				int h1 = cartesianYtoScreen(yy1);
				int h3 = cartesianYtoScreen(yy3);
				int h0 = cartesianYtoScreen(0);

				if (sx1 != sx3) {
					for (int j = sx1; j <= sx3; j++) {
						double xx = screenXtoCartesian(j);
						double yy = (xx - x2) * (xx - x3) * yy1 / (x1 - x2)
								/ (x1 - x3) + (xx - x1) * (xx - x3) * yy2
								/ (x2 - x1) / (x2 - x3) + (xx - x1) * (xx - x2)
								* yy3 / (x3 - x1) / (x3 - x2);

						int h = cartesianYtoScreen(yy);

						if (draw) {
							gr.setColor(new Color(200, 200, 255));
							gr.drawLine(j, h0, j, h);
							gr.setColor(Color.blue);
							gr.drawLine(j, h, j, h);
						}
					}
				} else if (draw) {
					gr.setColor(new Color(200, 200, 255));
					gr.drawLine(sx1, h0, sx1, h1);
					gr.setColor(Color.blue);
					gr.drawLine(sx1, h1, sx1, h1);
				}

				if (draw) {
					gr.setColor(Color.blue);
					gr.drawLine(sx1, h0, sx1, h1);
					gr.drawLine(sx3, h0, sx3, h3);
					gr.drawLine(sx1, h0, sx3, h0);
				}
			}

			return sum;
		}

		private Function f = null;

		private double min, max;

		private int subdivision;

		private int method;

		public double numericalIntegration(Function f, double min,
				double max, int n, int method) {
			this.f = f;
			this.min = min;
			this.max = max;
			this.subdivision = n;
			this.method = method;
			
			getModelList().removeAllModels();
			redrawAll();

			
			return calculateAndDrawNumericalIntegration(false);
		}

		public void drawModelList() {
			if (f != null)
				calculateAndDrawNumericalIntegration(true);
			super.drawModelList();
		}

		private double calculateAndDrawNumericalIntegration(boolean draw) {
			SymbolTable st = new SymbolTable();
			ValueTable vt = new ValueTable();

			double sum = Double.NaN;

			switch (method) {
			case MIDPOINT:
			case LH_RIEMANN:
			case RH_RIEMANN:
				sum = calculateAndDrawSimpleRule(f, min, max, subdivision,
						method, st, vt, draw);
				break;

			case TRAPEZOIDAL:
				sum = calculateAndDrawTrapezoidal(f, min, max, subdivision, st,
						vt, draw);
				break;

			case SIMPSON:
				sum = calculateAndDrawSimpson(f, min, max, subdivision, st, vt,
						draw);
				break;

			default:
				throw new IllegalArgumentException("Unrecognized method "
						+ method);
			}

		//	if (draw)
	//			draw(f, st, vt);

			return sum;
		}

	} //end class
}

class NumericalIntegrationInputPanel extends TextInputPanel
{	
	//the order of elements in this array is coupled with constants above.
	protected final static String[] methods = { "Left Riemann sum",
			"Right Riemann sum", "Midpoint Rule", "Trapezoidal Rule",
			"Simpson's Rule" };
	
	private JComboBox methodsJCB;
	public JTextField answerTF;
	

	protected NumericalIntegrationInputPanel()
	{
		super("Numerical Integration", new String[] { "f(x)=",
				"Lower Bound", "Upper Bound", "Subdivisions" },
				new String[] { "", "-4", "4", "10" });
	}
	
	
	protected JPanel createInputSubPanel(int n, String[] labels, String[] values)
	{
		JPanel jp = super.createInputSubPanel(n, labels, values);
		
		GridBagLayout gb = (GridBagLayout) jp.getLayout();
		
		addLeftSide(jp, gb, new JLabel("Methods"));
		addRightSide(jp, gb, methodsJCB  = new JComboBox(methods));
		addLeftSide(jp, gb, new JLabel("Approximation"));
		
		addRightSide(jp, gb, answerTF = new JTextField());
		
		answerTF.setEditable(false);
		
		return jp;
	}
	
	public void setAnswer(double x)
	{
		answerTF.setText(""+(float) x);
	}
	
	public String[] getValues()
	{
		String[] ret = super.getValues();
		String[] out = new String[ret.length+1];
		
		System.arraycopy(ret, 0, out, 0, ret.length);
		
		out[out.length-1] = ""+methodsJCB.getSelectedIndex();
		
		return out;
	}
}


