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


package net.gcalc.calc.parser;

import java.util.Stack;
import java.util.Vector;

/** 
 */
public class CalcParser
{
	private final static Token COMMA = new Token(",", Token.DELIMETER);
	private final static Token SEMICOLON = new Token(";", Token.DELIMETER);

	public static void main(String[] args) 
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < args.length; i++)
		{
			sb.append(args[i]);
			sb.append(" ");
		}

		try
		{
			ParseTree PT = CalcParser.parse(sb.toString());

			long t = System.currentTimeMillis();
			double n = 0;

			for (int i = 0; i < n; i++)
				PT = CalcParser.parse(sb.toString());

			System.out.println((System.currentTimeMillis() - t) / n);

			ParseTree.print(PT);

			System.out.println(PT.toInfix());
			System.out.println(PT.getVars());
		}
		catch (BadSyntaxException e)
		{
			System.err.println("*** " + e);
			System.err.println("  " + sb.toString());
			if (e.getPos() >= 0)
			{
				for (int i = 0; i < e.getPos() + 2; i++)
					System.err.print(' ');
				System.err.println("^");
			}
		}
	}

	/** This is the public interface to the Parsing process. 
	It returns a parse tree for the input string.
	
	It throws a BadSyntaxException if there is a problem parsing
	the string
	*/
	public static ParseTree parse(String s) throws BadSyntaxException
	{
		if (s==null)
			throw new BadSyntaxException("Cannot parse null string!");

		if (s.trim().length() == 0)
			throw new BadSyntaxException("Cannot parse empty string!");

		
		ParseTree p = parseSemiColon((new CalcTokenizer(s)).tokenize());
		p.setID(s);
		
		return p;
	}

	/* return a parse tree
	 */
	private static ParseTree parseAtom(Vector tokens) throws BadSyntaxException
	{
		boolean DEBUG = false;
		
		if (tokens.size()==0) 
			throw new BadSyntaxException("Encountered empty token vector.");
		

		Stack pf = new Stack();
		Stack op = new Stack();

		Token tk, lastTk = null;
		float precedence = -1;

		for (int cursor = 0; cursor < tokens.size(); cursor++)
		{

			tk = (Token) tokens.elementAt(cursor);

			if (DEBUG)
				System.out.println(tk);

			if (DEBUG)
				System.out.println("parseatom1: " + cursor + "\t" + tk);

			// NEGATION SIGN CODE 
			// If conditions are right, "-" can
			// mean the negative of a number and not subtraction.

			if (tk.isMinusSign() && (pf.isEmpty() || lastTk.isOperation()))
				tk = new Token("neg", tk);

			// POSITIVE SIGN CODE
			// If conditions are right, "+" can mean the positive
			// of a number and not addition.

			if (tk.isPlusSign() && (pf.isEmpty() || lastTk.isOperation()))
				tk = new Token("id", tk);

			// IMPLIED MULTIPLICATION CODE 
			// For example 2x(x+1)(x+2)3 should be converted to 
			// 2*x*(x+1)*(x+2)*3.

			if (lastTk != null && impliedMultiplication(lastTk, tk))
			{
				tk = new Token("*", Token.OPERATION, tk.getPos());

				//This counters get incremented later.  We have to back
				//up because we have essentially inserted a token. 
				cursor--;
			}

			precedence = tk.precedence();

			if (DEBUG)
				System.out.println("parseatom2: " + pf + "\t" + op);

			if (tk.isNumber())
			{
				//if it's a number, just add it to the tree.
				pf.push(new ParseTree(tk, null));
			}
			else if (tk.isLeftParen())
			{
				int leftParenPos = tk.getPos();
				Vector tokenBetweenParens = new Vector();
				cursor++;

				//find the matching right paren.  Record the tokens in
				//between to be parsed recursively later.
				int level = 1;
				while (level > 0)
				{
					if (cursor == tokens.size())
						throw new BadSyntaxException("Unmatched left parenthesis", leftParenPos);
					tk = (Token) tokens.elementAt(cursor);

					if (tk.isLeftParen())
						level++;
					else if (tk.isRightParen())
						level--;

					if (level > 0)
						tokenBetweenParens.add(tk);

					if (level == 0 && tk.getPos() - leftParenPos == 1)
						throw new BadSyntaxException("Empty parentheses", leftParenPos);

					cursor++;
				}

				//recursively parse the things between matching parens
				ParseTree pt = parseCommaList(tokenBetweenParens);

				if (!op.isEmpty())
				{
					//if the waiting op isn't binary, set it as root. 
					Token peek = (Token) op.peek();

					if (!peek.isBinary() && !peek.getName().equals("neg"))
						pt = new ParseTree((Token) op.pop(), pt.getArgs());
				}

				pt = ParseTree.removeUnnecessaryComma(pt);

				pf.push(pt);

				cursor--; // Sets the cursor to the right_paren so
				// that the next token is really next

				//set tk to the right_paren
				tk = (Token) tokens.elementAt(cursor);
			}
			else if (tk.isRightParen())
			{
				//all right parens should be encountered along with
				//left paren.  If we're in in here, that's not the
				//case.
				throw new BadSyntaxException("Unmatched right parenthesis", tk.getPos());
			}
			else if (op.isEmpty())
			{
				op.push(tk);
			}
			else if (tk.isEqualSign())
			{
				//right associative 
				while (!op.empty() && ((Token) op.peek()).precedence() < precedence)
					safeOpPush(pf, op);

				op.push(tk);
			}
			else if (tk.isCaret())
			{
				//Carets are treated differently from other oprations
				//because of the its special interaction with "neg".
				//Consider the difference between x^-2 and -x^2.
				//Yucky.  Essentially, neg gets a different precedence
				//if the current op is a caret.

				Token peek = (Token) op.peek();

				//right associative binary operations
				while (!op.empty()
					&& peek.precedence() < precedence
					&& !peek.getName().equals("neg"))
					safeOpPush(pf, op);

				op.push(tk);
			}
			else if (tk.isBinary())
			{
				// other binary operations are left associative
				while (!op.empty() && ((Token) op.peek()).precedence() <= precedence)
					safeOpPush(pf, op);

				op.push(tk);
			}
			else
			{
				Token peek = (Token) op.peek();

				while (!op.empty() && peek.precedence() < precedence)
					safeOpPush(pf, op);

				op.push(tk);
			}

			lastTk = tk;
		}

		while (!op.empty())
			safeOpPush(pf, op);

		return (ParseTree) (pf.pop());

	}

	/* Returns true if there should be a multiplication between lastTk
	   and tk, else return false.  Used to detect implied
	   multiplication.
	*/
	private static boolean impliedMultiplication(Token lastTk, Token tk)
	{
		return (lastTk.isRightParen() && tk.isLeftParen())
			|| (lastTk.isNumber() && tk.isLeftParen())
			|| (lastTk.isRightParen() && tk.isNumber())
			|| (lastTk.isRightParen() && tk.isFunction())
			|| (lastTk.isNumber() && tk.isFunction())
			|| (lastTk.isNumber() && tk.isNumber());
	}

	/* replaces code like "pf.push(op.pop());" 
	   with code that generates the tree.
	*/
	private static void safeOpPush(Stack pf, Stack op) throws BadSyntaxException
	{
		boolean DEBUG = false;

		Token tk = (Token) op.pop();
		Vector v = new Vector();

		if (tk.isBinary())
		{
			if (pf.size() >= 2)
			{
				ParseTree arg2 = (ParseTree) pf.pop();
				ParseTree arg1 = (ParseTree) pf.pop();

				if (arg1.getRoot().getPos() > tk.getPos() && arg2.getRoot().getPos() > tk.getPos())
					cannotFindOperands(tk);

				if (arg1.getRoot().getPos() < tk.getPos() && arg2.getRoot().getPos() < tk.getPos())
					cannotFindOperands(tk);

				v.add(arg1);
				v.add(arg2);
			}
			else
			{
				cannotFindOperands(tk);

			}
		}
		else
		{
			if (pf.size() >= 1)
				v.add(pf.pop());
			else
				cannotFindOperands(tk);
		}

		pf.push(new ParseTree(tk, v));

		if (DEBUG)
			System.out.println("safepush: " + tk + "\t" + pf + "\t" + op);
	}

	private static void cannotFindOperands(Token tk) throws BadSyntaxException
	{
		throw new BadSyntaxException("Cannot find operand(s) for '" + tk + "'", tk.getPos());
	}

	/* return a ParseTree with the root ','.
	 */
	private static ParseTree parseCommaList(Vector tokens) throws BadSyntaxException
	{
		Vector commaList = getCommaList(tokens);

		if (commaList.size() == 0)
			throw new BadSyntaxException("Empty parentheses", 0);

		for (int i = 0; i < commaList.size(); i++)
			commaList.setElementAt(parseAtom((Vector) commaList.elementAt(i)), i);

		/*	if  (commaList.size()==1)
			return (ParseTree) commaList.elementAt(0);
		*/
		return new ParseTree(COMMA, commaList);
	}

	/* return a Vector of token vectors
	 */
	private static Vector getCommaList(Vector tokens)
	{
		int level = 0;

		Vector commaList = new Vector();
		Vector T = new Vector();

		for (int i = 0; i < tokens.size(); i++)
		{
			Token tk = (Token) tokens.elementAt(i);

			if (tk.isComma() && level == 0)
			{
				commaList.add(T);
				T = new Vector();
			}
			else
			{
				T.add(tk);
			}

			if (tk.isLeftParen())
				level++;
			else if (tk.isRightParen())
				level--;
		}

		commaList.add(T);

		return commaList;
	}

	/* return a ParseTree with the root ';'.
	 */
	private static ParseTree parseSemiColon(Vector tokens) throws BadSyntaxException
	{
		Vector cmds = new Vector(); // vector to token vectors

		Vector T = new Vector();

		for (int i = 0; i < tokens.size(); i++)
		{
			Token tk = (Token) tokens.elementAt(i);

			if (tk.isSemiColon())
			{
				cmds.addElement(T);
				T = new Vector();
			}
			else
			{
				T.add(tk);
			}
		}

		cmds.addElement(T);

		for (int i = 0; i < cmds.size(); i++)
			cmds.setElementAt(parseAtom((Vector) cmds.elementAt(i)), i);

		return new ParseTree(SEMICOLON, cmds);
	}

}

