package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 *	Implementation of the SVM using the SMO algorithm. Based on the original implementation of:<br/>
 *	X. Jiang and H. Yu. SVM-JAVA: A Java implementation of the SMO (Sequential Minimal Optimization) for training SVM.<br/>
 *	Department of Computer Science and Engineering, Pohang University of Science and Technology (POSTECH), http://iis.hwanjoyu.org/svm-java, 2008.
 *	The code was adapted to the data structures and architecture of the libai. Some little optimization was made.
 *	@author kronenthaler
 */
public class SVM extends NeuralNetwork{
	private Kernel kernel = new GaussianKernel(2.0);	//the default kernel type is a gaussian function
	
	private Matrix[] densePoints;			//array with the patterns lo learn or learned...s
	private int target[];					//expected answers
	private Matrix precomputedDots;

	private double[] alph;					//Lagrange's multipliers
	private double b = 0;					//threshold
	private int nSupportVectors = -1;		//last index of the support vector.
	private double[] errorCache;			//stores the errors to reduce calculations.
	private double deltaB;
	private Random randGenerator = new Random(0);
	
	//trainning params.
	private double minerror;				//set in the trainning method.
	private double C = 0.05;
	private double epsilon = 0.01;//0.001;

	//static defs.
	public static final int PARAM_C = 0;
	public static final int PARAM_EPSILON = 1;

	public SVM(){
	}

	public SVM(Kernel _kernel){
		kernel = _kernel;
	}

	public void setTrainingParam(int param, double paramValue){
		if(param == PARAM_C)			C = paramValue;
		else if(param == PARAM_EPSILON) epsilon = paramValue;
		//other params...
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		if(progress != null){
			progress.setMaximum(0);
			progress.setMinimum(-epochs);
			progress.setValue(-epochs);
		}

		int d = patterns[offset].getRows();
		this.minerror = minerror;
		
		densePoints = new Matrix[length];
		for(int i=0;i<length;i++){
			densePoints[i]=(patterns[i+offset]);
		}

		target = new int[length];
		for(int i=offset;i<offset+length;i++)
			target[i-offset] = (int)NeuralNetwork.ssignum.eval(answers[i].position(0, 0));

		precomputedDots = new Matrix(length,length);
		for(int i=0;i<length-1;i++){
			for(int j=i;j<length;j++){
				precomputedDots.position(i,j, densePoints[i].dotProduct(densePoints[j]));
				precomputedDots.position(j,i, precomputedDots.position(i,j));
			}
		}

		nSupportVectors = length;
		b = 0;

		alph = new double[nSupportVectors];
		errorCache = new double[nSupportVectors];
		
		int numChanged = 0;
		boolean examineAll = true;
		while (epochs-- > 0 && (numChanged > 0 || examineAll)) {
			System.out.println(epochs);

			numChanged = 0;
			if (examineAll) {
				for (int k = 0; k < length; k++) {
					numChanged += examineExample(k);
				}
				examineAll = false;
			} else {
				for (int k = 0; k < length; k++) {
					if (alph[k] != 0 && alph[k] != C) {
						numChanged += examineExample(k);
					}
				}
				if (numChanged == 0)
					examineAll = true;
			}

			if(plotter!=null) plotter.setError(epochs, error(patterns, answers, offset, length));
			if(progress!=null) progress.setValue(-epochs);

			/*int nonBoundSupport = 0;
			int boundSupport = 0;
			for (int i = 0; i < nSupportVectors; i++) {
				if (alph[i] > 0) {
					if (alph[i] < C) {
						nonBoundSupport++;
					} else {
						boundSupport++;
					}
				}
			}
			System.out.println("non_bound= " + nonBoundSupport + "\t" + "bound_support= " + boundSupport);*/
		}
		//clip data to keep just the support vectors.
		int tempnSupportVectors = 0;
		for(int i=0;i<alph.length;i++)
			if(alph[i]>0)
				tempnSupportVectors++;

		double tempAlph[] = new double[tempnSupportVectors];
		int[] tempTarget = new int[tempnSupportVectors];
		Matrix[] tempDensePoints = new Matrix[tempnSupportVectors];//(tempnSupportVectors,d);
		
		for(int i=0,j=0,n=alph.length;j<n;j++){
			if(alph[j] > 0.0){
				tempTarget[i] = target[j];
				tempDensePoints[i] = densePoints[j];
				tempAlph[i] = alph[j];
				i++;
			}
		}

		alph = tempAlph;
		densePoints = tempDensePoints;
		target = tempTarget;
		nSupportVectors = tempnSupportVectors;
		
		if(progress!=null) progress.setValue(1);
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix temp = new Matrix(1,1);//siempre devuelve 1 sola clase
		simulate(pattern,temp);
		return temp;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		result.position(0,0,NeuralNetwork.ssignum.eval(learnedFunc(pattern)));
	}

	@Override
	public boolean save(String path) {
		//guardar los multiplicadores, el b y los support vectors.
		try{
			PrintStream os = new PrintStream(new FileOutputStream(path));

			os.println(densePoints[0].getRows());
			os.println(b);
			
			int n_support_vectors = 0;
			for (int i = 0; i < nSupportVectors; i++) {
				if (alph[i] > 0) {
					n_support_vectors++;
				}
			}
			os.println(n_support_vectors);

			for (int i = 0; i < nSupportVectors; i++) {
				if (alph[i] > 0) {
					os.println(alph[i]);
				}
			}

			for (int i = 0; i < nSupportVectors; i++) {
				if (alph[i] > 0) {
					for (int j = 0; j < densePoints[0].getRows(); j++) {
						os.print(densePoints[i].position(j,0));
						os.print(" ");
					}
					os.println(target[i]);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean open(String path) {
		try {
			Scanner in = new Scanner(new FileInputStream(path));

			int d = in.nextInt();
			b = in.nextDouble(); //os.println(b);
			
			nSupportVectors = in.nextInt(); //os.println(n_support_vectors);
			alph = new double[nSupportVectors];

			for (int i = 0; i < nSupportVectors; i++) {
				alph[i]=in.nextDouble();//os.println(alph.get(i));
			}

			densePoints = new Matrix[nSupportVectors];
			target = new int[nSupportVectors];
			for (int i = 0; i < nSupportVectors; i++) {
				Matrix m = new Matrix(d,1);
				for (int j = 0; j < d; j++){
					m.position(j,0, in.nextDouble());
				}
				densePoints[i]=m;
				
				target[i] = in.nextInt();//os.println(target[i]);
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		int error=0;
		for(int i=offset;i<offset+length;i++){
			if(simulate(patterns[i]).position(0,0)*answers[i].position(0,0) < 0)
				error++;
		}
		return error / (double)length;
	}

	private int examineExample(int i1) {
		double y1 = 0, alph1 = 0, E1 = 0, r1 = 0;
		y1 = target[i1];
		alph1 = alph[i1];

		if (alph1 > 0 && alph1 < C) {
			E1 = errorCache[i1];
		} else {
			E1 = learnedFunc(i1) - y1;
		}

		r1 = y1 * E1;
		if ((r1 < -minerror && alph1 < C) || (r1 > minerror && alph1 > 0)) {

			int k = 0, i2 = 0, k0 = 0;
			double tmax = 0;
			double rands = 0;

			for (i2 = (-1), tmax = 0, k = 0; k < nSupportVectors; k++) {
				if (alph[k] > 0 && alph[k] < C) {
					double E2 = 0, temp = 0;

					E2 = errorCache[k];
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
			for (rands = randGenerator.nextDouble(), k0 = (int) (rands * nSupportVectors), k = k0; k < nSupportVectors + k0; k++) {
				i2 = k % nSupportVectors;
				if (alph[i2] > 0 && alph[i2] < C) {
					if (takeStep(i1, i2) == 1) {
						return 1;
					}
				}
			}

			rands = 0;
			for (rands = randGenerator.nextDouble(), k0 = (int) (rands * nSupportVectors), k = k0; k < nSupportVectors + k0; k++) {
				i2 = k % nSupportVectors;
				if (takeStep(i1, i2) == 1) {
					return 1;
				}
			}

		}
		return 0;
	}

	private int takeStep(int i1, int i2) {
		if (i1 == i2) return 0;
		
		int y1 = 0, y2 = 0, s = 0;
		double alph1 = 0, alph2 = 0; /* old_values of alpha_1, alpha_2 */
		double a1 = 0, a2 = 0;       /* new values of alpha_1, alpha_2 */
		double E1 = 0, E2 = 0, L = 0, H = 0, k11 = 0, k22 = 0, k12 = 0, eta = 0, Lobj = 0, Hobj = 0;

		alph1 = alph[i1];
		y1 = target[i1];
		if (alph1 > 0 && alph1 < C) {
			E1 = errorCache[i1];
		} else {
			E1 = learnedFunc(i1) - y1;
		}

		alph2 = alph[i2];
		y2 = target[i2];
		if (alph2 > 0 && alph2 < C) {
			E2 = errorCache[i2];
		} else {
			E2 = learnedFunc(i2) - y2;
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

		if (L == H) return 0;
		
		k11 = kernel.eval(precomputedDots.position(i1,i1)/*densePoints[i1], densePoints[i1]*/);
		k12 = kernel.eval(precomputedDots.position(i1,i2)/*densePoints[i1], densePoints[i2]*/);
		k22 = kernel.eval(precomputedDots.position(i2,i2)/*densePoints[i2], densePoints[i2]*/);
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

		if (Math.abs(a2 - alph2) < epsilon * (a2 + alph2 + epsilon))
			return 0;


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

		double t1 = y1 * (a1 - alph1);
		double t2 = y2 * (a2 - alph2);

		for (int i = 0; i < nSupportVectors; i++) {
			if (0 < alph[i] && alph[i] < C) {
				double tmp = errorCache[i];
				tmp += (t1 * kernel.eval(precomputedDots.position(i1,i)/*densePoints[i1], densePoints[i]*/)) +
					   (t2 * kernel.eval(precomputedDots.position(i2,i)/*densePoints[i2], densePoints[i]*/)) -
					   deltaB;
				errorCache[i]= tmp;
			}
		}
		errorCache[i1] = 0.0;
		errorCache[i2] = 0.0;

		alph[i1] = a1;
		alph[i2] = a2;

		return 1;
	}

	//to the simulation.
	private double learnedFunc(Matrix k) {
		double s = 0;
		for (int i = 0; i < nSupportVectors; i++) {
			if (alph[i] > 0) {
				s += alph[i] * target[i] * kernel.eval(densePoints[i], k);
			}
		}
		s -= b;
		return s;
	}

	//to optimize the training.
	private double learnedFunc(int k){
		double s = 0;
		for (int i = 0; i < nSupportVectors; i++) {
			if (alph[i] > 0) {
				s += alph[i] * target[i] * kernel.eval(precomputedDots.position(i,k)/*densePoints[i], k*/);
			}
		}
		s -= b;
		return s;
	}

	/*public static void main(String arg[]){
		try{
			ArrayList<Matrix> patterns = new ArrayList<Matrix>();
			ArrayList<Matrix> answers = new ArrayList<Matrix>();
			Random r = new Random(0);
			
			BufferedReader in = new BufferedReader(new FileReader("iris2.data"));
			//BufferedReader in = new BufferedReader(new FileReader("magic2.data"));
			//BufferedReader in = new BufferedReader(new FileReader("/home/kronenthaler/temp/SVM/tic-tac-toe"));
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
		
			System.out.println(patterns.size());

			//Kernel kernel = new SigmoidalKernel(0.75,5);
			Kernel kernel = new LinearKernel();
			SVM svm = new SVM(kernel);
			Matrix []p = patterns.toArray(new Matrix[0]);
			Matrix []t = answers.toArray(new Matrix[0]);
			svm.train(p, t, 0, 1000);

			System.out.println("error: "+svm.error(p, t));

			if(svm.save("testtt.svm")){
				SVM svm1 = new SVM(kernel);
				svm1.open("testtt.svm");
				System.out.println("error after reopen: "+svm1.error(p, t)+" "+svm.error(p,t));
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
}