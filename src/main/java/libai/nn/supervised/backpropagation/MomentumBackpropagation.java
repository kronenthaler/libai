package libai.nn.supervised.backpropagation;

import libai.common.Matrix;
import libai.common.functions.Function;
import libai.nn.NeuralNetwork;

/**
 * Created by kronenthaler on 18/01/2017.
 */
public class MomentumBackpropagation extends StandardBackpropagation {
	private double beta;

	public MomentumBackpropagation(double beta){
		if (beta < 0 || beta >= 1)
			throw new IllegalArgumentException("beta should be positive and less than 1");
		this.beta = beta;
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort = new int[length];
		double error = nn.error(patterns, answers, offset, length);
		Matrix e = new Matrix(answers[0].getRows(), answers[0].getColumns());
		Matrix temp3;

		for (int i = 0; i < length; i++) {
			sort[i] = i;
		}

		Matrix Wprev[] = new Matrix[layers];
		Matrix bprev[] = new Matrix[layers];//momemtum
		for (int i = 1; i < layers; i++) {
			Wprev[i] = new Matrix(nperlayer[i], nperlayer[i - 1]);
			bprev[i] = new Matrix(nperlayer[i], 1);
			W[i].copy(Wprev[i]);
			b[i].copy(bprev[i]);
		}

		while (error > minerror && epochs-- > 0) {
			//shuffle patterns
			nn.shuffle(sort);

			error = 0;
			for (int i = 0; i < length; i++) {
				//Y[i]=Fi(<W[i],Y[i-1]>+b)
				nn.simulate(patterns[sort[i] + offset]);

				//e=-2(t-Y[n-1])
				answers[sort[i] + offset].subtract(Y[layers - 1], e);

				//calculate the error
				for (int m = 0; m < nperlayer[layers - 1]; m++)
					error += (e.position(m, 0) * e.position(m, 0));

				//d[0] = F0'(<W[i],Y[i-1]>).e
				for (int j = 0; j < u[layers - 1].getRows(); j++)
					d[layers - 1].position(j, 0, -2 * alpha * func[layers - 1].getDerivate().eval(u[layers - 1].position(j, 0)) * e.position(j, 0));

				//d[i]=Fi'(<W[i],Y[i-1]>).W[i+1]^t.d[i+1]
				for (int k = layers - 2; k > 0; k--) {
					for (int j = 0; j < u[k].getRows(); j++) {
						double acum = 0;
						for (int t = 0; t < W[k + 1].getRows(); t++)
							acum += W[k + 1].position(t, j) * d[k + 1].position(t, 0);
						d[k].position(j, 0, alpha * acum * func[k].getDerivate().eval(u[k].position(j, 0)));
					}
				}

				for (int l = 1; l < layers; l++) {
					Y[l - 1].transpose(Yt[l - 1]);
					temp3 = new Matrix(d[l].getRows(), Y[l - 1].getRows());

					d[l].multiply(1 - beta, d[l]);			//(1-beta)*alpha.d[i]
					d[l].multiply(Yt[l - 1], temp3);		//(1-beta)*alpha.d[i].Y[i-1]^t

					//W[i]=W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t
					W[l].subtractAndCopy(Wprev[l], M[l], Wprev[l]);//(W[i]-Wprev[i]), WPrev[l]=W[l]
					M[l].multiplyAndAdd(beta, W[l], W[l]);//W[i] + beta*(W[i]-Wprev[i])
					W[l].subtract(temp3, W[l]);			//W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t

					temp3 = null;
					temp3 = new Matrix(b[l].getRows(), b[l].getColumns());

					//B[i]=B[i]+ beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i];
					b[l].subtractAndCopy(bprev[l], temp3, bprev[l]);//(B[i]-Bprev[i]), Bprev[l] = B[l]
					temp3.multiplyAndAdd(beta, b[l], b[l]);//B[i] + beta*(B[i]-Bprev[i])
					b[l].subtract(d[l], b[l]);		//B[i] + beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i]

					temp3 = null;
				}
			}

			error /= length;

			if (nn.getPlotter() != null)
				nn.getPlotter().setError(epochs, error);
			if (nn.getProgressBar() != null)
				nn.getProgressBar().setValue(-epochs);
		}
	}
}
