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

 
package net.gcalc.plugin.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.gcalc.calc.gui.ShutdownDialog;
import net.gcalc.calc.gui.ShutdownWindowAdapter;
import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.AbstractPlugin;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.models.ColoredModel;
import net.gcalc.calc.models.RenderableModel;
import net.gcalc.calc.parser.BadSyntaxException;


public class ModelEditingDialog extends ShutdownDialog 
{
    private TextInputPanel ip;
    private RenderableModel originalModel, newModel;
    private SimpleColorChooser colorChooser;
    private JButton apply, revert, cancel;
    
    private Action applyAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            String[] s = ip.getValues();
            Function[] F = new Function[s.length];
            
            try {
            		for (int i=0; i<s.length; i++)
            			F[i] = FunctionFactory.getFunction(s[i]);
                newModel = new ColoredModel(F, s, colorChooser.getColor());
                setVisible(false);
            }
            catch (BadSyntaxException exception) {
                SwingGUI.popupMessage("Bad Syntax in '"+s+"'!\n"
                        +exception.getMessage(), (AbstractPlugin) getOwner(), JOptionPane.ERROR_MESSAGE);
            }
            
        }
    };
    
    private Action revertAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            ip.setValues(new String[] {originalModel.toString()});
            colorChooser.setColor(originalModel.getColor());
        }
    };
    
    private Action cancelAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);    
        }
    };
    
    public ModelEditingDialog(AbstractCartesianGraphPlugin plugin, RenderableModel model)
    {
    		this(plugin,model,plugin.getModelLabels());
    }
    
    private ModelEditingDialog(AbstractPlugin plugin, RenderableModel model, String[] labels)
     {
        super(plugin, "Edit "+model, true);
        originalModel = model;
        
        this.addWindowListener(new ShutdownWindowAdapter(this));
        
         int n = model.getNumberOfFunctions();
        String[] text = new String[n];
        
        for (int i=0; i<n; i++) {
        		text[i]=model.getFunction(i).toInfix();
        }
    
    
        ip = new  TextInputPanel("Edit Expression", null, labels, text);
        colorChooser = new SimpleColorChooser(model.getColor(), 100,50);
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(apply = new JButton("OK"));
        buttonBox.add(revert = new JButton("Revert"));
        buttonBox.add(cancel = new JButton("Cancel"));
        apply.addActionListener(applyAction);
        revert.addActionListener(revertAction);
        cancel.addActionListener(cancelAction);
        
        this.getContentPane().add(ip, BorderLayout.NORTH);
        this.getContentPane().add(SwingGUI.wrapTitledBorder(colorChooser, "Color"), BorderLayout.CENTER);
        this.getContentPane().add(buttonBox, BorderLayout.SOUTH);
     
       pack();
        this.setResizable(false);
        center();
    }
    
    
    public void shutdown()
    {
        this.setVisible(false);
    }
    
    public RenderableModel getNewModel()
    {
        return newModel;
    }
}


//
//
//
//public class ModelEditingDialog extends ShutdownDialog 
//{
//    private TextInputPanel ip;
//    private RenderableModel originalModel, newModel;
//    private SimpleColorChooser colorChooser;
//    private JButton apply, revert, cancel;
//    
//    private Action applyAction = new AbstractAction() {
//        public void actionPerformed(ActionEvent e) {
//            String s = ip.getValues()[0];
//            try {
//                Function F = FunctionFactory.getFunction(s);
//                newModel = new ColoredModel(F, s, colorChooser.getColor());
//                setVisible(false);
//            }
//            catch (BadSyntaxException exception) {
//                SwingGUI.popupMessage("Bad Syntax in '"+s+"'!\n"
//                        +exception.getMessage(), (AbstractPlugin) getOwner(), JOptionPane.ERROR_MESSAGE);
//            }
//            
//        }
//    };
//    
//    private Action revertAction = new AbstractAction() {
//        public void actionPerformed(ActionEvent e) {
//            ip.setValues(new String[] {originalModel.toString()});
//            colorChooser.setColor(originalModel.getColor());
//        }
//    };
//    
//    private Action cancelAction = new AbstractAction() {
//        public void actionPerformed(ActionEvent e) {
//            setVisible(false);    
//        }
//    };
//    
//    public ModelEditingDialog(AbstractPlugin plugin, RenderableModel model)
//    {
//        super(plugin, "Edit "+model, true);
//        originalModel = model;
//        
//        this.addWindowListener(new ShutdownWindowAdapter(this));
//        
//        
//        int n = model.getNumberOfFunctions();
//        String[] text = new String[n];
//        
//        for (int i=0; i<n; i++) {
//        		text[i]=model.getFunction(i).toInfix().toString();
//        }
//    
//    
//        ip = new  TextInputPanel("Edit Expression", null, getLabels(), text);
//        colorChooser = new SimpleColorChooser(model.getColor(), 100,50);
//        
//        Box buttonBox = Box.createHorizontalBox();
//        buttonBox.add(Box.createHorizontalGlue());
//        buttonBox.add(apply = new JButton("OK"));
//        buttonBox.add(revert = new JButton("Revert"));
//        buttonBox.add(cancel = new JButton("Cancel"));
//        apply.addActionListener(applyAction);
//        revert.addActionListener(revertAction);
//        cancel.addActionListener(cancelAction);
//        
//        this.getContentPane().add(ip, BorderLayout.NORTH);
//        this.getContentPane().add(SwingGUI.wrapTitledBorder(colorChooser, "Color"), BorderLayout.CENTER);
//        this.getContentPane().add(buttonBox, BorderLayout.SOUTH);
//     
//       pack();
//        this.setResizable(false);
//        center();
//    }
//    
//    
//    protected String[] getLabels()
//    {
//    		String[] ret = new String[originalModel.getNumberOfFunctions()];
//    		Arrays.fill(ret, "");
//    		
//    		return ret;    		
//    }
//    
//    public void shutdown()
//    {
//        this.setVisible(false);
//    }
//    
//    public RenderableModel getNewModel()
//    {
//        return newModel;
//    }
//}
//
