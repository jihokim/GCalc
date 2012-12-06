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


public class BadSyntaxRuntimeException extends RuntimeException {
    private int pos;

    public BadSyntaxRuntimeException(String s)
    {
	this(s,-1);
    }

    public BadSyntaxRuntimeException(String s, int p) {
	super(s);
	pos = p;
    }

    public BadSyntaxRuntimeException(int p) {
	this("Bad Syntax", p);
    }

    public BadSyntaxRuntimeException() {
	this("Bad Syntax", -1);
    }

    public int getPos() {
	return pos;
    }
}

