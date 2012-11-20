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


package net.gcalc.calc.models;

import java.util.Vector;

import javax.swing.AbstractListModel;


public class ModelList extends AbstractListModel
{
    private Vector list;
    
    public ModelList()
    {
        list = new Vector();
    }
    
    public ModelList(Model m)
    {
        this();
        add(m);
    }
    
    public synchronized void removeAllModels()
    {
        list.removeAllElements();
    }
    
    public synchronized void add(Model model)
    {
        list.add(model);
        this.fireContentsChanged(this, 0,list.size()-1);
    }
    
    public synchronized void setModelAt(Model model, int n)
    {
        list.set(n, model);
        this.fireContentsChanged(this, 0,list.size()-1);
    }
    
    public synchronized Object getElementAt(int n)
    {
        return list.elementAt(n);
    }
    
    public Model getModelAt(int n)
    {
        return (Model) getElementAt(n);
    } 
    
    public synchronized void removeElementAt(int n)
    {
        //this completely removes the element... and then changes the 
        //numberingof the rest...  This is a departure from before...
        list.removeElementAt(n);
        this.fireContentsChanged(this, 0,list.size()-1);
    }
    
    public synchronized void flagAll(boolean b) {
		for (int i = 0; i < list.size(); i++) {
			Model model = getModelAt(i);

			if (model != null && model instanceof RenderableModel) {
			    ((RenderableModel) model).setDrawn(b);
			}
		}
	}
    
    public synchronized void deleteCache() {
        for (int i = 0; i < list.size(); i++) {
            Model model = getModelAt(i);
            if (model != null && model instanceof RenderableModel) {
                ((RenderableModel) model).setImage(null);
              //  ((RenderableModel) model).setThumbnailImage(null);
            }
        }
    }
    
    public synchronized int getSize()
    {
        return list.size();
    }
    
    public synchronized Object[] getModelArray()
    {
        return list.toArray();
    }
    
    public synchronized Object[] getNotDrawnModels()
    {
        Model m;
        Vector v = new Vector();
        for (int i=0; i<getSize(); i++) {
            if ((m=getModelAt(i)) instanceof RenderableModel)
            if (! ((RenderableModel) m).isDrawn())
                v.add(getModelAt(i));
        }
            
        return v.toArray();
    }
    
    public void poke()
    {
        this.fireContentsChanged(this, 0,list.size()-1);
    }
}
