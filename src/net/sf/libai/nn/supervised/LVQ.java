package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.unsupervised.Competitive;

/**
 *
 * @author kronenthaler
 */
public class LVQ extends Competitive{
	protected Matrix W2;
	protected int subclasses;

	public LVQ(int in, int subclass, int out){
		ins = in;
		outs = out;
		subclasses = subclass;

		W=new Matrix(subclasses*outs,ins);
		W2=new Matrix(outs,subclasses*outs);

		W.fill();
		W2.setValue(0);

		//recorrer y llenar W2 con 1 por filas
		for(int i=0,j=0,k=0;i < W2.getCols();i++){
			W2.position(j,i,1);
			if(k++ == subclasses-1){
				j++;
				k=0;
			}
		}
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		double error=0;
		Matrix r = new Matrix(1,ins);
		Matrix row = new Matrix(1, W.getCols());

		Matrix[] patternsT=new Matrix[length];
		for(int i=0;i<length;i++)
			patternsT[i]=patterns[i+offset].transpose();

		while((error=error(patterns,answers,offset,length)) > minerror && epochs-- > 0){

			for(int i=0;i<length;i++){
				//calculate the distance of each pattern to each neuron (rows in W), keep the winner
				int winnerOut = -1;
				int winnerT = -1;

				simulateNoChange(patterns[i + offset]);

				//find the row with the value 1 in the column winner of W2
				for(int j=0;j<W2.getRows();j++){
					if(W2.position(j,winner)==1) winnerOut=j;
					if(answers[i].position(j,0)==1) winnerT=j;
				}

				//Ww = Ww +/- alpha . (p - Ww); //w is the row of winner neuron
				patternsT[i].copy(r);
				row.setRow(0,W.getRow(winner));
				r.subtract(row,r);
				r.multiply((winnerT == winnerOut)?alpha:-alpha,r); //if winner in T == winner int out + else -
				row.add(r,r);

				W.setRow(winner, r.getRow(0));
			}
		}
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix ret = new Matrix(outs,1);
		Matrix ret1 = super.simulate(pattern);//new Matrix(W2.getCols(),1);
		W2.multiply(ret1,ret);
		return ret;
	}

	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		//relation between correct answers and total answers
		int correct=0;
		Matrix ret1=new Matrix(W2.getCols(),1);
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
