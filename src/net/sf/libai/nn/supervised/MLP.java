package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 * TODO: too slow, to implement a batch update algorithm or RProp 
 * @author kronenthaler
 */
public class MLP extends NeuralNetwork{
	private Matrix W[], b[], 
					Y[], d[], u[],
					Wt[], Yt[], 
					Wprev[], bprev[],//momemtum
					I[], M[];//momentum
	private int nperlayer[]; //number of neurons per layer, including the input layer
	private int layers;
	private Function[] func;
	double beta;	//momentum term should be in [0,1], if is 0 there is not momentum term.

	public MLP(){}

	public MLP(int[] nperlayer,Function[] funcs,double beta){
		this.nperlayer = nperlayer;
		func = funcs;
		this.beta = beta;
		layers = nperlayer.length;

		W = new Matrix[layers];//position zero reserved
		b=new Matrix[layers];//position zero reserved
		Y=new Matrix[layers];//position zero reserved for the input pattern
		d=new Matrix[layers];//position zero reserved
		u=new Matrix[layers];//position zero reserved
		Wt=new Matrix[layers];
		Yt=new Matrix[layers];
		Wprev=new Matrix[layers];
		bprev=new Matrix[layers];
		I=new Matrix[layers];
		M=new Matrix[layers];

		init();
	}

	private void init(){
		Yt[0]=new Matrix(1,nperlayer[0]);
		Y[0]=new Matrix(nperlayer[0],1);

		for(int i=1;i<layers;i++){
			W[i]=new Matrix(nperlayer[i],nperlayer[i-1]);
			Wprev[i]=new Matrix(nperlayer[i],nperlayer[i-1]);
			Wt[i]=new Matrix(nperlayer[i-1],nperlayer[i]);
			b[i]=new Matrix(nperlayer[i],1);
			bprev[i]=new Matrix(nperlayer[i],1);

			W[i].fill(); //llenar de manera aleatoria
			W[i].copy(Wprev[i]);
			b[i].fill(); //llenar de manera aleatoria
			b[i].copy(bprev[i]);

			u[i]=new Matrix(W[i].getRows(),Y[i-1].getCols());
			Y[i]=new Matrix(u[i].getRows(),u[i].getCols());
			Yt[i]=new Matrix(u[i].getCols(),u[i].getRows());

			I[i]=new Matrix(u[i].getRows(),u[i].getRows());
			M[i]=new Matrix(u[i].getRows(),Y[i-1].getRows());
		}

		d[layers-1]=new Matrix(u[layers-1].getRows(),1);
		for(int k=layers-2;k>0;k--)
			d[k]=new Matrix(u[k].getRows(),1);
	}

	//TODO: implementar un algoritmo tipo batch RPROP seria una buena opcion.
	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		double error=error(patterns,answers,offset,length),prevError=error;
		int i,j,k,l;

		Matrix temp,temp3,
			temp2=new Matrix(answers[0].getRows(),answers[0].getCols()),
			e=new Matrix(answers[0].getRows(),answers[0].getCols());
			//I=new Matrix(u[layers-1].getRows(),u[layers-1].getRows());
		
		while(error > minerror && /*(error=error(patterns,answers,offset,length)) > minerror &&/**/ epochs-- > 0){ //<-- cuello de botella...
			//if(error > prevError) break;
			
			error = 0;
			for(i=0;i<length;i++){
				//Y[i]=Fi(<W[i],Y[i-1]>+b)
				simulate(patterns[i+offset]);
				
				//e=-2(t-Y[n-1])
				answers[i+offset].subtract(Y[layers-1],temp2);
				
				//calcular el error
				for(int m=0;m<nperlayer[layers-1];m++)
					error += (temp2.position(m,0)*temp2.position(m,0));

				temp2.multiply(-2,e);

				//d[0] = F0'(<W[i],Y[i-1]>+b).e
				u[layers-1].applyInIdentity(func[layers-1].getDerivate(),I[layers-1]);
				I[layers-1].multiply(e,d[layers-1]);
								
				//d[i]=Fi'(<W[i],Y[i-1]>+b).W[i+1]^t.d[i+1]
				for(k=layers-2;k>0;k--){
					temp3 = new Matrix(u[k].getRows(),W[k+1].getRows());
					u[k].applyInIdentity(func[k].getDerivate(),I[k]);
					W[k+1].transpose(Wt[k+1]);
					I[k].multiply(Wt[k+1],temp3);
					temp3.multiply(d[k+1],d[k]);
				}

				//actualizar pesos y umbrales
				for(l=1;l<layers;l++){
					Y[l-1].transpose(Yt[l-1]);
					if(beta <= 0){// BP without momentum
						//temp = new Matrix(d[l].getRows(),Y[l-1].getRows());
						d[l].multiply(alpha,d[l]);
						d[l].multiply(Yt[l-1],M[l]);
						W[l].subtract(M[l],W[l]);
						b[l].subtract(d[l],b[l]);
						//temp = null;
					}else{// BP with momentum */
						temp3 = new Matrix(d[l].getRows(),Y[l-1].getRows());

						d[l].multiply(alpha*(1-beta),d[l]);	//(1-beta)*alpha.d[i]
						d[l].multiply(Yt[l-1],temp3);		//(1-beta)*alpha.d[i].Y[i-1]^t

						//W[i]=W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t
						W[l].subtract(Wprev[l],M[l]);		//(W[i]-Wprev[i])
						W[l].copy(Wprev[l]);				//Wprev[i] = W[i]
						M[l].multiply(beta,M[l]);			//beta*(W[i]-Wprev[i])
						W[l].add(M[l],W[l]);				//W[i] + beta*(W[i]-Wprev[i])
						W[l].subtract(temp3,W[l]);			//W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t

						temp3 = null;
						temp3 = new Matrix(b[l].getRows(),b[l].getCols());

						//B[i]=B[i]+ beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i];
						b[l].subtract(bprev[l],temp3);	//(B[i]-Bprev[i])
						b[l].copy(bprev[l]);			//Bprev[i] = B[i]
						temp3.multiply(beta,temp3);		//beta*(B[i]-Bprev[i])
						b[l].add(temp3,b[l]);			//B[i] + beta*(B[i]-Bprev[i])
						b[l].subtract(d[l],b[l]);		//B[i] + beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i]

						temp3 = null;
						//*/
					}
				}
			}

			error /= length;
			prevError=error;

			if(plotter!=null) plotter.setError(epochs, error);
		}
		temp2 = null;
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		simulate(pattern, null);

		return Y[layers-1];
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		//Y[0]=x
		pattern.copy(Y[0]);

		//Y[i]=Fi(<W[i],Y[i-1]>+b)
		for(int j=1;j<layers;j++){
			W[j].multiply(Y[j-1],u[j]);
			u[j].add(b[j],u[j]);
			u[j].apply(func[j],Y[j]);
		}

		if(result!=null)
			Y[layers-1].copy(result);
	}

	public boolean open(String path){
		try{
			Scanner in = new Scanner(new FileInputStream(path));
			layers = in.nextInt();

			nperlayer=new int[layers];
			for(int i=0;i<layers;i++)
				nperlayer[i]=in.nextInt();

			W=new Matrix[layers];//position zero reserved
			b=new Matrix[layers];//position zero reserved
			Y=new Matrix[layers];//position zero reserved for the input pattern
			d=new Matrix[layers];//position zero reserved
			u=new Matrix[layers];//position zero reserved
			Wt=new Matrix[layers];
			Yt=new Matrix[layers];
			Wprev=new Matrix[layers];
			bprev=new Matrix[layers];
			I=new Matrix[layers];
			M=new Matrix[layers];

			init(); //create the matrix

			//fill W[i], b[i]
			for(int l=1;l<layers;l++){
				for(int i=0;i<W[l].getRows();i++){
					for(int j=0;j<W[l].getCols();j++){
						W[l].position(i,j,in.nextDouble());
					}
				}

				for(int i=0;i<b[l].getRows();i++){
					for(int j=0;j<b[l].getCols();j++){
						b[l].position(i,j,in.nextDouble());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save(String path){
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path), true);
		
			out.printf("%d\n",layers);

			for(int i=0;i<layers;out.printf("%d ",nperlayer[i++]));
			out.println();

			for(int i=1;i<layers;i++){
				out.println(W[i]);
				out.println(b[i]);
			}

			out.close();
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
