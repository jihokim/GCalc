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


public class GComplex extends GNumber {
	
	private GNumber re, im;
	
	public GComplex(double x, double y)
	{
		this(new GDouble(x), new GDouble(y));
	}

	public GComplex(GNumber a, GNumber b) {
		re = a;
		im = b;
	}
	
	public static GComplex polar(GNumber r, GNumber t) {
		return new GComplex(r.multiply(t.cos()),r.multiply(t.sin()));
	}
	
	public static GNumber principalRootOfUnity(int n) {
		if (n==1)
			return ONE;
		
		if (n==2)
			return IMAG;

		if (n==3)
			return new GComplex(new GRational(1,2), new GDouble(Math.sqrt(3)/2));
		
		double t = Math.PI/n;
		return new GComplex(new GDouble(Math.cos(t)), new GDouble(Math.sin(t)) );
	}

	private GNumber add(GComplex b) {
		GNumber x = re.add(b.re);
		GNumber y = im.add(b.im);
		return new GComplex(x,y);
	}
	
	private GNumber subtract(GComplex b) {
		GNumber x = re.subtract(b.re);
		GNumber y = im.subtract(b.im);
		return new GComplex(x,y);
	}
	
	private GNumber multiply(GComplex b) {
		GNumber x = re.multiply(b.re).subtract(im.multiply(b.im));
		GNumber y = re.multiply(b.im).add(im.multiply(b.re));
		return new GComplex(x,y);
	}
	
	private GNumber divide(GComplex b) {
		GNumber x = re.multiply(b.re).add(im.multiply(b.im));
		GNumber y = im.multiply(b.re).subtract(re.multiply(b.im));
		GNumber z = b.modulusSquared();
		return new GComplex(x.divide(z), y.divide(z));
	}
	
	public GNumber add(GNumber b) {
		if (b instanceof GComplex) 
			return add((GComplex) b);
		
		return new GComplex(re.add(b), im);
	}
	
	public GNumber subtract(GNumber b) {
		if (b instanceof GComplex) 
			return subtract((GComplex) b);
		
		return new GComplex(re.subtract(b), im);
	}
	
	public GNumber multiply(GNumber b) {
		if (b instanceof GComplex) 
			return multiply((GComplex) b);
		
		return new GComplex(re.multiply(b), im.multiply(b));
	}
	
	public GNumber divide(GNumber b) {
		if (b instanceof GComplex) 
			return divide((GComplex) b);
		
		return new GComplex(re.divide(b), im.divide(b));
	}
	
	public GNumber modulus() {
		return modulusSquared().sqrt();
	}
	
	public GNumber modulusSquared() {
		return re.multiply(re).add(im.multiply(im));
	}
	
	public GNumber argument() {
		return new GDouble(Math.atan2(im.doubleValue(), re.doubleValue()));
	}
	
	public GNumber conjugate() {
		return new GComplex(re, im.negate());
	}

	public GNumber negate() {
		return new GComplex(re.negate(),im.negate());
	}

	public GNumber sqrt() {
		GNumber mag = modulus().sqrt();
		GNumber arg = argument().divide(GNumber.TWO);
		
//		System.out.println(this+" "+mag);
		return polar(mag,arg);
	}

	public GNumber cbrt() {
		return root(3);
	}

	public double doubleValue() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isZero() {
		return re.isZero() && im.isZero();
	}

	public boolean isOne() {
		return re.isOne() && im.isZero();
	}

	public GComplex floatingValue() {
		return new GComplex(re.floatingValue(), im.floatingValue());
	}

	public GComplex integerValue() {
		return new GComplex(re.integerValue(), im.integerValue());
	}

	public GComplex complexValue() {
		return this;
	}

	public GNumber exp() {
		// e^(a+bi) = e^a*e^(bi) = e^a(cos(b)+i*sin(b))
		GNumber exp = re.exp();
		
		return new GComplex(exp.multiply(im.cos()), exp.multiply(im.sin()));
	}
	
	public GNumber ln() {
		return new GComplex(modulus().ln(), argument());
	}

	public GNumber cos() {  
		GNumber x = re.cos().multiply(im.cosh());
		GNumber y = re.sin().multiply(im.sinh());
		return new GComplex(x,y.negate());
	}
	
	public GNumber sin() { 
		GNumber x = re.sin().multiply(im.cosh());
		GNumber y = re.cos().multiply(im.sinh());
		return new GComplex(x,y);
	}
	
	public GNumber tan() { 
		return sin().divide(cos());
	}

	public GNumber sec() { 
		return ONE.divide(cos()); 
	}
	
	public GNumber csc() { 
		return ONE.divide(sin());
	}
	
	public GNumber cot() { 
		return cos().divide(sin());
	}

	public GNumber cosh() {  
		GNumber x = re.cosh().multiply(im.cos());
		GNumber y = re.sinh().multiply(im.sin());
		return new GComplex(x,y.negate());
	}
	
	public GNumber sinh() { 
		GNumber x = re.sinh().multiply(im.cos());
		GNumber y = re.cosh().multiply(im.sin());
		return new GComplex(x,y);
	}
	
	public GNumber tanh() { 
		//tanh(a+bi) = (sin(2a)+i*sinh(x))/(cos(2a)+cosh(2a))
		GNumber twoRe = re.multiply(TWO);
		GNumber twoIm = im.multiply(TWO);
		GNumber factor = twoIm.cos().add(twoIm.cosh());
		GNumber x = twoRe.sin().divide(factor);
		GNumber y = twoRe.sinh().divide(factor);
		return new GComplex(x,y);
	}

	public GNumber sech() { 
		return ONE.divide(cosh()); 
	}
	
	public GNumber csch() { 
		return ONE.divide(sinh());
	}
	
	public GNumber coth() { 
		return cosh().divide(sinh());
	}
	
	public GNumber asin() {  
		return ONE.subtract(this.multiply(this)).sqrt().add(this.multiply(IMAG)).ln().multiply(IMAG).negate();
	}

	public GNumber acos() {  
		return this.add(ONE.subtract(this.multiply(this)).sqrt().multiply(IMAG)).ln().multiply(IMAG).negate();
	}
	
	public GNumber atan() { 
		GNumber m = new GComplex(im.negate(), re);
		return ONE.subtract(m).ln().subtract(ONE.add(m).ln()).multiply(HALFI);
	}
	public GNumber asec() { 
		return ONE.divide(this).acos(); 
	}
	
	public GNumber acsc() { 
		return ONE.divide(this).asin();
	}
	
	public GNumber acot() { 
		return ONE.divide(this).atan();
	}
	
	public GNumber asinh() {
		return this.add(this.multiply(this).add(ONE).sqrt()).ln();
	}
	
	public GNumber acosh() {
		return this.subtract(ONE).sqrt().multiply(this.add(ONE).sqrt()).add(this).ln();
	}
	
	public GNumber atanh() { 
		return ONE.add(this).sqrt().subtract(ONE.subtract(this).sqrt()).divide(TWO);
	}
	
	public GNumber asech() { 
		return ONE.divide(this).acos();
	}

	public GNumber acsch() { 
		return ONE.divide(this).asin();
	}
	
	public GNumber acoth() { 
		return ONE.divide(this).atan();
	}
	
	public boolean lt(GNumber x) { throw new UnsupportedOperationException(); }
	public boolean le(GNumber x) { throw new UnsupportedOperationException(); }
	public boolean gt(GNumber x) { throw new UnsupportedOperationException(); }
	public boolean ge(GNumber x) { throw new UnsupportedOperationException(); }

	public GNumber pow(GNumber x) {
		return pow(x.complexValue());
	}

	protected GNumber pow(GComplex x) {
		GNumber argument = argument();
		GNumber factor = modulus().pow(x.re).divide(x.im.multiply(argument).exp());
		GNumber angle = argument.multiply(x.re).add(modulus().ln().multiply(x.im));

		return GComplex.polar(factor, angle);
	}
	
	public String toString() {
		return re+"+"+im+"I";
	}

	public GNumber pow(int n) {
		return pow(new GInteger(n));
	}

	public GNumber root(int n) {
		return pow(new GRational(1,n));
	}
}
