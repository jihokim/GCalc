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


package net.gcalc.proto.plugin.space;


public class Matrix 
{
	private double[][] m;
	
	public static Matrix getIdentity(int n)
	{
		return new Matrix(n);
	}
	
	public Matrix(int r, int c, double[] a)
	{
		if (r<=0 || c<=0)
			throw new IllegalArgumentException("Bad matrix size!");
		
		m = new double[r][c];
		int k=0;
		for (int i=0; i<r; i++) {
			for (int j=0; j<r; j++) {
				m[i][j]=(k<a.length)?a[k]:0;
				k++;
			}
		}
	}
	
	public Matrix(int r, int c)
	{
		this(r,c,new double[] {});
	}
	
	/** 
	 * Constructor for identity matrix of a given size
	 * @param r size of matrix
	 */
	private Matrix(int r)
	{
		this(r,r);
		for (int i=0; i<r; i++)
			m[i][i]=1;
	}
	
	public double entry(int r, int c)
	{
		return m[r][c];
	}
	
	public boolean isSquare()
	{
		return rows()==columns();
	}
	
	public int rows()
	{
		return m.length;
	}
	
	public int columns()
	{
		return m[0].length;
	}
	
	public Matrix multiply(Matrix b)
	{
		if (this.columns()!=b.rows())
			throw new IllegalArgumentException("Bad matrix size!");
		
		int size = rows()*b.columns();
		double[] e = new double[size];
		
		int r = rows();
		int c = b.columns();
		int n = columns();
		int t = 0;	//temp
		int idx = 0;
		
		for (int i=0; i<r; i++)
			for (int j=0; j<c; j++) {
				t = 0;
				for (int k=0; k<n; k++) 
					t+=entry(i,k)*entry(k,j);
				e[idx]=t;
				idx++;
			}
	
		return new Matrix(r,c,e);
	}
}

