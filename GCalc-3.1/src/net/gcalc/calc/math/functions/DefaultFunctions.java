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


package net.gcalc.calc.math.functions;

import net.gcalc.calc.parser.Token;


public interface DefaultFunctions {
	public final static Token LN_TOKEN = new Token("ln", Token.OPERATION);
	public final static Token MULT_TOKEN = new Token("*", Token.OPERATION);
	public final static Token DIV_TOKEN = new Token("/", Token.OPERATION);
	public final static Token PLUS_TOKEN = new Token("+", Token.OPERATION);
	public final static Token CARET_TOKEN = new Token("^", Token.OPERATION);
	public final static Token MINUS_TOKEN = new Token("-", Token.OPERATION);
	public final static Token NEGATE_TOKEN = new Token("neg", Token.OPERATION);
	public final static Token SUM_TOKEN = new Token("sum", Token.OPERATION);
	public final static Token PROD_TOKEN = new Token("prod", Token.OPERATION);

	public final static Token SIN_TOKEN = new Token("sin", Token.OPERATION);
	public final static Token COS_TOKEN = new Token("cos", Token.OPERATION);
	public final static Token TAN_TOKEN = new Token("tan", Token.OPERATION);
	public final static Token CSC_TOKEN = new Token("csc", Token.OPERATION);
	public final static Token SEC_TOKEN = new Token("sec", Token.OPERATION);
	public final static Token COT_TOKEN = new Token("cot", Token.OPERATION);
	public final static Token SINH_TOKEN = new Token("sinh", Token.OPERATION);
	public final static Token COSH_TOKEN = new Token("cosh", Token.OPERATION);
	public final static Token TANH_TOKEN = new Token("tanh", Token.OPERATION);
	public final static Token CSCH_TOKEN = new Token("csch", Token.OPERATION);
	public final static Token SECH_TOKEN = new Token("sech", Token.OPERATION);
	public final static Token COTH_TOKEN = new Token("coth", Token.OPERATION);
	public final static Token ASIN_TOKEN = new Token("asin", Token.OPERATION);
	public final static Token ACOS_TOKEN = new Token("acos", Token.OPERATION);
	public final static Token ATAN_TOKEN = new Token("atan", Token.OPERATION);
	public final static Token ACSC_TOKEN = new Token("acsc", Token.OPERATION);
	public final static Token ASEC_TOKEN = new Token("asec", Token.OPERATION);
	public final static Token ACOT_TOKEN = new Token("acot", Token.OPERATION);
	public final static Token ASINH_TOKEN = new Token("asinh", Token.OPERATION);
	public final static Token ACOSH_TOKEN = new Token("acosh", Token.OPERATION);
	public final static Token ATANH_TOKEN = new Token("atanh", Token.OPERATION);
	public final static Token ACSCH_TOKEN = new Token("acsch", Token.OPERATION);
	public final static Token ASECH_TOKEN = new Token("asech", Token.OPERATION);
	public final static Token ACOTH_TOKEN = new Token("acoth", Token.OPERATION);

	public final static Token SQRT_TOKEN = new Token("sqrt", Token.OPERATION);
	public final static Token ROOT_TOKEN = new Token("root", Token.OPERATION);

	public final static Token MIN_TOKEN = new Token("min", Token.OPERATION);
	public final static Token MAX_TOKEN = new Token("max", Token.OPERATION);
	public final static Token SGN_TOKEN = new Token("sgn", Token.OPERATION);

	public final static Token NAN_TOKEN = new Token("NaN", Token.CONSTANT);
	public final static Token ONE_TOKEN = new Token("1.0", Token.CONSTANT);
	public final static Token ZERO_TOKEN = new Token("0.0", Token.CONSTANT);
	public final static Token TWO_TOKEN = new Token("2.0", Token.CONSTANT);
	public final static Token FOUR_TOKEN = new Token("4.0", Token.CONSTANT);
	public final static Token TEN_TOKEN = new Token("10.0", Token.CONSTANT);

	public final static Function NOT_A_NUMBER = FunctionFactory.getFunction(NAN_TOKEN);
	public final static Function ZERO = FunctionFactory.getFunction(ZERO_TOKEN);
	public final static Function ONE = FunctionFactory.getFunction(ONE_TOKEN);
	public final static Function TWO = FunctionFactory.getFunction(TWO_TOKEN);
	public final static Function FOUR = FunctionFactory.getFunction(FOUR_TOKEN);
	public final static Function TEN = FunctionFactory.getFunction(TEN_TOKEN);
	public final static Function LOG10 = FunctionFactory.getFunction(LN_TOKEN, TEN);
	public final static Function NEG_ONE = FunctionFactory.getFunction(NEGATE_TOKEN, ONE);
}

