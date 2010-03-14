package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public interface Kernel {
	/**
	 *	Evaluates a kernel between the two Vectors .
	 *	@param A The first pattern to calculate the kernel
	 *	@param B The second pattern to calculate the kernel
	 *	@return A double with the value of the calculated kernel
	 */
	public double eval(Matrix A, Matrix B);

	public double eval(double dotProduct);
}