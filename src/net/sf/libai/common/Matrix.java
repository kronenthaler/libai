package net.sf.libai.common;

import java.util.*;
import java.io.*;

/**
 *	Matrix implementation.
 *	This class can handle the basic operations of matrix space, add, substract, product
 *	scalar product, transponse, and other useful operations for this API.
 *	@author kronenthaler
 */
public final class Matrix {
	/** Matrix's data, stored for row in a sequential array. */
	private double matrix[];

	/** Number of rows and columns of the matrix. */
	private int rows, cols;

	/** Seed for random number generation. Is useful for debugging purposes. */
	private long seed;

	/**
	 *	Constructor. Allocated the matrix and could initialize with the identity.
	 *	By default, the matrix is created filled with zeroes.
	 *	@param r number of rows
	 *	@param c number of columns
	 *	@param identity if you need initialized with an identity.
	 */
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

	/**
	 *	Constructor alias for Matrix(r,c,false).
	 *	@param r number of rows
	 *	@param c number of cols
	 */
	public Matrix(int r,int c){
		this(r,c,false);
	}

	/**
	 *	Constructor. Creates a matrix and initialize with the data on <code>data</code>
	 *	@param r number of rows
	 *	@param c number of cols
	 *	@param data values to initialize the matrix.
	 */
	public Matrix(int r, int c, double[] data){
		this(r,c,false);

		//if(r != data.length || c != data[0].length)
		//	throw new IllegalArgumentException("Mismatch dimensions");

		System.arraycopy(data, 0, matrix, 0, r*c);
	}

	/**
	 *	Create a new Matrix filled with low random numbers.
	 *	@param r number of rows
	 *	@param c number of cols
	 *	@return a new matrix filled with low random numbers.s
	 */
	public static Matrix random(int r,int c){
		Matrix ret = new Matrix(r,c);
		ret.fill();
		return ret;
	}

	/**
	 *	Adds two matrix. This + a, and left the result on b.
	 *	The matrix b must be created and has the same dimension of this and a.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param a The matrix to add
	 *	@param b The matrix to put the result
	 */
	public void add(final Matrix a,final Matrix b){
		assert rows == a.rows && cols == a.cols && a.rows == b.rows && a.cols == b.cols;
		
		for(int i=0,n=rows*cols;i<n;i++)
			b.matrix[i] = matrix[i] + a.matrix[i];
	}

	/**
	 *	Subtract two matrix. This - a, and left the result on b.
	 *	The matrix b must be created and has the same dimension of this and a.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param a The matrix to subtract
	 *	@param b The matrix to put the result
	 */
	public void subtract(final Matrix a,final Matrix b){
		assert rows == a.rows && cols == a.cols && a.rows == b.rows && a.cols == b.cols;
		
		for(int i=0,n=rows*cols;i<n;i++)
			b.matrix[i] = matrix[i] - a.matrix[i];
	}

	/**
	 *	Multiply this matrix by an scalar. This * a, and left the result on b.
	 *	The matrix b must be created and has the same dimension of this and a.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param a The scalar to multiply
	 *	@param b The matrix to put the result
	 */
	public void multiply(final double a,final Matrix b){
		assert rows == b.rows && cols == b.cols;

		for(int i=0,n=rows*cols;i<n;i++)
			b.matrix[i] = a * matrix[i];
	}

	/**
	 *	Multiply two matrix. This * a, and left the result on b.
	 *	The matrix b must be created and has the right dimensions.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param a The matrix to multiply
	 *	@param b The matrix to put the result
	 */
	public void multiply(final Matrix a,final Matrix b){
		assert cols == a.rows && b.rows == rows && b.cols == a.cols;
		
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

	/**
	 *	Apply one function over the elements of the matrix and left the result on a.
	 *	For each element on this a(i,j) = F(this(i,j)).
	 *	The matrix a must be created and has the same dimension of this and a.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param f function to apply.
	 *	@param a The matrix to put the result.
	 */
	public void apply(Function f,final Matrix a){
		assert rows == a.rows && cols == a.cols;
		
		for(int i=0,n=rows*cols;i<n;i++)
			a.matrix[i] = f.eval(matrix[i]);
	}

	/**
	 *	Apply one function over the elements of the matrix and left on the main diagonal
	 *	of a identity matrix 'a'.
	 *	For each element on this a(i,i) = F(this(i,j)).
	 *	The matrix this must be a column matrix.
	 *	The matrix a must be created and has the same dimension of this and a.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param f function to apply.
	 *	@param a The matrix to put the result.
	 */
	public void applyInIdentity(Function f,final Matrix a){
		assert cols == 1 && rows == a.rows && rows == a.cols;
		
		for(int i=0;i<rows;i++)
			a.position(i,i,f.eval(position(i,0)));
	}

	/**
	 *	Fill the matrix with random values.
	 *	Alias for fill(true).
	 */
	public void fill(){ fill(true); }

	/**
	 *	Fill the matrix with random values. If the fill must be positive call with
	 *	false.
	 *	@param signed if allow signed values or not
	 */
	public void fill(boolean signed){
		Random r = new Random(seed);
		for(int i=0,n=rows*cols;i<n;i++){
			matrix[i] = r.nextDouble()*0.01*(double)Math.pow(-1,r.nextInt(2));
			if(!signed)
				matrix[i] = Math.abs(matrix[i]);
		}
	}

	/**
	 *	Copy this matrix to another matrix a.
	 *	The matrix a must be created and match the dimensions of this.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param a The matrix to put the result.
	 */
	public void copy(final Matrix a){
		assert rows == a.rows && cols == a.cols;
		
		System.arraycopy(matrix, 0, a.matrix, 0, matrix.length);
	}

	/**
	 *	Calculate the dot product between this matrix and another.
	 *	This method can safely calculate the dot product between row-column, row-row, column-column matrices.
	 *	@param a The matrix to multiply
	 *	@return the scalar of the dot product.
	 */
	public double dotProduct(Matrix a){
		double acum = 0;

		for(int i=0;i<cols*rows;i++){
			acum += matrix[i]*a.matrix[i];
		}

		return acum;
	}

	/**
	 *	Transpose this matrix and left the result on a.
	 *	The matrix a must be created and has the right dimensions.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param a The matrix to put the result.
	 */
	public void transpose(final Matrix a){
		assert rows == a.cols && cols == a.rows;
		
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				a.position(j,i,position(i,j));
	}

	/**
	 *	Return the transpose of this matrix.
	 *	@return A new matrix with the transpose of this.
	 */
	public Matrix transpose(){
		Matrix ret = new Matrix(cols,rows);
		transpose(ret);
		return ret;
	}

	/**
	 *	Replace one row of values of this matrix.
	 *	The number of values of data must match with the number of columns of this.
	 *	NOTE: Assertions of the dimensions are made with assert statement. You must
	 *	enable this on runtime to be effective.
	 *	@param index The index of the row to place the values.
	 *	@param data	The values to put in that row.
	 */
	public void setRow(int index, double[] data){
		assert cols == data.length;

		System.arraycopy(data, 0, matrix, index*cols, data.length);
	}

	/**
	 *	Set a value on each position of the matrix.
	 *	@param v The value to put in the matrix.
	 */
	public void setValue(double v){
		Arrays.fill(matrix, v);
	}

	/**
	 *	Return an array with the values of the specified row.
	 *	@param index The index of the row to return.
	 *	@return the array with the values.
	 */
	public double[] getRow(int index){
		double[] ret = new double[cols];
		System.arraycopy(matrix, index*cols, ret, 0, ret.length);
		return ret;
	}
	
	/**
	 *	Return an array with the values of the specified column.
	 *	@param index The index of the column to return.
	 *	@return the array with the values.
	 */
	public double[] getCol(int index){
		double[] ret = new double[rows];
		for(int i=0;i<rows;ret[i] = position(i++,index));
		return ret;
	}

	/**
	 *	Return the value on the position (i,j).
	 *	@param i index of the row
	 *	@param j index of the column
	 *	@return the value of that position
	 */
	public final double position(int i,int j){
		try{
			return matrix[(i*cols) + j];
		}catch(RuntimeException e){
			System.out.println(i+","+j);
			throw e;
		}
	}

	/**
	 *	Set the value v on the position (i,j).
	 *	@param i index of the row
	 *	@param j index of the column
	 *	@param v the value to put into. 
	 */
	public final void position(int i,int j,double v){
		matrix[(i*cols)+j]=v;
	}

	/**
	 *	Substract the value of this with the value of b, and let the result on resultSubstract
	 *	Also, copy the original value of this into resultCopy.
	 *	@param b Matrix to substract
	 *	@param resultSubstract Matrix to hold the result of the substraction
	 *	@param resultCopy Matrix to hold the copy of this.
	 */
	public void subtractAndCopy(Matrix b, Matrix resultSubstract, Matrix resultCopy){
		for(int i=0;i<matrix.length;i++){
			resultSubstract.matrix[i] = matrix[i]-b.matrix[i];
			resultCopy.matrix[i] = matrix[i];
		}
	}
	/**
	 *	Return in result the value of (this*a + b) for each This(i,j)
	 *	@param a constant to multiply
	 *	@param b matrix to add
	 *	@param result	Matrix to hold the result of the operation.
	 */
	public void multiplyAndAdd(double a, Matrix b, Matrix result){
		for(int i=0;i<matrix.length;i++){
			result.matrix[i] = matrix[i]*a + b.matrix[i];
		}
	}

	/**
	 *	Increments the value of one position by v.
	 *	@param i index of the row
	 *	@param j index of the column
	 *	@param v value to increment.
	 */
	public final void increment(int i,int j, double v){
		matrix[(i*cols)+j] += v;
	}
	
	public void swap(int i,int j){
		double[] a = getRow(i);
		double[] b = getRow(j);
		setRow(i,b);
		setRow(j,a);
	}

	/*//TODO needs translation to this matrix class.
	public Matrix solve(Matrix rhs) {
        if (rows != cols || rhs.rows != cols || rhs.cols != 1)
            throw new RuntimeException("Illegal matrix dimensions.");

        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);

        // Gaussian elimination with partial pivoting
        for (int i = 0; i < cols; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < cols; j++)
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
                    max = j;

            A.swap(i, max);
            b.swap(i, max);

            // singular
            if (A.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

            // pivot within b
            for (int j = i + 1; j < cols; j++)
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];

            // pivot within A
            for (int j = i + 1; j < cols; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i+1; k < cols; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }

        // back substitution
        Matrix x = new Matrix(cols, 1);
        for (int j = cols - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < cols; k++)
                t += A.data[j][k] * x.data[k][0];
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;
    }
	*/

	/**
	 *	Print this matrix in the OpenOffice's formula format.
	 *	Useful for copy & paste in OO documents.
	 *	@param out Stream to write on.
	 */
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

	/**
	 *	Check if two matrix are equals position to position with a precision of 1e-7.
	 *	If the dimensions mismatch they aren't equals.
	 *	If one position differs by more than 1e-7 then are differents.
	 *	@param b1 The matrix to compare
	 *	@return <code>true</code> if are equals, <code>false</code> otherwise.
	 */
	public boolean equals(Object b1){
		Matrix b = (Matrix) b1;
		if(rows != b.rows || cols != b.cols) 
			return false;

		for(int i=0,n=rows*cols;i<n;i++)
			if(Math.abs(matrix[i]-b.matrix[i]) > 1.e-7) //'equals'
				return false;
		return true;
	}

	/**
	 *	Return the string representation of this matrix.
	 *	Useful to write on a file or for debugging.
	 *	@return An string with the values of the matrix.
	 */
	@Override
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
	 * @return The number of rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return The number of columns
	 */
	public int getColumns() {
		return cols;
	}
}
