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

public class GRational extends GNumber {
	boolean simplified = false;
	private BigInteger num, den;
	
	public BigInteger getNumerator()
	{
		return num;
	}

	public BigInteger getDenominator()
	{
		return den;
	}

	protected GRational(BigInteger n, BigInteger d) {
		if (BigInteger.ZERO.equals(d))
			throw new IllegalArgumentException("Zero denominator");
		num = n;
		den = d;
		simplify();
	}
	
	
	private void simplify() {
		if (simplified)
			return;
		
		BigInteger gcd = num.gcd(den);
		num = num.divide(gcd);
		den = den.divide(gcd);
		
		if (den.compareTo(BigInteger.ZERO)<0) {
			num = num.negate();
			den = den.abs();
		}
	}
	
	public GRational(int x, int y) {
		this(new BigInteger(""+x), new BigInteger(""+y));
	}
	
	public String toString() {
		simplify();
		if (BigInteger.ONE.equals(den)) {
			return "rat("+num+")";
		}
		
		
		return "rat("+num+"/"+den+")";
	}
	
	public boolean isOne() {
		simplify();
		return num.equals(BigInteger.ONE);
	}

	public boolean isZero() {
		return num.equals(BigInteger.ZERO);
	}

	public GNumber sqrt() { 
		if (lt(ZERO))
			return new GComplex(ZERO, negate().sqrt());
		
		return new GDouble(Math.sqrt(doubleValue())); 
	}
	public GNumber cbrt() { return new GDouble(Math.cbrt(doubleValue())); }
	public GNumber exp() { return new GDouble(Math.exp(doubleValue())); }
	public GNumber ln() { return new GDouble(Math.log(doubleValue())); }

	public GNumber sin() { return new GDouble(Math.sin(doubleValue())); }
	public GNumber cos() { return new GDouble(Math.cos(doubleValue())); }
	public GNumber tan() { return new GDouble(Math.tan(doubleValue())); }
	public GNumber sec() { return new GDouble(1/Math.cos(doubleValue())); }
	public GNumber csc() { return new GDouble(1/Math.sin(doubleValue())); }
	public GNumber cot() { return new GDouble(1/Math.tan(doubleValue())); }
	public GNumber asin() { return new GDouble(Math.asin(doubleValue())); }
	public GNumber acos() { return new GDouble(Math.acos(doubleValue())); }
	public GNumber atan() { return new GDouble(Math.atan(doubleValue())); }
	public GNumber acsc() { return new GDouble(Math.asin(1/doubleValue())); }
	public GNumber asec() { return new GDouble(Math.acos(1/doubleValue())); }
	public GNumber acot() { return new GDouble(Math.atan(1/doubleValue())); }
	public GNumber sinh() { return new GDouble(Math.sinh(doubleValue())); }
	public GNumber cosh() { return new GDouble(Math.cosh(doubleValue())); }
	public GNumber tanh() { return new GDouble(Math.tanh(doubleValue())); }
	public GNumber sech() { return new GDouble(1/Math.cosh(doubleValue())); }
	public GNumber csch() { return new GDouble(1/Math.sinh(doubleValue())); }
	public GNumber coth() { return new GDouble(1/Math.tanh(doubleValue())); }
	public GNumber asinh() { return new GDouble(asinh(doubleValue())); }
	public GNumber acosh() { return new GDouble(acosh(doubleValue())); }
	public GNumber atanh() { return new GDouble(atanh(doubleValue())); }
	public GNumber asech() { return new GDouble(asech(doubleValue())); }
	public GNumber acsch() { return new GDouble(acsch(doubleValue())); }
	public GNumber acoth() { return new GDouble(acoth(doubleValue())); }

	public GNumber floatingValue() {
		return new GDouble(doubleValue());
	}

	public GInteger integerValue() {
		return new GInteger(num.divide(den));
	}

	public GComplex complexValue() {
		return new GComplex(this,ZERO);
	}

	@Override
	public GNumber add(GNumber x) {
		if (x instanceof GInteger) {
			GInteger val = (GInteger) x;
			return new GRational(num.add(den.multiply(val.bigIntegerValue())), den);
		}
		else if (x instanceof GRational) {
			GRational val = (GRational) x;
			BigInteger n = num.multiply(val.den).add(val.num.multiply(den));
			BigInteger d = den.multiply(val.den);
			return new GRational(n,d);
		}
		
		return x.add(this);
	}

	@Override
	public GNumber divide(GNumber x) {
		if (x instanceof GInteger) {
			GInteger val = (GInteger) x;
			return new GRational(num, den.multiply(val.bigIntegerValue()));
		}
		else if (x instanceof GRational) {
			GRational val = (GRational) x;
			return new GRational(num.multiply(val.den),den.multiply(val.num));
		}
		return floatingValue().divide(x);
	}

	public GNumber multiply(GNumber x) {
		if (x instanceof GInteger) {
			GInteger val = (GInteger) x;
			return new GRational(num.multiply(val.bigIntegerValue()), den);
		}
		else if (x instanceof GRational) {
			GRational val = (GRational) x;
			return new GRational(num.multiply(val.num), den.multiply(val.den));
		}
		
		return x.multiply(this);
	}

	public GNumber negate() {		
		return new GRational(num.negate(), den);
	}

	public GNumber pow(GNumber x) {
		if (x instanceof GInteger) {
			int n = ((GInteger) x).intValue();
			return new GRational(num.pow(n),den.pow(n));
		}
		
		return floatingValue().pow(x);
	}

	public boolean le(GNumber x) {
		if (x instanceof GRational) {
			return le((GRational) x);
		}
		else if (x instanceof GInteger) {
			return le((GInteger) x);
		}
		
		return doubleValue() <= x.doubleValue();
	}
	
	public boolean lt(GNumber x) {
		if (x instanceof GRational) {
			return lt((GRational) x);
		}
		else if (x instanceof GInteger) {
			return lt((GInteger) x);
		}
		
		return doubleValue() < x.doubleValue();
	}

	public boolean ge(GNumber x) {
		if (x instanceof GRational) {
			return ge((GRational) x);
		}
		else if (x instanceof GInteger) {
			return ge((GInteger) x);
		}
		
		return doubleValue() >= x.doubleValue();
	}
	
	public boolean gt(GNumber x) {
		if (x instanceof GRational) {
			return gt((GRational) x);
		}
		else if (x instanceof GInteger) {
			return gt((GInteger) x);
		}
		
		return doubleValue() > x.doubleValue();
	}

	private boolean lt(GRational x) {
		return num.multiply(x.den).compareTo(x.num.multiply(den))<0;
	}
	private boolean gt(GRational x) {
		return num.multiply(x.den).compareTo(x.num.multiply(den))>0;
	}
	private boolean le(GRational x) {
		return num.multiply(x.den).compareTo(x.num.multiply(den))<=0;
	}
	private boolean ge(GRational x) {
		return num.multiply(x.den).compareTo(x.num.multiply(den))>=0;
	}

	private boolean lt(GInteger x) {
		return num.compareTo(x.bigIntegerValue().multiply(den))<0;
	}
	private boolean gt(GInteger x) {
		return num.compareTo(x.bigIntegerValue().multiply(den))>0;
	}
	private boolean le(GInteger x) {
		return num.compareTo(x.bigIntegerValue().multiply(den))<=0;
	}
	private boolean ge(GInteger x) {
		return num.compareTo(x.bigIntegerValue().multiply(den))>=0;
	}

	public double doubleValue() {
		return num.doubleValue()/den.doubleValue();
	}

	public GNumber pow(int n) {
		return new GRational(num.pow(n), den.pow(n));
	}

	public GNumber root(int n) {
		return this.floatingValue().root(n);
	}

}
