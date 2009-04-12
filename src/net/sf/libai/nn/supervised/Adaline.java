package net.sf.libai.nn.supervised;

import net.sf.libai.common.Matrix;

/**
 *
 * @author kronenthaler
 */
public class Adaline extends Perceptron{
	
	public Adaline(){}

	public Adaline(int ins, int outs){
		super(ins, outs);
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		super.train(patterns, answers, 2*alpha, epochs, offset, length, minerror);
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		W.multiply(pattern,result);	//inner product
		result.add(b,result);		//bias
	}
}
