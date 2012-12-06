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

package net.gcalc.gcalc10;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;

import net.gcalc.gcalc10.action.HelpAction;
import net.gcalc.gcalc10.action.QuitAction;
import net.gcalc.gcalc10.action.ZoomAction;
import net.gcalc.gcalc10.drawable.ColoredDrawable;
import net.gcalc.gcalc10.drawable.DrawableList;
import net.gcalc.gcalc10.drawable.DrawableListTableModel;
import net.gcalc.gcalc10.drawable.TR;
import net.gcalc.gcalc10.drawable.TXY;
import net.gcalc.gcalc10.drawable.XY;
import net.gcalc.gcalc10.editor.ColorRenderer;
import net.gcalc.gcalc10.editor.DrawableFunctionEditor;
import net.gcalc.gcalc10.editor.HeteroTable;
import net.gcalc.gcalc10.input.CompositeInputPanel;
import net.gcalc.gcalc10.input.SourceChangeEvent;
import net.gcalc.gcalc10.input.SourceListener;
import net.gcalc.gcalc10.input.TextInputPanel;
import net.gcalc.gcalc10.util.Util;
import net.gcalc.juu.environment.GAngleUnit;
import net.gcalc.juu.environment.GNumber;

import org.xml.sax.SAXException;

public class GCalc10 extends JFrame {
	private final String initSource = "/resource/gcalcx.xml";
	final private GCalcInit init;
	final private AbstractAction remove;
	final private AbstractAction edit;

	private Box statusBox = Box.createHorizontalBox();
	private JTextField cartesianDimension = new JTextField();

	private DrawableList<ColoredDrawable> list;
	private GraphAttributes graphAttributes;

	private Canvas canvas ;

	private DrawableFunctionEditor functionEditor = new DrawableFunctionEditor();
//	private ColorEditor colorEditor = new ColorEditor();
	private EditDialog editDialog = new EditDialog(this);

	private JButton removeButton, editButton;

	private JTable table;


	public GCalc10() throws ParserConfigurationException, SAXException, IOException {
		init = new GCalcInit(initSource);

		String applicationName = init.getString("application.name");

		setTitle(applicationName);

		graphAttributes = new GraphAttributes(init);
		list = new DrawableList<ColoredDrawable>();
		canvas = new Canvas(list, graphAttributes);
		table = new HeteroTable(new DrawableListTableModel(list));

		remove = new AbstractAction(init.getString("action.remove.text"),
				getIcon(init.getString("action.remove.file"))) {
			public void actionPerformed(ActionEvent e) {
				int[] indices = table.getSelectedRows();
				for (int i = indices.length - 1; i >= 0; i--) {
					list.remove(indices[i]);
				}
				table.clearSelection();
				canvas.recompute();
			}
		};

		edit = new AbstractAction(init.getString("action.edit.text"),
				getIcon(init.getString("action.edit.file"))) {

			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				functionEditor.stopCellEditing();
				table.getSelectionModel().setSelectionInterval(index, index);

				ColoredDrawable f = editDialog.edit(list.get(index));

				if (f!=null)
					list.set(index, f);

				GCalc10.this.repaint();
				canvas.recompute();
			}
		};

		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.getTableHeader().setReorderingAllowed(false);

		TableColumn column;

		column = table.getColumnModel().getColumn(0);
		column.setHeaderValue("Type");
		column.setMaxWidth(50);

		column = table.getColumnModel().getColumn(1);
		column.setHeaderValue("Function expression");
//		column.setCellEditor(functionEditor);
		column.setPreferredWidth(200);

		column = table.getColumnModel().getColumn(2);
		column.setHeaderValue("Color");
		column.setMaxWidth(50);
		column.setCellRenderer(new ColorRenderer());
//		column.setCellEditor(colorEditor);


		table.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				canvas.recompute();
			}
		});

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int selectedRows = table.getSelectedRowCount();
						editButton.setEnabled(selectedRows == 1);
						removeButton.setEnabled(selectedRows > 0);
					}
				});

	}
	
	private CompositeInputPanel<ColoredDrawable> createCompositeInputPanel() {
		final CompositeInputPanel<ColoredDrawable> cip = new CompositeInputPanel<ColoredDrawable>();
		cip.addSource(new TextInputPanel<ColoredDrawable>("y(x)=") {
			public String getTitle() { return "y(x)"; }
			public ColoredDrawable getValue() { 
				return new XY(fields[0].getText());
			}
		});
		cip.addSource(new TextInputPanel<ColoredDrawable>("r(t)=") {
			public String getTitle() { return "r(t)"; }
			public ColoredDrawable getValue() { 
				return new TR(fields[0].getText());
			}
		});
		cip.addSource(new TextInputPanel<ColoredDrawable>("x(t)=", "y(t)=") {
			public String getTitle() { return "(x(t),y(t))"; }
			public ColoredDrawable getValue() { 
				return new TXY(fields[0].getText(),fields[1].getText());
			}
		});
		cip.addSourceListener(new SourceListener<ColoredDrawable>() {
			public void sourceChange(SourceChangeEvent<ColoredDrawable> e) {
				ColoredDrawable f = e.getValue();

				if (f!=null) {
					f.setColor(Util.getColor());
					list.add(f);
					cip.clear();
				}
			}
		});
		
		return cip;
	}
	
	private AbstractAction showDimensions = new AbstractAction("Show Dimensions") {
		JFrame dialog = null;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (dialog==null) {
				dialog = new JFrame("Window Dimensions");
				final CanvasDimensionPanel panel = new CanvasDimensionPanel();
				dialog.getContentPane().add(panel);
				canvas.addRecomputationListener(panel);
				canvas.recompute();
				dialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						canvas.removeRecomputationListener(panel);
					}
				});
				
				
				dialog.pack();
				dialog.setMinimumSize(dialog.getSize());
				dialog.setLocationRelativeTo(GCalc10.this);

//				Rectangle rect = GCalc10.this.getBounds();
//				dialog.setLocation(rect.x+rect.width, rect.y);
			}
			dialog.setVisible(true);
		}
	};
	
	private AbstractAction standardZoom = new AbstractAction("Standard") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.setTranslate(0,0);
			canvas.setScale(40,40);
			graphAttributes.GRID_XGAP.setValue(init.getDoubleString("gui.graph_attributes.xgap.value"));
			graphAttributes.GRID_YGAP.setValue(init.getDoubleString("gui.graph_attributes.ygap.value"));
			canvas.recompute();
		}
	};
	
	private AbstractAction trigZoom = new AbstractAction("Trignometric") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.setTranslate(0,0);
			canvas.setScale(40,40);
			graphAttributes.GRID_XGAP.setValue(Util.get("pi/2"));
			graphAttributes.GRID_YGAP.setValue(Util.get("1/4"));
			canvas.recompute();
		}
	};
	
	private AbstractAction squareZoom = new AbstractAction("Square") {

		@Override
		public void actionPerformed(ActionEvent e) {
			CanvasContext context = canvas.getContext();
			AffineTransform transform = context.getTransform();
			double x = transform.getScaleX();
			double y = transform.getScaleY();
			
			double scale = Math.min(Math.abs(x),Math.abs(y));
			
			canvas.setScale(scale, scale);
			canvas.recompute();
		}
	};
	
	private AbstractAction fitZoom = new AbstractAction("Fit") {
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(GCalc10.this, "Not implemented", "Not implemented", JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	private JMenuBar populateMenuBar() {
		JMenuBar jmb = new JMenuBar();
		JMenu file = new JMenu(init.getString("menu.file"));
		file.add(new QuitAction(init, this));
		jmb.add(file);

		JMenu zoom = new JMenu(init.getString("menu.zoom"));
		zoom.add(showDimensions);
		zoom.add(new JSeparator());
		zoom.add(standardZoom);
		zoom.add(trigZoom);
		zoom.add(squareZoom);
		zoom.add(fitZoom);
		jmb.add(zoom);
		
//		JMenu juu = new JMenu(init.getString("menu.juu"));
//		juu.add(new AbstractAction(init.getString("action.demo.text")) {
//			public void actionPerformed(ActionEvent e) {
//				Thread thread = new Thread() {
//					public void run() {
//						JuuDemo.createInstance(SwingUtilities.getWindowAncestor(GCalc10.this));
//					}
//				};
//				thread.start();
//			}
//		});
//		jmb.add(juu);

		JMenu help = new JMenu(init.getString("menu.help"));
		help.add(new HelpAction(init, this));
		jmb.add(help);
		
		return jmb;
	}

	public void initLayout() {
		statusBox.add(cartesianDimension);
		cartesianDimension.setEditable(false);
		
		canvas.setPreferredSize(new Dimension(800, 500));

		CompositeInputPanel<ColoredDrawable> cip = createCompositeInputPanel();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(init.getString("gui.input.title")));
		panel.add(BorderLayout.CENTER, cip);

		JRadioButton radButton = new JRadioButton(new AbstractAction("Rad") {

			@Override
			public void actionPerformed(ActionEvent e) {
				GNumber.angleUnit = GAngleUnit.RADIAN;
				canvas.recompute();				
			}
		});
		radButton.setSelected(true);
		
		JRadioButton degButton = new JRadioButton(new AbstractAction("Deg") {

			@Override
			public void actionPerformed(ActionEvent e) {
				GNumber.angleUnit = GAngleUnit.DEGREE;
				canvas.recompute();				
			}
		});
		ButtonGroup angleButtonGroup = new ButtonGroup();
		angleButtonGroup.add(radButton);
		angleButtonGroup.add(degButton);

		JToolBar jtb = new JToolBar(JToolBar.VERTICAL);
		jtb.add(new ZoomAction(init.getString("action.zoomin.text"), getIcon(init.getString("action.zoomin.file")), -1, canvas));
		jtb.add(new ZoomAction(init.getString("action.zoomout.text"), getIcon(init.getString("action.zoomout.file")), 1, canvas));
		jtb.add(radButton);
		jtb.add(degButton);
		
		JPanel listPanel = getListPanel();

		Box right = Box.createVerticalBox();
		right.add(listPanel);
		right.add(new GraphAttributePanel(init, graphAttributes));
		right.setPreferredSize(new Dimension(0, 0));

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, right);
		jsp.setResizeWeight(1);

		getContentPane().add(BorderLayout.NORTH, panel);
		getContentPane().add(BorderLayout.CENTER, jsp);
		getContentPane().add(BorderLayout.WEST, jtb);
		getContentPane().add(BorderLayout.SOUTH, statusBox);

		this.setJMenuBar(populateMenuBar());
	}

	private JPanel getListPanel() {
		removeButton = new JButton(remove);
		removeButton.setEnabled(false);

		editButton = new JButton(edit);
		editButton.setEnabled(false);

		Box listButtonBox = Box.createHorizontalBox();
		listButtonBox.add(Box.createHorizontalGlue());
		listButtonBox.add(editButton);
		listButtonBox.add(removeButton);
		listButtonBox.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel listPanel = new JPanel(new BorderLayout());
		JScrollPane jspane = new JScrollPane(table);
		listPanel.add(BorderLayout.CENTER, jspane);
		listPanel.add(BorderLayout.SOUTH, listButtonBox);
		listPanel.setBorder(new TitledBorder(init.getString("gui.graph_list.title")));
		// listPanel.setPreferredSize(new Dimension(100,300));

		return listPanel;
	}

	private Icon getIcon(String name) {
		URL url = this.getClass().getResource(name);

		if (url == null) {
			System.err.println("Could not find " + name);
			return null;
		}

		return new ImageIcon(url);
	}


	public static void start(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					sprint();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}

			public void sprint() throws ParserConfigurationException, SAXException, IOException {
				GCalc10 frame;
				frame = new GCalc10();
				frame.initLayout();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public static void main(String[] args) {
		GCalc10.start(args);
	}
}

