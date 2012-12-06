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

public class GDouble extends GNumber
{	
	private double value;
	
	public GDouble(double x) {
		value = x;
	}
	
	public GNumber add(GNumber x) 
	{
		if (x instanceof GComplex) {
			return complexValue().add(x);
		}

		return new GDouble(value+x.doubleValue());
	}
	
	public GNumber subtract(GNumber x)
	{
		if (x instanceof GComplex) {
			return complexValue().subtract(x);
		}
		return new GDouble(value-x.doubleValue());
	}

	public GNumber multiply(GNumber x)
	{
		if (x instanceof GComplex) {
			return complexValue().multiply(x);
		}
		return new GDouble(value*x.doubleValue());
	}

	public GNumber divide(GNumber x)
	{
		if (x instanceof GComplex) {
			return complexValue().divide(x);
		}
		
		return new GDouble(value/x.doubleValue());
	}

	public GNumber negate()
	{
		return new GDouble(-value);
	}

	public GNumber sqrt() { 
		return (value<0) ? 
			new GComplex(ZERO, negate().sqrt()) :
			new GDouble(Math.sqrt(value)); 
	}
	public GNumber cbrt() { return new GDouble(Math.cbrt(value)); }
	public GNumber exp() {
		double x = Math.exp(value);
		return new GDouble(x); 
	}
	public GNumber ln() { return new GDouble(Math.log(value)); }

	public GNumber sin() { return new GDouble(sin(value)); }
	public GNumber cos() { return new GDouble(cos(value)); }
	public GNumber tan() { return new GDouble(tan(value)); }
	public GNumber sec() { return new GDouble(1/cos(value)); }
	public GNumber csc() { return new GDouble(1/sin(value)); }
	public GNumber cot() { return new GDouble(1/tan(value)); }
	public GNumber asin() { return new GDouble(asin(value)); }
	public GNumber acos() { return new GDouble(acos(value)); }
	public GNumber atan() { return new GDouble(atan(value)); }
	public GNumber acsc() { return new GDouble(asin(1/value)); }
	public GNumber asec() { return new GDouble(acos(1/value)); }
	public GNumber acot() { return new GDouble(atan(1/value)); }
	public GNumber sinh() { return new GDouble(Math.sinh(value)); }
	public GNumber cosh() { return new GDouble(Math.cosh(value)); }
	public GNumber tanh() { return new GDouble(Math.tanh(value)); }
	public GNumber sech() { return new GDouble(1/Math.cosh(value)); }
	public GNumber csch() { return new GDouble(1/Math.sinh(value)); }
	public GNumber coth() { return new GDouble(1/Math.tanh(value)); }
	public GNumber asinh() { return new GDouble(asinh(value)); }
	public GNumber acosh() { return new GDouble(acosh(value)); }
	public GNumber atanh() { return new GDouble(atanh(value)); }
	public GNumber asech() { return new GDouble(asech(value)); }
	public GNumber acsch() { return new GDouble(acsch(value)); }
	public GNumber acoth() { return new GDouble(acoth(value)); }

	public GDouble floatingValue() {
		return this;
	}	
	
	public double doubleValue() {
		return value;
	}

	public GInteger integerValue() {
		return new GInteger((int) value);
	}	
	
	public GComplex complexValue() {
		return new GComplex(this,ZERO);
	}
	
	public long longValue() {
		return (long) value;
	}

	public int intValue() {
		return (int) value;
	}
	
	public String toString() {
		return "num("+value+")";
	}

	public boolean ge(GNumber x) {
		return value >= x.doubleValue();
	}

	public boolean gt(GNumber x) {
		return value > x.doubleValue();
	}

	public boolean le(GNumber x) {
		return value <= x.doubleValue();
	}

	public boolean lt(GNumber x) {
		return value < x.doubleValue();
	}

	public boolean isOne() {
		return value==1.0;
	}

	public boolean isZero() {
		return value==0.0;
	}

	public GNumber pow(GNumber x)
	{
		if (x instanceof GComplex)
			return complexValue().pow(x);
		
		if (x instanceof GRational && value<0) {
			return pow((GRational) x);
		}

		return new GDouble(Math.pow(value,x.doubleValue()));
	}
	
	private GNumber pow(GRational r) {
		BigInteger den = r.getDenominator();

		if (den.intValue()%2==1) {
			return new GDouble(Math.signum(value)*Math.pow(Math.abs(value), 1.0/den.intValue())).pow(r.getNumerator().intValue());
		}
		
		return complexValue().pow(r);
	}
	
	public GNumber pow(int n) {
		if (n==0) {
			return ONE;
		}
		else if (n>0) {
			return new GDouble(Math.pow(value,n));
		}
		
		return ONE.divide(this.pow(-n));
	}

	public GNumber root(int n) {
		if (value>=0)
			return new GDouble(Math.pow(value, 1.0/n));

		if (n%2==1)
			return new GDouble(-Math.pow(-value, 1.0/n));
		
		return new GDouble(Math.pow(-value, 1.0/n)).multiply(GComplex.principalRootOfUnity(n));
	}
	
}