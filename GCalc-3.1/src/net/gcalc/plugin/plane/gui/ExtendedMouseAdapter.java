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


package net.gcalc.plugin.plane.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import net.gcalc.calc.main.SymbolTable;
import net.gcalc.calc.main.ValueTable;

/**
 * Just acts as a unified adapter for Mouse[Motion]Adapter.
 * 
 * @author jkim
 *
 */
public class ExtendedMouseAdapter extends MouseAdapter implements MouseMotionListener
{
    protected SymbolTable st = new SymbolTable();
    protected ValueTable vt = new ValueTable();
    
    public void mouseMoved(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    
    public void reset(){}
}

