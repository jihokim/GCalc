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


package net.gcalc.calc.gui.gradient;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.gcalc.calc.gui.BasicFrame;


public class GradientCanvas extends JPanel 
{	
	private CompoundGradient gradient;
	private int n = 13;		
	
	
	public GradientCanvas(CompoundGradient g)
	{
		super(true);
		
		gradient = g;
		setPreferredSize(new Dimension(150,20));
	
		CustomMouseAdapter cma = new CustomMouseAdapter();
		addMouseListener(cma);
		addMouseMotionListener(cma);
	}
	
	public GradientCanvas()
	{
		this(new CompoundGradient(Color.black, Color.white));
	}
	
	public CompoundGradient getGradient()
	{
		return gradient;
	}
	
	public void setMode(int mode)
	{
		gradient.setMode(mode);
	}

	public int getMode()
	{
		return gradient.getMode();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		double w = getWidth();
		
		for (int i=0; i<w; i++) {
			g.setColor(gradient.getColor(i/(w-1)));
			g.drawLine(i,0, i,getHeight()-n);
		}
		
		Marker[] markers = gradient.getMarkers();
		
		double p;
		Color l, c, r;
		int[] x = new int[3];
		int[] y = new int[3];

		for (int i=0; i<markers.length; i++) {
			p = markers[i].getPosition();
			l = markers[i].getLeft();
			r = markers[i].getRight();
			c = markers[i].getCenter();
			
			x[2]=x[0]=(int) (p*w);
			y[1]=y[2]=getHeight()-1;
			
			y[0]=getHeight()-n-4;
			x[1]=x[0]+5;
			g.setColor(r);
			g.fillPolygon(x,y,3);
			g.setColor(Color.black);
			g.drawPolygon(x,y,3);
			
			x[1]=x[0]-5;
			g.setColor(l);
			g.fillPolygon(x,y,3);
			g.setColor(Color.black);
			g.drawPolygon(x,y,3);
			
			y[0]=getHeight()-n/2;
			x[1]=x[0]+5;
			x[2]=x[0]-5;
			g.setColor(c);
			g.fillPolygon(x,y,3);
			g.setColor(Color.black);
			g.drawPolygon(x,y,3);
		}
	}
	
	class CustomMouseAdapter extends MouseAdapter implements Observer, MouseMotionListener
	{
		private Marker marker = null;
		private JColorChooser chooser = new JColorChooser();
		
		
		public void mousePressed(MouseEvent e) 
		{
			int pos = 0;
			Marker[] markers = gradient.getMarkers();
			for (int i=1; i<markers.length-1; i++) {
				pos = (int) (markers[i].getPosition()*getWidth());
				if (Math.abs(pos-e.getX())<10 && Math.abs(e.getY()-getHeight())<15) {
					marker = markers[i];
					return;
				}
			}
			
			
		}		
				
		public void mouseDragged(MouseEvent e) 
		{
			if (marker!=null) {
				marker.setPosition(e.getX()/(double) getWidth());
			}
		}
		
		public void mouseReleased(MouseEvent e) 
		{
			marker = null;
			gradient.compact();
			
			//System.out.println(gradient);
			repaint();
		}
		
		public void mouseMoved(MouseEvent e) 
		{
			Marker[] markers = gradient.getMarkers();
			for (int i=1; i<markers.length-1; i++) {
				int pos = (int) (markers[i].getPosition()*getWidth());
				if (Math.abs(pos-e.getX())<10 &&  Math.abs(e.getY()-getHeight())<15) {
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					return;
				}
				
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		}
		
		public void mouseExited(MouseEvent e)
		{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		}
		
		public void mouseEntered(MouseEvent e)
		{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		}
		
		public void mouseClicked(MouseEvent e) {
			
			if (e.getClickCount()==1 && e.getButton()==MouseEvent.BUTTON3 && e.getY()>getHeight()-n) {
				Marker[] markers = gradient.getMarkers();
				for (int i=0; i<markers.length; i++) {
					int pos = (int) (markers[i].getPosition()*getWidth());
					if (Math.abs(pos-e.getX())<10 &&  Math.abs(e.getY()-getHeight())<15) {
						
						ActionListener action = new ChangeMarkerAction(markers[i], chooser);
						JDialog dialog = JColorChooser.createDialog(GradientCanvas.this, "Chooser a color", true, chooser, action, new AbstractAction() {public void actionPerformed(ActionEvent e){}});
							
						dialog.setVisible(true);
						i = markers.length;
					}
				}
				
				
				
			}
			else if (e.getClickCount()==2) {
				//double-clicked
				double position =e.getX()/(double) getWidth();
				chooser.setColor(gradient.getColor(position));
				
				Marker m = new Marker(position);
				m.addObserver(this);
				
				ActionListener action = new NewMarkerAction(m, chooser);
				
				JDialog dialog = JColorChooser.createDialog(GradientCanvas.this, "Chooser a color", true, chooser, action, new AbstractAction() {public void actionPerformed(ActionEvent e){}});
			
				dialog.setVisible(true);
			}
		}
		
		public void update(Observable m, Object o)
		{
			repaint();
		}
	}
	
	class ChangeMarkerAction implements ActionListener
	{
		private Marker marker;
		private JColorChooser chooser;
		
		ChangeMarkerAction(Marker m, JColorChooser c)
		{	
			marker = m;
			chooser = c;			
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			marker.setColor(chooser.getColor());
			
			repaint();
		}
	}
	
	class NewMarkerAction implements ActionListener
	{
		private Marker marker;
		private JColorChooser chooser;
		
		NewMarkerAction(Marker m, JColorChooser c)
		{	
			marker = m;
			chooser = c;			
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			marker.setColor(chooser.getColor());
			
			gradient.addMarker(marker);
			
			repaint();
		}
	}
	
	

	public static void main(String[] args) {
		BasicFrame bf = new BasicFrame("Test") {
			public void shutdown() {System.exit(0); }
		};
		
		bf.getContentPane().add(new GradientCanvas());
		bf.setSize(500,100);
		bf.setVisible(true);
	}
}

