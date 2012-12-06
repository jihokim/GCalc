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
/**
 *  GCalc 10 - Copyright Jiho Kim 2010
 *
 *  Do not redistribute.
 */

package net.gcalc.gcalc10.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.gcalc.gcalc10.AttributeChangeEvent;
import net.gcalc.gcalc10.AttributeChangeListener;
import net.gcalc.gcalc10.DoubleSlider;
import net.gcalc.gcalc10.GraphAttributes;

public class Util {
	public static AffineTransform invert(AffineTransform t) {
		AffineTransform inverse = null;
		try {
			inverse = t.createInverse();
		}
		catch (NoninvertibleTransformException e) {}

		return inverse;
	}

	public static void linearlyInterpolate(double[] x, double min, double max) {
		for (int i=0; i<x.length; i++) {
			x[i] = min+i*(max-min)/(x.length-1);
		}
	}

	private final static Object lock = new Object();
	private final static Map<List<Double>, double[]> domains = Collections.synchronizedMap(new HashMap<List<Double>, double[]>());
	private final static Queue<List<Double>> domainsKey = new LinkedList<List<Double>>();

	public static double[] linearlyInterpolate(double min, double max, int n) {
		List<Double> key = Arrays.asList(min, max, (double) n);
		double[] x = null;

		x = domains.get(key);

		if (x==null) {
			x = new double[n];
		}

		for (int i=0; i<n; i++) {
			x[i] = min+i*(max-min)/(n-1);
		}

		synchronized(lock) {
			domains.put(key, x);
			domainsKey.add(key);

			if (domainsKey.size()>50) {
				domains.remove(domainsKey.poll());
			}
		}

		return x;
	}

	static int hue = 0;

	public static Color getColor() {
		hue = (hue+2)%7;

		return Color.getHSBColor(hue/7f, 1f, 1.f);
	}



	public static JCheckBox createAttributeCheckBox(final GraphAttributes.Attribute<Boolean> attribute) {
		JCheckBox jcb = new JCheckBox();
		jcb.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				attribute.setValue(! attribute.getValue());
			}
		});
		jcb.setSelected(attribute.getValue());
		return jcb;
	}


	public static DoubleSlider createDoubleSlider(final GraphAttributes.Attribute<Double> attribute, int n, double min, double val, double max) {
		final DoubleSlider slider = new DoubleSlider(n, min, val, max);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Double value = slider.getValue();
				attribute.setValue(value);
			}
		});
		attribute.addAttributeChangeListener(
				new AttributeChangeListener() {
					public <E> void attributeChange(AttributeChangeEvent<E> e) {
						double width = attribute.getValue();
						slider.setValue(width);
					}
				});

		return slider;
	}

	public static JTextField createDoubleField(final GraphAttributes.Attribute<DoubleString> attribute) {
		final JTextField field = new JTextField();

		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DoubleString val = Util.get(field.getText());
					attribute.setValue(val);
					field.setBackground(Color.white);
					field.setFont(field.getFont().deriveFont(Font.PLAIN));
				}
				catch (Exception exception) {
					field.setBackground(Color.orange);
				}
			}
		});

		attribute.addAttributeChangeListener(new AttributeChangeListener() {
			public <E> void attributeChange(AttributeChangeEvent<E> e) {
				DoubleString val = attribute.getValue();
				field.setText(""+val.string);
			}
		});

		attribute.touch();

		return field;
	}

	public static JTextField createIntegerField(final GraphAttributes.Attribute<Integer> attribute) {
		final JTextField field = new JTextField();

		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Integer val = Integer.parseInt(field.getText());
					attribute.setValue(val);
					field.setBackground(Color.white);
					field.setFont(field.getFont().deriveFont(Font.PLAIN));
				}
				catch (Exception exception) {
					field.setBackground(Color.orange);
				}
			}
		});

		attribute.addAttributeChangeListener(new AttributeChangeListener() {
			public <E> void attributeChange(AttributeChangeEvent<E> e) {
				Integer val = attribute.getValue();
				field.setText(""+val);
			}
		});

		attribute.touch();

		return field;
	}

	public static DoubleString get(String s) {
		try {
			return DoubleString.getInstance(s);
		} catch (Exception e) {
			throw new IllegalArgumentException(s, e);
		}
	}

	private static void issueErrorDialog(Window parent, Throwable throwable) {
		String[] options = {"Ignore", "Trace"};

		String title = throwable.getClass().toString();

		int choice = JOptionPane.showOptionDialog(parent,
				throwable.getMessage(), title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);

		if ("Trace".equals(options[choice])) {
			StringWriter sw = new StringWriter();
			throwable.printStackTrace(new PrintWriter(sw));
			
			JTextArea area = new JTextArea();
			area.setText(sw.toString());
			area.setCaretPosition(0);
			
			JScrollPane pane = new JScrollPane(area);
			
			JDialog dialog = new JDialog(parent, "Exception Trace");
			dialog.getContentPane().add(pane);

			dialog.setSize(512, 384);
			dialog.setVisible(true);
		}

	}

	private static void issueErrorDialog(String title, Throwable e, Component ... components) {
		Window parent = null;
		for (Component c : components) {
			parent = SwingUtilities.getWindowAncestor(c);
			if (parent!=null) {
				issueErrorDialog(parent, e);

				break;
			}
		}
	}

	public static void issueErrorDialog(Exception e, Component ... components) {
		issueErrorDialog(e.getClass().getSimpleName(), e, components);
	}

	public static String sigfig(double x, int n) {
		double absx = Math.abs(x);
		String ret = null;
		
		if (x==0) {
			ret = "0";
		}
		else if (absx<Math.pow(10,-n)) {
			ret = "0.0";
		}
		else if (absx>1e5 || absx<1e-5) {
			DecimalFormat format = new DecimalFormat("0.#E0");
			format.setMaximumFractionDigits(n);
			ret = format.format(x);
		}
		else {
			DecimalFormat format = new DecimalFormat("0.#");
			format.setMaximumFractionDigits(n);
			ret = format.format(x);
			
		}
		return ret;
	}
}
