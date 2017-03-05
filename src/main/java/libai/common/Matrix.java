/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package libai.common;

import libai.common.functions.Function;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Matrix implementation. This class can handle the basic operations of matrix
 * space, add, subtract, product scalar product, transpose, and other useful
 * operations for this API.
 *
 * @author kronenthaler
 */
public final class Matrix implements Serializable {
	private static final long serialVersionUID = 4152602945322905714L;

	/**
	 * Matrix's data, stored for row in a sequential array.
	 */
	private final double matrix[];
	/**
	 * Number of rows and columns of the matrix.
	 */
	private final int rows, cols;

	/**
	 * Constructor. Allocated the matrix and could initialize with the identity.
	 * By default, the matrix is created filled with zeroes.
	 *
	 * @param r number of rows
	 * @param c number of columns
	 * @param identity if you need initialized with an identity.
	 * @throws IllegalArgumentException if either {@code r} or {@code c} are
	 * less or equal than zero.
	 */
	public Matrix(int r, int c, boolean identity) {
		if (r <= 0) {
			String msg = "The number of rows must be a non zero positive"
					   + "integer current %d";
			throw new IllegalArgumentException(String.format(msg, r));
		}
		if (c <= 0) {
			String msg = "The number of columns must be a non zero positive"
					   + "integer current %d";
			throw new IllegalArgumentException(String.format(msg, c));
		}

		matrix = new double[r * c];
		rows = r;
		cols = c;

		if (identity) {
			for (int i = 0, m = Math.min(r, c); i < m; i++)
				matrix[i * c + i] = 1;
		}
	}

	/**
	 * Constructor alias for Matrix(r,c,false).
	 *
	 * @param r number of rows
	 * @param c number of columns
	 * @throws IllegalArgumentException if either {@code r} or {@code c} are
	 * less or equal than zero.
	 */
	public Matrix(int r, int c) {
		this(r, c, false);
	}

	/**
	 * Constructor.
	 * <p>Creates a matrix and initialize with the data on {@code data}.</p>
	 * <p>The values are read as row based: data -&gt; row1, row2, ..., rown</p>
	 *
	 * @param r number of rows
	 * @param c number of columns
	 * @param data values to initialize the matrix (length must be
	 * {@code r * c}).
	 * @throws IllegalArgumentException if either {@code r} or {@code c} are
	 * less or equal than zero or if {@code data} is {@code null} or has the
	 * wrong dimension.
	 */
	public Matrix(int r, int c, double[] data) {
		this(r, c, false);

		if (data == null) {
			throw new IllegalArgumentException("Data array must be not null");
		}

		if(data.length != r * c) {
			String msg = "Wrong data array length expected %d got %d";
			throw new IllegalArgumentException(String.format(msg, r * c, data.length));
		}

		System.arraycopy(data, 0, matrix, 0, r * c);
	}

	public Matrix(Matrix copy){
		this(copy.getRows(), copy.getColumns(), copy.matrix);
	}

	public static Random getDefaultRandom(){
		return ThreadLocalRandom.current();
	}

	/**
	 * Create a new Matrix filled with low random numbers.
	 *
	 * @param r number of rows
	 * @param c number of columns
	 * @param signed {@code true} if the matrix should be filled with positive
	 * and negative numbers {@code false} if the numbers should be greater or
	 * equal than zero.
	 * @return a new matrix filled with low random numbers.
	 * @see Matrix#fill(boolean, java.util.Random)
	 * @throws IllegalArgumentException if either {@code r} or {@code c} are
	 * less or equal than zero.
	 */
	public static Matrix random(int r, int c, boolean signed) {
		return random(r, c, signed, getDefaultRandom());
	}

	/**
	 * Create a new Matrix filled with low random numbers.
	 *
	 * @param r number of rows
	 * @param c number of columns
	 * @param signed {@code true} if the matrix should be filled with positive
	 * and negative numbers {@code false} if the numbers should be greater or
	 * equal than zero.
	 * @param rand The {@link Random} object used to fill the matrix, if
	 * {@code null} it will fallback to {@link ThreadLocalRandom#current()}
	 * @return a new matrix filled with low random numbers.
	 * @see Matrix#fill(boolean, java.util.Random)
	 * @throws IllegalArgumentException if either {@code r} or {@code c} are
	 * less or equal than zero.
	 */
	public static Matrix random(int r, int c, boolean signed, Random rand) {
		Matrix ret = new Matrix(r, c);
		ret.fill(signed, rand);
		return ret;
	}

	/**
	 * Create a new Matrix filled with low random numbers.
	 *
	 * @param r number of rows
	 * @param c number of columns
	 * @return a new matrix filled with low random numbers.
	 * @see Matrix#random(int, int, boolean)
	 * @see Matrix#random(int, int, boolean, java.util.Random)
	 * @throws IllegalArgumentException if either {@code r} or {@code c} are
	 * less or equal than zero.
	 */
	public static Matrix random(int r, int c) {
		return random(r, c, true);
	}

	/**
	 * Adds two matrices. <b>{@code b = this + a}</b>
	 * <p>The matrix {@code b} must be created and has the same dimension of
	 * {@code this} and {@code a}. </p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a The matrix to add
	 * @param b The matrix to put the result
	 */
	public void add(final Matrix a, final Matrix b) {
		assert a != null && b != null : "a & b must be not null";
		assert rows == a.rows && rows == b.rows
			&& cols == a.cols && cols == b.cols :
			   "this, a & b must have the same dimensions";

		for (int i = 0, n = rows * cols; i < n; i++) {
			b.matrix[i] = matrix[i] + a.matrix[i];
		}
	}

	/**
	 * Subtract two matrices. <b>{@code b = this - a}</b>.
	 * <p>The matrix {@code b} must be created and has the same dimension of
	 * {@code this} and {@code a}. </p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a The matrix to subtract
	 * @param b The matrix to put the result
	 */
	public void subtract(final Matrix a, final Matrix b) {
		assert a != null && b != null : "a & b must be not null";
		assert rows == a.rows && rows == b.rows
			&& cols == a.cols && cols == b.cols :
			   "this, a & b must have the same dimensions";

		for (int i = 0, n = rows * cols; i < n; i++) {
			b.matrix[i] = matrix[i] - a.matrix[i];
		}
	}

	/**
	 * Multiply this matrix by an scalar. <b>{@code b = this * a}</b>.
	 * <p>The matrix {@code b} must be created and has the same dimension of
	 * {@code this}. </p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a The scalar to multiply
	 * @param b The matrix to put the result
	 */
	public void multiply(final double a, final Matrix b) {
		assert b != null : "b must be not null";
		assert rows == b.rows && cols == b.cols :
			   "this & b must have the same dimensions";

		for (int i = 0, n = rows * cols; i < n; i++) {
			b.matrix[i] = a * matrix[i];
		}
	}

	/**
	 * Multiply two matrix. <b>{@code b = this * a}</b>
	 * <p>The matrix {@code a} must be created and has the right dimensions,
	 * that is {@code a.rows = this.cols}.</p>
	 * <p>The matrix {@code b} must be created and has the right dimensions,
	 * that is {@code b.rows = this.rows} and {@code b.cols = a.cols}.</p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a The matrix to multiply
	 * @param b The matrix to put the result
	 */
	public void multiply(final Matrix a, final Matrix b) {
		assert a != null && b != null : "a & b must be not null";
		assert cols == a.rows : "a must have as many rows as this columns";
		assert b.rows == rows && b.cols == a.cols : "b dimensions mismatch";

		double sum;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < b.cols; j++) {
				sum = 0;
				for (int k = 0; k < cols; k++) {
					sum += position(i, k) * a.position(k, j);
				}
				b.position(i, j, sum);
			}
		}
	}

	/**
	 * Apply one function over the elements of the matrix and left the result on
	 * a. For each element on this <b>{@code a(i,j) = F(this(i,j))}</b>.
	 * <p>The matrix {@code a} must have the same dimension as {@code this}.</p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param f function to apply.
	 * @param a The matrix to put the result.
	 */
	public void apply(Function f, final Matrix a) {
		assert f != null : "The function must be not null";
		assert a != null : "a must be not null";
		assert rows == a.rows && cols == a.cols :
			   "this & b must have the same dimensions";

		for (int i = 0, n = rows * cols; i < n; i++) {
			a.matrix[i] = f.eval(matrix[i]);
		}
	}

	/**
	 * Apply one function over the elements of the matrix and left on the main
	 * diagonal of a identity matrix 'a'. For each element on this a(i,i) =
	 * F(this(i,j)). The matrix this must be a column matrix. The matrix a must
	 * be created and has the same dimension of this and a. NOTE: Assertions of
	 * the dimensions are made with assert statement. You must enable this on
	 * runtime to be effective.
	 *
	 * @param f function to apply.
	 * @param a The matrix to put the result.
	 */
	public void applyInIdentity(Function f, final Matrix a) {
		assert cols == 1 && rows == a.rows && rows == a.cols;

		for (int i = 0; i < rows; i++)
			a.position(i, i, f.eval(position(i, 0)));
	}

	/**
	 * Fill the matrix with random values between (-1, 1). Alias for
	 * {@code fill(true)}.
	 * @see Matrix#fill(boolean)
	 * @see Matrix#fill(boolean, java.util.Random)
	 */
	public void fill() {
		fill(true);
	}

	/**
	 * Fill the matrix with random values between [0, 1) if {@code signed} is
	 * {@code is false}, and (-1, 1) if {@code true}.
	 *
	 * @param signed {@code false} if all the numbers should be positive,
	 * {@code false} otherwise
	 * @see Matrix#fill(boolean, java.util.Random)
	 */
	public void fill(boolean signed) {
		fill(signed, getDefaultRandom());
	}

	/**
	 * Fill the matrix with random values between [0, 1) if {@code signed} is
	 * {@code is false}, and (-1, 1) if {@code true}.
	 * <p>This method is based only in {@link Random#nextDouble()}, so in
	 * case other intervals are needed the only thing that's needed is a
	 * custom implementation of {@code nextDouble()}, for instance:</p><pre>
	 *
	 *     Random myRand = new Random(){
	 *         public double nextDouble() {
	 *             return super.nextDouble() / 1000.;
	 *         }
	 *     }
	 * </pre>
	 *
	 * @param signed {@code false} if all the numbers should be positive,
	 * {@code false} otherwise
	 * @param r The {@link Random} object used to fill the matrix, if
	 * {@code null} it will fallback to {@link ThreadLocalRandom#current()}
	 */
	public void fill(boolean signed, Random r) {
		for (int i = 0, n = rows * cols; i < n; i++) {
			matrix[i] = r.nextDouble();

			if (signed) {
				matrix[i] *= Math.pow(-1, r.nextInt(2));
			}
		}
	}

	/**
	 * Copy this matrix to another matrix a.
	 * <p>The matrix {@code a} must have the same dimension as {@code this}.</p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a The matrix to put the result.
	 */
	public void copy(final Matrix a) {
		assert a != null : "a must be not null";
		assert rows == a.rows && cols == a.cols :
			   "this & a must have the same dimensions";

		System.arraycopy(matrix, 0, a.matrix, 0, matrix.length);
	}

	/**
	 * Calculate the dot product between this matrix and another. This method
	 * can safely calculate the dot product between row-column, row-row,
	 * column-column matrices.
	 *
	 * @param a The matrix to multiply
	 * @return the scalar of the dot product.
	 * @throws IllegalArgumentException if either {@code this} or {@code a} are
	 * neither row nor column matrices.
	 */
	public double dotProduct(Matrix a) {
		assert a != null : "a must be not null";
		assert rows * cols == a.rows * a.cols : "Mismatched dimensions";

		if (this.cols != 1 && this.rows != 1) {
			String msg = "This must be either a row or a column matrix";
			throw new IllegalArgumentException(msg);
		}

		if (a.cols != 1 && a.rows != 1) {
			String msg = "a must be either a row or a column matrix";
			throw new IllegalArgumentException(msg);
		}

		double acum = 0;

		for (int i = 0; i < cols * rows; i++) {
			acum += matrix[i] * a.matrix[i];
		}

		return acum;
	}

	/**
	 * Transpose this matrix and left the result on a.
	 * <p>The matrix a must be created and has the right dimensions.</p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a The matrix to put the result.
	 */
	public void transpose(final Matrix a) {
		assert rows == a.cols && cols == a.rows :
			   "Matrix a has the wrong dimensions";

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				a.position(j, i, position(i, j));
	}

	/**
	 * Return the transpose of this matrix.
	 *
	 * @return A new matrix with the transpose of {@code this}.
	 */
	public Matrix transpose() {
		Matrix ret = new Matrix(cols, rows);
		transpose(ret);
		return ret;
	}

	/**
	 * Replace one row of values of this matrix.
	 * <p>The number of values of {@code data} must match with the number of
	 * columns of {@code this}.
	 * <p><i>NOTE:</i> Assertions of the dimensions and indexes are made with
	 * {@code assert} statement. You must enable this on runtime to be
	 * effective.</p>
	 *
	 * @param index The index of the row to place the values.
	 * @param data	The values to put in that row.
	 */
	public void setRow(int index, double[] data) {
		assert cols == data.length : "Wrong vector dimension, expected: " + cols;
		assert index >= 0 && index < rows :
			   "index must be in the interval [0, " + rows + ")";

		System.arraycopy(data, 0, matrix, index * cols, data.length);
	}

	/**
	 * Set a value on each position of the matrix.
	 *
	 * @param v The value to put in the matrix.
	 */
	public void setValue(double v) {
		Arrays.fill(matrix, v);
	}

	/**
	 * Return an array with the values of the specified row.
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be
	 * effective.</p>
	 *
	 * @param index The index of the row to return.
	 * @return the array with the values.
	 */
	public double[] getRow(int index) {
		assert index >= 0 && index < rows : String.format(
			  "index must be in the interval [0, %d) current: %d", rows, index);

		final double[] ret = new double[cols];
		System.arraycopy(matrix, index * cols, ret, 0, ret.length);
		return ret;
	}

	/**
	 * Return an array with the values of the specified column.
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param index The index of the column to return.
	 * @return the array with the values.
	 */
	public double[] getCol(int index) {
		assert index >= 0 && index < cols : String.format(
			  "index must be in the interval [0, %d) current: %d", cols, index);

		final double[] ret = new double[rows];
		for (int i = 0; i < rows; i++)
			ret[i] = position(i, index);
		return ret;
	}

	/**
	 * Return the value on the position (i,j).
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param i index of the row
	 * @param j index of the column
	 * @return the value of that position
	 */
	public final double position(int i, int j) {
		assert i >= 0 && i < rows : String.format(
			  "i must be in the interval [0, %d) current: %d", rows, i);
		assert j >= 0 && j < cols : String.format(
			  "j must be in the interval [0, %d) current: %d", cols, j);

		return matrix[(i * cols) + j];
	}

	/**
	 * Set the value v on the position (i,j).
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param i index of the row
	 * @param j index of the column
	 * @param v the value to put into.
	 */
	public final void position(int i, int j, double v) {
		assert i >= 0 && i < rows : String.format(
			  "i must be in the interval [0, %d) current: %d", rows, i);
		assert j >= 0 && j < cols : String.format(
			  "j must be in the interval [0, %d) current: %d", cols, j);

		matrix[(i * cols) + j] = v;
	}

	/**
	 * Subtract the value of this with the value of b, and let the result on
	 * resultSubstract Also, copy the original value of this into resultCopy.
	 *
	 * @param b Matrix to subtract
	 * @param resultSubtract Matrix to hold the result of the subtraction
	 * @param resultCopy Matrix to hold the copy of this.
	 */
	public void subtractAndCopy(Matrix b, Matrix resultSubtract, Matrix resultCopy) {
		for (int i = 0; i < matrix.length; i++) {
			resultSubtract.matrix[i] = matrix[i] - b.matrix[i];
			resultCopy.matrix[i] = matrix[i];
		}
	}

	/**
	 * Return in result the value of (this*a + b) for each This(i,j)
	 *
	 * @param a constant to multiply
	 * @param b matrix to add
	 * @param result Matrix to hold the result of the operation.
	 */
	public void multiplyAndAdd(double a, Matrix b, Matrix result) {
		for (int i = 0; i < matrix.length; i++) {
			result.matrix[i] = matrix[i] * a + b.matrix[i];
		}
	}

	/**
	 * Increments the value of one position by v.
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param i index of the row
	 * @param j index of the column
	 * @param v value to increment.
	 */
	public final void increment(int i, int j, double v) {
		assert i >= 0 && i < rows : String.format(
			  "i must be in the interval [0, %d) current: %d", rows, i);
		assert j >= 0 && j < cols : String.format(
			  "j must be in the interval [0, %d) current: %d", cols, j);

		matrix[(i * cols) + j] += v;
	}

	/**
	 * Scales the value of one position by v.
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param i index of the row
	 * @param j index of the column
	 * @param v value to scale.
	 */
	public final void scale(int i, int j, double v){
		assert i >= 0 && i < rows : String.format(
				"i must be in the interval [0, %d) current: %d", rows, i);
		assert j >= 0 && j < cols : String.format(
				"j must be in the interval [0, %d) current: %d", cols, j);

		matrix[(i * cols) + j] *= v;
	}

	/**
	 * Swaps two rows.
	 * <p><i>NOTE:</i> Assertions of the indexes are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param i1 index of the first row
	 * @param i2 index of the second row
	 */
	public void swap(int i1, int i2) {
		assert i1 >= 0 && i1 < rows : String.format(
			  "i1 must be in the interval [0, %d) current: %d", rows, i1);
		assert i2 >= 0 && i2 < cols : String.format(
			  "i2 must be in the interval [0, %d) current: %d", rows, i2);

		final double[] a = getRow(i1);
		final double[] b = getRow(i2);
		setRow(i1, b);
		setRow(i2, a);
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
	 * Check if two matrix are equals position to position with a precision of
	 * 1e-7. If the dimensions mismatch they aren't equals. If one position
	 * differs by more than 1e-7 then are different.
	 *
	 * @param b1 The matrix to compare
	 * @return <code>true</code> if are equals, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object b1) {
		if (this == b1) {
			return true;
		}
		if (b1 == null || getClass() != b1.getClass()) {
			return false;
		}

		Matrix b = (Matrix) b1;
		if (rows != b.rows || cols != b.cols)
			return false;

		for (int i = 0, n = rows * cols; i < n; i++) {
			if (Math.abs(matrix[i] - b.matrix[i]) > 1.e-7) { //'equals'
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash + Arrays.hashCode(this.matrix);
		hash = 73 * hash + this.rows;
		hash = 73 * hash + this.cols;
		return hash;
	}

	/**
	 * Return the string representation of this matrix. Useful to write on a
	 * file or for debugging.
	 *
	 * @return An string with the values of the matrix.
	 */
	@Override
	public String toString() {
		return toString(8);
	}

	public String toString(int precision){
		StringBuilder str = new StringBuilder();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				str.append(String.format(Locale.US, "%."+precision+"f ", position(i, j)));
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
