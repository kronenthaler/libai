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
package libai.nn.supervised;

import libai.common.Pair;
import libai.common.Precondition;
import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.common.functions.SymmetricSign;
import libai.common.kernels.Kernel;

import java.util.Random;

/**
 * Implementation of the SVM using the SMO algorithm. Based on the original
 * implementation of:<br>
 * X. Jiang and H. Yu. SVM-JAVA: A Java implementation of the SMO (Sequential
 * Minimal Optimization) for training SVM.<br>
 * Department of Computer Science and Engineering, Pohang University of Science
 * and Technology (POSTECH), http://iis.hwanjoyu.org/svm-java, 2008. The code
 * was adapted to the data structures and architecture of the libai. Some little
 * optimization was made.
 *
 * @author kronenthaler
 */
public class SVM extends SupervisedLearning {
	private static final long serialVersionUID = 5875835056527034341L;

	//static defs.
	public static final int PARAM_C = 0;
	public static final int PARAM_EPSILON = 1;
	public static final int PARAM_TOLERANCE = 2;

	protected static SymmetricSign ssign = new SymmetricSign();

	// learning constants
	private transient double C = 0.05;
	private transient double tolerance = 0.001;
	private transient double epsilon = 0.01;

	// state of the neural network
	private Kernel kernel;
	private double[] lambda; /* Lagrange multipliers */
	private double b = 0; /* threshold */
	private int[] target; // answers, need to be learned too.
	private Matrix densePoints[]; // equivalent to W or matrix of prototype vectors

	public SVM(Kernel _kernel) {
		this(_kernel, getDefaultRandomGenerator());
	}

	public SVM(Kernel _kernel, Random rand) {
		super(rand);
		kernel = _kernel;
	}

	public void setTrainingParam(int param, double paramValue) {
		if (param == PARAM_C)
			C = paramValue;
		else if (param == PARAM_EPSILON)
			epsilon = paramValue;
		else if (param == PARAM_TOLERANCE)
			tolerance = paramValue;
		//other params...
	}

	@Override
	protected void validatePreconditions(Column[] patterns, Column[] answers, int epochs, int offset, int length, double minerror) {
		super.validatePreconditions(patterns, answers, epochs, offset, length, minerror);
		Precondition.check(answers[0].getRows() == 1, "Answers can only be one-dimensional elements but %d-dimensions found", answers[0].getRows());
	}

	@Override
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		validatePreconditions(patterns, answers, epochs, offset, length, minerror);

		initializeProgressBar(epochs);

		b = 0;
		lambda = new double[length];
		densePoints = new Matrix[length];
		target = new int[length];

		for (int i = 0; i < target.length; i++) {
			densePoints[i] = new Matrix(patterns[i + offset]); //copy
			target[i] = (int) ssign.eval(answers[i + offset].position(0, 0));
		}

		// pre-calculate the kernel values
		double[] errorCache = new double[length];
		double[][] precomputeKernels = precomputeKernels();

		boolean changed = false;
		boolean examineAll = true;
		for (int currentEpoch = 0; currentEpoch < epochs && (changed || examineAll); currentEpoch++) {
			changed = false;
			for (int k = 0; k < length; k++) {
				if (examineAll || (lambda[k] != 0 && lambda[k] != C))
					changed |= examineExample(k, precomputeKernels, errorCache);
			}
			examineAll = !examineAll && !changed;

			if (plotter != null)
				plotter.setError(epochs, error(patterns, answers, offset, length));
			if (progress != null)
				progress.setValue(currentEpoch);
		}

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}


	@Override
	public Column simulate(Column pattern) {
		Column temp = new Column(1); //always returns a single class
		simulate(pattern, temp);
		return temp;
	}

	@Override
	public void simulate(Column pattern, Column result) {
		result.position(0, 0, ssign.eval(learnedFunction(pattern)));
	}

	@Override
	public double error(Column[] patterns, Column[] answers, int offset, int length) {
		Precondition.check(patterns.length == answers.length, "There must be the same amount of patterns and answers");
		Precondition.check(offset >= 0 && offset < patterns.length, "offset must be in the interval [0, %d), found,  %d", patterns.length, offset);
		Precondition.check(length >= 0 && length <= patterns.length - offset, "length must be in the interval (0, %d], found,  %d", patterns.length - offset, length);

		int error = 0;
		for (int i = 0; i < length; i++) {
			if (simulate(patterns[i + offset]).position(0, 0) * answers[i + offset].position(0, 0) < 0)
				error++;
		}
		return error / (double) length;
	}

	// internal methods
	private double[][] precomputeKernels() {
		double[][] precomputed_kernels = new double[densePoints.length][densePoints.length];
		for (int i = 0; i < precomputed_kernels.length; i++) {
			for (int j = 0; j < precomputed_kernels.length; j++) {
				precomputed_kernels[i][j] = kernel.eval(densePoints[i], densePoints[j]);
			}
		}
		return precomputed_kernels;
	}

	private int findMaxDifference(double E1, double[] errorCache) {
		int i2 = -1;
		double tmax = 0;
		for (int k = 0; k < errorCache.length; k++) {
			if (!(0 < lambda[k] && lambda[k] < C)) {
				continue;
			}

			double E2 = errorCache[k];
			double temp = Math.abs(E1 - E2);
			if (temp > tmax) {
				tmax = temp;
				i2 = k;
			}
		}
		return i2;
	}

	private boolean examineExample(int i1, double[][] precomputedKernels, double[] errorCache) {
		int y1 = target[i1];
		double alph1 = lambda[i1];
		double E1 = partialError(i1, y1, alph1, errorCache);

		double r1 = y1 * E1;
		if (!(r1 < -tolerance && alph1 < C) && !(r1 > tolerance && alph1 > 0)) {
			return false;
		}

		int i2 = findMaxDifference(E1, errorCache);
		if (i2 >= 0 && takeStep(i1, i2, precomputedKernels, errorCache)) {
			return true;
		}

		//first search if it's possible to take a step within the multipliers
		int k = random.nextInt(lambda.length);
		for (int t = 0; t < lambda.length; k = (k + 1) % lambda.length, t++) {
			if (0 < lambda[k] && lambda[k] < C && takeStep(i1, k, precomputedKernels, errorCache)) {
				return true;
			}
		}

		// if no step is not possible within the multiplier take it from wherever possible.
		k = random.nextInt(lambda.length);
		for (int t = 0; t < lambda.length; k = (k + 1) % lambda.length, t++) {
			if (takeStep(i1, k, precomputedKernels, errorCache)) {
				return true;
			}
		}

		return false;
	}

	private double partialError(int i, int y, double lambda, double[] errorCache) {
		if (0 < lambda && lambda < C)
			return errorCache[i];
		return learnedFunction(densePoints[i]) - y;
	}

	private boolean takeStep(int i1, int i2, double[][] precomputedKernels, double[] errorCache) {
		if (i1 == i2)
			return false;

		double lambda1 = lambda[i1];
		int y1 = target[i1];
		double E1 = partialError(i1, y1, lambda1, errorCache);

		double lambda2 = lambda[i2];
		int y2 = target[i2];
		double E2 = partialError(i2, y2, lambda2, errorCache);

		Pair<Double, Double> range = getRange(y1, lambda1, y2, lambda2);
		double L = range.first;
		double H = range.second;

		if (L == H) {
			return false;
		}

		double k11 = precomputedKernels[i1][i1];
		double k12 = precomputedKernels[i1][i2];
		double k22 = precomputedKernels[i2][i2];
		double eta = 2 * k12 - k11 - k22;

		double l1 = 0;
		double l2 = 0; /* new values of lambda1, lambda2 */
		if (eta < 0) {
			l2 = lambda2 + y2 * (E2 - E1) / eta;
			if (l2 < L)
				l2 = L;
			else if (l2 > H)
				l2 = H;
		} else {
			double c1 = eta / 2;
			double c2 = y2 * (E1 - E2) - eta * lambda2;
			double Lobj = c1 * L * L + c2 * L;
			double Hobj = c1 * H * H + c2 * H;

			l2 = lambda2;
			if (Lobj > Hobj + epsilon)
				l2 = L;
			else if (Lobj < Hobj - epsilon)
				l2 = H;
		}

		if (Math.abs(l2 - lambda2) < epsilon * (l2 + lambda2 + epsilon))
			return false;

		double s = y1 * y2;
		l1 = lambda1 - s * (l2 - lambda2);
		if (l1 < 0) {
			l2 += s * l1;
			l1 = 0;
		} else if (l1 > C) {
			l2 += s * (l1 - C);
			l1 = C;
		}

		// update threshold and multipliers
		b += getDeltaB(precomputedKernels, errorCache, i1, lambda1, E1, l1, i2, lambda2, E2, l2);
		lambda[i1] = l1;
		lambda[i2] = l2;

		return true;
	}

	private Pair<Double, Double> getRange(double y1, double lambda1, double y2, double lambda2) {
		double L = 0;
		double H = 0;
		if (y1 == y2) {
			H = lambda1 + lambda2;
			L = 0;
			if (H > C) {
				L = H - C;
				H = C;
			}
		} else {
			L = lambda2 - lambda1;
			H = C;
			if (-L > 0) {
				L = 0;
				H = C + L;
			}
		}
		return new Pair<>(L, H);
	}

	private double getDeltaB(double[][] precomputedKernels, double[] errorCache,
							 int i1, double lambda1, double e1, double l1,
							 int i2, double lambda2, double e2, double l2) {
		int y1 = target[i1];
		int y2 = target[i2];

		double k11 = precomputedKernels[i1][i1];
		double k12 = precomputedKernels[i1][i2];
		double k22 = precomputedKernels[i2][i2];

		double deltaB = 0;
		double t1 = y1 * (l1 - lambda1);
		double t2 = y2 * (l2 - lambda2);
		double b1 = e1 + t1 * k11 + t2 * k12;
		double b2 = e2 + t1 * k12 + t2 * k22;

		if (l1 > 0 && l1 < C)
			deltaB = b1;
		else if (l2 > 0 && l2 < C)
			deltaB = b2;
		else
			deltaB = (b1 + b2) / 2;

		// update error cache
		for (int i = 0; i < lambda.length; i++) {
			if (0 < lambda[i] && lambda[i] < C) {
				double tmp = errorCache[i];
				tmp += (t1 * precomputedKernels[i1][i]) + (t2 * precomputedKernels[i2][i]) - deltaB;
				errorCache[i] = tmp;
			}
		}
		errorCache[i1] = 0f;
		errorCache[i2] = 0f;

		return deltaB;
	}

	private double learnedFunction(Matrix pattern) {
		double s = 0;
		for (int i = 0; i < lambda.length; i++) {
			if (lambda[i] > 0) {
				s += lambda[i] * target[i] * kernel.eval(densePoints[i], pattern);
			}
		}
		s -= b;
		return s;
	}
}
