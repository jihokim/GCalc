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


package net.gcalc.plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.StringCharacterIterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import net.gcalc.calc.gui.SwingGUI;
import net.gcalc.calc.main.AbstractPlugin;
import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.SymbolTableException;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.math.functions.FunctionFactory;
import net.gcalc.calc.parser.BadSyntaxException;
import net.gcalc.calc.parser.CalcParser;
import net.gcalc.calc.parser.ParseTree;
import net.gcalc.calc.parser.Token;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.RecallJTextField;

public class ScientificCalculatorPlugin extends AbstractPlugin
{
	protected JEditorPane jep;
	protected RecallJTextField inputTF;
	protected JButton inputButton;
	protected JMenuItem clearOutput, clearHistory, save, print, close, copy, cut, paste, showHelp, aboutPlugin;

	private SymbolTable st;
	private ValueTable vt;

	private ClipboardActionListener cal = new ClipboardActionListener();

	private SciCalcActionListener scal;

	public ScientificCalculatorPlugin()
	{
		super();
		st = new SymbolTable();
		vt = new ValueTable();
	}

	public void init()
	{
		if (this.jep != null)
			return;

	//	System.out.println("Initializing " + this.getPluginName() + "...");

		jep = new JEditorPane("text/html", "");
		jep.setEditable(false);

		Box inputBox = Box.createHorizontalBox();

		inputButton = new JButton("Go!");

		inputBox.add(inputTF = new RecallJTextField(""));
		inputBox.add(inputButton);
	
		scal = new SciCalcActionListener();

		inputButton.addActionListener(scal);
		inputTF.addActionListener(scal);

		inputTF.addFocusListener(cal);
		jep.addFocusListener(cal);

		Box componentBox = Box.createVerticalBox();
		componentBox.add(new JScrollPane(jep));

	
		this.getContentPane().add("Center", SwingGUI.wrapTitledBorder(componentBox, "Output"));
		this.getContentPane().add("South", SwingGUI.wrapTitledBorder(inputBox, "Input"));

		this.setJMenuBar(initMenuBar());

		setSize(400, 500);
		setResizable(true);
		inputTF.requestFocus();
	}

	/** Build and return the MenuBar for the plugin.
	   */
	protected JMenuBar initMenuBar()
	{
		JMenuBar jmb = new JMenuBar();
		JMenu editMenu = new JMenu("Edit");
		JMenu helpMenu = new JMenu("Help");
		JMenu pluginMenu = new JMenu("SciCalc");

		pluginMenu.add(clearOutput = new JMenuItem("Clear Calculator Output"));
		//pluginMenu.addSeparator();
		//pluginMenu.add(save = new JMenuItem("Save Calculator Output..."));
	//	pluginMenu.add(print = new JMenuItem("Print..."));
		pluginMenu.addSeparator();
		pluginMenu.add(close = new JMenuItem("Close Plugin"));

		//print.addActionListener(new PrintActionListener(this));
		close.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{
				shutdown();
			}
		});

		clearOutput.addActionListener(new TextComponentClearActionListener(jep) {
		    public void actionPerformed(ActionEvent e)
		    {
		        textComponent.setText("");
		        scal.clear();
		    }
		});

		editMenu.add(copy = new JMenuItem("Copy"));
		editMenu.add(cut = new JMenuItem("Cut"));
		editMenu.add(paste = new JMenuItem("Paste"));

		copy.addActionListener(cal);
		cut.addActionListener(cal);
		paste.addActionListener(cal);

		helpMenu.add(showHelp = new JMenuItem("Help..."));
		helpMenu.add(aboutPlugin = new JMenuItem("About Plugin..."));

		jmb.add(pluginMenu);
		jmb.add(editMenu);

		jmb.add(Box.createHorizontalGlue());
		jmb.add(helpMenu);

		return jmb;
	}

	public String inputBorderText()
	{
		return "Scientific Calculator";
	}

	public String getHTMLDescription()
	{
		return getStandardHeading()
			+ "<p>Acts like a scientific calculator.</p>"
			+ "<p>Hint: the last result is saved in the variable <code>ans</code>.</p>";
	}

	public String getPluginName()
	{
		return "Scientific Calculator";
	}

	public String getCreatorName()
	{
		return "Jiho Kim (jiho@gcalcul.us)";
	}

	public String getDescription()
	{
		return "Acts like a scientific calculator.";
	}

	class SciCalcActionListener implements ActionListener
	{
		private int count = 0;
		private StringBuffer sb;

		public SciCalcActionListener()
		{
			clear();
		}
		
		public void clear()
		{
			count = 0;
		    sb = new StringBuffer();
			sb.append("<table border=0 width=\"100%\">");
		}

		public void actionPerformed(ActionEvent e)
		{
			String input = inputTF.getText().trim();
		
			if (input.length() == 0)
				return;

			count++;

			String color = (count % 2 == 0) ? "" : " bgcolor=\"#f0f0f0\"";

			sb.append("<tr><td" + color + "><div align=left>" + formatForHTML(input) + "</div>");

			try
			{
				ParseTree pt = (ParseTree) CalcParser.parse(input).getArgs().elementAt(0);

				Token root = pt.getRoot();

				if (root.isEqualSign())
				{
					Vector args = pt.getArgs();

					ParseTree lhs = (ParseTree) args.elementAt(0);
					ParseTree rhs = (ParseTree) args.elementAt(1);

					if (!lhs.getRoot().isVariable())
						throw new BadSyntaxException("LHS is not variable");

					if (lhs.equals(rhs)) {
					    st.removeVariable(lhs.getRoot());
					   sb.append("<div align=right>"+lhs.getRoot().toString()+" unset</div>");
					}
					else {
					    st.setVariable(lhs.getRoot(), FunctionFactory.getFunction(rhs));
					    sb.append("<div align=right>" + input + "</div>");
					}
				}
				else
				{
					Function F = FunctionFactory.getFunction(input);
					String result = null;
					if (F.getRoot().isDerivative())
					{
						result = F.simplify().toInfix();
					}
					else
					{
						double val = F.evaluate(st, vt);
						
						if (! Double.isInfinite(val) && ! Double.isNaN(val)) {
							vt.setValue(new VariableToken("ans"), val);
							result = "" + val;
						}
						else 
							result = "Error";
					}
					sb.append("<div align=right>" + result + "</div>");

				}
			}
			catch (BadSyntaxException exception)
			{
				String message = exception.getMessage();
				outputErrorMessage(message);
			}
			catch (SymbolTableException ex2)
			{
				System.err.println(ex2);
				outputErrorMessage(ex2.getMessage());
			}

			sb.append("</td></tr>");

			inputTF.addToHistory(input);
			inputTF.setText("");

			jep.setText(sb.toString() + "</table>");
		}

		private void outputErrorMessage(String message)
		{
			if (message == null)
				message = "";

			sb.append("<div align=right><font color=red>ERROR:" + message + "</font></div>");
		}
		
		

	}

	abstract class TextComponentClearActionListener extends AbstractAction
	{
		protected JTextComponent textComponent = null;
		public TextComponentClearActionListener(JTextComponent tc)
		{
			textComponent = tc;
		}
	}

	class ClipboardActionListener extends FocusAdapter implements ActionListener
	{
		private JTextComponent ta = null;

		public void actionPerformed(ActionEvent e)
		{
			if (ta == null)
				return;

			Object src = e.getSource();

			if (src == copy)
			{
				ta.copy();
			}
			else if (src == paste)
			{
				ta.paste();
			}
			else if (src == cut)
			{
				ta.cut();
			}
		}

		public void focusGained(FocusEvent e)
		{
			if (e.getSource() instanceof JTextComponent)
				ta = (JTextComponent) e.getSource();

			paste.setEnabled(ta != jep);
			cut.setEnabled(ta != jep);
		}
	}

	/**
	 * Replace characters having special meaning <em>inside</em> HTML tags
	 * with their escaped equivalents, using character entities such as <tt>'&amp;'</tt>.
	 *
	 * <P>The escaped characters are :
	 * <ul>
	 * <li> <
	 * <li> >
	 * <li> "
	 * <li> '
	 * <li> \
	 * <li> &
	 * </ul>
	 *
	 * <P>This method ensures that arbitrary text appearing inside a tag does not "confuse"
	 * the tag. For example, <tt>HREF='Blah.do?Page=1&Sort=ASC'</tt>
	 * does not comply with strict HTML because of the ampersand, and should be changed to
	 * <tt>HREF='Blah.do?Page=1&amp;Sort=ASC'</tt>. This is commonly seen in building
	 * query strings. (In JSTL, the c:url tag performs this task automatically.)
	 */
	public static String formatForHTML(String aTagFragment){
		final StringBuffer result = new StringBuffer();
		
		final StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
		char character =  iterator.current();
		while (character != StringCharacterIterator.DONE ){
			if (character == '<') {
				result.append("&lt;");
			}
			else if (character == '>') {
				result.append("&gt;");
			}
			else if (character == '\"') {
				result.append("&quot;");
			}
			else if (character == '\'') {
				result.append("&#039;");
			}
			else if (character == '\\') {
				result.append("&#092;");
			}
			else if (character == '&') {
				result.append("&amp;");
			}
			else {
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
}



