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

 Email: jiho@gcalc.net
 Web: http://gcalc.net

 Snail Mail: 
 Jiho Kim
 1002 Monterey Lane
 Tacoma, WA 98466
 */
package net.gcalc.proto.plugin.example;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JTextField;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.AbstractPlugin;



/**
 * Simple Graph Plugin
 */
public class UnitConversionPlugin extends AbstractPlugin
{
    private JTextField input, output;

    public UnitConversionPlugin()
    {
        super();
        setResizable(true);
    }

    public void init()
    {
        if (this.isInitialized())
            return;

        input = new JTextField(12);
        output = new JTextField(12);

        JButton convertButton = new JButton("Convert!");

        Box mainBox = Box.createVerticalBox();

        mainBox.add(SwingGUI.wrapTitledBorder(input, "Fahrenheit Input"));
        mainBox.add(SwingGUI.wrapTitledBorder(output, "Celsius Output"));
        //  mainBox.add(Box.createHorizontalStrut(20));
        mainBox.add(convertButton);

        convertButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e)
            {
                performConversion();
            }
        });

        this.getContentPane().add(mainBox);

        this.setTitle(getPluginName());
        this.pack();

        this.setInitialized(true);
    }

    protected void performConversion()
    {
       
        try
        {
            double x = Double.parseDouble(input.getText());
            output.setText(""+(x-32)*5/9);
        }
        catch (Exception e)
        {}
    }

    public String getCreatorName()
    {
        return "Jiho Kim (jiho@gcalc.net)";
    }

    public String getDescription()
    {
        return "<p>Converts between common units.</p>  <p>This could use a lot more development.</p>";
    }

    public String getPluginName()
    {
        return "Unit Converter";
    }

}

