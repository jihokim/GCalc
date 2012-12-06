/*  
GCalc 2.0 - Graphing calculator applet
Copyright (C) 2001 Jiho Kim

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

*/

import java.util.EmptyStackException;


/**
 *  Encapsulation of a postfix expression
 */
public class Postfix
{
    private double x = 0;
    private Stack pfStack;
    private String infixStr;
    private boolean isConstant;

    /**
     * Tests specific this class
     */
    public static void main(String[] args)
    {
	String str = "";

	for (int i=0; i<args.length; i++)
	    str +=args[i]+" ";

	str = str.trim();

	Postfix pf= new Postfix(str);
	System.out.println(pf.toPostfixString());
    }

    
    /**
     * Creates a very transient Postfix object which is only used for convenience.
     */
    Postfix(Stack s)
    {
	pfStack=s;
    }
    

    /**
     * Postfix object constructor
     * 
     * @param input An infix string from which to create the object.
     */
    public Postfix(String input)
    {
	infixStr = input.trim();

	Stack infix = new Stack();
	String eqn = input.trim();
	boolean okay = true;
	isConstant = true;

	Token tk = null;

	while (eqn.length()!=0 && okay) {
	    tk = new Token(eqn);
	    if (tk.isValid()) {
		isConstant = isConstant && ! tk.is("x");

		String content = tk.getTokenString();
		int num = tk.length();
		eqn = eqn.substring(num).trim();

		infix.push(tk);
	    }
	    else {
		infix.clear();
		okay = false;
	    }
	}

	pfStack = infix2postfix(infix.flip());

	//		System.out.println(pfStack.showAll());

	if (isMalformed())
	    pfStack = new Stack();
    }

    /**
     * Determines if the postfix generated is malformed.  For now,
     * checks if the parentheses match.
     *
     * @return <code>true</code> if parentheses match, <code>false</code> if parentheses don't match,
     */
    private boolean isMalformed()
    {
	boolean b = false;

	char[] ch = infixStr.toCharArray();
	int count=0;

	for (int i=0; i<ch.length; i++) {
	    if (ch[i]=='(') count++;
	    if (ch[i]==')') count--;

	    if (count<0) return true;
	}

	try {
	    b = (count!=0 || evaluate(0)==Double.MIN_VALUE);
	}
	catch (EmptyStackException e) {
	    return true;
	}

	return b;
    }

    /**
     * Computes and returns the derivative. 
     *
     * @return The derivative.
     */
    public Postfix derivative()
    {
	return PostfixDerivative.derivative(this);
    }

    /**
     * Performs the infix to postfix conversion.  No error checking occurs.
     *
     * @param infix Stack of tokens infix order.
     * @return the postfix stack equivalent to the valid infix input.  If the infix input is undefined, the return value is undefined.
     */
    private Stack infix2postfix(Stack infix)
    {
	Stack pfStack = new Stack();					//Stack for postfix
	Stack ops = new Stack();				//Stack for operations
	Stack temp = new Stack();

	Token lastTk = new Token("+");

	while (! infix.isEmpty()) {
	    pfStack.showAll();

	    Token tk = infix.pop();

	    /*NEGATION CODE
	      If conditions are right, "-" can mean the negative
	      of a number and not subtraction.
	    */
	    if (tk.is("-")) {
		if (pfStack.isEmpty()) {
		    tk = new Token("neg");
		}
		else if (lastTk.isOperation() && ! lastTk.is(")")) {
		    tk = new Token("neg");
		}
		else if (lastTk.isNumber()) {
		    tk = new Token("-");
		}
	    }

	    /*POSITIVE CODE
	      If conditions are right, "+" can mean the positive
	      of a number and not addition.
	    */
	    if (tk.is("+")) {
		if (pfStack.isEmpty()) {
		    tk = new Token("");
		}
		else if (lastTk.isOperation() && ! lastTk.is(")")) {
		    tk = new Token("");
		}
		else if (lastTk.isNumber()) {
		    tk = new Token("+");
		}
	    }

	    float precedence=tk.precedence();

	    // IMPLIED MULTIPLICATION CODE
	    if (! infix.isEmpty()) {
		Token nt = infix.peek();
		if ( (tk.is(")") && nt.is("(")) ||
		     (tk.isNumber() && nt.is("(")) ||
		     (tk.is(")") && nt.isNumber()) ||
		     (tk.is(")") && nt.precedence()==1) ||
		     (tk.isNumber() && nt.precedence()==1) ||
		     (tk.isNumber() && nt.isNumber())  )

		    infix.push(new Token("*"));
	    }

	    if (tk.isNumber()) {
		pfStack.push(tk);
	    }
	    else if (tk.isOperation()) {
		if (ops.isEmpty() || tk.is("(")) {
		    ops.push(tk);
		}
		else if (tk.is(")")) {
		    while (! ops.peek().is("(")) {
			pfStack.push(ops.pop());
			if (ops.isEmpty()) break;
		    }
		    if (! ops.isEmpty()) ops.pop();
		}
		else if (tk.isBinary()) {
		    while (ops.peek().precedence()<=precedence) {
			pfStack.push(ops.pop());
			if (ops.isEmpty()) break;
		    }
		    ops.push(tk);
		}
		else {

		    /* this if statement is to fix the -x^2 and x^-2
		     * problem.
		     */

		    if (tk.is("neg") && ops.peek().is("^"))
			precedence=1;

		    while (ops.peek().precedence()<precedence) {
			pfStack.push(ops.pop());
			if (ops.isEmpty()) break;
		    }
		    ops.push(tk);
		}
	    }

	    if (! tk.is("")) lastTk = tk;
	}

	while (! ops.isEmpty())
	    pfStack.push(ops.pop());

	Stack t = new Stack();
	while (! pfStack.isEmpty()) {
	    /* You can really tell I added the derivative code as an
	     * afterthought!  Uck! 
	     */

	    Token tk = pfStack.pop();
	    if (tk.is("ddx")) {
		Stack d = PostfixDerivative.derivative(new Postfix(sub(pfStack))).getPostfixStack();
		while (d!=null && ! d.isEmpty()) {
		    t.push(d.pop());
		}
	    }
	    else {
		t.push(tk);
	    }
	}

	while (! t.isEmpty())
	    pfStack.push(t.pop());

	return pfStack;
    }

    /**
     * Evaluates the postfix at x=val.
     *
     * @param val The evaluation point value.
     * @return The evaluated value.  Illegal operations yield a Double.NaN or Double.MIN_VALUE.
     */
    public double evaluate(double val)
    {
	double a=0;
	Stack rpf = new Stack();
	StackDoub ws = new StackDoub();
	double oper1, oper2;

	rpf=pfStack.flip();

	/* the following line of code introduces so nasty coupling
	 * between this class and the GCalc class.  How can we avoid
	 * it?
	 */
        double angleFactor = (GCalc.angle==GCalc.RADIAN)?1:Math.PI/180;

	//to catches malformed inputs.
	try {
	    while (! rpf.isEmpty()) {
		Token tk=rpf.pop();

		if (tk.isNumber()) {
		    if (tk.is("x"))
			ws.push(val);
		    else if (tk.is("E"))
			ws.push(Math.E);
		    else if (tk.is("PI"))
			ws.push(Math.PI);
		    else if (tk.is("rnd"))
			ws.push(Math.random());
		    else
			ws.push(Double.valueOf(tk.getTokenString()).doubleValue());
		}
		else if (tk.is("+")) {
		    oper2=ws.pop();
		    oper1=ws.pop();
		    ws.push(oper1+oper2);
		}
		else if (tk.is("-")) {
		    oper2=ws.pop();
		    oper1=ws.pop();
		    ws.push(oper1-oper2);
		}
		else if(tk.is("*")) {
		    oper2=ws.pop();
		    oper1=ws.pop();
		    ws.push(oper1*oper2);
		}
		else if(tk.is("/")) {
		    oper2=ws.pop();
		    oper1=ws.pop();
		    ws.push(oper1/oper2);
		}
		else if(tk.is("^")) {
		    oper2=ws.pop();
		    oper1=ws.pop();
		    ws.push(Math.pow(oper1,oper2));
		}
		else if(tk.is("sin"))
		    ws.push(Math.sin(ws.pop()*angleFactor));
		else if(tk.is("cos"))
		    ws.push(Math.cos(ws.pop()*angleFactor));
		else if(tk.is("tan"))
		    ws.push(Math.tan(ws.pop()*angleFactor));
		else if(tk.is("csc"))
		    ws.push(1.0/Math.sin(ws.pop()*angleFactor));
		else if(tk.is("sec"))
		    ws.push(1.0/Math.cos(ws.pop()*angleFactor));
		else if(tk.is("cot")) {
		    /* Don't do 1/tan(x) since cot will have a
		     * discontinuity at x=pi/2 (among others).
		     */
		     
		    oper1=ws.pop();
		    ws.push(Math.cos(oper1*angleFactor)/Math.sin(oper1*angleFactor));
		}
		else if(tk.is("asinh")) {
		    oper1=ws.pop();
		    ws.push(Math.log(Math.sqrt(oper1*oper1+1)+oper1));
		}
		else if(tk.is("acosh")) {
		    oper1=ws.pop();
		    ws.push(Math.log(Math.sqrt(oper1*oper1-1)+oper1));
		}
		else if(tk.is("atanh")) {
		    oper1=ws.pop();
		    ws.push(Math.log((1+oper1)/(1-oper1))/2);
		}
		else if(tk.is("acsch")) {
		    oper1=1/ws.pop();
		    ws.push(Math.log(Math.sqrt(oper1*oper1+1)+oper1));
		}
		else if(tk.is("asech")) {
		    oper1=1/ws.pop();
		    ws.push(Math.log(Math.sqrt(oper1*oper1-1)+oper1));
		}
		else if(tk.is("acoth")) {
		    oper1=1/ws.pop();
		    ws.push(Math.log((1+oper1)/(1-oper1))/2);
		}
		else if(tk.is("sinh")) {
		    oper1=ws.pop();
		    ws.push((Math.exp(oper1)-Math.exp(-oper1))/2);
		}
		else if(tk.is("cosh")) {
		    oper1=ws.pop();
		    ws.push((Math.exp(oper1)+Math.exp(-oper1))/2);
		}
		else if(tk.is("tanh")) {
		    oper1=ws.pop();
		    ws.push((Math.exp(oper1)-Math.exp(-oper1))/(Math.exp(oper1)+Math.exp(-oper1)));
		}
		else if(tk.is("csch")) {
		    oper1=ws.pop();
		    ws.push(2.0/(Math.exp(oper1)-Math.exp(-oper1)));
		}
		else if(tk.is("sech")) {
		    oper1=ws.pop();
		    ws.push(2.0/(Math.exp(oper1)+Math.exp(-oper1)));
		}
		else if(tk.is("coth")) {
		    oper1=ws.pop();
		    ws.push((Math.exp(oper1)+Math.exp(-oper1))/(Math.exp(oper1)-Math.exp(-oper1)));
		}
		else if(tk.is("asin"))
		    ws.push(Math.asin(ws.pop())/angleFactor);
		else if(tk.is("acos"))
		    ws.push(Math.acos(ws.pop())/angleFactor);
		else if(tk.is("atan"))
		    ws.push(Math.atan(ws.pop())/angleFactor);
		else if(tk.is("acsc"))
		    ws.push(Math.asin(1/ws.pop())/angleFactor);
		else if(tk.is("asec"))
		    ws.push(Math.acos(1/ws.pop())/angleFactor);
		else if(tk.is("acot"))
		    ws.push(Math.PI/2-Math.atan(ws.pop())/angleFactor);
		else if(tk.is("neg"))
		    ws.push(-ws.pop());
		else if(tk.is("abs"))
		    ws.push(Math.abs(ws.pop()));
		else if(tk.is("sqrt"))
		    ws.push(Math.sqrt(ws.pop()));
		else if(tk.is("log"))
		    ws.push(Math.log(ws.pop())/Math.log(10));
		else if(tk.is("ln"))
		    ws.push(Math.log(ws.pop()));
		else if(tk.is("exp"))
		    ws.push(Math.exp(ws.pop()));
		else if(tk.is("ceil"))
		    ws.push(Math.ceil(ws.pop()));
		else if(tk.is("floor"))
		    ws.push(Math.floor(ws.pop()));
		else if(tk.is("erf"))
		    ws.push(ExtendedMath.erf(ws.pop()));
		else if(tk.is("erfc"))
		    ws.push(ExtendedMath.erfc(ws.pop()));
		else if(tk.is("gamma"))
		    ws.push(ExtendedMath.Gamma(ws.pop()));
		else if(tk.is("lngamma"))
		    ws.push(ExtendedMath.LnGamma(ws.pop()));
		else if(tk.is("zeta"))
		    ws.push(ExtendedMath.zeta(ws.pop()));
		else if(tk.is("sign")) { {
		    oper1=ws.pop();
		    if (oper1>0)
			ws.push(1);
		    else if (oper1<0)
			ws.push(-1);
		    else
			ws.push(0);
		}
		}
	    }
	}
	catch (NullPointerException exception) {
	    //Primative malformed input detection.
	    ws = new StackDoub();
	    ws.push(Double.MIN_VALUE);
	}

	if (! ws.isEmpty())
	    a=ws.pop();
	else
	    a=Double.NaN;
	return a;
    }

    /**
     * Returns a copy of the internal postfix stack.
     *
     * @return copy of the postfix stack.
     */
    Stack getPostfixStack()
    {
	return pfStack.copy();
    }


    /**
     * Determines if <code>this</code> is a constant.
     *
     * @return <code>true</code> if postfix is constant, <code>false</code> otherwise.
     */
    public boolean isConstant()
    {
	return isConstant;
    }

    /**
     * alias for infix();
     *
     * @return a semi-fully-parenthesized infix expression.
     */
    public String toString()
    {
	return infix();
    }


    public String parenthesizedInfix=null;
    /**
     * Creates a semi-fully-parenthesized infix expression equivalent
     * to the postfix stack.
     *
     * @return a semi-fully-parenthesized infix expression.
     */
    public String infix()
    {
	if (pfStack!=null && parenthesizedInfix==null)
	    parenthesizedInfix=infix(pfStack.copy());

	return parenthesizedInfix;
    }


    /**
     * Creates a semi-fully-parenthesized infix expression equivalent
     * to the postfix stack.
     *
     * @param
     * @return a semi-fully-parenthesized infix expression.
     */
    private static String infix(Stack s)
    {
	String str = "";

	Token tk = s.pop();

	if (tk.isBinary()) {
	    Stack b = sub(s);
	    Stack a = sub(s);

	    str = "("+infix(a)+tk.getTokenString()+infix(b)+")";
	}
	else if (tk.isNumber()) {
	    str = tk.getTokenString();
	}
	else {
	    Stack a = sub(s);
	    str = "("+tk.getTokenString()+" "+infix(a)+")";
	}


	return str;

    }

    /**
     * Returns the top substack of a postfix stack.
     *
     * @param s a postfix stack.
     * @return the top substack
     */
    static Stack sub(Stack s)
    {
	Stack ret = new Stack();

	if (!s.isEmpty()) {
	    Token tk=s.pop();

	    if (tk.isNumber()) {
		ret.push(tk);
	    }
	    else if (tk.is("+") || tk.is("-") || tk.is("*") || tk.is("/") || tk.is("^") ) {
		//Binary Operations

		Stack oper2 = sub(s);
		Stack oper1 = sub(s);
		ret.push(oper1);
		ret.push(oper2);
		ret.push(tk);
	    }
	    else if(tk.is("sin") || tk.is("cos") || tk.is("tan") || tk.is("sec") || tk.is("csc") || tk.is("cot") ||
		    tk.is("asin") || tk.is("acos") || tk.is("atan") || tk.is("asec") || tk.is("acsc") || tk.is("acot") ||
		    tk.is("sinh") || tk.is("cosh") || tk.is("tanh") || tk.is("sech") || tk.is("csch") || tk.is("coth") ||
		    tk.is("asinh") || tk.is("acosh") || tk.is("atanh") || tk.is("asech") || tk.is("acsch") || tk.is("acoth") ||
		    tk.is("neg") || tk.is("abs") || tk.is("sqrt") || tk.is("log") || tk.is("ln") || tk.is("exp")	||
		    tk.is("erf") || tk.is("erfc") ||
		    tk.is("gamma") || tk.is("lngamma") ||
		    tk.is("ddx")
		    ) {
		//Unary Operations

		Stack oper1 = sub(s);
		ret.push(oper1);
		ret.push(tk);
	    }
	}

	return ret;
    }

    /**
     * Returns the string from which the postfix stack was generated.
     *
     * @return the original infix string.
     */
    public String infixString()
    {
	return infixStr=infixStr.trim();
    }

    /**
     * Determine if the infix-to-postfix conversion succeeded. 
     *
     * @return <code>true</code> if okay, else <code>false</code>
     */
    public boolean isValid()
    {
	return infixStr!=null && ! infixString().equals("");
    }

    /**
     * Returns the rendering of a postfix stack. 
     *
     * @return the postfix stack's tokens in a string.
     */
    public String toPostfixString()
    {
	return getPostfixStack().toString();
    }
}

final class PostfixDerivative
{
    private PostfixDerivative(){}

    private static Stack add(Stack a,Stack b)  // r = a+b
    {
	Stack r = new Stack();

	if (a.isZero() && b.isZero()) {
	    r.push("0");
	}
	else if (a.isZero()) {
	    r.push(b);
	}
	else if (b.isZero()) {
	    r.push(a);
	}
	else {
	    r.push(a);
	    r.push(b);
	    r.push("+");
	}

	return r;
    }

   private static Stack subtract(Stack a,Stack b)  // r = a-b
    {
	Stack r = new Stack();

	if (a.isZero() && b.isZero()) {
	    r.push("0");
	}
	else if (a.isZero()) {
	    r.push(b);
	    r.push("neg");
	}
	else if (b.isZero()) {
	    r.push(a);
	}
	else {
	    r.push(a);
	    r.push(b);
	    r.push("-");
	}

	return r;
    }

    private static Stack multiply(Stack a,Stack b)  // r = a*b
    {
	Stack r = new Stack();

	if (a.isZero() || b.isZero()) {
	    r.push("0");
	}
	else if (a.isOne()) {
	    r.push(b);
	}
	else if (b.isOne()) {
	    r.push(a);
	}
	else {
	    r.push(a);
	    r.push(b);
	    r.push("*");
	}

	return r;
    }

    private static Stack divide(Stack a,Stack b)  // r = a/b
    {
	Stack r = new Stack();

	if (b.isOne()) {
	    r.push(a);
	}
	else {
	    r.push(a);
	    r.push(b);
	    r.push("/");
	}

	return r;
    }

    private static Stack pow(Stack a,Stack b)  // r = a^b
    {
	Stack r = new Stack();

	if (a.isOne()) {
	    r.push("1");
	}
	else if (b.isZero() && ! a.isZero()) {
	    r.push("1");
	}
	else if (b.isOne()) {
	    r.push(a);
	}
	else {
	    r.push(a);
	    r.push(b);
	    r.push("^");
	}

	return r;
    }

    private static Stack exp(Stack b)  // r = exp(b)
    {
	Stack r = new Stack();

	r.push(b);
	r.push("exp");

	return r;
    }

    private static Stack ln(Stack a)  // r = ln a
    {
	Stack r = new Stack();

	if (a.isOne()) {
	    r.push("0");
	}
	else {
	    r.push(a);
	    r.push("ln");
	}

	return r;
    }

    private static Stack inverse(Stack a)  // r = 1/a
    {
	Stack r = new Stack();

	if (a.isOne()) {
	    r.push("1");
	}
	else {
	    r.push("1");
	    r.push(a);
	    r.push("/");
	}

	return r;
    }

    private static Stack negate(Stack a)  // r = -a
    {
	Stack r = new Stack();

	if (a.isZero()) {
	    r.push("0");
	}
	else {
	    r.push(a);
	    r.push("neg");
	}

	return r;
    }

    private static Stack square(Stack a)  // r = a^2
    {
	Stack r = new Stack();

	if (a.isZero()) {
	    r.push("0");
	}
	else if (a.isOne()) {
	    r.push("1");
	}
	else {
	    r.push(a);
	    r.push("2");
	    r.push("^");
	}

	return r;
    }

    private static Stack sqrt(Stack a)  // r = sqrt a
    {
	Stack r = new Stack();

	if (a.isOne()) {
	    r.push("1");
	}
	else {
	    r.push(a);
	    r.push("sqrt");
	}

	return r;
    }

    private static Stack abs(Stack a)  // r = sqrt a
    {
	Stack r = new Stack();

	r.push(a);
	r.push("abs");

	return r;
    }

    private static Stack one = null;
    private static Stack ln10 = null;
    private static Stack two = null;
    private static Stack pi = null;

    private static void init()
    {
	one = new Stack();
	one.push("1");

	ln10 = new Stack();
	ln10.push("10");
	ln10.push("ln");

	two = new Stack();
	two.push("2");

	pi = new Stack();
	pi.push("pi");
    }

    private static Stack derivativeStack(Stack rec)
    {
	if (one==null || ln10==null)
	    init();

	if (rec.isEmpty()) return null;

	Stack der=new Stack();
	Token tk = rec.pop();

	if (tk.isNumber()) {
	    if (tk.is("x")) {
		der.push("1");
	    }
	    else {
		der.push("0");
	    }
	}
	else if (tk.is("+")) {
	    Stack r = derivativeStack(rec);
	    Stack l = derivativeStack(rec);

	    der.push(add(l,r));
	}
	else if (tk.is("-")) {
	    Stack r = derivativeStack(rec);
	    Stack l = derivativeStack(rec);

	    der.push(subtract(l,r));
	}
	else if (tk.is("*")) {
	    Stack v = Postfix.sub(rec);
	    Stack dv = derivativeStack(v.copy());
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(add(multiply(v,du),multiply(u,dv)));

	}
	else if (tk.is("/")) {
	    Stack v = Postfix.sub(rec);
	    Stack dv = derivativeStack(v.copy());
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(divide(subtract(multiply(v,du),multiply(u,dv)),pow(v,two)));
	}
	else if (tk.is("^")) {
	    Stack h = Postfix.sub(rec);
	    Stack dh = derivativeStack(h.copy());
	    Stack g = Postfix.sub(rec);
	    Stack dg = derivativeStack(g.copy());

	    if (! h.isConstant()) {
		der.push(multiply(add(multiply(h,divide(dg,g)),multiply(dh,ln(g))),pow(g,h)));
	    }
	    else {
		der.push(multiply(multiply(h,pow(g,subtract(h,one))),dg));
	    }
	}
	else if (tk.is("neg")) {
	    Stack d = derivativeStack(Postfix.sub(rec).copy());

	    der.push(d);
	    der.push("neg");
	}
	else if (tk.is("abs")) {
	    Stack h = Postfix.sub(rec);
	    Stack dh = derivativeStack(h.copy());

	    der.push(multiply(divide(abs(h),h),dh));
	}
	else if (tk.is("exp")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    if (! u.isZero()) {
		der.push(exp(u));
		der = multiply(der,du);
	    }
	    else {
		der.push("1");
	    }
	}
	else if (tk.is("ln")) {
	    Stack h = Postfix.sub(rec);
	    Stack dh = derivativeStack(h.copy());

	    der.push(divide(dh,h));
	}
	else if (tk.is("log")) {
	    Stack h = Postfix.sub(rec);
	    Stack dh = derivativeStack(h.copy());

	    der.push(divide(dh,multiply(ln10,h)));
	}
	else if (tk.is("sin")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    Stack p = new Stack();
	    p.push(u);
	    p.push("cos");

	    der.push(multiply(du,p));
	}
	else if (tk.is("cos")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    Stack p = new Stack();
	    p.push(u);
	    p.push("sin");
	    p.push("neg");

	    der.push(multiply(du,p));
	}
	else if (tk.is("tan")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("sec");
	    der.push("2");
	    der.push("^");

	    der = multiply(du,der);
	}
	else if (tk.is("sec")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("sec");
	    der.push(u);
	    der.push("tan");
	    der.push("*");

	    der = multiply(du,der);
	}
	else if (tk.is("csc")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("csc");
	    der.push(u);
	    der.push("cot");
	    der.push("*");
	    der.push("neg");

	    der = multiply(du,der);
	}
	else if (tk.is("cot")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("csc");
	    der.push("2");
	    der.push("^");
	    der.push("neg");

	    der = multiply(du,der);
	}
	else if (tk.is("sqrt")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(".5");
	    der.push(sqrt(u));
	    der.push("/");

	    der = multiply(du,der);
	}
	else if (tk.is("asin")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(sqrt(subtract(one,square(u))))));
	}
	else if (tk.is("acos")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(negate(multiply(du,inverse(sqrt(subtract(one,square(u)))))));
	}
	else if (tk.is("atan")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(add(one,square(u)))));
	}
	else if (tk.is("acot")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(negate(multiply(du,inverse(add(one,square(u))))));
	}
	else if (tk.is("asec")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(multiply(square(u),sqrt(subtract(one,inverse(square(u))))))));
	}
	else if (tk.is("acsc")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(negate(multiply(du,inverse(multiply(square(u),sqrt(subtract(one,inverse(square(u)))))))));
	}
	else if (tk.is("sinh")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("cosh");

	    der = multiply(du,der);
	}
	else if (tk.is("cosh")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("sinh");

	    der = multiply(du,der);
	}
	else if (tk.is("tanh")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("tanh");

	    der = multiply(du,subtract(one,square(der)));
	}
	else if (tk.is("coth")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("coth");

	    der = multiply(du,subtract(one,square(der)));
	}
	else if (tk.is("csch")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("csch");
	    der.push("neg");
	    der.push(u);
	    der.push("coth");
	    der.push("*");

	    der = multiply(der,du);
	}
	else if (tk.is("sech")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(u);
	    der.push("sech");
	    der.push("neg");
	    der.push(u);
	    der.push("tanh");
	    der.push("*");

	    der = multiply(der,du);
	}
	else if (tk.is("asinh")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(sqrt(add(one,square(u))))));
	}
	else if (tk.is("acosh")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(multiply(sqrt(subtract(u,one)),sqrt(add(u,one))))));
	}
	else if (tk.is("atanh")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(subtract(one,square(u)))));
	}
	else if (tk.is("acoth")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(multiply(du,inverse(subtract(one,square(u)))));
	}
	else if (tk.is("asech")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(negate(multiply(du,inverse(multiply(square(u),sqrt(subtract(inverse(square(u)),one)))))));
	}
	else if (tk.is("acsch")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());

	    der.push(negate(multiply(du,inverse(multiply(square(u),sqrt(add(inverse(square(u)),one)))))));
	}
	else if (tk.is("erf")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());
	    
	    Stack K = divide(two, sqrt(pi));
	    
	    der.push(multiply(du, multiply(K,exp(negate(square(u))))));
	}
	else if (tk.is("erfc")) {
	    Stack u = Postfix.sub(rec);
	    Stack du = derivativeStack(u.copy());
	    
	    Stack K = negate(divide(two, sqrt(pi)));
	    
	    der.push(multiply(du, multiply(K,exp(negate(square(u))))));
	       

	}
	else if (tk.is("ddx")) {
	    Stack d2u = derivativeStack(derivativeStack(Postfix.sub(rec).copy()));

	    der.push(d2u);
	}
	else {
	    der.push("?");
	}

	return der;

    }



    static Postfix derivative(Postfix pf)
    {
	Stack der = derivativeStack(pf.getPostfixStack());
	return new Postfix(der);
    }
}


class ExtendedMath {

    public static double erfcx(double x)
    {
	/* This method returns really bad values for 0<<x */

	return Math.exp(x*x+Math.log(erfc(x)));
    }

    public static double erfc(double x)
    {
	return 1-erf(x);
    }

    public static double zeta(double x)
    {
	if (x==1)
	    return Double.NaN;

	if (x<0)
	    return zeta(1-x)/2*Math.pow(2*Math.PI, x)/Math.cos(.5*x*Math.PI)/Gamma(x);

	double s = 0;

	double tmp=1;

	for (int i=1; tmp>1e-13 && i<=10000; i++) {
	    tmp = Math.pow(i,-x);
	    s+=tmp;	 
	}

	return s;
    }



    public static double Gamma(double x)
    {
	if (x<0) 
	    return -Math.PI/-x/Math.sin(-Math.PI*x)/Gamma(-x);

	return Math.exp(LnGamma(x));
    }

    private final static double[] p = {76.18009172947146, -86.50532032941677, 
					24.01409824083091, -1.231739572450155, 
					1.208650973866179e-3, -5.395239384953e-6 };
    public static double LnGamma(double x)
    {
	//x has to be nonnegative!

	double S = 1.000000000190015;
	double xx = x;
	for (int i=0; i<=5; i++) {
	    S+=(p[i]/(++xx));
	}
	
	double tmp=x+5.5;

	return Math.log(Math.sqrt(2*Math.PI)/x*S)+(Math.log(tmp)*(x+.5)-tmp);
    }

    private static double sinh(double x)
    {
	return (Math.exp(x)+Math.exp(-x))/2;
    }


    /**
     * Returns the value of the error function (erf).
     *
     * @return the value of the erf function     
     */
    public static double erf(double x)
    {
	if (x<0)
	    return -erf(-x);

	if (x==0)
	    return 0;

	double ret=0;
	

	double S=0;
	double factorial=1;
	double power=1;
	double term=0;
	double n=1;
	double k=Math.exp(-x*x)/Math.sqrt(Math.PI);
	
	if (x>5) {
	    do {
		term = factorial/power*Math.pow(x,-n);

		S+=term;		
		
		factorial*=n;
		power*=-2;
		n+=2;
	    } while (Math.abs(k*term)>1e-15);
	    
	    ret = 1-k*S;
	}
	else if (x<5) {
	    k*=(2*x);
	    n=0;
	    do {
		term = power/factorial*Math.pow(x,n);

		S+=term;		
		
		n+=2;
		factorial*=(n+1);
		power*=2;

	    } while (Math.abs(k*term)>1e-15);
	    
	    ret = k*S;
	    
	}
	else {
	    ret = 2/Math.sqrt(Math.PI)*erfIntegrate(0, x, 1e-15);
	}

	return ret;

    }


    private static double erfIntegrate(double a, double b, double tolerance)
    {
	if (a==b)
	    return 0;
	if (a>b)
	    return -erfIntegrate(b, a, tolerance);

	double h = (b-a)/2;

	double c = a+h;
	double d = a+h/2;
	double e = a+3*h/2;

	
	double fa = Math.exp(-a*a);
	double fd = Math.exp(-d*d);
	double fc = Math.exp(-c*c);
	double fe = Math.exp(-e*e);
	double fb = Math.exp(-b*b);

	double S = h*(fa+4*fc+fb)/3;
	double S1 = h*(fa+4*fd+fc)/6;
	double S2 = h*(fc+4*fe+fb)/6;

	if (Math.abs(S-S1-S2)<tolerance)
	    return S1+S2;
	
	return erfIntegrate(a,c, tolerance)+erfIntegrate(c,b, tolerance);
    }


}
