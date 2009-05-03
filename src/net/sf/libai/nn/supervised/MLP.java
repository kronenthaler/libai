package net.sf.libai.nn.supervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 *	Multi Layer Perceptron or MLP. MLP was the first algorithm proposed to train
 *	multilayer neurons using the general delta rule.
 *	This implementation has the classical backpropagation algorithm and the main variant
 *	the backpropagation with momemtum.
 *	NOTE: The backpropagation is a very slow algorithm involves MANY matrix operation.
 *	I have planned to implement a batch algorithm or Resilent Backpropagation (Rprop)
 *	to replace this algorithm. Meanwhile be patient.
 *	@author kronenthaler
 */
public class MLP extends NeuralNetwork{
	private Matrix W[], b[], 
					Y[], d[], u[],
					Wt[], Yt[], 
					Wprev[], bprev[],//momemtum
					I[], M[];
	private int nperlayer[]; //number of neurons per layer, including the input layer
	private int layers;
	private Function[] func;
	private double beta;	//momentum term should be in [0,1], if is 0 there is not momentum term.

	public MLP(){}

	/**
	 *	Constructor. Creates a MLP with nperlayer.length layers. The number of neurons
	 *	per layer is defined in <code>nperlayer</code>. The nperlayer[0] means the input layer.
	 *	For each layer the neurons applys the output function <code>funcs[i]</code>. These functions
	 *	must be derivable. The parameter <code>beta</code> means the momemtum influence. If beta &lt;= 0
	 *	the momentum has no influence. if beta &gt; 0 and &lt; 1 that's the influence.
	 *	@param nperlayer Number of neurons per layer including the input layer.
	 *	@param funcs Function to apply per layer. The function[0] could be null.
	 *	@param beta The influence of momentum term.
	 */
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

	/**
	 *	Initialize the matrix and auxiliar buffers.
	 */
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

			u[i]=new Matrix(W[i].getRows(),Y[i-1].getColumns());
			Y[i]=new Matrix(u[i].getRows(),u[i].getColumns());
			Yt[i]=new Matrix(u[i].getColumns(),u[i].getRows());

			I[i]=new Matrix(u[i].getRows(),u[i].getRows());
			M[i]=new Matrix(u[i].getRows(),Y[i-1].getRows());
		}

		d[layers-1]=new Matrix(u[layers-1].getRows(),1);
		for(int k=layers-2;k>0;k--)
			d[k]=new Matrix(u[k].getRows(),1);
	}

	/**
	 *	Train the network using the standard backpropagation algorithm.
	 *	The pattern is propagated from the input to the final layer (the output).
	 *	Then the error for the final layer is computed. The error is calculated backwards to the first
	 *	hidden layer, calculating the diferentials between input and expected output (backpropagation).
	 *	Finally, the weights and biases are updated using the delta rule:<br/>
	 *	W[i] = W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t <br/>
	 *	B[i] = B[i] + beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i]<br/>
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
		double error=error(patterns,answers,offset,length),prevError=error;
		Matrix temp3,
				temp2=new Matrix(answers[0].getRows(),answers[0].getColumns()),
				e=new Matrix(answers[0].getRows(),answers[0].getColumns());

		for(int i=0;i<length;i++){
			sort[i] = i;
		}

		if(progress != null){
			progress.setMaximum(0);
			progress.setMinimum(-epochs);
			progress.setValue(-epochs);
		}

		while(error > minerror && /*(error=error(patterns,answers,offset,length)) > minerror &&/**/ epochs-- > 0){ //<-- cuello de botella...
			//if(error > prevError) break;
			//shuffle patterns
			shuffle(sort);
			
			error = 0;
			for(int i=0;i<length;i++){
				//Y[i]=Fi(<W[i],Y[i-1]>+b)
				simulate(patterns[sort[i]+offset]);

				//e=-2(t-Y[n-1])
				answers[sort[i]+offset].subtract(Y[layers-1],e);
				
				//calcular el error
				for(int m=0;m<nperlayer[layers-1];m++)
					error += (e.position(m,0)*e.position(m,0));

				//d[0] = F0'(<W[i],Y[i-1]>).e
				for(int j=0;j<u[layers-1].getRows();j++)
					d[layers-1].position(j,0,-2*alpha*func[layers-1].getDerivate().eval(u[layers-1].position(j,0))*e.position(j, 0));
				
				//d[i]=Fi'(<W[i],Y[i-1]>).W[i+1]^t.d[i+1]
				for(int k=layers-2;k>0;k--){
					for(int j=0;j<u[k].getRows();j++){
						double acum = 0;
						for(int t=0; t<W[k+1].getRows(); t++)
							acum += W[k+1].position(t,j)*d[k+1].position(t,0);
						d[k].position(j, 0, alpha*acum*func[k].getDerivate().eval(u[k].position(j,0)));//cual es el valor que se agrega al delta?
					}
				}

				//actualizar pesos y umbrales
				for(int l=1;l<layers;l++){
					Y[l-1].transpose(Yt[l-1]);
					if(beta <= 0){// BP without momentum
						d[l].multiply(Yt[l-1],M[l]);
						W[l].subtract(M[l],W[l]);
						b[l].subtract(d[l],b[l]);
					}else{// BP with momentum */
						temp3 = new Matrix(d[l].getRows(),Y[l-1].getRows());

						d[l].multiply(1-beta,d[l]);	//(1-beta)*alpha.d[i]
						d[l].multiply(Yt[l-1],temp3);		//(1-beta)*alpha.d[i].Y[i-1]^t

						//W[i]=W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t
						W[l].subtract(Wprev[l],M[l]);		//(W[i]-Wprev[i])
						W[l].copy(Wprev[l]);				//Wprev[i] = W[i]
						M[l].multiply(beta,M[l]);			//beta*(W[i]-Wprev[i])
						W[l].add(M[l],W[l]);				//W[i] + beta*(W[i]-Wprev[i])
						W[l].subtract(temp3,W[l]);			//W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t

						temp3 = null;
						temp3 = new Matrix(b[l].getRows(),b[l].getColumns());

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
			if(progress!=null) progress.setValue(-epochs);
		}
		
		if(progress!=null) progress.setValue(1);
		//System.out.println("last epoch: "+epochs);
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
	
	@Override
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
					for(int j=0;j<W[l].getColumns();j++){
						W[l].position(i,j,in.nextDouble());
					}
				}

				for(int i=0;i<b[l].getRows();i++){
					for(int j=0;j<b[l].getColumns();j++){
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

	@Override
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
