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

import java.math.BigInteger;

public class GInteger extends GNumber {
	private BigInteger value;

	public GInteger(String x) {
		this(new BigInteger(x));
	}

	public GInteger(BigInteger x) {
		value = x;
	}

	public GInteger(int x) {
		this(new BigInteger(String.valueOf(x)));
	}

	public GInteger(long x) {
		this(new BigInteger(String.valueOf(x)));
	}

	public boolean isOne() {
		return value.equals(BigInteger.ONE);
	}

	public boolean isZero() {
		return value.equals(BigInteger.ZERO);
	}

	public GDouble floatingValue() {
		return new GDouble(value.doubleValue());
	}

	public GInteger integerValue() {
		return new GInteger(value.intValue());
	}

	public BigInteger bigIntegerValue() {
		return value;
	}

	public int intValue() {
		return value.intValue();
	}

	public GNumber multiply(GNumber x) {
		if (x instanceof GInteger) {
			GInteger gbi = (GInteger) x;
			return new GInteger(value.multiply(gbi.value));
		}

		return x.multiply(this);
	}

	public GNumber add(GNumber x) {
		if (x instanceof GInteger) {
			GInteger gbi = (GInteger) x;
			return new GInteger(value.add(gbi.value));
		}
		return x.add(this);
	}

	public GNumber divide(GNumber x) {
		if (x instanceof GInteger) {
			GInteger val = (GInteger) x;
			return new GRational(value, val.value);
		}

		return this.floatingValue().divide(x);
	}

	public GNumber negate() {
		return new GInteger(value.negate());
	}

	public GNumber pow(GNumber x) {
		if (x instanceof GInteger) {
			return pow((GInteger) x);
		} else if (x instanceof GRational) {
			return pow((GRational) x);
		}

		return floatingValue().pow(x);
	}

	private GNumber pow(GInteger x) {
		return pow(x.intValue());
	}

	public GNumber pow(int n) {
		if (n == 0) {
			return GNumber.ONE;
		} 
		else if (n<0) {
			return ONE.divide(this.pow(-n));
		}

		return new GInteger(value.pow(n));
	}

	private GNumber pow(GRational x)
	{
		if (x.isZero())
			return ONE;
		
		if (x.lt(ZERO)) {
			return ONE.divide(this.pow(x.negate()));
		}
		
		BigInteger n = x.getNumerator().abs();
		BigInteger d = x.getDenominator().abs();
		
		return this.root(d.intValue()).pow(n.intValue());
	}
	
	public GNumber root(int n) {
		return floatingValue().root(n);
	}

	public GNumber sqrt() {
		return (value.compareTo(BigInteger.ZERO)<0) ?
				new GComplex(ZERO, negate().sqrt()) :
				new GDouble(Math.sqrt(doubleValue()));
	}

	public GNumber cbrt() {
		return new GDouble(Math.cbrt(value.doubleValue()));
	}

	public GNumber exp() {
		return new GDouble(Math.exp(value.doubleValue()));
	}

	public GNumber ln() {
		return new GDouble(Math.log(value.doubleValue()));
	}

	public GNumber sin() {
		return new GDouble(Math.sin(value.doubleValue()));
	}

	public GNumber cos() {
		return new GDouble(Math.cos(value.doubleValue()));
	}

	public GNumber tan() {
		return new GDouble(Math.tan(value.doubleValue()));
	}

	public GNumber sec() {
		return new GDouble(1 / Math.cos(value.doubleValue()));
	}

	public GNumber csc() {
		return new GDouble(1 / Math.sin(value.doubleValue()));
	}

	public GNumber cot() {
		return new GDouble(1 / Math.tan(value.doubleValue()));
	}

	public GNumber asin() {
		return new GDouble(Math.asin(value.doubleValue()));
	}

	public GNumber acos() {
		return new GDouble(Math.acos(value.doubleValue()));
	}

	public GNumber atan() {
		return new GDouble(Math.atan(value.doubleValue()));
	}

	public GNumber acsc() {
		return new GDouble(Math.asin(1 / value.doubleValue()));
	}

	public GNumber asec() {
		return new GDouble(Math.acos(1 / value.doubleValue()));
	}

	public GNumber acot() {
		return new GDouble(Math.atan(1 / value.doubleValue()));
	}

	public GNumber sinh() {
		return new GDouble(Math.sinh(value.doubleValue()));
	}

	public GNumber cosh() {
		return new GDouble(Math.cosh(value.doubleValue()));
	}

	public GNumber tanh() {
		return new GDouble(Math.tanh(value.doubleValue()));
	}

	public GNumber sech() {
		return new GDouble(1 / Math.cosh(value.doubleValue()));
	}

	public GNumber csch() {
		return new GDouble(1 / Math.sinh(value.doubleValue()));
	}

	public GNumber coth() {
		return new GDouble(1 / Math.tanh(value.doubleValue()));
	}

	public GNumber asinh() {
		return new GDouble(asinh(value.doubleValue()));
	}

	public GNumber acosh() {
		return new GDouble(acosh(value.doubleValue()));
	}

	public GNumber atanh() {
		return new GDouble(atanh(value.doubleValue()));
	}

	public GNumber asech() {
		return new GDouble(asech(value.doubleValue()));
	}

	public GNumber acsch() {
		return new GDouble(acsch(value.doubleValue()));
	}

	public GNumber acoth() {
		return new GDouble(acoth(value.doubleValue()));
	}

	public String toString() {
		return "int(" + value + ")";
	}

	public boolean ge(GNumber x) {
		if (x instanceof GInteger)
			return value.compareTo(((GInteger) x).value) >= 0;

		return floatingValue().ge(x);
	}

	public boolean gt(GNumber x) {
		if (x instanceof GInteger)
			return value.compareTo(((GInteger) x).value) > 0;

		return floatingValue().gt(x);
	}

	public boolean le(GNumber x) {
		if (x instanceof GInteger)
			return value.compareTo(((GInteger) x).value) <= 0;

		return floatingValue().le(x);
	}

	public boolean lt(GNumber x) {
		if (x instanceof GInteger)
			return value.compareTo(((GInteger) x).value) < 0;

		return floatingValue().lt(x);
	}

	public long longValue() {
		return value.longValue();
	}

	public double doubleValue() {
		return value.doubleValue();
	}

	public GComplex complexValue() {
		return new GComplex(this, ZERO);
	}
}