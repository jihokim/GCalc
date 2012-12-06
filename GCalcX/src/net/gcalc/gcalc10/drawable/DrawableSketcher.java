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
package net.gcalc.gcalc10.drawable;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.gcalc.gcalc10.CanvasContext;
import net.gcalc.gcalc10.GraphAttributes;
import net.gcalc.gcalc10.script.Function;
import net.gcalc.gcalc10.util.Util;
import net.gcalc.gcalc10.util.adaptive.Adaptive;
import net.gcalc.gcalc10.util.adaptive.PointNode;

class PointNodeXY extends PointNode<PointNodeXY> {
	private Function function;
	private AffineTransform transform;
	public PointNodeXY(Function f, AffineTransform transform, PointNodeXY next, double t) {
		super(next, t);
		function = f;
		this.transform = transform;
	}

	public PointNodeXY(Function f, AffineTransform t, double t0, double t1) {
		this(f,t,new PointNodeXY(f,t,null, t1), t0);
	}

	@Override
	protected Point2D getPoint(double t) {
		return new Point2D.Double(t, function.eval(t));
	}

	@Override
	protected Point2D getScreenPoint(Point2D p) {
		return transform.transform(p, screenPt);
	}
}

class PointNodeTXY extends PointNode<PointNodeTXY> {
	private TXY txy;
	private AffineTransform transform;
	public PointNodeTXY(TXY f, AffineTransform transform, PointNodeTXY next, double t) {
		super(next, t);
		txy = f;
		this.transform = transform;
	}

	public PointNodeTXY(TXY f, AffineTransform t, double t0, double t1) {
		this(f,t,new PointNodeTXY(f,t,null, t1), t0);
	}

	@Override
	protected Point2D getPoint(double t) {
		return new Point2D.Double(txy.getX().eval(t), txy.getY().eval(t));
	}

	@Override
	protected Point2D getScreenPoint(Point2D p) {
		return transform.transform(p, screenPt);
	}
}



class PointNodeTR extends PointNode<PointNodeTR> {
	private TR tr;
	private AffineTransform transform;
	public PointNodeTR(TR f, AffineTransform transform, PointNodeTR next, double t) {
		super(next, t);
		tr = f;
		this.transform = transform;
	}

	public PointNodeTR(TR f, AffineTransform t, double t0, double t1) {
		this(f,t,new PointNodeTR(f,t,null, t1), t0);
	}

	@Override
	protected Point2D getPoint(double t) {
		double r = tr.getR().eval(t);
		
		
		return new Point2D.Double(Math.cos(t)*r, Math.sin(t)*r);
	}

	@Override
	protected Point2D getScreenPoint(Point2D p) {
		return transform.transform(p, screenPt);
	}
}

class CascadeRunnable implements Runnable
{
	List<Runnable> runnables = new LinkedList<Runnable>();
	
	public void add(Runnable a) {
		runnables.add(a);
	}
	
	public void run() {
		for (Runnable runnable: runnables) {
			runnable.run();
		}
	}
}

class BasicDrawableRunnable implements Runnable
{
	private boolean transform;
	private boolean fill;
	private Paint color;
	private Shape shape;
	private CanvasContext context;
	private Double value;
	private float stroke;
	
	BasicDrawableRunnable(CanvasContext context, Shape shape, Paint color, boolean transform, boolean fill) {
		this.color = color;
		this.shape = shape;
		this.transform = transform;
		this.fill = fill;
		this.context = context;
		this.value = context.attributes.STROKE_WIDTH.getValue();
		this.stroke = value.floatValue();
	}
	
	BasicDrawableRunnable(CanvasContext context, Shape shape, Paint color, boolean transform, boolean fill, float stroke) {
		this.color = color;
		this.shape = shape;
		this.transform = transform;
		this.fill = fill;
		this.stroke = stroke;
		this.context = context;
	}

	public void run() {
		draw(context, shape, color, transform, fill, stroke);
	}
	
	private static synchronized void draw(CanvasContext context, Shape shape, Paint color, boolean transform, boolean fill, float stroke)
	{
		Graphics2D g = context.getGraphics();
		synchronized(g) {
			g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setPaint(color);
			if (transform) {
				g.draw(context.getTransform().createTransformedShape(shape));
			}
			else {
				g.draw(shape);
			}
		}
	}

}

public class DrawableSketcher extends AbstractDrawableVisitor<Runnable>
{
	public static enum SketchingAlgorithm
	{
		CONNECT_THE_DOTS ,
		ADAPTIVE ;

		public String toString() {
			switch(this) {
			case CONNECT_THE_DOTS:
				return "Connect the dots";
			case ADAPTIVE:
				return "Adaptive";
			}
			throw new RuntimeException("Programmer error.");
		}
	}

	private final Runnable DO_NOTHING = new Runnable() { public void run() {} };

	private final CanvasContext context;
	private final ExecutorService es;

	public DrawableSketcher(CanvasContext c, ExecutorService es) {
		context = c;
		this.es = es;
	}

	public Runnable drawConnectTheDots(final XY f) {
		final int n = context.getPixelBounds().width;

		final Rectangle2D loose = context.getLooseCartesianBounds();
		final Rectangle2D bounds = context.getCartesianBounds();

		double[] x = Util.linearlyInterpolate(bounds.getMinX(), bounds.getMaxX(), n);		
		double[] y = new double[n];

		Function.unaryEvaluation(f.getY(), x, y, 0, n);

		boolean start = true;
		final Path2D.Double path = new Path2D.Double();


		for (int i=0; i<n; i++) {
			if (loose.getMinY()<=y[i] && y[i]<=loose.getMaxY()) {
				if (start) {
					path.moveTo(x[i], y[i]);					
					start = false;
				}
				else {
					path.lineTo(x[i], y[i]);
				}
			}
			else {
				start = true;
			}
		}
		
		return new BasicDrawableRunnable(context, path, f.getColor(), true, false);
//
//
//		return new Runnable() {
//			public void run() {
//				Double value = context.attributes.STROKE_WIDTH.getValue();
//				float width = value.floatValue();
//				drawShape(context, f.getColor(), path, true, false, width);
//			}
//		};
	}

	


	public Runnable drawAdaptiveXY(final XY f) {
		final int n = context.getPixelBounds().width;

		final Rectangle2D bounds = context.getCartesianBounds();

		final Function func = f.getY();
		final AffineTransform transform = context.getTransform();

		PointNodeXY dn = new PointNodeXY(func, transform, bounds.getMinX(),bounds.getMaxX());

		Adaptive<PointNodeXY> adaptive = new Adaptive<PointNodeXY>() {
			public void insertNearerNext(PointNodeXY node) {
				double t = (1-PHI)*node.getT()+PHI*node.next.getT();
				node.next = new PointNodeXY(func, transform, node.next, t);
			}

			public boolean keepRecursing(PointNodeXY node) {
				return node.getScreenPoint().distance(node.next.getScreenPoint())>20 || n*Math.abs(node.getX()-node.next.getX())>bounds.getWidth();
			}			
		};

		adaptive.recurse(dn, 15);

		return getRunnable(dn, context, f.getColor());
	}
	
	public Runnable drawAdaptiveTXY(final TXY f) {
		GraphAttributes ga = context.attributes;
		
		
		final double tmin ;
		final double tmax ;
		final int tsegs ;

		if (f.useDefaultDomain()) {
			tmin = ga.TMIN.getValue().value;
			tmax = ga.TMAX.getValue().value;
			tsegs = ga.TSEGS.getValue();
		}
		else {
			tmin = f.getTmin().value;
			tmax = f.getTmax().value;
			tsegs = f.getTsegs();
		}

		final AffineTransform transform = context.getTransform();

		PointNodeTXY dn = new PointNodeTXY(f, transform, tmin, tmax);

		Adaptive<PointNodeTXY> adaptive = new Adaptive<PointNodeTXY>() {
			public void insertNearerNext(PointNodeTXY node) {
				double t = PHI*node.getT()+(1-PHI)*node.next.getT();
				node.next = new PointNodeTXY(f, transform, node.next, t);
			}

			public boolean keepRecursing(PointNodeTXY node) {
				return node.getScreenPoint().distance(node.next.getScreenPoint())>3 || tsegs*Math.abs(node.getT()-node.next.getT())>Math.abs(tmax-tmin);
			}			
		};

		adaptive.recurse(dn, 15);

		return getRunnable(dn, context, f.getColor());
	}

	
	public Runnable drawAdaptiveTR(final TR f) {
		GraphAttributes ga = context.attributes;
		
		final double tmin ;
		final double tmax ;
		final int tsegs ;

		if (f.useDefaultDomain()) {
			tmin = ga.TMIN.getValue().value;
			tmax = ga.TMAX.getValue().value;
			tsegs = ga.TSEGS.getValue();
		}
		else {
			tmin = f.getTmin().value;
			tmax = f.getTmax().value;
			tsegs = f.getTsegs();
		}

		final AffineTransform transform = context.getTransform();

		PointNodeTR dn = new PointNodeTR(f, transform, tmin, tmax);

		Adaptive<PointNodeTR> adaptive = new Adaptive<PointNodeTR>() {
			public void insertNearerNext(PointNodeTR node) {
				double t = PHI*node.getT()+(1-PHI)*node.next.getT();
				node.next = new PointNodeTR(f, transform, node.next, t);
			}

			public boolean keepRecursing(PointNodeTR node) {
				return node.getScreenPoint().distance(node.next.getScreenPoint())>3 || tsegs*Math.abs(node.getT()-node.next.getT())>Math.abs(tmax-tmin);
			}			
		};

		adaptive.recurse(dn, 15);
		
		return getRunnable(dn, context, f.getColor());
	}
	
	public static Runnable getRunnable(PointNode<?> dn, CanvasContext context, Paint color) {
		Rectangle bounds = context.getPixelBounds();

		double height = bounds.getHeight();
		double width = bounds.getWidth();
		double lengthBound = Math.sqrt(height*height+width+width);


		final Path2D.Double path = new Path2D.Double();
		
		final int START = 0;
		final int LINETO = 2;
		int state = START;

		PointNode<?> cursor = dn;
		Point2D lastScreenPoint = null;
		Point2D lastCartesianPoint = null;
		Line2D line = new Line2D.Double();
		
		while (cursor!=null) {
			Point2D screenPoint = cursor.getScreenPoint();
			
			switch (state) {
			case START:
				if (lastScreenPoint!=null) {
					line.setLine(lastScreenPoint, screenPoint);
					if (line.getP1().distance(line.getP2())<lengthBound) {
						path.moveTo(lastCartesianPoint.getX(), lastCartesianPoint.getY());
						path.lineTo(cursor.getX(), cursor.getY());
						state = LINETO;
					}
				}

				break;
				
			case LINETO:
				line.setLine(lastScreenPoint, screenPoint);
				if (line.intersects(bounds)) {
					path.lineTo(cursor.getX(), cursor.getY());
					state = LINETO;
				}
				else {
					state = START;
				}
				break;
			
			default:
				throw new RuntimeException("Programming Error");
			}
			
			
			lastScreenPoint = screenPoint;
			lastCartesianPoint = cursor.getCartesianPoint();
			cursor = cursor.next;
		}
		
		return new BasicDrawableRunnable(context, path, color, true, false);
//
//		return new Runnable() {
//			public void run() {
//				Double value = context.attributes.STROKE_WIDTH.getValue();
//				float width = value.floatValue();
//				drawShape(context, f.getColor(), path, true, false, width);
//			}
//		};
	}

	public Runnable visit(final XY f) {
		switch (f.getAlgorithm()) {
		case CONNECT_THE_DOTS:
			return this.drawConnectTheDots(f);

		case ADAPTIVE:
			return this.drawAdaptiveXY(f);
		}

		throw new RuntimeException("Programming Error");
	}

	public Runnable visit(final TR f) {
		switch (f.getAlgorithm()) {
		case CONNECT_THE_DOTS:
			return this.drawConnectTheDots(f);

		case ADAPTIVE:
			return this.drawAdaptiveTR(f);
		}

		throw new RuntimeException("Programming Error");
	}
	
	
	public Runnable drawConnectTheDots(final TR f) {
		Rectangle2D loose = context.getLooseCartesianBounds();

		GraphAttributes ga = context.attributes;

		double tmin = 0;
		double tmax = 0;
		int tsegs = 0;

		if (f.useDefaultDomain()) {
			tmin = ga.TMIN.getValue().value;
			tmax = ga.TMAX.getValue().value;
			tsegs = ga.TSEGS.getValue();
		}
		else {
			tmin = f.getTmin().value;
			tmax = f.getTmax().value;
			tsegs = f.getTsegs();
		}

		int n = tsegs+1;

		double[] x = Util.linearlyInterpolate(tmin, tmax, n);		
		double[] y = new double[n];
		Function.unaryEvaluation(f.getR(), x, y, 0, n);
		boolean start = true;
		final Path2D.Double path = new Path2D.Double();
		Point2D p;

		for (int i=0; i<n; i++) {
			p = new Point2D.Double(Math.cos(x[i])*y[i], Math.sin(x[i])*y[i]);
			if (loose.contains(p)) {
				if (start) {
					path.moveTo(p.getX(), p.getY());					
					start = false;
				}
				else {
					path.lineTo(p.getX(), p.getY());					
				}
			}
			else {
				start = true;
			}
		}
		
		return new BasicDrawableRunnable(context, path, f.getColor(), true, false);
	}

	public Runnable visit(final TXY f) {
		switch (f.getAlgorithm()) {
		case CONNECT_THE_DOTS:
			return this.drawConnectTheDotsTXY(f);

		case ADAPTIVE:
			return this.drawAdaptiveTXY(f);
		}

		throw new RuntimeException("Programming Error");
	}
	
	public Runnable drawConnectTheDotsTXY(final TXY f) {
		Rectangle2D loose = context.getLooseCartesianBounds();

		GraphAttributes ga = context.attributes;

		double tmin = 0;
		double tmax = 0;
		int tsegs = 0;

		if (f.useDefaultDomain()) {
			tmin = ga.TMIN.getValue().value;
			tmax = ga.TMAX.getValue().value;
			tsegs = ga.TSEGS.getValue();
		}
		else {
			tmin = f.getTmin().value;
			tmax = f.getTmax().value;
			tsegs = f.getTsegs();
		}

		int n = tsegs+1;


		double[] t = Util.linearlyInterpolate(tmin, tmax, n);		
		double[] x = new double[n];
		double[] y = new double[n];
		Function.unaryEvaluation(f.getX(), t, x, 0, n);
		Function.unaryEvaluation(f.getY(), t, y, 0, n);
		boolean start = true;
		final Path2D.Double path = new Path2D.Double();
		Point2D p;

		for (int i=0; i<n; i++) {
			p = new Point2D.Double(x[i], y[i]);
			if (loose.contains(p)) {
				if (start) {
					path.moveTo(p.getX(), p.getY());					
					start = false;
				}
				else {
					path.lineTo(p.getX(), p.getY());					
				}
			}
			else {
				start = true;
			}
		}
		
		return new BasicDrawableRunnable(context, path, f.getColor(), true, false);
	}

	

	public <L extends Drawable> Runnable visit(final DrawableList<L> list) {
		try {
			final List<Future<Runnable>> futures = es.invokeAll(list.getTasks(this));
			return new Runnable() {
				public void run() {
					for (Future<Runnable> future: futures) {
						try {
							future.get().run();
						} 
						catch (InterruptedException e) {
							e.printStackTrace();
							return;
						} 
						catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
			};
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		return DO_NOTHING;
	}

	public Runnable visit(final DrawableShape f) {
		
		return new BasicDrawableRunnable(context, f.getShape(), f.getColor(), true, f.getFill(), 1f);
	}

	public Runnable visit(final Grid f) {
		Rectangle pixelBound = context.getPixelBounds();
		Rectangle2D bound = context.getCartesianBounds();

		int xmin = (int) (bound.getMinX() / f.xscale)-1;
		int xmax = (int) (bound.getMaxX() / f.xscale)+1;
		int ymin = (int) (bound.getMinY() / f.yscale)-1;
		int ymax = (int) (bound.getMaxY() / f.yscale)+1;

		final Path2D grid = new Path2D.Double();
		final Path2D axes = new Path2D.Double();
		final Path2D ticks = new Path2D.Double();

		AffineTransform inverse = context.getInverse();

		double xpix = inverse.getScaleX()*5;
		double ypix = inverse.getScaleY()*5;

		if (xmax< pixelBound.width/2+xmin) {
			if (context.attributes.TICKS.getValue()) {
				for (int i=xmin; i<=xmax; i++) {
					ticks.moveTo(i*f.xscale, -ypix);
					ticks.lineTo(i*f.xscale, ypix);
				}
			}
			if (context.attributes.GRID.getValue()) {
				for (int i=xmin; i<=xmax; i++) {
					grid.moveTo(i*f.xscale,bound.getMinY());
					grid.lineTo(i*f.xscale,bound.getMaxY());	
				} 
			}
		}
		if (context.attributes.AXES.getValue()) {
			axes.moveTo(0,bound.getMinY());
			axes.lineTo(0,bound.getMaxY());
		}

		if (ymax < pixelBound.height/2 + ymin) {
			if (context.attributes.GRID.getValue()) {
				for (int i=ymin; i<=ymax; i++) {
					grid.moveTo(bound.getMinX(), i*f.yscale);
					grid.lineTo(bound.getMaxX(), i*f.yscale);
				}
			}

			if (context.attributes.TICKS.getValue()) {
				for (int i=ymin; i<=ymax; i++) {
					ticks.moveTo(-xpix, i*f.yscale);
					ticks.lineTo(xpix, i*f.yscale);
				}
			} 
		}
		if (context.attributes.AXES.getValue()) {
			axes.moveTo(bound.getMinX(), 0);
			axes.lineTo(bound.getMaxX(), 0);
		}
		
		CascadeRunnable result = new CascadeRunnable();
		if (context.attributes.GRID.getValue()) 
			result.add(new BasicDrawableRunnable(context, grid,f.gridPaint, true, false,1f));
		if (context.attributes.AXES.getValue())
			result.add(new BasicDrawableRunnable(context, axes,f.axesPaint, true, false,1f));
		if (context.attributes.TICKS.getValue())
			result.add(new BasicDrawableRunnable(context, ticks,f.ticksPaint, true, false,1f));

		return result;
	}

	public Runnable visit(final Labels f) {
		if (! context.attributes.LABELS.getValue()) {
			return DO_NOTHING;
		}

		Rectangle pixelBound = context.getPixelBounds();
		Rectangle2D bound = context.getCartesianBounds();
		final Graphics2D g = context.getGraphics();

		double xscale = f.getXscale();
		double yscale = f.getYscale();

		int xmin = (int) (bound.getMinX() / xscale);
		int xmax = (int) (bound.getMaxX() / xscale);
		int ymin = (int) (bound.getMinY() / yscale);
		int ymax = (int) (bound.getMaxY() / yscale);

		AffineTransform transform = context.getTransform();

		Point2D p = new Point2D.Float();
		Point2D q = new Point2D.Float();

		FontMetrics fm = g.getFontMetrics();

		Rectangle2D previous;
		boolean intersect = false;
		final Map<Point2D.Float, String> textmap = new HashMap<Point2D.Float, String>();

		double scale;
		Map<Point2D.Float, Rectangle2D> rectmap = new HashMap<Point2D.Float, Rectangle2D>();

		scale = xscale;
		int width = Math.max(pixelBound.width, 1);
		while (4*(xmax-xmin) >= width) {
			scale*=2;
			xmin/=2;
			xmax/=2;
		}
		do {			
			previous = null;
			textmap.clear();
			rectmap.clear();
			intersect = false;

			for (int i=xmin; i<=xmax; i++) {

				if (i!=0) {
					p.setLocation(i*scale, 0);
					transform.transform(p,q);
					String s = Labels.convert(i*scale);

					Rectangle2D rect = fm.getStringBounds(s, g);
					float dx = (float) (q.getX()-rect.getWidth()/2);
					float dy = (float) (q.getY()+4+fm.getAscent());
					rect.setRect(dx+rect.getMinX(),dy+rect.getMinY(), rect.getWidth(), rect.getHeight());
					if (previous!=null && rect.intersects(previous)) {
						intersect = true;
						break;
					}
					else {
						previous = rect;
						Point2D.Float point = new Point2D.Float(dx,dy);
						textmap.put(point, s);
						rectmap.put(point, rect);
					}
				}
			}
			scale*=2;
			xmin/=2;
			xmax/=2;

		} while (intersect);


		scale = yscale;
		int height = Math.max(pixelBound.height, 1);
		while (4*(ymax-ymin) >= height) {
			scale*=2;
			ymin/=2;
			ymax/=2;
		}
		do {
			previous = null;
			rectmap.clear();
			intersect = false;

			for (int i=ymin; i<=ymax; i++) {
				if (i!=0) {
					p.setLocation(0, i*scale);
					transform.transform(p,q);
					String s = Labels.convert(i*scale);

					Rectangle2D rect = fm.getStringBounds(s, g);
					float dx = (float) (q.getX()-rect.getWidth()-4);
					float dy = (float) (q.getY()-1+fm.getAscent()/2);
					rect.setRect(dx+rect.getMinX(),dy+rect.getMinY(), rect.getWidth(), rect.getHeight());
					if (previous!=null && rect.intersects(previous)) {
						intersect = true;
						break;
					}
					else {
						previous = rect;
						Point2D.Float point = new Point2D.Float(dx,dy);
						textmap.put(point, s);
						rectmap.put(point, rect);
					}
				} 
			}
			scale*=2;
			ymin/=2;
			ymax/=2;

		} while (intersect);



		return new Runnable() {
			public void run() {
				g.setColor(f.color);

				for (Point2D.Float point : textmap.keySet()) {
					g.drawString(textmap.get(point), point.x, point.y);
				}
//				for (Point2D.Float point : textmap2.keySet()) {
//					g.drawString(textmap2.get(point), point.x, point.y);
//				}
			}
		};
	}

	@Override
	public Runnable visit(Points f) {
		// TODO Auto-generated method stub
		return null;
	}


}
