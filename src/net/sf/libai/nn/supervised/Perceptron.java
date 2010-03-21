package net.sf.libai.nn.supervised;

import net.sf.libai.nn.*;
import java.io.*;
import java.util.*;

import net.sf.libai.common.*;

/**
 *	Perceptron is the first trainable neural network proposed.
 *	The network is formed by one matrix (Weights) and one vector (Bias).
 *	The output for the network is calculated by O = signum(W * pattern + b).
 *	@author kronenthaler
 */
public class Perceptron extends NeuralNetwork{
	protected Matrix W, b;
    protected int ins,outs;
	
	public Perceptron(){}

	/**
	 *	Constructor.
	 *	@param in Number of inputs for the network = number of elements in the patterns.
	 *	@param out Number of outputs for the network.
	 */
    public Perceptron(int in,int out){
		ins=in;
		outs=out;

		W = new Matrix(outs,ins);
		b = new Matrix(out,1);

		W.fill();
		b.fill();
	}

	/**
	 *	Train the perceptron using the standard update rule: <br/>
	 *	W = W + alpha.e.pattern^t<br/>
	 *	b = b + alpha.e
	 *	@param patterns	The patterns to be learned.
	 *	@param answers The expected answers.
	 *	@param alpha	The learning rate.
	 *	@param epochs	The maximum number of iterations
	 *	@param offset	The first pattern position
	 *	@param length	How many patterns will be used.
	 *	@param minerror The minimal error expected.
	 */
	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort=new int[length]; // [0,length)
		double error=1,prevError=error(patterns,answers,offset,length);
		Matrix Y=new Matrix(outs, 1);
		Matrix E=new Matrix(outs, 1);
		Matrix aux=new Matrix(outs, ins);

		//initialize sort array
		Matrix[] patternsT=new Matrix[length];
		for(int i=0;i<length;i++){
			patternsT[i] = patterns[i + offset].transpose();
			sort[i] = i;
		}

		if(progress != null){
			progress.setMaximum(0);
			progress.setMinimum(-epochs);
			progress.setValue(-epochs);
		}

		while((error=error(patterns,answers,offset,length)) > minerror && epochs-- > 0){
			//if(error > prevError) break; //optional to avoid overtrainning problems
			//shuffle patterns
			shuffle(sort);

			for(int i=0;i<length;i++){
				//F(wx+b)
				simulate(patterns[sort[i]+offset],Y);

				//e=t-y
				answers[sort[i]+offset].subtract(Y,E);	//error

				//alpha*e.p^t
				E.multiply(alpha,E);
				E.multiply(patternsT[sort[i]],aux);

				W.add(aux,W);//W+(alpha*e.p^t)
				b.add(E,b);  //b+(alpha*e)
			}
			
			prevError=error;
			if(plotter!=null) plotter.setError(epochs, error);
			if(progress!=null) progress.setValue(-epochs);
		}
		if(progress!=null) progress.setValue(1);
	}

	@Override
	public Matrix simulate(Matrix p) {
		Matrix Y=new Matrix(outs, 1);
		simulate(p,Y);
		return Y;
	}

	/**
	 *	Calculate the output for the pattern and left the result on result.
	 *	result = signum(W * pattern + b)
	 *	@param pattern The input pattern
	 *	@param result The output result.
	 */
	@Override
	public void simulate(Matrix pattern, Matrix result) {
		W.multiply(pattern,result);		//inner product
		result.add(b,result);			//bias
		result.apply(signum,result);	//thresholding
	}

	public static Perceptron open(String path) {
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
			Perceptron p = (Perceptron)in.readObject();
			in.close();
			return p;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}