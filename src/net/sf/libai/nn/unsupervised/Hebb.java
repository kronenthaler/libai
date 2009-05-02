package net.sf.libai.nn.unsupervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 *
 * @author kronenthaler
 */
public class Hebb extends NeuralNetwork{
	protected double phi;
	protected Matrix W;

	public Hebb(){}

	public Hebb(int inputs){
		this(inputs, 0.02);
	}

	public Hebb(int inputs, double phi){
		this.phi = 1-phi; //precalculation for the momentum 1-phi
		W = new Matrix(inputs,inputs);
		W.fill();
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		Random rand = new Random();
		int[] sort=new int[length];
		Matrix Y=new Matrix(W.getRows(),1);
		Matrix temp=new Matrix(W.getRows(),W.getColumns());

		Matrix[] patternsT=new Matrix[length];
		for(int i=0;i<length;i++){
			patternsT[i]=patterns[i+offset].transpose();
			sort[i] = i;
		}
		
		while(epochs-- > 0){
			//shuffle patterns
			for(int i=0;i<length;i++){
				int j = rand.nextInt(length);
				int aux=sort[i];
				sort[i]=sort[j];
				sort[j]=aux;
			}

			for(int i=0;i<length;i++){
				//F(wx)
				simulate(patterns[sort[i] + offset], Y);
				//W.multiply(,Y);//inner product
				//Y.apply(signum,Y);	 //thresholding

				//W=(1-phi)*W + alpha*Y*pt;
				W.multiply(phi,W);
				Y.multiply(patternsT[sort[i]],temp);
				temp.multiply(alpha,temp);
				W.add(temp,W);

				//alternative rule: no just have decay term, also inhibit the connections
				//Wij=Wij+(phi*yi*(alpha/phi*xi - Wij))
				//require 2 cicles to update properly the weights
				/*for(int k=0;k<W.rows;k++){
					for(int j=0;j<W.cols;j++){
						W.position(k,j)=W.position(k,j) + phi*Y.position(k,0)*(((alpha/phi)*p[patron[i]].position(k,0))-W.position(k,j));
					}
				}*/
			}
		}
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix ret = new Matrix(pattern.getRows(),pattern.getColumns());
		simulate(pattern, ret);
		return ret;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		W.multiply(pattern,result);
		result.apply(signum,result);
	}

	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		Matrix X=new Matrix(patterns[0].getRows(),patterns[0].getColumns());
		Matrix Y=new Matrix(patterns[0].getRows(),patterns[0].getColumns());

		double error=0;
		for(int i=0;i<length;i++){
			simulate(patterns[i+offset],X);
			patterns[i+offset].apply(signum,Y);

			for(int j=0;j<X.getRows();j++)
				error += Math.pow(Y.position(j,0)-X.position(j,0),2);
		}

		return error/(double)(length*patterns[0].getRows());
	}

	@Override
	public boolean save(String path) {
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path), true);
			out.printf("%d\n",W.getRows());

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
			int ins = in.nextInt();
			
			W=new Matrix(ins,ins);

			for(int i=0;i<ins;i++)
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
