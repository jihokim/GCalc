/**
 * Juu Programming Language 
 * Copyright (C) 2010 Jiho Kim
 * 
 * This file is part of Juu.
 * 
 * Juu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Juu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Juu. If not, see <http://www.gnu.org/licenses/>.
 */

package net.gcalc.juu.environment;


public enum MathOps {
	CSC { GNumber evaluate(GNumber x) { return x.csc(); } },
	SEC { GNumber evaluate(GNumber x) { return x.sec(); } },
	COT { GNumber evaluate(GNumber x) { return x.cot(); } },
	SINH { GNumber evaluate(GNumber x) { return x.sinh(); } },
	COSH { GNumber evaluate(GNumber x) { return x.cosh(); } },
	TANH { GNumber evaluate(GNumber x) { return x.tanh(); } },
	CSCH { GNumber evaluate(GNumber x) { return x.csch(); } },
	SECH { GNumber evaluate(GNumber x) { return x.sech(); } },
	COTH { GNumber evaluate(GNumber x) { return x.coth(); } },
	ASIN { GNumber evaluate(GNumber x) { return x.asin(); } },
	ACOS { GNumber evaluate(GNumber x) { return x.acos(); } },
	ATAN { GNumber evaluate(GNumber x) { return x.atan(); } },
	ACSC { GNumber evaluate(GNumber x) { return x.acsc(); } },
	ASEC { GNumber evaluate(GNumber x) { return x.asec(); } },
	ACOT { GNumber evaluate(GNumber x) { return x.acot(); } },
	ASINH { GNumber evaluate(GNumber x) { return x.asinh(); } },
	ACOSH { GNumber evaluate(GNumber x) { return x.acosh(); } },
	ATANH { GNumber evaluate(GNumber x) { return x.atanh(); } },
	ACSCH { GNumber evaluate(GNumber x) { return x.acsch(); } },
	ASECH { GNumber evaluate(GNumber x) { return x.asech(); } },
	ACOTH { GNumber evaluate(GNumber x) { return x.acoth(); } },
	SQRT { GNumber evaluate(GNumber x) { return x.sqrt(); } },
	CBRT { GNumber evaluate(GNumber x) { return x.cbrt(); } },
	LN { GNumber evaluate(GNumber x) { return x.ln(); } },
	EXP { GNumber evaluate(GNumber x) { return x.exp(); } },
	SIN { GNumber evaluate(GNumber x) { return x.sin(); } },
	COS { GNumber evaluate(GNumber x) { return x.cos(); } },
	TAN { GNumber evaluate(GNumber x) { return x.tan(); } },
	;

	abstract GNumber evaluate(GNumber x);
}
