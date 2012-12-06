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

package net.gcalc.proto.plugin.example;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.gcalc.calc.main.AbstractPlugin;


/**
 * @author jkim
 *
 */
public class ArithmeticDrillPlugin extends AbstractPlugin implements ActionListener
{
    private JLabel problemLabel = new JLabel("    ", JLabel.CENTER);
    private JTextField input = new JTextField(20);
    private Problem currentProblem;
    
    public ArithmeticDrillPlugin()
    {
        super();
    }
    
    public void init()
    {   
        this.getContentPane().add(problemLabel, BorderLayout.CENTER);
        this.getContentPane().add(input, BorderLayout.SOUTH);
        
        problemLabel.setFont(new Font("Serif", Font.PLAIN, 45));
        this.setSize(400,300);
        this.setVisible(true);
        input.addActionListener(this);
        newProblem();
    }
    
    public String getCreatorName()
    {
        return "Jiho Kim (jiho@gcalcul.us)";
    }

    public String getDescription()
    {
        return "<p>Drills students on arithmetic problems.</p>";
    }

    public String getPluginName()
    {
        return "Arithmetic Drill";
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource()!=input)
            return;
        
        String ans = input.getText();
        boolean correct = currentProblem.checkAnswer(ans);
        
        if (correct) {
            //update stats
            
            newProblem();
        }
    }
    
    private void newProblem()
    {
        currentProblem = createNewProblem();
        
        problemLabel.setText(currentProblem.getStatement());
        input.setText("");
        input.requestFocus();
    }
    
    private Problem createNewProblem()
    {
       return SmallIntegerAdditionProblem.getNewProblem();
    }
    
}

abstract class Problem
{
    public static Random random = new Random();
    
   
    
    private String statement;
    private Solution answer;
    
    protected Problem(String s, Solution ans)
    {
        statement = s;
        answer = ans;
    }
    
    public Class getAnswerType()
    {
        return answer.getClass();
    }
    
    public boolean checkAnswer(String attempt)
    {
        return answer.correct(attempt);
    }
    
    public String getStatement()
    {
        return statement;
    }
    
}

class SmallIntegerAdditionProblem extends Problem
{
    public static Problem getNewProblem()
    {
        int a = random.nextInt()%10;
        int b = random.nextInt()%10;
       
        return new SmallIntegerAdditionProblem(a, b);
    }
    
    private SmallIntegerAdditionProblem(int a, int b)
    {
        super(a+"+"+b, new IntegerSolution(a+b));
    }
}

class SmallIntegerMultiplicationProblem extends Problem
{
    public static Problem getNewProblem()
    {
        int a = random.nextInt()%13;
        int b = random.nextInt()%13;
       
        return new SmallIntegerMultiplicationProblem(a, b);
    }
    
    private SmallIntegerMultiplicationProblem(int a, int b)
    {
        super(a+"x"+b, new IntegerSolution(a*b));
    }
}

abstract class Solution 
{
    public abstract boolean correct(String s);
}

class IntegerSolution extends Solution
{
    int integer = 0;
    
    public IntegerSolution(int n)
    {
     integer = n;
    }
    
    public boolean correct(String str)
    {
        int x = 0;
        try {
            x = Integer.parseInt(str);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        
        return integer==x;
    }
    
}

