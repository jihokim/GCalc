package net.gcalc.calc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GCalcApplet extends JApplet {
	String[] pluginList = {
			"net.gcalc.plugin.ScientificCalculatorPlugin",
			"net.gcalc.plugin.plane.CartesianGraphPlugin",
			"net.gcalc.proto.plugin.space.CartesianGraph3DPlugin",
			"net.gcalc.plugin.plane.InequalitiesPlugin",
			"net.gcalc.plugin.plane.PolarGraphPlugin",
			"net.gcalc.plugin.plane.ParametricGraphPlugin",
			"net.gcalc.plugin.plane.ImplicitFunctionPlugin",	
			"net.humblestar.gcalcdemo.calculus.NumericalIntegrationPlugin",
			"net.gcalc.plugin.plane.DirectionFieldPlugin",
			"net.humblestar.gcalcdemo.calculus.FunctionGymnastics"
	};
	
	JButton[] button;
	
	JMenuItem quitMI, aboutMI;
	
	/*
	public static void main(String[] args) {
		Descartes app = new Descartes();
		app.setSize(650,400);
		app.setVisible(true);
	}
	*/
	
	public void init() {
		//super("GCalc 3.0 CR1");
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("GCalc");
		menu.add(aboutMI = new JMenuItem(new AboutAction()));
		menu.add(quitMI = new JMenuItem(new QuitAction()));
		menubar.add(menu);
	//	setJMenuBar(menubar);
		
		
			JList list = new JList(pluginList);
	//	list.setCellRenderer(new MyCellRenderer());
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (! e.getValueIsAdjusting())
					GCalc.main(new String[] {pluginList[e.getFirstIndex()]});
			}
		});
		
		JLabel copyright = new JLabel("<html><center>Copyright 2006 Jiho Kim <br>http://gcalc.net</center></html>", JLabel.CENTER);
		copyright.setFont(new Font("SansSerif", Font.PLAIN, 10));
			
		
		button = new JButton[pluginList.length];
		
		JPanel grid = new JPanel(new GridLayout(3,4,20,20));
		grid.add(button[0]=new MyButton("Scientific Calculator"));
		grid.add(button[1]=new MyButton("Function Graph"));
		grid.add(button[2]=new MyButton("3D Graph"));
		grid.add(button[3]=new MyButton("Bivariate Inequality"));
		grid.add(button[4]=new MyButton("Polar Graph"));
		grid.add(button[5]=new MyButton("Parametric Graph"));
		grid.add(button[6]=new MyButton("Implicit Function"));
		grid.add(button[7]=new MyButton("Numerical Integration"));
		grid.add(button[8]=new MyButton("Direction Field"));
		grid.add(button[9]=new MyButton("Function Gymnastics"));
		grid.add(new JPanel());
		grid.add(copyright);
		
		
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i=0; i<button.length; i++)
					if (button[i]==e.getSource())
						start(i);
			}
		};
		
		for (int i=0; i<button.length; i++) {
			button[i].addActionListener(listener);
		}
		
		JPanel panel = new JPanel(new BorderLayout(20,20));
		panel.setBorder(new EmptyBorder(20,20,20,20));
		panel.add(grid, BorderLayout.CENTER);
		
		setContentPane(panel);
	}
	
	class MyButton extends JButton {
		
		public MyButton(String s) {
			super();
			
			StringTokenizer st = new StringTokenizer(s);
			
			String html = "<html><center>";
			
			while (st.hasMoreTokens())  {
				html+=st.nextToken();
				if (st.hasMoreTokens()) {
					html+="<br>";
				}
			}
			
			this.setText(html);
		}
	}
	
	private void start(int n) {
		GCalc.main(new String[] {pluginList[n]});
	}
	
	class AboutAction extends AbstractAction {
		AboutAction() {
			super("About this program");
		}
		
		public void actionPerformed(ActionEvent e) {
			
		}
	}

	class QuitAction extends AbstractAction {
		QuitAction() {
			super("Quit");
		}
		
		public void actionPerformed(ActionEvent e) {
			quit();
		}
	}
	
	
	private void quit()
	{
		System.exit(0);
	}
	
	 class MyCellRenderer extends JLabel implements ListCellRenderer {


	     // This is the only method defined by ListCellRenderer.
	     // We just reconfigure the JLabel each time we're called.

	     public Component getListCellRendererComponent(
	       JList list,
	       Object value,            // value to display
	       int index,               // cell index
	       boolean isSelected,      // is the cell selected
	       boolean cellHasFocus)    // the list and the cell have the focus
	     {
	    	     setPreferredSize(new Dimension(50,50));
		     String s = value.toString();
	         int n=10;
	         while (n>0) {
	        	 	s = s.substring(n = s.indexOf('.')+1);
	         }
	         
	         
	         if (isSelected) {
	             setBackground(list.getSelectionBackground());
	             setForeground(list.getSelectionForeground());
	         }
	         else {
	               setBackground(list.getBackground());
	               setForeground(list.getForeground());
	           }
	         setEnabled(list.isEnabled());
	         setOpaque(true);
	         
	         setText(s);
	            
	         return this;
	     }
	 }
}
