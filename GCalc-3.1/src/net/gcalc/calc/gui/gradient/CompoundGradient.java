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


package net.gcalc.calc.gui.gradient;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;



/**
 * @author jkim
 */
public class CompoundGradient extends Gradient implements Observer
{	
	private Collection markerCollection;
	private boolean sorted;
	private Marker[] markers;
	
	/**
	 * 
	 */
	public CompoundGradient() {
		this(Color.black, Color.black);
	}
	
	/**
	 * @param l
	 * @param r
	 */
	public CompoundGradient(Color l, Color r) {
		this(l, r, Gradient.RGB_MODE);
	}
	
	/**
	 * @param l
	 * @param r
	 * @param mode
	 */
	public CompoundGradient(Color l, Color r, int mode) {
		super(mode);
		markerCollection = new TreeSet();
		markerCollection.add(new Marker(0, r, l, l));
		markerCollection.add(new Marker(1, r, r, l));
		sorted = false;
	}
	
	public void addMarker(Marker m)
	{
		markerCollection.add(m);
		m.addObserver(this);
		
		sorted = false;
	}
	
	public void removeMarker(Marker m)
	{
		markerCollection.remove(m);
		m.deleteObserver(this);
		
		sorted = false;
	}
	
	public Marker[] getMarkers()
	{
		Marker[] m = new Marker[markers.length];
		System.arraycopy(markers,0,m,0,markers.length);
		return markers;
	}
	
	public void compact()
	{
		Marker[] m = getMarkers();
		
		for (int i=0; i<m.length; i++) {
			double p = m[i].getPosition();
			if (p<0 || p>1)
				removeMarker(m[i]);
		}
		
		sorted= false;
	}
	
	public Color getColor(double d)
	{
		if (! sorted) {
			markers = new Marker[markerCollection.size()];
			markerCollection.toArray(markers);
			Arrays.sort(markers);
			sorted = true;
		}
		
		if (d==1) {
			return markers[markers.length-1].getCenter();
		}
		if (d==0) {
			return markers[0].getCenter();
		}

		int idx = Arrays.binarySearch(markers, new Marker(d));
		
		if (idx>=0) {
			return markers[idx].getCenter();
		}
		
		//insertion point
		idx = -(idx+1);
		
		//because we sorted the array, p >= d >= q.  
		//But we know p!=d and q!=d, so p>q, strictly.
		double p = markers[idx].getPosition();
		double q = markers[idx-1].getPosition();
		int mode =  Gradient.HSB_MODE;
		SimpleGradient gradient = new SimpleGradient(markers[idx-1].getRight(), markers[idx].getLeft(),mode);
				
		return gradient.getColor((d-q)/(p-q));
	}
	
	public void update(Observable m, Object o)
	{
		sorted = false;
		//System.out.println(markerCollection);
	}
	
	public String toString()
	{
		return markerCollection.toString();
	}
}

