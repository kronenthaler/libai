package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.unsupervised.Competitive;

/**
 *	Learning Vector Quantization or LVQ. Is an hybrid neural network with 3 layers
 *	(1-input, 1-hidden, 1-output). The first set of weights are trainned using a competitive aproach,
 *	and the second set of weights are trainned using a supervised aproach. Therefore the first steps
 *	are taken from the competitive network.
 *	This network was proposed by Teuvo Kohonen as alternative to the standard competitive.
 *	networks.
 *	@author kronenthaler
 */
public class LVQ extends Competitive{
	protected Matrix W2;
	protected int subclasses;

	/**
	 *	Constructor. Number of inputs, number of subclasses and number of outputs.
	 *	@param in Number of input to the network.
	 *	@param subclasses	Number of subclases for output class. Greater subdivision provides better clasification.
	 *	@param out Number of outputs for the network
	 */
	public LVQ(int in, int subclass, int out){
		ins = in;
		outs = out;
		subclasses = subclass;

		W=new Matrix(subclasses*outs,ins);
		W2=new Matrix(outs,subclasses*outs);

		W.fill();
		W2.setValue(0);

		//recorrer y llenar W2 con 1 por filas
		for(int i=0,j=0,k=0;i < W2.getColumns();i++){
			W2.position(j,i,1);
			if(k++ == subclasses-1){
				j++;
				k=0;
			}
		}
	}

	/**
	 *	Train the network using a hybrid scheme. Uses the "winner takes all" rule.
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
		int[] sort = new int[length];
		double error=0;
		Matrix r = new Matrix(1,ins);
		Matrix row = new Matrix(1, W.getColumns());

		Matrix[] patternsT=new Matrix[length];
		for(int i=0;i<length;i++){
			patternsT[i]=patterns[i+offset].transpose();
			sort[i] = i;
		}

		if(progress != null){
			progress.setMaximum(0);
			progress.setMinimum(-epochs);
			progress.setValue(-epochs);
		}

		while((error=error(patterns,answers,offset,length)) > minerror && epochs-- > 0){
			//shuffle patterns
			shuffle(sort);

			for(int i=0;i<length;i++){
				//calculate the distance of each pattern to each neuron (rows in W), keep the winner
				int winnerOut = -1;
				int winnerT = -1;

				simulateNoChange(patterns[sort[i]+offset]);

				//find the row with the value 1 in the column winner of W2
				for(int j=0;j<W2.getRows();j++){
					if(W2.position(j,winner)==1) winnerOut=j;
					if(answers[sort[i]+offset].position(j,0)==1) winnerT=j;
				}

				//Ww = Ww +/- alpha . (p - Ww); //w is the row of winner neuron
				patternsT[sort[i]].copy(r);
				row.setRow(0,W.getRow(winner));
				r.subtract(row,r);
				r.multiply((winnerT == winnerOut)?alpha:-alpha,r); //if winner in T == winner int out + else -
				row.add(r,r);

				W.setRow(winner, r.getRow(0));

			}
			
			if(plotter!=null) plotter.setError(epochs, error);
			if(progress!=null) progress.setValue(-epochs);
		}
		if(progress!=null) progress.setValue(1);
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix ret = new Matrix(outs,1);
		Matrix ret1 = super.simulate(pattern);//new Matrix(W2.getCols(),1);
		W2.multiply(ret1,ret);
		return ret;
	}

	/**
	 *	Calculates the number of incorrect answers over the total.
	 *	@param patterns The array with the patterns to test
	 *	@param answers The array with the expected answers for the patterns.
	 *	@param offset The initial position inside the array.
	 *	@param length How many patterns must be taken from the offset.
	 *	@return The relation between the incorrect answers and the total number of answers.
	 */
	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		//relation between correct answers and total answers
		int correct=0;
		Matrix ret1=new Matrix(W2.getColumns(),1);
		Matrix ret=new Matrix(outs,1);
		
		for(int i=offset;i<length;i++){
			simulate(patterns[i], ret1);
			W2.multiply(ret1,ret);
			
			if(ret.equals(answers[i]))
				correct++;
		}

		return (length-correct)/(double)length;
	}

	@Override
	public boolean save(String path) {
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path), true);
			out.printf("%d %d %d\n",ins, subclasses,outs);

			out.println(W);//W.print(f);
			out.println(W2);

			out.close();
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean open(String path) {
		try{
			Scanner in = new Scanner(new FileInputStream(path));
			ins = in.nextInt();
			subclasses = in.nextInt();
			outs = in.nextInt();

			W = new Matrix(outs*subclasses,ins);
			W2 = new Matrix(outs,outs*subclasses);

			for(int i=0;i<outs*subclasses;i++)
				for(int j=0;j<ins;j++){
					W.position(i,j,in.nextDouble());
				}

			for(int i=0;i<outs;i++)
				for(int j=0;j<outs*subclasses;j++){
					W2.position(i,j,in.nextDouble());
				}

			in.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
