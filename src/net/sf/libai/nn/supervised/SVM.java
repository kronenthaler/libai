package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 *
 * @author kronenthaler
 */
public class SVM extends NeuralNetwork{
	private Kernel kernel = new GaussianKernel();	//the default kernel type is a gaussian function
	private double[] kernelParams;			//kernel extra parameters

	private Matrix precomputedDotProduct;	//matrix with the dot products precomputed between the patterns
	private Matrix densePoints;
	private int target[];					//expected answers
	
	private ArrayList<Double> alph;			//Lagrange's multipliers
	private double b = 0;					//threshold
	private int N = 0;						//amount of vectors
	private int d = -1;						//dimension of the vectors
	private int firstTestIndex = 0;			//first index to test
	private int endSupportIndex = -1;		//last index of the support vector.
	private boolean isLinearKernel;
	private ArrayList<Double> errorCache;	//stores the errors to reduce calculations.
	private ArrayList<Double> w;			//just used by the linear kernel
	private int learned_func_flag;
	private double deltaB;
	private Random randGenerator = new Random(0);

	//trainning params.
	private double minerror;				//set in the trainning method.
	private double C = 0.05;
	private double epsilon = 0.001;

	//static defs.
	public static final int PARAM_C = 0;
	public static final int PARAM_EPSILON = 1;

	public SVM(){
	}

	public SVM(Kernel _kernel){
		kernel = _kernel;
	}

	public void setKernelParams(double... params){
		this.kernelParams = params;
	}

	public void setTrainingParam(int param,double paramValue){
		if(param == PARAM_C)			C = paramValue;
		else if(param == PARAM_EPSILON) epsilon = paramValue;
		//other params...
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		N = length;
		d = patterns[offset].getRows();
		this.minerror = minerror;
		isLinearKernel = kernel instanceof LinearKernel;
		
		precomputedDotProduct = new Matrix(N,N);
		for(int i=offset;i<offset+length-1;i++){
			for(int j=i;j<offset+length;j++){
				precomputedDotProduct.position(i-offset, j-offset, patterns[i].dotProduct(patterns[j]));
				precomputedDotProduct.position(j-offset, i-offset, precomputedDotProduct.position(i-offset,j-offset));
			}
		}

		densePoints = new Matrix(N,d);
		for(int i=offset;i<offset+length;i++){
			for(int j=0;j<d;j++){
				densePoints.position(i-offset,j,patterns[i].position(j, 0));
			}
		}

		target = new int[N];
		for(int i=offset;i<offset+length;i++)
			target[i-offset] = (int)NeuralNetwork.ssignum.eval(answers[i].position(0, 0));

		firstTestIndex = 0;
		endSupportIndex = N;

		alph = new ArrayList<Double>(endSupportIndex);
		for(int i=0;i<endSupportIndex;i++)
			alph.add(0.0);

		errorCache = new ArrayList<Double>(N);
		for(int i=0;i<N;i++)
			errorCache.add(0.0);

		b = 0;
		if (isLinearKernel) {
			w = new ArrayList<Double>(d);
			for(int i=0;i<d;i++)
				w.add(0.0);
		}

		learned_func_flag = isLinearKernel?3:4;

		int numChanged = 0;
		boolean examineAll = true;
		while (numChanged > 0 || examineAll) {
			numChanged = 0;
			if (examineAll) {
				for (int k = 0; k < N; k++) {
					numChanged += examineExample(k);
				}
			} else {
				for (int k = 0; k < N; k++) {
					if (alph.get(k) != 0 && alph.get(k) != C) {
						numChanged += examineExample(k);
					}
				}
			}
			if (examineAll) {
				examineAll = false;
			} else if (numChanged == 0) {
				examineAll = true;
			}

			int nonBoundSupport = 0;
			int boundSupport = 0;
			for (int i = 0; i < N; i++) {
				if (alph.get(i) > 0) {
					if (alph.get(i) < C) {
						nonBoundSupport++;
					} else {
						boundSupport++;
					}
				}
			}
			System.out.println("non_bound= " + nonBoundSupport + "\t" + "bound_support= " + boundSupport);
		}

		System.out.println("Error_rate=" + errorRate());
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix temp = new Matrix(1,1);//siempre devuelve 1 sola clase
		simulate(pattern,temp);
		return temp;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean save(String path) {
		//guardar los multiplicadores, el b y los support vectors.
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean open(String path) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	private int examineExample(int i1) {
		double y1 = 0, alph1 = 0, E1 = 0, r1 = 0;
		y1 = target[i1];
		alph1 = alph.get(i1);

		if (alph1 > 0 && alph1 < C) {
			E1 = errorCache.get(i1);
		} else {
			E1 = learnedFunc(i1, learned_func_flag) - y1;
		}

		r1 = y1 * E1;
		if ((r1 < -minerror && alph1 < C) || (r1 > minerror && alph1 > 0)) {

			int k = 0, i2 = 0, k0 = 0;
			double tmax = 0;
			double rands = 0;

			for (i2 = (-1), tmax = 0, k = 0; k < endSupportIndex; k++) {
				if (alph.get(k) > 0 && alph.get(k) < C) {
					double E2 = 0, temp = 0;

					E2 = errorCache.get(k);
					temp = Math.abs(E1 - E2);
					if (temp > tmax) {
						tmax = temp;
						i2 = k;

					}
				}
			}

			if (i2 >= 0) {
				if (takeStep(i1, i2) == 1) {
					return 1;
				}
			}

			k = k0 = i2 = 0;
			for (rands = randGenerator.nextDouble(), k0 = (int) (rands * endSupportIndex), k = k0; k < endSupportIndex + k0; k++) {
				i2 = k % endSupportIndex;
				if (alph.get(i2) > 0 && alph.get(i2) < C) {
					if (takeStep(i1, i2) == 1) {
						return 1;
					}
				}
			}

			rands = 0;
			for (rands = randGenerator.nextDouble(), k0 = (int) (rands * endSupportIndex), k = k0; k < endSupportIndex + k0; k++) {
				i2 = k % endSupportIndex;
				if (takeStep(i1, i2) == 1) {
					return 1;
				}
			}

		}
		return 0;
	}

	private int takeStep(int i1, int i2) {
		if (i1 == i2) {
			return 0;
		}

		int y1 = 0, y2 = 0, s = 0;
		double alph1 = 0, alph2 = 0; /* old_values of alpha_1, alpha_2 */
		double a1 = 0, a2 = 0;       /* new values of alpha_1, alpha_2 */
		double E1 = 0, E2 = 0, L = 0, H = 0, k11 = 0, k22 = 0, k12 = 0, eta = 0, Lobj = 0, Hobj = 0;

		alph1 = alph.get(i1);
		y1 = target[i1];
		if (alph1 > 0 && alph1 < C) {
			E1 = errorCache.get(i1);
		} else {
			E1 = learnedFunc(i1, learned_func_flag) - y1;
		}

		alph2 = alph.get(i2);
		y2 = target[i2];
		if (alph2 > 0 && alph2 < C) {
			E2 = errorCache.get(i2);
		} else {
			E2 = learnedFunc(i2, learned_func_flag) - y2;
		}

		s = y1 * y2;

		if (y1 == y2) {
			double gamma = alph1 + alph2;
			if (gamma > C) {
				L = gamma - C;
				H = C;
			} else {
				L = 0;
				H = gamma;
			}
		} else {
			double gamma = alph1 - alph2;
			if (gamma > 0) {
				L = 0;
				H = C - gamma;
			} else {
				L = -gamma;
				H = C;
			}
		}

		if (L == H) {
			return 0;
		}

		k11 = kernel.eval(i1, i1, precomputedDotProduct,kernelParams);
		k12 = kernel.eval(i1, i2, precomputedDotProduct,kernelParams);
		k22 = kernel.eval(i2, i2, precomputedDotProduct,kernelParams);
		eta = 2 * k12 - k11 - k22;

		if (eta < 0) {
			a2 = alph2 + y2 * (E2 - E1) / eta;
			if (a2 < L) {
				a2 = L;
			} else if (a2 > H) {
				a2 = H;
			}
		} else {
			double c1 = eta / 2;
			double c2 = y2 * (E1 - E2) - eta * alph2;
			Lobj = c1 * L * L + c2 * L;
			Hobj = c1 * H * H + c2 * H;

			if (Lobj > Hobj + epsilon) {
				a2 = L;
			} else if (Lobj < Hobj - epsilon) {
				a2 = H;
			} else {
				a2 = alph2;
			}
		}

		if (Math.abs(a2 - alph2) < epsilon * (a2 + alph2 + epsilon)) {
			return 0;
		}

		a1 = alph1 - s * (a2 - alph2);
		if (a1 < 0) {
			a2 += s * a1;
			a1 = 0;
		} else if (a1 > C) {
			double t = a1 - C;
			a2 += s * t;
			a1 = C;
		}

		double b1 = 0, b2 = 0, bnew = 0;

		if (a1 > 0 && a1 < C) {
			bnew = b + E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
		} else {
			if (a2 > 0 && a2 < C) {
				bnew = b + E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;
			} else {
				b1 = b + E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
				b2 = b + E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;
				bnew = (b1 + b2) / 2;
			}
		}

		deltaB = bnew - b;
		b = bnew;

		if (isLinearKernel) {
			double t1 = y1 * (a1 - alph1);
			double t2 = y2 * (a2 - alph2);
			for (int i = 0; i < d; i++) {
				double temp = densePoints.position(i1,i) * t1 + densePoints.position(i2,i) * t2;
				double temp1 = w.get(i);
				w.set(i, temp + temp1);
			}
		}

		double t1 = y1 * (a1 - alph1);
		double t2 = y2 * (a2 - alph2);

		for (int i = 0; i < endSupportIndex; i++) {
			if (0 < alph.get(i) && alph.get(i) < C) {
				double tmp = errorCache.get(i);
				tmp += t1 * kernel.eval(i1, i, precomputedDotProduct, kernelParams) + t2 * kernel.eval(i2, i, precomputedDotProduct, kernelParams)
						- deltaB;
				errorCache.set(i, tmp);
			}
		}
		errorCache.set(i1, 0.0);
		errorCache.set(i2, 0.0);

		alph.set(i1, a1);
		alph.set(i2, a2);

		return 1;
	}

	private double learnedFunc(int i, int flag) {
		double result = 0;
		if (flag == 3) {
			result = learnedFuncLinearDense(i);
		} else if (flag == 4) {
			result = learnedFuncNonlinear(i);
		}
		return result;
	}

	private double learnedFuncLinearDense(int k) {
		double s = 0;
		for (int i = 0; i < d; i++) {
			s += w.get(i) * densePoints.position(k,i);
		}
		s -= b;
		return s;
	}

	private double learnedFuncNonlinear(int k) {
		double s = 0;
		for (int i = 0; i < endSupportIndex; i++) {
			if (alph.get(i) > 0) {
				s += alph.get(i) * target[i] * kernel.eval(i, k, precomputedDotProduct, kernelParams);
			}
		}
		s -= b;
		return s;
	}

	private double errorRate() {
		int n_total = 0;
		int n_error = 0;
		for (int i = firstTestIndex; i < N; i++) {
			if ((learnedFunc(i, learned_func_flag) > 0) != (target[i] > 0)) {
				n_error++;
			}
			n_total++;
		}
		return n_error / (double)n_total;
	}

	public static void main(String arg[]){
		try{
			BufferedReader in = new BufferedReader(new FileReader("/home/kronenthaler/temp/SVM/tic-tac-toe"));
			ArrayList<Matrix> patterns = new ArrayList<Matrix>();
			ArrayList<Matrix> answers = new ArrayList<Matrix>();
			while(true){
				String line = in.readLine();
				if(line == null) break;

				String toks[] = line.split(" ");
				Matrix p = new Matrix(toks.length-1,1);
				for(int i=0;i<toks.length-1;i++)
					p.position(i,0,Double.parseDouble(toks[i]));

				Matrix t = new Matrix(1,1);
				t.position(0,0,Double.parseDouble(toks[toks.length-1]));

				patterns.add(p);
				answers.add(t);
			}

			SVM svm = new SVM();
			svm.setKernelParams(2.0);
			svm.train(patterns.toArray(new Matrix[0]), answers.toArray(new Matrix[0]), 0, 0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}