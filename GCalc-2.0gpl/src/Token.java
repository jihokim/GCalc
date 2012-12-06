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


/**
 *  Encapsulation of a parsing unit.  
 */
public class Token
{
    /** 
     * Array of valid strings.  The order of these tokens matter and
     * are often hard coded into the code.  Take care not to disturb
     * it without knowing what's going on.  Essentially strings of
     * index n cannot be a prefix of a strings of index n+k, where k
     * is positive.
     */
    private static String[] validString =
    {
	"0","1","2","3","4","5","6","7","8","9",".",
	"(", ")", "^", "*", "/", "+", "-",
	"sinh", "cosh", "tanh", "csch", "sech", "coth",
	"asinh", "acosh", "atanh", "acsch", "asech", "acoth",
	"sin", "cos", "tan", "csc", "sec", "cot",
	"asin", "acos", "atan",	"acsc", "asec", "acot",
	"sqrt", "neg",
	"gamma", "lngamma", "zeta",
	"log", "abs", "ln", "exp", "sign",
	"floor", "ceil",
        "erfc", "erf", 	
	"ddx",
	"rnd","x","PI", "E"
    };

    private int index=-1;
    private String tokenString="";
    private boolean numerical;
    private int length;

    /** 
     * Token Constructor.  Creates a token of the first recognizeable
     * substring of the input.  
     *
     * @param str Infix string from which to strip to the first token.
     */
    public Token(String str)
    {
	String t = str.trim().toLowerCase();
	numerical = false;

	for (int i = 0; i<validString.length && tokenString.equals(""); i++) {
	    if (t.startsWith(validString[i].toLowerCase())) {
		tokenString = validString[i];
		index = i;

		if (i<11) {
		    tokenString = getValue(t);
		    if (tokenString.equals("?")) index =-1;
		    numerical = (index!=-1);
		}
		else {
		    numerical= (tokenString.equals("x") ||
				tokenString.equals("PI") ||
				tokenString.equals("E") ||
				tokenString.equals("rnd")
				);
		}
		
		length = tokenString.length();
	    }
	}

	//Makes sure all numerical values have index 0
	if (index>=0 && index<11)
	    index=0;
    }

    /** 
     * Checks the identity of the token.
     *
     * @param tokenString 
     */
    public boolean is(String tokenString)
    {
	return getTokenString().equalsIgnoreCase(tokenString);
    }

    /** 
     * Determines if the token is valid.
     */
    public boolean isValid()
    {
	return index!=-1;
    }

    /** 
     * Determines if the token is a numerical value.
     */
    public boolean isNumber()
    {
	return (numerical);
    }

    /** 
     * Determines if the token is an operation.
     */
    public boolean isOperation()
    {
	return (precedence()>0);
    }

    /** 
     * Determines if the token is a binary operation.
     */
    public boolean isBinary()
    {
	//bad code.
	return (precedence()==4 || precedence()==3 || precedence()==2);
    }

    /** 
     * Returns the string representation of this token.
     */
    public String getTokenString()
    {
	return toString();
    }

    /** 
     * Returns the string representation of this token.
     */
    public String toString()
    {
	return tokenString;
    }

    /** 
     * Returns the relative precedence code of the token, which is
     * used during parsing.  
     *
     * The code at this point is hard-wired to the values in this
     * method.  That needs to be changed.
     */
    public float precedence()
    {
	float order=-1;
	Token tk = this;

	String tk_str = tk.getTokenString().toLowerCase();

	if (tk_str.equals("(")) {
	    order = 5;
	}
	else if (tk_str.equals(")")) {
	    order = 6;
	}
	else if (tk_str.equals("+")) {
	    order = 4;
	}
	else if (tk_str.equals("-")) {
	    order = 4;
	}
	else if (tk_str.equals("neg")) {
	    /* This is only valid if this 'neg' doesn't follow a '^'.
	     * The difference can be seen in x^-2 and -x^2.
	     */
	    order = 2.5f   ;
	}
	else if (tk_str.equals("*")) {
	    order = 3;
	}
	else if (tk_str.equals("/")) {
	    order = 3;
	}
	else if (tk_str.equals("^")) {
	    order = 2;
	}
	else if (tk_str.equals("sin") ||		//Trig functions
		 tk_str.equals("cos") ||
		 tk_str.equals("tan") ||
		 tk_str.equals("csc") ||
		 tk_str.equals("sec") ||
		 tk_str.equals("cot") ||

		 tk_str.equals("asin") ||	//Inverse trig functions
		 tk_str.equals("acos") ||
		 tk_str.equals("atan") ||
		 tk_str.equals("acsc") ||
		 tk_str.equals("asec") ||
		 tk_str.equals("acot") ||

		 tk_str.equals("sinh") ||	//Hyperbolic trig functions
		 tk_str.equals("cosh") ||
		 tk_str.equals("tanh") ||
		 tk_str.equals("csch") ||
		 tk_str.equals("sech") ||
		 tk_str.equals("coth") ||

		 tk_str.equals("asinh") ||	//Inverse hyperbolic trig functions
		 tk_str.equals("acosh") ||
		 tk_str.equals("atanh") ||
		 tk_str.equals("acsch") ||
		 tk_str.equals("asech") ||
		 tk_str.equals("acoth") ||

		 tk_str.equals("ddx") ||	

		 tk_str.equals("sqrt") ||	
		 tk_str.equals("exp") ||	
		 tk_str.equals("ln") ||		
		 tk_str.equals("log") ||	
		 tk_str.equals("abs") ||	
		 tk_str.equals("sign") ||		
		 tk_str.equals("erf") ||	
		 tk_str.equals("erfc") ||	
		 tk_str.equals("gamma") ||	
		 tk_str.equals("lngamma") ||	
		 tk_str.equals("zeta") ||	
		 tk_str.equals("floor") ||	
		 tk_str.equals("ceil")		
		 )
	    {
		order=1;
	    }
	else if (tk.isNumber()) {
	    order = 0;
	}
	else {
	    order =-1;
	}

	return order;
    }

    /** 
     * Returns the longest recognizable numeric prefix of the input
     * string.  Implemented as a "smart" state machine.
     *
     * @param s 
     */
    private static String getValue(String s)
    {
	String ret = "";
	char[] input = s.toLowerCase().toCharArray();
	int state = 1;
	int i = 0;

	StackDoub st = new StackDoub();

	while (state<30) {
	    st.push(state);
	    //			System.out.println(state+" "+input[i]);

	    switch (state) {
	    case 1:
		if (Character.isDigit(input[i]))
		    state = 2;
		else if (input[i]=='.')
		    state = 6;
		else
		    state = 99;
		break;

	    case 2:
		if (Character.isDigit(input[i]))
		    state = 2;
		else if (input[i]=='e')
		    state = 3;
		else if (input[i]=='.')
		    state = 7;
		else {
		    i = i - 1;
		    state = 99;
		}
		break;

	    case 3:
		if (Character.isDigit(input[i]))
		    state = 5;
		else if (input[i]=='e') {
		    i = i - 2;
		    st.pop();
		    state = 99;
		}
		else if (input[i]=='.')
		    state = 8;
		else if (input[i]=='+' || input[i]=='-')
		    state = 4;
		else
		    state = 99;
		break;

	    case 4:
		if (Character.isDigit(input[i]))
		    state = 5;
		else if (input[i]=='e') {
		    i = i - 3;
		    st.pop();
		    st.pop();
		    state = 99;
		}
		else if (input[i]=='.') {
		    i = i - 3;
		    st.pop();
		    st.pop();
		    state = 99;
		}
		else if (input[i]=='+' || input[i]=='-') {
		    i = i - 3;
		    st.pop();
		    st.pop();
		    state = 99;
		}
		else
		    state = 99;
		break;

	    case 5:
		if (Character.isDigit(input[i]))
		    state = 5;
		else if (input[i]=='.')
		    state = 8;
		else {
		    i = i - 1;
		    state = 99;
		}
		break;

	    case 6:
		if (Character.isDigit(input[i]))
		    state = 7;
		else if (input[i]=='e')
		    state = 8;
		else if (input[i]=='.')
		    state = 8;
		else if (input[i]=='+' || input[i]=='-')
		    state = 8;
		else
		    state = 99;
		break;

	    case 7:
		if (Character.isDigit(input[i]))
		    state = 7;
		else if (input[i]=='e')
		    state = 3;
		else if (input[i]=='.')
		    state = 8;
		else {
		    i = i - 1;
		    state = 99;
		}
		break;

	    case 8:
		break;
	    }

	    if (i==(input.length-1) || state==99) {
		if (state == 99)
		    state = (int) st.pop();

		if (state!=2 && state!=7 && state != 5)
		    state=8;
		else
		    state = 99;
	    }

	    if (state == 8) {
		s = "?";
		i=0;
		state = 99;
	    }

	    i++;
	}

	ret = s.substring(0,i);

	return ret;

    }

    /** 
     * Returns the length of the token string.
     *
     * @param s 
     */
    public int length()
    {
	return length;
    }
}
