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

Email: gcalc@humblestar.net
Web: http://www.humblestar.net/GCalc

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/

import java.util.Vector;
import java.awt.Label;
import java.awt.Color;


class Stack
{
    StackCell top;

    Stack()
    {
	top = null;
    }

    public void clear()
    {
	top = null;
    }

    public boolean isEmpty()
    {
	return top == null;
    }

    public void push(Token s)
    {
	StackCell newCell =  new StackCell(s, top);
	top = newCell;
    }

    public void push(String s)
    {
	push(new Token(s));
    }

    public void push(Stack s)
    {
	Stack t = new Stack();
	if (s==null) return;
	while (! s.isEmpty())
	    t.push(s.pop());

	while (! t.isEmpty()) {
	    push(t.peek());
	    s.push(t.pop());
	}
    }

    public Token pop()
    {
	if (top!=null) {
	    Token result = top.now;
	    top = top.next;
	    return result;
	}

	return new Token("?");
    }

    public Token peek()
    {
	return top.now;
    }

    public String showAll()
    {
	return this.toString();
    }

    public String toString()
    {
	Stack s1 = new Stack();
	String ret="";
	while (! this.isEmpty()) {
	    Token tk = this.pop();
	    ret = tk.getTokenString()+" "+ret;
	    s1.push(tk);
	}

	while (! s1.isEmpty()) {
	    this.push(s1.pop());
	}

	return ret;
    }

    public Stack copy ()
    {
	Stack s1 = new Stack();
	Stack s2 = new Stack();
	while (! this.isEmpty()) {
	    s1.push(this.pop());
	}
	while (! s1.isEmpty()) {
	    s2.push(new Token(s1.peek().getTokenString()));
	    this.push(s1.pop());
	}

	return s2;
    }

    public Stack flip ()
    {
	Stack s1 = new Stack();
	Stack s2 = new Stack();
	while (! this.isEmpty()) {
	    s1.push(this.peek());
	    s2.push(this.pop());
	}
	while (! s1.isEmpty()) {
	    this.push(s1.pop());
	}

	return s2;
    }

    public boolean isConstant()
    {
	Stack s1 = new Stack();
	boolean isconstant = true;

	while (! isEmpty()) {
	    Token tk = pop();
	    s1.push(tk);

	    if (tk.is("x")) isconstant = false;
	}

	while (! s1.isEmpty())
	    push(s1.pop());

	return isconstant;
    }

    public boolean isZero()
    {
	Postfix p = new Postfix(this);
	return (this.isConstant() && Math.abs(p.evaluate(10))<1e-20);
    }

    public boolean isOne()
    {
	Postfix p = new Postfix(this);
	return (this.isConstant() && Math.abs(p.evaluate(10)-1)<1e-20);
    }

    public boolean isNegativeOne()
    {
	Postfix p = new Postfix(this);
	return (this.isConstant() && Math.abs(p.evaluate(10)+1)<1e-20);
    }
 
}

class StackCell
{
    Token now;
    StackCell next;

    StackCell(Token a, StackCell b)
    {
	now = a;
	next = b;
    }
}

class StackDoub
{
    java.util.Stack stack;

    StackDoub()
    {
	stack = new java.util.Stack();
    }

    public boolean isEmpty()
    {
	return stack.empty();
    }

    public void push(double s)
    {
	stack.push(new Double(s));
    }

    public double pop()
    {
	return ((Double) stack.pop()).doubleValue();
    }

    public double peek()
    {
	return ((Double) stack.peek()).doubleValue();
    }
}

class PostfixList
{
    Vector List;
    int listSize;

    PostfixList()
    {
	List = new Vector();
	listSize=0;
	add(null,null);
	remove(0);
    }

    void add(Postfix pf, Color c)
    {
	boolean placed=false;

	if (listSize<List.size()) {
	    int i=0;
	    for (int k=listSize; ! placed; i++) {
		String s = get(i).infixString().trim();

		if (s.length()==0) {
		    placed=true;
		    List.setElementAt(new PostfixListNode(pf, c),i);
		}
		else {
		    k--;
		}
	    }
	}
	else {
	    List.addElement(new PostfixListNode(pf,c));

	}

	listSize++;
    }

    Postfix get(int ind)
    {
	Postfix temp = getNodeAt(ind).pf;

	if (temp==null)
	    temp = new Postfix("");

	return temp;
    }

    PostfixListNode getNodeAt(int ind)
    {
	return (PostfixListNode) List.elementAt(ind);
    }

    void remove(int n)
    {
	try {
	    List.setElementAt(new PostfixListNode(null, null),n);
	    listSize--;
	}
	catch (ArrayIndexOutOfBoundsException e) { }
    }

    void deflagAll()
    {
	PostfixListNode temp = null;
	for (int i=0; i<List.size(); i++) {
	    temp = getNodeAt(i);
	    temp.drawn = false;
	}
    }
}

class PostfixListNode
{
    public Postfix pf;
    public boolean drawn;
    public Color color;

    PostfixListNode(Postfix now, Color color2)
    {
	pf=now;
	drawn=false;
	color=color2;
    }
}

class StringList
{
    Vector v;
    int cursor;

    StringList()
    {
	clear();
    }

    void resetCursor()
    {
	cursor = v.size()-1;
    }

    void clear()
    {
	v = new Vector();
	v.addElement("");
	resetCursor();
    }

    void add(String str)
    {
	v.insertElementAt(str, 0);

	if (v.size()>100)
	    v.removeElementAt(100);
    }

    String getPrev()
    {
	cursor = (cursor+1)%v.size();

	return (String) v.elementAt(cursor);
    }

    String getNext()
    {
	cursor = (cursor-1+v.size())%v.size();

	return (String) v.elementAt(cursor);
    }

}

class LabelPair {

    Label A, B;
    LabelPair(Label a, Label b)
    {
	A = a;
	B = b;
    }

    void setText(String a, String b)
    {
	A.setText("x = "+a);
	B.setText("y = "+b);
    }
}
