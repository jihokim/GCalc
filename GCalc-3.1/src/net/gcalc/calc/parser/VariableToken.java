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

public class VariableToken extends Token {

	public VariableToken(String s) {
		super(s, Token.VARIABLE);
	}
	
	public final static Token A_VAR = new VariableToken("a");
	public final static VariableToken B_VAR = new VariableToken("b");
	public final static VariableToken C_VAR = new VariableToken("c");
	public final static VariableToken D_VAR = new VariableToken("d");
	public final static VariableToken E_VAR = new VariableToken("e");
	public final static VariableToken F_VAR = new VariableToken("f");
	public final static VariableToken G_VAR = new VariableToken("g");

	public final static VariableToken H_VAR = new VariableToken("h");
	public final static VariableToken I_VAR = new VariableToken("i");
	public final static VariableToken J_VAR = new VariableToken("j");
	public final static VariableToken K_VAR = new VariableToken("k");
	public final static VariableToken L_VAR = new VariableToken("l");
	public final static VariableToken M_VAR = new VariableToken("m");
	public final static VariableToken N_VAR = new VariableToken("n");
	
	public final static VariableToken O_VAR = new VariableToken("o");
	public final static VariableToken P_VAR = new VariableToken("p");
	public final static VariableToken Q_VAR = new VariableToken("q");
	public final static VariableToken R_VAR = new VariableToken("r");
	public final static VariableToken S_VAR = new VariableToken("s");
	public final static VariableToken T_VAR = new VariableToken("t");
	public final static VariableToken U_VAR = new VariableToken("u");
	public final static VariableToken V_VAR = new VariableToken("v");
	public final static VariableToken W_VAR = new VariableToken("w");
	public final static VariableToken X_VAR = new VariableToken("x");
	public final static VariableToken Y_VAR = new VariableToken("y");
	public final static VariableToken Z_VAR = new VariableToken("z");
}

