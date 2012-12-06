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

import static java.lang.Double.NaN;

import java.math.BigInteger;


public abstract class GNumber {

	public final static GDouble PI = new GDouble(Math.PI);
	public final static GDouble E = new GDouble(Math.E);
	public final static GInteger ZERO = new GInteger(BigInteger.ZERO);
	public final static GInteger ONE = new GInteger(BigInteger.ONE);
	public final static GInteger TWO = new GInteger(2);
	public final static GInteger THREE = new GInteger(3);
	public final static GNumber HALF = ONE.divide(TWO);	
	public final static GNumber THIRD = new GRational(1,3);	
	public final static GComplex IMAG = new GComplex(ZERO,ONE);
	public final static GComplex HALFI = new GComplex(ZERO,HALF);
	public final static GDouble NAN = new GDouble(Double.NaN);

	public static GAngleUnit angleUnit = GAngleUnit.RADIAN;	
	
	public static double asinh(double x) {
		return Math.log(x+Math.sqrt(x*x+1));
	}
	public static double acosh(double x) {
		return x>=1 ? Math.log(x+Math.sqrt(x*x-1)) : NaN;
	}
	public static double atanh(double x) {
		return Math.abs(x)<1 ? Math.log((1+x)/(1-x))/2 : NaN;
	}
	public static double acsch(double x) {
		return Math.log(1/x+Math.sqrt(1+x*x)/Math.abs(x));
	}
	public static double asech(double x) {
		return (0<x && x<=1) ? Math.log((1+Math.sqrt(1-x*x))/x) : NaN;
	}
	public static double acoth(double x) {
		return Math.abs(x)>1 ? Math.log((1+x)/(1-x))/2 : NaN;
	}
	public static double cos(double x) {
		return Math.cos(angleUnit.factor*x);
	}
	public static double sin(double x) {
		return Math.sin(angleUnit.factor*x);
	}
	public static double tan(double x) {
		return Math.tan(angleUnit.factor*x);
	}
	public static double acos(double x) {
		return Math.acos(x)/angleUnit.factor;
	}
	public static double asin(double x) {
		return Math.acos(x)/angleUnit.factor;
	}
	public static double atan(double x) {
		return Math.atan(x)/angleUnit.factor;
	}
	

	public GNumber subtract(GNumber x) {
		return this.add(x.negate());
	}	

	public abstract boolean isZero();
	public abstract boolean isOne();
	
	public abstract GNumber add(GNumber x);
	public abstract GNumber multiply(GNumber x);
	public abstract GNumber divide(GNumber x);
	public abstract GNumber negate();
	public abstract GNumber pow(GNumber x);
	
	public abstract GNumber sqrt();
	public abstract GNumber pow(int n);
	public abstract GNumber root(int n);
	public abstract GNumber cbrt();
	public abstract GNumber exp();
	public abstract GNumber ln();

	public abstract GNumber cos();
	public abstract GNumber sin();
	public abstract GNumber tan();
	public abstract GNumber sec();
	public abstract GNumber csc();
	public abstract GNumber cot();
	public abstract GNumber asin();
	public abstract GNumber acos();
	public abstract GNumber atan();
	public abstract GNumber asec();
	public abstract GNumber acsc();
	public abstract GNumber acot();
	public abstract GNumber sinh();
	public abstract GNumber cosh();
	public abstract GNumber tanh();
	public abstract GNumber sech();
	public abstract GNumber csch();
	public abstract GNumber coth();
	public abstract GNumber asinh();
	public abstract GNumber acosh();
	public abstract GNumber atanh();
	public abstract GNumber asech();
	public abstract GNumber acsch();
	public abstract GNumber acoth();
	
	public abstract boolean lt(GNumber x);
	public abstract boolean le(GNumber x);
	public abstract boolean gt(GNumber x);
	public abstract boolean ge(GNumber x);

	public abstract GNumber floatingValue();
	public abstract GNumber integerValue();
	public abstract GComplex complexValue();
	
	public abstract double doubleValue();
	
	public double evaluation() {
		return complexValue().modulus().doubleValue();
	}
	
	
	public boolean equals(Object o) {
		if (!(o instanceof GNumber))
			return false;
		
		GNumber x = (GNumber) o;
		
		return this.subtract(x).isZero();
	}

}



