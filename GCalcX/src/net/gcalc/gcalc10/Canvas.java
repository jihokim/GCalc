/** 
GCalcX
Copyright (C) 2010 Jiho Kim 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Email: jiho@gcalcul.us
Web: http://gcalcul.us
*/
/**
 *  GCalc 10 - Copyright Jiho Kim 2010
 *
 *  Do not redistribute.
 */

package net.gcalc.gcalc10;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.META_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import net.gcalc.gcalc10.drawable.ColoredDrawable;
import net.gcalc.gcalc10.drawable.Drawable;
import net.gcalc.gcalc10.drawable.DrawableList;
import net.gcalc.gcalc10.drawable.DrawableSketcher;
import net.gcalc.gcalc10.drawable.Grid;
import net.gcalc.gcalc10.drawable.Labels;
import net.gcalc.gcalc10.util.Util;

public class Canvas extends JPanel implements AttributeChangeListener
{
	private final static double ZOOM_FACTOR = 2.0/3.0;

	private List<RecomputationListener> recomputationListeners = new CopyOnWriteArrayList<RecomputationListener>();

	ExecutorService es = Executors.newFixedThreadPool(3);

	private BufferedImage image = new BufferedImage(10,10, BufferedImage.TYPE_4BYTE_ABGR);
	private Graphics2D buffer = image.createGraphics();

	private AffineTransform transform = new AffineTransform();
	private AffineTransform composite = new AffineTransform();
	private double tx = 0;
	private double ty = 0;
	private double halfWidth = 0;
	private double halfHeight = 0;
	private int width = 10;
	private int height;

	private DrawableList<Drawable> masterList = new DrawableList<Drawable>();
	private DrawableList<Drawable> background = new DrawableList<Drawable>();
	private DrawableList<ColoredDrawable> list = null;

	private DrawableSketcher sketcher = null;

	private final GraphAttributes ga;

	private Grid grid = new Grid(2,2);
	private Labels labels = new Labels(2,2);

	public Canvas(DrawableList<ColoredDrawable> dlist, GraphAttributes graphAttribute)
	{
		this.addKeyListener(new CanvasKeyAdapter());

		CanvasMouseInputAdapter cmia = new CanvasMouseInputAdapter();
		addMouseListener(cmia);
		addMouseMotionListener(cmia);

		addMouseWheelListener(new CanvasMouseWheelAdapter());
		addComponentListener(new CanvasComponentListener());

		transform.scale(40,-40);

		list = dlist;
		initDrawableList();

		ga = graphAttribute;

		setBackground(Color.white);
		ga.GRID_XGAP.addAttributeChangeListener(new AttributeChangeListener() {
			public <E> void attributeChange(AttributeChangeEvent<E> e) {
				double val = ga.GRID_XGAP.getValue().value;
				grid.setXGap(val);
				labels.setXscale(val);
				recompute();
			}			
		});
		ga.GRID_YGAP.addAttributeChangeListener(new AttributeChangeListener() {
			public <E> void attributeChange(AttributeChangeEvent<E> e) {
				double val = ga.GRID_YGAP.getValue().value;
				grid.setYGap(val);
				labels.setYscale(val);
				recompute();
			}			
		});

		AttributeChangeListener recompute = new AttributeChangeListener() {
			public <E> void attributeChange(AttributeChangeEvent<E> e) {
				recompute();
			}			
		};

		ga.addAttributeChangeListener(recompute, ga.AXES, ga.GRID, ga.LABELS, ga.TICKS, ga.STROKE_WIDTH, ga.TMIN, ga.TMAX, ga.TSEGS);
	}

	public boolean isFocusable() {
		return true;
	}

	private void initDrawableList() {
		masterList.add(background);
		masterList.add(list);

		background.add(grid);
		background.add(labels);
	}

	public void addRecomputationListener(RecomputationListener listener)
	{
		recomputationListeners.add(listener);
	}

	public void removeRecomputationListener(RecomputationListener listener)
	{
		recomputationListeners.remove(listener);
	}

	protected void fireRecomputationEvent(final CanvasContext context)
	{
		for (RecomputationListener l : recomputationListeners) {
			final RecomputationListener listener = l;

			listener.computationPerformed(context);
		}
	}

	public CanvasContext getContext()
	{
		composite.setToTranslation(tx+halfWidth, ty+halfHeight);
		composite.concatenate(transform);

		buffer.clearRect(0,0,getWidth(),getHeight());
		Rectangle pixelBound = new Rectangle(0,0, width, height);

		AffineTransform inverse = Util.invert(composite);
		double sx = inverse.getScaleX();
		double sy = Math.abs(inverse.getScaleY());
		double tx = inverse.getTranslateX();
		double ty = inverse.getTranslateY();

		Rectangle2D cartesianBound = new Rectangle2D.Double(tx,ty-height*sy,width*sx,height*sy);
		return new CanvasContext(buffer, composite, inverse, pixelBound, cartesianBound, ga);
	}



	public void recompute() {

		CanvasContext context = getContext();

		sketcher = new DrawableSketcher(context, es);
		Runnable runnable = masterList.visit(sketcher);

		runnable.run();
		repaint();
		fireRecomputationEvent(context);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(image, 0,0, null);
	}

	public void zoom(int i, int j) {
		zoom(i,j,null);
	}

	public void zoom(int i, int j, Point2D screenPoint) {
		if (screenPoint==null) {
			screenPoint = new Point2D.Double(halfWidth,halfHeight);
		}

		Point2D point = Util.invert(composite).transform(screenPoint, null);
		zoom(point,i,j);
	}

	public void zoom(Point2D cartesianPoint, int i, int j)
	{
		double xscale = Math.pow(ZOOM_FACTOR, i);
		double yscale = Math.pow(ZOOM_FACTOR, j);

		transform.translate(cartesianPoint.getX(), cartesianPoint.getY());
		transform.scale(xscale,yscale);
		transform.translate(-cartesianPoint.getX(), -cartesianPoint.getY());

		recompute();
	}

	class CanvasMouseInputAdapter extends MouseInputAdapter {
		private Point down = null;

		public void mousePressed(MouseEvent e) {
			down = e.getPoint();	
			requestFocus();
		}

		public void mouseDragged(MouseEvent e) {			
			Point drag = e.getPoint();

			tx += drag.x-down.x;
			ty += drag.y-down.y;
			down = drag;

			recompute();
		}

		public void mouseClicked(MouseEvent e) {
			System.out.println(e.getPoint()+" "+Util.invert(composite).transform(e.getPoint(),null));
		}
	}

	private class CanvasMouseWheelAdapter implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int click = e.getWheelRotation();
			int modifiers = e.getModifiersEx();

			int MASK = SHIFT_DOWN_MASK | CTRL_DOWN_MASK;

			Point2D point = null;

			// if ALT is pressed, the zoom should happen with respect
			// to the cursor position.  Else the zoom happens with respect
			// to the center of the screen.
			if ((modifiers & ALT_DOWN_MASK) == ALT_DOWN_MASK) {
				point = e.getPoint();
			}

			if ((modifiers & MASK) == SHIFT_DOWN_MASK) {
				zoom(click, 0,point);			
			}
			else if ((modifiers & MASK) == CTRL_DOWN_MASK){
				zoom(0, click, point);							
			}
			else {
				zoom(click, click, point);
			}
		}
	}

	private class CanvasComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			width = getWidth();
			height = getHeight();
			halfWidth = width/2.0;
			halfHeight = height/2.0;
			if (image.getWidth() < width || image.getHeight() < height) {
				image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
				buffer = image.createGraphics();
				buffer.setBackground(Color.white);
				buffer.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);			
			}
			recompute();
		}
	}

	private class CanvasKeyAdapter extends KeyAdapter {
		long time = 0;
		int key = 0;
		int count = 0;
		public void keyPressed(KeyEvent e) {
			int modifiers = e.getModifiersEx();
			int onmask = SHIFT_DOWN_MASK;
			int mask = SHIFT_DOWN_MASK | CTRL_DOWN_MASK | META_DOWN_MASK | ALT_DOWN_MASK;

			if ((modifiers & mask) == onmask) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					zoom(-1,-1);
					break;
				case KeyEvent.VK_DOWN:
					zoom(+1,+1);
					break;
				}
			}
			else if ((modifiers & mask) == 0){
				long current = System.currentTimeMillis();
				int increment = 10;

				if (key==e.getKeyCode()) {
					count++;
					if (current-time < 100 && count>16) {
						increment = 20;
					}					
				}
				else {
					count = 0;
				}
				time = current;
				key = e.getKeyCode();

				switch (key) {
				case KeyEvent.VK_UP:
					ty-=increment;
					break;
				case KeyEvent.VK_DOWN:
					ty+=increment;
					break;
				case KeyEvent.VK_LEFT:
					tx-=increment;
					break;
				case KeyEvent.VK_RIGHT:
					tx+=increment;
					break;
				}
				recompute();
			}
		}
	}

	public <E> void attributeChange(AttributeChangeEvent<E> e) {
		recompute();		
	}

	public void setTranslate(double x, double y) {
		tx = x;
		ty = y;	
	}

	public void setScale(double x, double y) {
		transform = new AffineTransform();
		transform.scale(x, -y);
	}
}

