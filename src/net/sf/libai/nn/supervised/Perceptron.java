package net.sf.libai.nn.supervised;

import net.sf.libai.nn.*;
import java.io.*;
import java.util.*;

import net.sf.libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class Perceptron extends NeuralNetwork{
	protected Matrix W, b;
    protected int ins,outs;
	
	public Perceptron(){}

    public Perceptron(int in,int out){
		ins=in;
		outs=out;

		W = new Matrix(outs,ins);
		b = new Matrix(out,1);

		W.fill();
		b.fill();
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		double error=1,prevError=error(patterns,answers,offset,length);
		Matrix Y=new Matrix(outs, 1);
		Matrix E=new Matrix(outs, 1);
		Matrix aux=new Matrix(outs, ins);

		//initialize sort array
		Matrix[] patternsT=new Matrix[length];
		for(int i=0;i<length;i++)
			patternsT[i] = patterns[i + offset].transpose();
		
		while((error=error(patterns,answers,offset,length)) > minerror && epochs-- > 0){
			//if(error > prevError) break; //optional to avoid overtrainning problems

			for(int i=0;i<length;i++){
				//F(wx+b)
				simulate(patterns[i + offset],Y);

				//e=t-y
				answers[i].subtract(Y,E);	//error

				//alpha*e.p^t
				E.multiply(alpha,E);
				E.multiply(patternsT[i],aux);

				W.add(aux,W);//W+(alpha*e.p^t)
				b.add(E,b);  //b+(alpha*e)
			}
			
			prevError=error;
			if(plotter!=null) plotter.setError(epochs, error);
		}
	}

	@Override
	public Matrix simulate(Matrix p) {
		Matrix Y=new Matrix(outs, 1);
		simulate(p,Y);
		return Y;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		W.multiply(pattern,result);		//inner product
		result.add(b,result);			//bias
		result.apply(signum,result);	//thresholding
	}

	@Override
	public boolean save(String path) {
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path), true);
			out.printf("%d %d\n",ins,outs);

			out.println(W);//W.print(f);
			out.println(b);//b.print(f);

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
			b=new Matrix(outs,1);

			for(int i=0;i<outs;i++)
				for(int j=0;j<ins;j++)
					W.position(i,j,in.nextDouble());
		
			for(int i=0;i<outs;i++)
				b.position(i,0,in.nextDouble());

			in.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}