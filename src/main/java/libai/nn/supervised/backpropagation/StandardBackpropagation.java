package libai.nn.supervised.backpropagation;

import libai.common.matrix.Matrix;
import libai.common.functions.Function;
import libai.nn.NeuralNetwork;

/**
 * Created by kronenthaler on 08/01/2017.
 */
public class StandardBackpropagation implements Backpropagation{
	protected NeuralNetwork nn;
	protected int[] nperlayer;
	protected int layers;
	protected Function[] func;
	protected Matrix[] W, Y, b, u;
	protected Matrix[] d,  M, Yt; // auxiliary buffers

	@Override
	public void initialize(NeuralNetwork nn, int[] nperlayer, Function[] functions, Matrix[]W, Matrix[]Y, Matrix[]b, Matrix[]u){
		this.nn = nn;
		this.nperlayer = nperlayer;
		this.layers = nperlayer.length;
		this.func = functions;
		this.W = W;
		this.b = b;
		this.Y = Y;
		this.u = u;

		initialize();
	}

	private void initialize(){
		d = new Matrix[layers];//position zero reserved
		Yt = new Matrix[layers];
		M = new Matrix[layers];

		Yt[0] = new Matrix(1, nperlayer[0]);
		for (int i = 1; i < layers; i++) {
			Y[i] = new Matrix(u[i].getRows(), u[i].getColumns());
			Yt[i] = new Matrix(u[i].getColumns(), u[i].getRows());

			M[i] = new Matrix(u[i].getRows(), Y[i - 1].getRows());
		}

		d[layers - 1] = new Matrix(u[layers - 1].getRows(), 1);
		for (int k = layers - 2; k > 0; k--)
			d[k] = new Matrix(u[k].getRows(), 1);
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort = new int[length];
		double error = nn.error(patterns, answers, offset, length);
		Matrix e = new Matrix(answers[0].getRows(), answers[0].getColumns());

		for (int i = 0; i < length; i++) {
			sort[i] = i;
		}

		for(int currentEpoch=0; currentEpoch < epochs && error > minerror; currentEpoch++){
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

				//update weights and thresholds
				for (int l = 1; l < layers; l++) {
					Y[l - 1].transpose(Yt[l - 1]);
					d[l].multiply(Yt[l - 1], M[l]);
					W[l].subtract(M[l], W[l]);
					b[l].subtract(d[l], b[l]);
				}
			}

			error /= length;

			if (nn.getPlotter() != null)
				nn.getPlotter().setError(currentEpoch, error);
			if (nn.getProgressBar() != null)
				nn.getProgressBar().setValue(currentEpoch);
		}
	}
}
