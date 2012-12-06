/**
 * Juu Programming Language 
 * Copyright (C) 2010 Jiho Kim
 * 
 * This file is part of Juu.
 * 
 * Juu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Juu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Juu. If not, see <http://www.gnu.org/licenses/>.
 */

package net.gcalc.juu.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import net.gcalc.juu.Evaluator;


public class JuuDemo extends JPanel {
	private Object lock = new Object();
	private ExecutorService es = Executors.newCachedThreadPool();
	private List<Future<Void>> futures = new ArrayList<Future<Void>>();;

	private Font font = new Font("Monospaced", Font.BOLD, 12);
	private JEditorPane jep1;
	private JEditorPane jep2;
	private JButton evalButton, clearButton, stopButton;
	private JTextField field = new JTextField("100000");
	private JLabel label = new JLabel();

	public JuuDemo() {
		super(new BorderLayout());

		jep1 = new JEditorPane();
		jep2 = new JEditorPane();
		evalButton = new JButton(new AbstractAction("Evaluate") {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		clearButton = new JButton(new AbstractAction("Clear") {
			public void actionPerformed(ActionEvent e) {
				jep2.setText("");
			}
		});
		stopButton = new JButton(new AbstractAction("Stop") {
			public void actionPerformed(ActionEvent e) {
				synchronized(lock) {
					cancel();					
				}
			}
		});

		jep1.setFont(font);
		jep2.setFont(font);

		System.setOut(new PrintStream(new TextComponentOutputStream(jep2)));	

		JScrollPane jsp1 = new JScrollPane(jep1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane jsp2 = new JScrollPane(jep2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		jsp1.setBorder(new TitledBorder("Program"));
		jsp2.setBorder(new TitledBorder("Output"));

		label.setPreferredSize(new Dimension(80,10));

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp1, jsp2);

		Box buttons = Box.createHorizontalBox();
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(evalButton);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(stopButton);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(clearButton);
		buttons.add(Box.createHorizontalStrut(100));
		buttons.add(new JLabel("Timeout"));
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(field);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(label);
		buttons.add(Box.createHorizontalStrut(10));

		buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		add(BorderLayout.CENTER, sp);
		add(BorderLayout.SOUTH, buttons);

		sp.setDividerLocation(400);
		sp.setResizeWeight(.8);
	}

	public void cancel() {
		if (futures!=null) {
			for (Future<?> future : futures) {
				future.cancel(true);
			}
			futures.clear();
		}
	}

	public void load(String file) {
		String s = "./data/"+file;

		System.out.println(s);
		try {
			FileReader fr = new FileReader(s);
			BufferedReader br = new BufferedReader(fr);

			StringBuffer sb = new StringBuffer();
			String line;

			while ((line=br.readLine())!=null) {
				sb.append(line);
				sb.append("\n");
			}

			jep1.setText(sb.toString());
		} 
		catch (Exception e) {
			System.out.println("Oops.");
			System.out.println(e);
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void start() {
		final Evaluator evaluator = new Evaluator();

		List<Callable<Void>> list = new ArrayList<Callable<Void>>();

		list.add(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					long value = -1;
					
					try {
						value = Long.parseLong(field.getText());
					}
					catch (NumberFormatException e) {
						field.setText("-1");
					}
					evaluator.setTimeout(value);
					evaluator.evaluate(jep1.getText());
				}
				catch (InterruptedException e) {
					System.out.println("\nInterrupted.\n");
				}
				catch (final Exception e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							handleException(e);
						}
					});
				}
				return null;
			}
		});

		list.add(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				while (true) {
					Thread.sleep(500);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							label.setText(""+evaluator.getTimeout());
						}
					});
				}
			}
		});

		synchronized (lock) {
			cancel();

			for (Callable<Void> callable : list) {
				futures.add(es.submit(callable));
			}			
		}
	}

	public void handleException(Throwable e) {

		//		interpreter.terminate();
		JOptionPane.showMessageDialog(this, e, "Exception", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	public static void createInstance(Window w) {
		String[] files = { 
				"test0.juu",
				"test1.juu", 
				"test2.juu", 
				"test3.juu",
				"test4.juu", 
				"test5.juu", 
				"test6.juu", 
				"test7.juu" 
		};

		final JDialog dialog = new JDialog(w, "Juu Demonstration");
		final JuuDemo juuDemo = new JuuDemo();
		dialog.getContentPane().add(juuDemo);


		JMenuBar menuBar = new JMenuBar();
		dialog.setJMenuBar(menuBar);

		JMenu programs = new JMenu("Programs");
		menuBar.add(programs);		

		for (int i=0; i<files.length; i++) {
			programs.add(new AbstractAction(files[i]) {
				public void actionPerformed(ActionEvent e) {
					juuDemo.load(getValue(NAME).toString());
				}
			});
		}

		dialog.setSize(600,400);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	public static void main(String[] args) {
		JuuDemo.createInstance(null);
	}
}


