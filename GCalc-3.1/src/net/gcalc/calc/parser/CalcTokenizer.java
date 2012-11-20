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


package net.gcalc.calc.parser;

import java.util.Vector;

import net.gcalc.calc.math.functions.FunctionFactory;

public class CalcTokenizer
{

	//this array of strings needs to be ordered from longest to shortest.
	private final static String[] OPS =
		{ "&&", "||", "<=", ">=", "!=", "==", "+", "-", "*", "/", "^", "=", "<", ">", };

	private final static String DELIMCHARS = "(),";

//  this next line is what it's suppose to be if you want to use semicolons.
//	private final static String DELIMCHARS = "(),;";

	private String input;
	private int pos;
	//private int negpos;

	public static void main(String[] args) throws BadSyntaxException
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < args.length; i++)
		{
			sb.append(args[i]);
			sb.append(" ");
		}

		System.out.println((new CalcTokenizer(sb.toString())).tokenize());
	}

	//why does it break without the " "?
	public CalcTokenizer(String s)
	{
		input = s;
		pos = 0;
		//negpos = -1;
	}

	public Vector tokenize() throws BadSyntaxException
	{
		Vector tokens = new Vector();

		for (; pos < input.length(); pos++)
		{
			char c = input.charAt(pos);
			
			if (!Character.isWhitespace(c))
			{
				if (Character.isDigit(c) || c == '.')
					tokens.addElement(parseDouble());
				else if (isOperationPrefix(c))
					tokens.addElement(parseOperation());
				else if (isDelimeter(c))
					tokens.addElement(parseDelimeter());
				else if (Character.isLetter(c))
					tokens.addElement(parseKeyword());
				else
				{
					throw new BadSyntaxException("Unrecognized character " + c, pos);
				}
			}

			//	    System.out.println(tokens);

		}

		return tokens;
	}

	private boolean isOperationPrefix(char c)
	{
		for (int i = 0; i < OPS.length; i++)
			if (OPS[i].charAt(0) == c)
				return true;

		return false;
	}

	private boolean isDelimeter(char c)
	{
		return DELIMCHARS.indexOf(c) >= 0;
	}

	private final static int BAD_STATE = -1;
	private final static int START = 1;
	private final static int JUST_DOT = 2;
	private final static int ACCEPTABLE_DECIMAL = 3;
	private final static int SIGNED_EXPONENT = 4;
	private final static int ACCEPTABLE_INTEGER = 5;
	private final static int START_EXPONENT = 6;
	private final static int ACCEPTABLE_EXPONENT = 7;

	private Token parseDouble() throws BadSyntaxException
	{
		StringBuffer sb = new StringBuffer();
		StringBuffer cache = new StringBuffer();

		char c;
		boolean exit = false;
		int state = START;
		int p1 = pos;

		do
		{
			c = input.charAt(pos);
			
			switch (state)
			{
				case START :
					if (Character.isDigit(c))
						state = ACCEPTABLE_INTEGER;
					else if (c == '.')
						state = JUST_DOT;
					else
						exit = true;

					break;

				case JUST_DOT :
					if (Character.isDigit(c))
						state = ACCEPTABLE_DECIMAL;
					else
						exit = true;
					break;

				case ACCEPTABLE_DECIMAL :
					if (Character.isDigit(c))
						state = ACCEPTABLE_DECIMAL;
					else if (c == 'E')
						state = START_EXPONENT;
					else
						exit = true;
					break;

				case SIGNED_EXPONENT :
					if (Character.isDigit(c))
						state = ACCEPTABLE_EXPONENT;
					else
						exit = true;
					break;

				case ACCEPTABLE_INTEGER :
					if (Character.isDigit(c))
						state = ACCEPTABLE_INTEGER;
					else if (c == '.')
						state = ACCEPTABLE_DECIMAL;
					else if (c == 'E')
						state = START_EXPONENT;
					else
						exit = true;
					break;

				case START_EXPONENT :
					if (Character.isDigit(c))
						state = ACCEPTABLE_EXPONENT;
					else if (c == '+' || c == '-')
						state = SIGNED_EXPONENT;
					else
						exit = true;
					break;

				case ACCEPTABLE_EXPONENT :
					if (Character.isDigit(c))
						state = ACCEPTABLE_EXPONENT;
					else
						exit = true;
					break;

				default :
					//should never happen.
					state = BAD_STATE;
					exit = true;
			}

			if (!exit)
			{
				if (state == ACCEPTABLE_DECIMAL
					|| state == ACCEPTABLE_INTEGER
					|| state == ACCEPTABLE_EXPONENT)
				{
					sb.append(cache.toString());
					sb.append(c);

					cache.setLength(0);
				}
				else if (
					state == START
						|| state == JUST_DOT
						|| state == SIGNED_EXPONENT
						|| state == START_EXPONENT)
				{
					cache.append(c);
				}
			}

			pos++;
		}
		while (!exit && pos < input.length());
		
		if (state==JUST_DOT && exit)
			throw new BadSyntaxException("Extraneous Dot", pos);

		//	pos -= (cache.length() + 2); // may be off by 1

		pos = p1 + sb.length() - 1; //hope it's not off by 1.

		return new Token(sb.toString(), Token.CONSTANT, p1);
	}

	private Token parseKeyword()
	{
		int p1 = pos;
		int p2 = pos;
		char c;

		while (p2 < input.length()
			&& (Character.isLetter(c = input.charAt(p2)) || Character.isDigit(c)))
			p2++; //loop just to increment pos2...

		String word = input.substring(p1, p2);
		int type = Token.VARIABLE;

		if (FunctionFactory.isFunctionName(word))
			type = Token.OPERATION;
	
		pos = p2 - 1;
		return new Token(word, type, p1);
	}

	private Token parseOperation() throws BadSyntaxException
	{
		String s = null;
		for (int i = 0; s == null && i < OPS.length; i++)
			if (input.substring(pos).startsWith(OPS[i]))
				s = OPS[i];
			
		if (s==null)
			throw new BadSyntaxException("Cannot parse!");

		pos += (s.length() - 1);

		return new Token(s, Token.OPERATION, pos);
	}

	private Token parseDelimeter()
	{
		return new Token(input.charAt(pos), Token.DELIMETER, pos);
	}

}

