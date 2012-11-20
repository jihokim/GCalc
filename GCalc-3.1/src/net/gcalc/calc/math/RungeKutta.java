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

Email: jiho@gcalc.net
Web: http://gcalc.net

Snail Mail: 
  Jiho Kim
  1002 Monterey Lane
  Tacoma, WA 98466
*/

package net.gcalc.calc.math;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.parser.Token;
import net.gcalc.calc.parser.VariableToken;



public class RungeKutta
{
    private final static Token X = VariableToken.X_VAR;
    private final static Token Y = VariableToken.Y_VAR;

    private double k1, k2, k3, k4, p1, p2, p3, p4;

    public static double[] singleStep(Function F, Function G, double xn,
            double yn, double h, ValueTable vt, SymbolTable st, double[] ret)
    {
        return (new RungeKutta()).oneStep(F, G, xn, yn, h, vt, st, ret);
    }

    public RungeKutta()
    {}

    public synchronized double[] oneStep(Function F, Function G, double xn,
            double yn, double h, ValueTable vt, SymbolTable st, double[] ret)
    {
        double[] u = ret;
        if (u==null)
            u = new double[2];

        vt.setValue(X, xn);
        vt.setValue(Y, yn);

        k1 = F.evaluate(st, vt);
        p1 = G.evaluate(st, vt);

        vt.setValue(X, xn+.5*h*k1);
        vt.setValue(Y, yn+.5*h*p1);

        k2 = F.evaluate(st, vt);
        p2 = G.evaluate(st, vt);

        vt.setValue(X, xn+.5*h*k2);
        vt.setValue(Y, yn+.5*h*p2);

        k3 = F.evaluate(st, vt);
        p3 = G.evaluate(st, vt);

        vt.setValue(X, xn+h*k3);
        vt.setValue(Y, yn+h*p3);

        k4 = F.evaluate(st, vt);
        p4 = G.evaluate(st, vt);

        u[0] = xn+h/6*(k1+2*(k2+k3)+k4);
        u[1] = yn+h/6*(p1+2*(p2+p3)+p4);

        return u;
    }
}

