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


package net.gcalc.plugin.plane.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.gcalc.calc.main.HackHack;
import net.gcalc.calc.models.ModelList;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.plugin.gui.AbstractCartesianGraphPlugin;
import net.gcalc.plugin.gui.ModelEditingDialog;
import net.gcalc.plugin.plane.graph.GraphCanvas;
import net.gcalc.plugin.properties.GraphProperties;



/**
 * This is seriously broken.
 * 
 * @author jkim
 */
public class ModelListPanel extends JPanel implements ListDataListener
{
    private GraphProperties properties;
    private JList list;
    private JButton removeButton, editButton;
    
    public ModelListPanel(GraphProperties gp)
    {
        this(gp, true, true);
    }
    
    public ModelListPanel(GraphProperties gp, boolean edit, boolean remove)
     {
        super();
        properties = gp;

        ModelList modelList = (ModelList) properties.get(GraphProperties.MODEL_LIST);
         
        list = new JList(modelList);
        
        modelList.addListDataListener(this);	
         
        JScrollPane sp = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setPreferredSize(new Dimension(200,200));
        
        Box buttonBox = Box.createHorizontalBox();
        
        if (edit) {
            buttonBox.add(editButton = new JButton("Edit"));
            editButton.addActionListener(new EditAction());
        }
        if (remove) {
            buttonBox.add(removeButton = new JButton("Remove"));
            removeButton.addActionListener(new RemoveAction());
        }
         
        Box box = Box.createVerticalBox(); 
        box.add(sp);
        box.add(buttonBox);
        
        this.add(box);
        
        list.setCellRenderer(new CustomCellRenderer());
    }

   
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}
   public void contentsChanged(ListDataEvent e) 
   {
      ModelList modelList = getModelList();
    
      if (modelList!=null)
          list.setListData(modelList.getModelArray());
   }
   
   private ModelList getModelList()
   {
       return (ModelList) properties.get(GraphProperties.MODEL_LIST);
   }


   class EditAction extends AbstractAction
   {
	   public void actionPerformed(ActionEvent e) 
	   {
		   int selected = list.getSelectedIndex();
		   if (selected!=-1) {
			   RenderableModel model = (RenderableModel) getModelList().getModelAt(selected);
			   ModelEditingDialog editDialog = model.getEditingDialog((AbstractCartesianGraphPlugin) properties.get(GraphProperties.PLUGIN));
			   
			   editDialog.setVisible(true);
			   
			   RenderableModel newModel = editDialog.getNewModel();
			   
			   if (newModel!=null)
				   getModelList().setModelAt(newModel, selected);
			   
			   // System.out.println(newModel);
		   }
		   
		   GraphCanvas graph = (GraphCanvas) properties.get(GraphProperties.GRAPH_CANVAS);
		   graph.redrawAll();
	   }
	   
   }

   class RemoveAction extends AbstractAction
   {
	   public void actionPerformed(ActionEvent e) 
       {
           int[] selected = list.getSelectedIndices();
           
           for (int i=selected.length-1; i>=0; i--)
               getModelList().removeElementAt(selected[i]);
           
           GraphCanvas graph = (GraphCanvas) properties.get(GraphProperties.GRAPH_CANVAS);
           graph.redrawAll();
       }
   }


   
   
   class CustomCellRenderer extends JLabel implements ListCellRenderer {
       private final static int SIZE = 30;
       
       public Component getListCellRendererComponent(
               JList list,
               Object value,            // value to display
               int index,               // cell index
               boolean isSelected,      // is the cell selected
               boolean cellHasFocus)    // the list and the cell have the focus
       {
           String s = value.toString();
           setText(s);
           
           
           if (value instanceof RenderableModel) {
               RenderableModel model = (RenderableModel) value;
               Color c = model.getColor();
               
               //transform the color to be closer to white.
               float[] rgb = c.getRGBColorComponents(null);
               for (int i=0; i<3; i++)
                   rgb[i]=(rgb[i]+6)/7;
               c = new Color(rgb[0],rgb[1],rgb[2]);
               
               Image iconImage = HackHack.checkeredImage(Color.white, c, SIZE,SIZE,3);
               Graphics iconGraphics = iconImage.getGraphics();
               
               Image image = model.getThumbnailImage(SIZE+2, SIZE+2);
               
               /*
               if (image==null) {
                   View view = properties.getViewProperty(GraphProperties.VIEW);
                   SimpleCartesianGraph graph = new SimpleCartesianGraph(view);
                   graph.resetSize(SIZE, SIZE);
                   graph.draw(new NeverDrawnModel(model));
                   graph.repaint();
                   image = graph.getImage();
               }
               */
               
               if (image!=null)
                   iconGraphics.drawImage(image, 0,0,null);
               
               setIcon(new ImageIcon(iconImage));
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
           setFont(list.getFont());
           setOpaque(true);
           
           setMinimumSize(new Dimension(SIZE,SIZE));
           
           this.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
           
           return this;
       }
       
   
   }
   
}