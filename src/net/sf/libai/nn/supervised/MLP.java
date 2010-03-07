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
	public static final int STANDARD_BACKPROPAGATION = 0;
	public static final int MOMEMTUM_BACKPROPAGATION = 1;
	public static final int RESILENT_BACKPROPAGATION = 2;
	
	private Matrix W[], b[], 
					Y[], d[], u[],
					Wt[], Yt[], 
					M[];
	private int nperlayer[]; //number of neurons per layer, including the input layer
	private int layers;
	private Function[] func;
	private double params[];	//momentum term should be in [0,1], if is 0 there is not momentum term.
	private int trainingType = STANDARD_BACKPROPAGATION;
	
	public MLP(){}

	/**
	 *	Constructor. Creates a MLP with nperlayer.length layers. The number of neurons
	 *	per layer is defined in <code>nperlayer</code>. The nperlayer[0] means the input layer.
	 *	For each layer the neurons applies the output function <code>funcs[i]</code>. These functions
	 *	must be derivable. The training algorithm is standard backpropagation.
	 *	@param nperlayer Number of neurons per layer including the input layer.
	 *	@param funcs Function to apply per layer. The function[0] could be null.
	 */
	public MLP(int[] nperlayer,Function[] funcs){
		this(nperlayer, funcs, 0);
		trainingType = STANDARD_BACKPROPAGATION;
	}
	
	/**
	 *	Constructor. Creates a MLP with nperlayer.length layers. The number of neurons
	 *	per layer is defined in <code>nperlayer</code>. The nperlayer[0] means the input layer.
	 *	For each layer the neurons appliss the output function <code>funcs[i]</code>. These functions
	 *	must be derivable. The parameter <code>beta</code> means the momemtum influence. If beta &lt;= 0
	 *	the momentum has no influence. if beta &gt; 0 and &lt; 1 that's the influence.
	 *	@param nperlayer Number of neurons per layer including the input layer.
	 *	@param funcs Function to apply per layer. The function[0] could be null.
	 *	@param beta The influence of momentum term.
	 */
	public MLP(int[] nperlayer,Function[] funcs,double beta){
		if(beta < 0 || beta >=1) throw new IllegalArgumentException("beta should be positive and less than 1");
		
		this.nperlayer = nperlayer;
		func = funcs;
		this.params = new double[]{beta};
		
		trainingType = beta > 0 ? MOMEMTUM_BACKPROPAGATION : STANDARD_BACKPROPAGATION;
		layers = nperlayer.length;

		W = new Matrix[layers];//position zero reserved
		b=new Matrix[layers];//position zero reserved
		Y=new Matrix[layers];//position zero reserved for the input pattern
		d=new Matrix[layers];//position zero reserved
		u=new Matrix[layers];//position zero reserved
		Wt=new Matrix[layers];
		Yt=new Matrix[layers];
		M=new Matrix[layers];

		init();
	}

	/**
	 *	Set the training algorithm to use. If this method is not called, the standard backpropagation is used.
	 *	If some algorithm has extra parameters like momentum constant, those parameters must be pass as an array of values.
	 *	@param _trainingType The training algorithm to use, STANDARD_BACKPROPAGATION, MOMENTUM_BACKPROPAGATION or RESILENT_BACKPROPAGATION.
	 *	@param params The set of parameters for the algorithm selected.
	 */
	public void setTrainingType(int _trainingType, double... params){
		trainingType = _trainingType;
		if(trainingType == MOMEMTUM_BACKPROPAGATION){
			if(params.length < 1) throw new IllegalArgumentException("Momemtum algorithm requires 1 parameter: beta");
			this.params = params;
		}else if(trainingType == RESILENT_BACKPROPAGATION){
			//rprop doesn't requires any extra parameters the default parameter works fine.
		}
		//standard bp doesn't requires any extra parameters.
	}

	/**
	 *	Initialize the matrix and auxiliar buffers.
	 */
	private void init(){
		Yt[0]=new Matrix(1,nperlayer[0]);
		Y[0]=new Matrix(nperlayer[0],1);

		for(int i=1;i<layers;i++){
			W[i]=new Matrix(nperlayer[i],nperlayer[i-1]);
			Wt[i]=new Matrix(nperlayer[i-1],nperlayer[i]);
			b[i]=new Matrix(nperlayer[i],1);
			
			W[i].fill(); //llenar de manera aleatoria
			b[i].fill(); //llenar de manera aleatoria
			
			u[i]=new Matrix(W[i].getRows(),Y[i-1].getColumns());
			Y[i]=new Matrix(u[i].getRows(),u[i].getColumns());
			Yt[i]=new Matrix(u[i].getColumns(),u[i].getRows());

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
		if(progress != null){
			progress.setMaximum(0);
			progress.setMinimum(-epochs);
			progress.setValue(-epochs);
		}
		
		if(trainingType == MOMEMTUM_BACKPROPAGATION){
			momemtumBP(patterns, answers, alpha, epochs, offset, length, minerror);
		}else if(trainingType == RESILENT_BACKPROPAGATION){
			resilentBP(patterns, answers, alpha, epochs, offset, length, minerror);
		}else{
			standardBP(patterns, answers, alpha, epochs, offset, length, minerror);
		}
		
		if(progress!=null) progress.setValue(1);
	}

	private void standardBP(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror){
		int[] sort = new int[length];
		double error=error(patterns,answers,offset,length),prevError=error;
		Matrix temp2=new Matrix(answers[0].getRows(),answers[0].getColumns()),
				e=new Matrix(answers[0].getRows(),answers[0].getColumns());

		for(int i=0;i<length;i++){
			sort[i] = i;
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
					d[l].multiply(Yt[l-1],M[l]);
					W[l].subtract(M[l],W[l]);
					b[l].subtract(d[l],b[l]);
				}
			}

			error /= length;
			prevError=error;

			if(plotter!=null) plotter.setError(epochs, error);
			if(progress!=null) progress.setValue(-epochs);
		}
	}

	private void momemtumBP(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror){
		int[] sort = new int[length];
		double error=error(patterns,answers,offset,length),prevError=error;
		Matrix temp2=new Matrix(answers[0].getRows(),answers[0].getColumns()),
				e=new Matrix(answers[0].getRows(),answers[0].getColumns());
		Matrix temp3;
		double beta = params[0];
		
		for(int i=0;i<length;i++){
			sort[i] = i;
		}
		
		Matrix Wprev[] = new Matrix[layers];
		Matrix bprev[] = new Matrix[layers];//momemtum
		for(int i=1;i<layers;i++){
			Wprev[i]=new Matrix(nperlayer[i],nperlayer[i-1]);
			bprev[i]=new Matrix(nperlayer[i],1);
			W[i].copy(Wprev[i]);
			b[i].copy(bprev[i]);
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

				for(int l=1;l<layers;l++){
					Y[l-1].transpose(Yt[l-1]);
					temp3 = new Matrix(d[l].getRows(),Y[l-1].getRows());

					d[l].multiply(1-beta,d[l]);			//(1-beta)*alpha.d[i]
					d[l].multiply(Yt[l-1],temp3);		//(1-beta)*alpha.d[i].Y[i-1]^t

					//W[i]=W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t
					W[l].subtractAndCopy(Wprev[l],M[l],Wprev[l]);//(W[i]-Wprev[i]), WPrev[l]=W[l]
					M[l].multiplyAndAdd(beta, W[l], W[l]);//W[i] + beta*(W[i]-Wprev[i])
					W[l].subtract(temp3,W[l]);			//W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t

					temp3 = null;
					temp3 = new Matrix(b[l].getRows(),b[l].getColumns());

					//B[i]=B[i]+ beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i];
					b[l].subtractAndCopy(bprev[l], temp3, bprev[l]);//(B[i]-Bprev[i]), Bprev[l] = B[l]
					temp3.multiplyAndAdd(beta, b[l], b[l]);//B[i] + beta*(B[i]-Bprev[i])
					b[l].subtract(d[l],b[l]);		//B[i] + beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i]

					temp3 = null;
				}
			}

			error /= length;
			prevError=error;

			if(plotter!=null) plotter.setError(epochs, error);
			if(progress!=null) progress.setValue(-epochs);
		}
	}

	private void resilentBP(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror){
		int[] sort = new int[length];
		double error=error(patterns,answers,offset,length),prevError=error;
		Matrix e=new Matrix(answers[0].getRows(),answers[0].getColumns());

		double	Nplus = 1.2,
				Nminus = 0.5,
				MaxUpdate = 50,
				MinUpdate = 1e-6,
				InitialUpdate = 0.1;

		for(int i=0;i<length;i++){
			sort[i] = i;
		}
		
		Matrix dacum[] = new Matrix[layers];
		Matrix dacumPrev[] = new Matrix[layers];
		Matrix updates[] = new Matrix[layers];

		Matrix dacumb[] = new Matrix[layers];
		Matrix dacumbPrev[] = new Matrix[layers];
		Matrix updatesb[] = new Matrix[layers];
		
		for(int i=1;i<layers;i++){
			dacum[i]=new Matrix(u[i].getRows(),Y[i-1].getRows());
			dacumPrev[i]=new Matrix(u[i].getRows(),Y[i-1].getRows());
			updates[i]=new Matrix(u[i].getRows(),Y[i-1].getRows());
			updates[i].setValue(InitialUpdate);

			dacumb[i]=new Matrix(nperlayer[i],1);
			dacumbPrev[i]=new Matrix(nperlayer[i],1);
			updatesb[i]=new Matrix(nperlayer[i],1);
			updatesb[i].setValue(0.1);
		}
		
		while(error > minerror && epochs-- > 0){
			//if(error > prevError) break;
			//shuffle patterns
			//shuffle(sort); //is irrelevant! you need them all to calculate the gradient.

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
				for(int j=0;j<u[layers-1].getRows();j++){
					d[layers-1].position(j,0,-2*func[layers-1].getDerivate().eval(u[layers-1].position(j,0))*e.position(j, 0));
				}

				//d[i]=Fi'(<W[i],Y[i-1]>).W[i+1]^t.d[i+1]
				for(int k=layers-2;k>0;k--){
					for(int j=0;j<u[k].getRows();j++){
						double acum = 0;
						for(int t=0; t<W[k+1].getRows(); t++)
							acum += W[k+1].position(t,j)*d[k+1].position(t,0);
						d[k].position(j, 0, acum*func[k].getDerivate().eval(u[k].position(j,0)));//cual es el valor que se agrega al delta?
					}
				}
				
				for(int l=1;l<layers;l++){
					Y[l-1].transpose(Yt[l-1]);
					d[l].multiply(Yt[l-1],M[l]);
					dacum[l].add(M[l],dacum[l]);
					dacumb[l].add(d[l],dacumb[l]);
				}
			}

			//actualizar pesos y umbrales
			for(int l=1;l<layers;l++){
				for(int i=0;i<W[l].getRows();i++){
					for(int j=0;j<W[l].getColumns();j++){
						double change = dacum[l].position(i, j)*dacumPrev[l].position(i, j);
						double sign = dacum[l].position(i,j) > 0 ? 1 : -1;
						if(change > 0){
							updates[l].position(i,j,Math.min(updates[l].position(i,j)*Nplus, MaxUpdate));
							W[l].position(i,j,W[l].position(i, j)+ (-sign*updates[l].position(i,j)));
							dacumPrev[l].position(i, j, dacum[l].position(i,j));
						}else if(change < 0){
							updates[l].position(i,j, Math.max(updates[l].position(i,j)*Nminus, MinUpdate));
							dacumPrev[l].position(i,j,0);
						}else{
							W[l].position(i,j,W[l].position(i, j)+ (-sign*updates[l].position(i,j)));
							dacumPrev[l].position(i, j, dacum[l].position(i,j));
						}
						dacum[l].position(i,j,0);
					}

					for(int j=0;j<b[l].getColumns();j++){
						double change = dacumb[l].position(i, j)*dacumbPrev[l].position(i, j);
						double sign = dacumb[l].position(i,j) > 0 ? 1 : -1;
						if(change > 0){
							updatesb[l].position(i,j,Math.min(updatesb[l].position(i,j)*Nplus, MaxUpdate));
							b[l].position(i,j,b[l].position(i, j)+ (-sign*updatesb[l].position(i,j)));
							dacumbPrev[l].position(i, j, dacumb[l].position(i,j));
						}else if(change < 0){
							updatesb[l].position(i,j, Math.max(updatesb[l].position(i,j)*Nminus, MinUpdate));
							dacumbPrev[l].position(i,j,0);
						}else{
							b[l].position(i,j,b[l].position(i, j)+ (-sign*updatesb[l].position(i,j)));
							dacumbPrev[l].position(i, j, dacumb[l].position(i,j));
						}
						dacumb[l].position(i,j,0);
					}
				}
			}

			error /= length;
			prevError=error;

			if(plotter!=null) plotter.setError(epochs, error);
			if(progress!=null) progress.setValue(-epochs);
		}
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
	@SuppressWarnings("empty-statement")
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
