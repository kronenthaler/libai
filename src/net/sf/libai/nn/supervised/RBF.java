package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 *
 * @author kronenthaler
 */
public class RBF extends Adaline{
	private Matrix c[];
	int nperlayer[];//{#inputs,#Neurons,#outputs}
	double[] sigma;

	public RBF(){}

	public RBF(int[] nperlayer){
		super(nperlayer[1],nperlayer[2]); // input, outputs

		this.nperlayer=nperlayer;

		sigma=new double[nperlayer[1]];
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		double error,prevError,current;

		//apply k-means to the patterns
		c = kmeans(nperlayer[1],patterns,offset,length);

		//calculate sigmas. p-closest neightbors, p is the input dimension
		PriorityQueue<Double>[] neighbors=new PriorityQueue[nperlayer[1]];
		for(int i=0;i<nperlayer[1];i++)
			neighbors[i]=new PriorityQueue<Double>();

		for(int i=0;i<nperlayer[1]-1;i++){
			for(int j=i+1;j<nperlayer[1];j++){
				current = Math.sqrt(euclideanDistance2(c[i],c[j]));
				neighbors[i].add(-current);
				neighbors[j].add(-current);
			}
		}

		for(int i=0;i<nperlayer[1];i++){
			double acum=0;
			for(int j=0;j<nperlayer[0] && !neighbors[i].isEmpty() ;j++){
				acum+= -neighbors[i].poll();
			}
			sigma[i] = acum/nperlayer[0];
		}

		//precalculate the aouputs for each pattern in the hidden layer
		Matrix Y[]=new Matrix[length];
		Matrix Yt[]=new Matrix[length];
		for(int j=0;j<length;j++){
			Y[j]=new Matrix(nperlayer[1],1);
			Yt[j]=new Matrix(1,nperlayer[1]);
			simulateNoChange(patterns[j+offset],Y[j]);
			Y[j].transpose(Yt[j]);
		}

		Matrix aux=new Matrix(nperlayer[2], nperlayer[1]);
		Matrix out=new Matrix(nperlayer[2], 1);
		Matrix E=new Matrix(nperlayer[2], 1);
		
		while((error=error(patterns,answers,offset,length)) > minerror && epochs-- > 0){
			//if(error > prevError) break; //optional to avoid overtrainning problems

			for(int i=0;i<length;i++){
				//F(wx+b)
				super.simulate(Y[i + offset],out); //force call to the right function

				//e=t-y
				answers[i].subtract(out,E);	//error

				//alpha*e.p^t
				E.multiply(alpha,E);
				E.multiply(Yt[i],aux);

				W.add(aux,W);//W+(alpha*e.p^t)
				b.add(E,b);  //b+(alpha*e)
			}

			prevError=error;
			if(plotter !=null) plotter.setError(epochs, error);
		}
	}

	private Matrix[] kmeans(int k, Matrix[] patterns,int offset,int length){
		int i,j,l;
		Random rand = new Random();

		Matrix[] ctemp=new Matrix[k];
		ArrayList<Integer>[] partitions = new ArrayList[k];
		Matrix aux=new Matrix(patterns[0].getRows(),patterns[0].getCols());
		Matrix aux1=new Matrix(patterns[0].getRows(),patterns[0].getCols());

		for(i=0;i<k;i++){
			ctemp[i]=new Matrix(patterns[0].getRows(),patterns[0].getCols());
			int index = rand.nextInt(length) + offset;//abs((int)(ctemp[i].random(&xzxzx)*npatterns));;
			patterns[index].copy(ctemp[i]);
			partitions[i]=new ArrayList<Integer>();
		}
		int iter=0;
		while(true){
			double min,current;
			for(l=0;l<k;l++)
				partitions[l].clear();
			j=0;
			for(i=0;i<length;i++){
				min=Double.MAX_VALUE;
				for(l=0;l<k;l++){
					current=euclideanDistance2(patterns[i+offset],ctemp[l]);
					if(current < min){
						min=current;
						j=l;
					}
				}
				partitions[j].add(i + offset);
			}

			boolean exit=true;
			for(i=0;i<k;i++){
				int total=0;
				aux1.setValue(0);
				for(j=0;j<partitions[i].size();j++){
					aux1.add(patterns[partitions[i].get(j)],aux1);
					total++;
				}

				if(total==0){
					//empty partition take a random pattern as centroid
					ctemp[i]=new Matrix(patterns[0].getRows(),patterns[0].getCols());
					int index=rand.nextInt(length) + offset;
					patterns[index].copy(ctemp[i]);

					exit=false;
				}else{
					aux1.multiply(1.0/(double)total,aux);

					if(!(aux.equals(ctemp[i]))){
						aux.copy(ctemp[i]);
						exit=false;
					}
				}
			}
			if(exit) return ctemp;
		}
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix y=new Matrix(nperlayer[2],1);
		simulate(pattern, y);
		return y;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		Matrix aux = new Matrix(nperlayer[1],1);
		simulateNoChange(pattern, aux);
		super.simulate(aux,result);
	}

	private void simulateNoChange(Matrix pattern, Matrix result){
		for(int i=0;i<nperlayer[1];i++){
			double current=euclideanDistance2(pattern,c[i]);
			result.position(i, 0, NeuralNetwork.gaussian(current,sigma[i]));
		}
	}

	public boolean save(String path){
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path), true);
			for(int i=0;i<3;out.printf("%d ",nperlayer[i++]));
			out.println();

			for(int i=0;i<nperlayer[1];i++) //centers
				out.println(c[i]);

			for(int i=0;i<nperlayer[1];i++) //centers
				out.println(sigma[i]);

			out.println(W);
			out.println(b);

			out.close();
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean open(String path){
		try{
			Scanner in = new Scanner(new FileInputStream(path));

			nperlayer=new int[3];
			
			nperlayer[0] = in.nextInt();
			nperlayer[1] = in.nextInt();
			nperlayer[2] = in.nextInt();


			W=new Matrix(nperlayer[2],nperlayer[1]);
			b=new Matrix(nperlayer[2],1);
			c=new Matrix[nperlayer[1]];
			sigma=new double[nperlayer[1]];

			for(int i=0;i<nperlayer[1];i++){
				c[i]=new Matrix(nperlayer[0],1);
				for(int j=0;j<nperlayer[0];j++){
					c[i].position(j,0,in.nextDouble());
				}
			}

			for(int i=0;i<nperlayer[1];i++)
				sigma[i]=in.nextDouble();

			for(int i=0;i<nperlayer[2];i++)
				for(int j=0;j<nperlayer[1];j++){
					W.position(i,j,in.nextDouble());
				}

			for(int i=0;i<nperlayer[2];i++){
				b.position(i,0,in.nextDouble());
			}

			in.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
