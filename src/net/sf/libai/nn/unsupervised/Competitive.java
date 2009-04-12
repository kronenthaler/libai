package net.sf.libai.nn.unsupervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.Matrix;
import net.sf.libai.nn.NeuralNetwork;

/**
 *
 * @author kronenthaler
 */
public class Competitive extends NeuralNetwork{
	protected Matrix W;
	protected int ins, outs;
	protected int winner;

	public Competitive(){}

	public Competitive(int in, int out){
		ins = in;
		outs = out;
		W = new Matrix(outs, ins);
		
		W.fill();
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort=new int[length]; // [0,length)
		double error = 0;
		Random rand = new Random();

		Matrix r = new Matrix(1,ins);
		Matrix row = new Matrix(1,ins);

		//initialize sort array
		Matrix[] patternsT=new Matrix[length];
		for(int i=0;i<length;i++){
			patternsT[i] = patterns[i+offset].transpose();
			sort[i] = i;
		}

		while((error=error(patterns,answers,offset,length)) > minerror && epochs-- > 0){
			//shuffle patterns
			for(int i=0;i<length;i++){
				int j = rand.nextInt(length);
				int aux=sort[i];
				sort[i]=sort[j];
				sort[j]=aux;
			}

			for(int i=0;i<length;i++){
				//calculate the distance of each pattern to each neuron (rows in W), keep the winner
				simulateNoChange(patterns[sort[i] + offset]);
				
				//Ww = Ww + aplha . (p - Ww); w is the row of winner neuron
				patternsT[sort[i]].copy(r);
				row.setRow(0,W.getRow(winner));
				r.subtract(row,r);
				r.multiply(alpha,r);
				row.add(r,r);

				W.setRow(winner, r.getRow(0));
			}
		}
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix ret=new Matrix(W.getRows(),1);
		simulate(pattern, ret);
		return ret;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		simulateNoChange(pattern);

		result.setValue(0);
		result.position(winner,0,1);
	}

	protected void simulateNoChange(Matrix pattern){
		double[] row;
		double d = Double.MAX_VALUE;
		winner = -1;
		for(int j=0;j<W.getRows();j++){
			row=W.getRow(j);
			double dist = euclideanDistance2(pattern.getCol(0),row);
			if(dist < d){
				d=dist;
				winner = j;
			}
		}
	}

	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		//average of the distances to the closest neuron
		double[] row;
		double acum=0;
		for(int i=offset;i<offset+length;i++){
			double d = Double.MAX_VALUE;

			for(int j=0;j<outs;j++){
				row = W.getRow(j);
				double dist = Math.sqrt(euclideanDistance2(patterns[i].getCol(0),row));
				d = Math.min(dist,d);
			}

			acum+=d;
		}

		return acum/(double)length;
	}

	@Override
	public boolean save(String path) {
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path), true);
			out.printf("%d %d\n",ins,outs);

			out.println(W);//W.print(f);

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
			outs = in.nextInt();

			W=new Matrix(outs,ins);

			for(int i=0;i<outs;i++)
				for(int j=0;j<ins;j++)
					W.position(i,j,in.nextDouble());

			in.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
