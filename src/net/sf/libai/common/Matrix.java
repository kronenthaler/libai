package net.sf.libai.common;

import java.util.*;
import java.io.*;

/**
 *
 * @author kronenthaler
 */
public final class Matrix {
	private double matrix[];
	private int rows, cols;
	private long seed;

	public Matrix(int r,int c,boolean identity){
		matrix = new double[r*c];
		rows = r;
		cols = c;
		seed = System.currentTimeMillis();
		//seed = 0;

		if(identity){
			for(int i=0;i<r;i++)
				for(int j=0;j<c;j++)
					matrix[i*cols + j] = i==j?1:0;
		}
	}

	public Matrix(int r,int c){
		this(r,c,false);
	}

	public Matrix(int r, int c, double[] data){
		this(r,c,false);

		//if(r != data.length || c != data[0].length)
		//	throw new IllegalArgumentException("Mismatch dimensions");

		System.arraycopy(data, 0, matrix, 0, r*c);
	}

	public void add(final Matrix a,final Matrix b){
		//if(rows != a.rows || cols != a.cols ||
		//	a.rows != b.rows || a.cols != b.cols)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") + ("+a.rows+","+a.cols+") = ("+b.rows+","+b.cols+")");

		for(int i=0,n=rows*cols;i<n;i++)
			b.matrix[i] = matrix[i] + a.matrix[i];
	}

	public void subtract(final Matrix a,final Matrix b){
		//if(rows != a.rows || cols != a.cols ||
		//	a.rows != b.rows || a.cols != b.cols)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") - ("+a.rows+","+a.cols+") = ("+b.rows+","+b.cols+")");

		for(int i=0,n=rows*cols;i<n;i++)
			b.matrix[i] = matrix[i] - a.matrix[i];
	}

	public void multiply(final double a,final Matrix b){
		//if(rows != b.rows || cols != b.cols)
		//	throw new IllegalArgumentException("Mismatch dimensions: a x ("+rows+","+cols+") = ("+b.rows+","+b.cols+")");

		for(int i=0,n=rows*cols;i<n;i++)
			b.matrix[i] = a * matrix[i];
	}

	public void multiply(final Matrix a,final Matrix b){
		//if(cols != a.rows || b.rows != rows && b.cols != a.cols)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") x ("+a.rows+","+a.cols+") = ("+b.rows+","+b.cols+")");

		double sum=0;
		for(int i=0;i<rows;i++){
			for(int j=0;j<b.cols;j++){
				sum=0;
				for(int k=0;k<cols;k++)
					sum += position(i,k) * a.position(k,j);
				b.position(i,j,sum);
			}
		}
	}

	public void apply(Function f,final Matrix a){
		//if(a.rows != rows || cols != a.cols)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") = ("+a.rows+","+a.cols+")");

		for(int i=0,n=rows*cols;i<n;i++)
			a.matrix[i] = f.eval(matrix[i]);
	}

	public void applyInIdentity(Function f,final Matrix a){
		//if(a.rows != rows || cols != 1)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") = ("+a.rows+","+a.cols+")");

		for(int i=0;i<rows;i++)
			a.position(i,i,f.eval(position(i,0)));
	}

	public void fill(){ fill(true); }
	
	public void fill(boolean signed){
		Random r = new Random(seed);
		for(int i=0,n=rows*cols;i<n;i++){
			matrix[i] = r.nextDouble()*0.01*(double)Math.pow(-1,r.nextInt(2));
			if(!signed)
				matrix[i] = Math.abs(matrix[i]);
		}
	}

	public void copy(Matrix a){
		//if(rows != a.rows || cols != a.cols)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") = ("+a.rows+","+a.cols+")");

		System.arraycopy(matrix, 0, a.matrix, 0, matrix.length);
	}

	public void transpose(Matrix a){
		//if(rows != a.cols || cols != a.rows)
		//	throw new IllegalArgumentException("Mismatch dimensions: ("+rows+","+cols+") = ("+a.rows+","+a.cols+")");

		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				a.position(j,i,position(i,j));
	}

	public Matrix transpose(){
		Matrix ret = new Matrix(cols,rows);
		transpose(ret);
		return ret;
	}

	public void setRow(int index, double[] data){
		//if(cols != data.length)
		//	throw new IllegalArgumentException("Mismatch dimensions: "+cols+" = "+data.length);
		System.arraycopy(data, 0, matrix, index*cols, data.length);
	}

	public void setValue(double v){
		Arrays.fill(matrix, v);
	}

	public double[] getRow(int index){
		double[] ret = new double[cols];
		System.arraycopy(matrix, index*cols, ret, 0, ret.length);
		return ret;
	}

	public double[] getCol(int index){
		double[] ret = new double[rows];
		for(int i=0;i<rows;ret[i] = position(i++,index));
		return ret;
	}

	public final double position(int i,int j){
		try{
			return matrix[(i*cols) + j];
		}catch(RuntimeException e){
			System.out.println(i+","+j);
			throw e;
		}
	}

	public final void position(int i,int j,double v){
		matrix[(i*cols)+j]=v;
	}

	public void printOOFormat(PrintStream out){
		out.print("left [ matrix{");
		for(int i=0,k=0;i<rows;i++){
			if(i>0)out.print( " ## ");
			for(int j=0;j<cols;j++){
				if(j>0) out.print( " # ");
				out.print(matrix[k++]);
			}
		}
		out.println("} right ]newLine");
	}

	public boolean equals(Object b1){
		Matrix b = (Matrix) b1;
		if(rows != b.rows || cols != b.cols) 
			return false;

		for(int i=0,n=rows*cols;i<n;i++)
			if(Math.abs(matrix[i]-b.matrix[i]) > 1.e-7) //'equals'
				return false;
		return true;
	}

	public String toString(){
		StringBuffer str = new StringBuffer();

		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++)
				str.append(String.format(Locale.US, "%.8f ", position(i,j)));
			str.append('\n');
		}
		str.append('\n');

		return str.toString();
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the cols
	 */
	public int getCols() {
		return cols;
	}
}
