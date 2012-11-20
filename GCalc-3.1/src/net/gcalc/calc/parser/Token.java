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

import net.gcalc.calc.math.functions.FunctionFactory;

public class Token  {
	
	public final static int CONSTANT = 0;
	public final static int OPERATION = 1;
	public final static int DELIMETER = 2;
	public final static int VARIABLE = 3;

	private String s;
	private int type;
	private int pos;
	private int hashcode;

	public Token(String s, int type, int pos) {
		this.s = s;
		this.type = type;
		this.pos = pos;
		hashcode = 0;
	}

	public Token(String s, Token T) {
		this(s, T.type, T.pos);
	}

	public Token(char c, int type, int pos) {
		this("" + c, type, pos);
	}

	public Token(String s, int type) {
		this(s, type, -1);
	}

	public boolean equals(Object o) {
		return s.equals(((Token) o).s);
	}

	public String getName() {
		return s;
	}

	public String toString() {
		boolean DEBUG = false;

		if (DEBUG)
			return "<" + s + "," + pos + ">";

		return s;
	}

	public boolean isConstant() {
		return type == CONSTANT;
	}

	public boolean isNumber() {
		return isConstant() || isVariable();
	}

	public boolean isVariable() {
		return type == VARIABLE;
	}

	public boolean isSemiColon() {
		return s.equals(";");
	}

	public boolean isRightParen() {
		return s.equals(")");
	}

	public boolean isLeftParen() {
		return s.equals("(");
	}

	public boolean isComma() {
		return s.equals(",");
	}

	public boolean isMinusSign() {
		return s.equals("-");
	}

	public boolean isPlusSign() {
		return s.equals("+");
	}

	public boolean isCaret() {
		return s.equals("^");
	}

	public boolean isMultiply() {
		return s.equals("*");
	}

	public boolean isDerivative() {
		return s.equals("diff");
	}

	public boolean isDivide() {
		return s.equals("/");
	}

	public boolean isEqualSign() {
		return s.equals("=");
	}

	public boolean isGreaterThan() {
		return s.equals(">");
	}

	public boolean isGreaterThanOrEqualTo() {
		return s.equals(">=");
	}

	public boolean isLessThan() {
		return s.equals("<");
	}

	public boolean isLessThanOrEqualTo() {
		return s.equals("<=");
	}

	public boolean isEquality() {
		return s.equals("==");
	}

	public boolean isNotEqualTo() {
		return s.equals("!=");
	}

	public boolean isOr() {
		return s.equals("||");
	}

	public boolean isAnd() {
		return s.equals("&&");
	}

	public boolean isInequality() {
		return isGreaterThan()
			|| isLessThan()
			|| isGreaterThanOrEqualTo()
			|| isLessThanOrEqualTo()
			|| isNotEqualTo();

	}

	public boolean isBooleanOperation() {
		return isOr() || isAnd();
	}

	public boolean isBinary() {
		return isCaret()
			|| isPlusSign()
			|| isMinusSign()
			|| isMultiply()
			|| isDivide()
			|| isEqualSign()
			|| isBooleanOperation()
			|| isInequality()
			|| isEquality();
	}

	public float precedence() {
		if (isFunction())
			return 1;
		if (isCaret())
			return 2;
		if (isDivide())
			return 3;
		if (isMultiply())
			return 3;
		if (isPlusSign())
			return 4;
		if (isMinusSign())
			return 4;
		if (isEquality())
			return 6;
		if (isInequality())
			return 6;
		if (isBooleanOperation())
			return 7;
		if (isEqualSign())
			return 10;

		return -1;
	}
		
	public boolean isFunction() {
		return FunctionFactory.isFunctionName(s);
	}

	public boolean isOperation() {
		return isFunction() || isBinary();
	}

	public int getPos() {
		return pos;
	}

	public int hashCode() {
		//cache hashcode...
		if (hashcode == 0)
			hashcode = (s + type).hashCode();

		return hashcode;
	}
}

