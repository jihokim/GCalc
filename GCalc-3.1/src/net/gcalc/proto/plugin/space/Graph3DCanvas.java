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


package net.gcalc.proto.plugin.space;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.Vector;

import net.gcalc.calc.main.ValueTable;
import net.gcalc.calc.math.functions.Function;
import net.gcalc.calc.models.Model;
import net.gcalc.calc.parser.VariableToken;
import net.gcalc.plugin.gui.BufferedCanvas;

class Graph3DCanvas extends BufferedCanvas implements Runnable
{
	private int res = 45; //resolution of grid

	private double scalingFactor; // scaling factor
	private double rho; // distance between camera and origin
	private double theta;
	private double phi;

	private int px, py;
	private double phi0, theta0;
	private double dtheta = 0, dphi = 0;
	boolean mousePressed = false;

	double xmin = -2;
	double ymin = -2;
	double zmin = -2;
	double xmax = 2;
	double ymax = 2;
	double zmax = 2;

	private double maxz = Double.NaN, minz = Double.NaN;

	protected Segment3D[] Q = new Segment3D[0];

	protected Vector axes;

	public Graph3DCanvas()
	{
		super();
		setSize(400, 400);
		setPreferredSize(new Dimension(400, 400));

		this.addMouseListener(new CustomMouseAdapter());
		this.addMouseMotionListener(new CustomMouseMotionAdapter());

		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		double m = Math.max(xmax - xmin, ymax - ymin);
		m = Math.max(m, zmax - zmin);
		scalingFactor = 700.0 * 8 / m;
		rho = 30;

		initAxes();

		defaultPerspective();
	}

	protected void initAxes()
	{

		double[] X = { xmin, xmax };
		double[] Y = { ymin, ymax };
		double[] Z = { zmin, zmax };

		axes = new Vector();

		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
			{
				axes.add(
					new Segment3D(
						new DoubleTriple(X[0], Y[i], Z[j]),
						new DoubleTriple(X[1], Y[i], Z[j]),
						Color.gray,
						Color.red,
						true));
				axes.add(
					new Segment3D(
						new DoubleTriple(X[i], Y[0], Z[j]),
						new DoubleTriple(X[i], Y[1], Z[j]),
						Color.gray,
						Color.green,
						true));
				axes.add(
					new Segment3D(
						new DoubleTriple(X[i], Y[j], Z[0]),
						new DoubleTriple(X[i], Y[j], Z[1]),
						Color.gray,
						Color.blue,
						true));
			}
	}

	private double[][] getGrid(Function F)
	{
	    ValueTable vt = new ValueTable();

		double[][] data = new double[res][res];
	
		int i, j;

		double x, y;

		for (i = 0; i < res; i++)
		{
			x = xmin + (xmax - xmin) * i / (res - 1);
			vt.setValue(VariableToken.X_VAR, x);
			for (j = 0; j < res; j++)
			{
				y = ymin + (ymax - ymin) * j / (res - 1);
				vt.setValue(VariableToken.Y_VAR, y);

				data[i][j] = F.evaluate(vt);
			}
		}

		return data;
	}

	public double gridXtoCartesian(int i)
	{
		return xmin + (xmax - xmin) * i / (res - 1);
	}

	public double gridYtoCartesian(int j)
	{
		return ymin + (ymax - ymin) * j / (res - 1);
	}

	private Vector getGridSegments(double[][] r)
	{
		Vector v = new Vector();
		double x, y;
		DoubleTriple A, B, C;

		for (int i = 0; i < res; i++)
		{
			x = gridXtoCartesian(i);
			for (int j = 0; j < res; j++)
			{
				if (r[i][j] <= zmax && zmin <= r[i][j])
				{
					y = gridYtoCartesian(j);
					A = new DoubleTriple(x, y, r[i][j]);

					if (i + 1 < res
						&& !Double.isNaN(r[i + 1][j])
						&& r[i + 1][j] <= zmax
						&& zmin <= r[i + 1][j])
					{

						B = new DoubleTriple(gridXtoCartesian(i + 1), y, r[i + 1][j]);
						v.add(new Segment3D(A, B, Color.gray, Color.black));
					}

					if (j + 1 < res
						&& !Double.isNaN(r[i][j + 1])
						&& r[i][j + 1] <= zmax
						&& zmin <= r[i][j + 1])
					{

						C = new DoubleTriple(x, gridYtoCartesian(j + 1), r[i][j + 1]);
						v.add(new Segment3D(A, C, Color.gray, Color.black));
					}
				}
			}
		}

		return v;
	}

	public synchronized void setModel(Model fg)
	{
		Q = new Segment3D[0];

		double[][] r = getGrid(fg.getFunction());

		Vector v = getGridSegments(r);

		v.addAll(axes);

		Segment3D[] segs = new Segment3D[Q.length + v.size()];

		System.arraycopy(Q, 0, segs, 0, Q.length);
		for (int k = 0; k < v.size(); k++)
			segs[Q.length + k] = (Segment3D) v.elementAt(k);

		Q = segs;

		//defaultPerspective();

		redrawAll();
	}

	public void defaultPerspective()
	{
		phi = Math.PI / 3;
		theta = -2 * Math.PI / 3;
		dphi = 0;
		dtheta = 0;
	}

	private void transformation(double theta, double phi, double rho, double sf)
	{
		if (Q.length == 0)
			return;

		double s1 = Math.sin(theta);
		double c1 = Math.cos(theta);
		double s2 = Math.sin(phi);
		double c2 = Math.cos(phi);
		double x0 = (xmin + xmax) / 2;
		double y0 = (ymin + ymax) / 2;
		double z0 = (zmin + zmax) / 2;

		for (int i = 0; i < Q.length; i++)
		{
			Q[i].transform(s1, c1, s2, c2, x0, y0, z0, rho, sf);
		}

		Arrays.sort(Q);

		minz = Q[0].averageDistance();
		maxz = Q[Q.length - 1].averageDistance();

	}

	public synchronized void clear()
	{
		Q = new Segment3D[axes.size()];

		for (int k = 0; k < axes.size(); k++)
			Q[k] = (Segment3D) axes.elementAt(k);

		redrawAll();
	}

	public void run()
	{
		double t = 0, p = 0, r = 0, d = 0;
		while (true)
		{

			if (!mousePressed && Q.length >= axes.size())
			{
				theta += dtheta;
				phi += dphi;
				if ((t != theta || p != phi || r != rho || d != scalingFactor) && this.isShowing())
				{
					redrawAll();

					t = theta;
					p = phi;
					r = rho;
					d = scalingFactor;
				}
			}
			try
			{
				Thread.sleep(60);
			}
			catch (InterruptedException e)
			{
				return;
			}

		}
	}

	public synchronized void redrawAll()
	{
		if (gr == null)
			return;

		gr.setColor(Color.white);
		gr.fillRect(0, 0, 400, 400);

		if (Q.length == 0)
		{
			repaint();
			return;
		}

		transformation(theta, phi, rho, scalingFactor);

		for (int i = Q.length - 1; i >= 0; i--)
		{
			drawSegment(Q[i]);
		}

		repaint();
	}

	private void drawSegment(Segment3D s)
	{
		DoubleTriple p1 = s.getP1();
		DoubleTriple p2 = s.getP2();

		int x1 = (int) (p1.getX() / p1.getZ() + getWidth() / 2);
		int y1 = (int) (getHeight() / 2 - p1.getY() / p1.getZ());
		int x2 = (int) (p2.getX() / p2.getZ() + getWidth() / 2);
		int y2 = (int) (getHeight() / 2 - p2.getY() / p2.getZ());

		double avgz = s.averageDistance();
		double percentage = (avgz - minz) / (maxz - minz);

		double dx = x2 - x1;
		double dy = y2 - y1;
		double len = Math.sqrt(dx * dx + dy * dy);

		dx = dx / len;
		dy = dy / len;

		double scl = 5;

		gr.setColor(colorGradient(s.nearColor(), s.farColor(), (float) percentage));
		gr.drawLine(x1, y1, x2, y2);

		if (s.isArrow())
		{
			gr.drawLine((int) (x2 - (dx - dy) * scl), (int) (y2 - (dx + dy) * scl), x2, y2);
			gr.drawLine((int) (x2 - (dx + dy) * scl), (int) (y2 - (-dx + dy) * scl), x2, y2);
			//		gr.drawLine(x1,y1+1,x2,y2);
		}
	}

	private Color colorGradient(Color a, Color b, float f)
	{
		float[] c = a.getRGBComponents(null);
		float[] d = b.getRGBComponents(null);
		float[] e = new float[4];

		for (int i = 0; i < 4; i++)
		{
			e[i] = c[i] + f * (d[i] - c[i]);
		}

		return new Color(e[0], e[1], e[2]);
	}

	class CustomMouseAdapter extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			px = e.getX();
			py = e.getY();
			phi0 = phi;
			theta0 = theta;
			dphi = 0;
			dtheta = 0;
			mousePressed = true;
		}

		public void mouseReleased(MouseEvent e)
		{
			mousePressed = false;
		}
	}

	class CustomMouseMotionAdapter extends MouseMotionAdapter
	{
		public void mouseDragged(MouseEvent e)
		{
			double p = phi0 + (py - e.getY()) * 2 * Math.PI / getHeight();
			double t = theta0 + (e.getX() - px) * 2 * Math.PI / getWidth();

			dphi = p - phi;
			dtheta = t - theta;

			phi = p;
			theta = t;
			redrawAll();
		}
	}
}

