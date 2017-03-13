package libai.nn.supervised.backpropagation;

import libai.common.matrix.Column;
import libai.common.matrix.Matrix;

/**
 * Created by kronenthaler on 18/01/2017.
 */
public class ResilientBackpropagation extends StandardBackpropagation {
	protected double nPlus,
			nMinus,
			minUpdate,
			maxUpdate,
			initialUpdate;

	public ResilientBackpropagation() {
		this(1.2, 0.5, 1e-6, 50, 0.1);
	}

	public ResilientBackpropagation(double nPlus, double nMinus, double minUpdate, double maxUpdate, double initialUpdate) {
		this.nPlus = nPlus;
		this.nMinus = nMinus;
		this.minUpdate = minUpdate;
		this.maxUpdate = maxUpdate;
		this.initialUpdate = initialUpdate;
	}

	@Override
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort = new int[length];
		double error = nn.error(patterns, answers, offset, length);
		Matrix e = new Matrix(answers[0].getRows(), answers[0].getColumns());

		for (int i = 0; i < length; i++) {
			sort[i] = i;
		}

		Matrix dacum[] = new Matrix[layers];
		Matrix dacumPrev[] = new Matrix[layers];
		Matrix updates[] = new Matrix[layers];

		Matrix dacumb[] = new Matrix[layers];
		Matrix dacumbPrev[] = new Matrix[layers];
		Matrix updatesb[] = new Matrix[layers];

		for (int i = 1; i < layers; i++) {
			dacum[i] = new Matrix(u[i].getRows(), Y[i - 1].getRows());
			dacumPrev[i] = new Matrix(u[i].getRows(), Y[i - 1].getRows());
			updates[i] = new Matrix(u[i].getRows(), Y[i - 1].getRows());
			updates[i].setValue(initialUpdate);

			dacumb[i] = new Column(nperlayer[i]);
			dacumbPrev[i] = new Column(nperlayer[i]);
			updatesb[i] = new Column(nperlayer[i]);
			updatesb[i].setValue(0.1);
		}

		for (int currentEpoch = 0; currentEpoch < epochs && error > minerror; currentEpoch++) {
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
				for (int j = 0; j < u[layers - 1].getRows(); j++) {
					d[layers - 1].position(j, 0, -2 * func[layers - 1].getDerivate().eval(u[layers - 1].position(j, 0)) * e.position(j, 0));
				}

				//d[i]=Fi'(<W[i],Y[i-1]>).W[i+1]^t.d[i+1]
				for (int k = layers - 2; k > 0; k--) {
					for (int j = 0; j < u[k].getRows(); j++) {
						double acum = 0;
						for (int t = 0; t < W[k + 1].getRows(); t++)
							acum += W[k + 1].position(t, j) * d[k + 1].position(t, 0);
						d[k].position(j, 0, acum * func[k].getDerivate().eval(u[k].position(j, 0)));
					}
				}

				for (int l = 1; l < layers; l++) {
					Y[l - 1].transpose(Yt[l - 1]);
					d[l].multiply(Yt[l - 1], M[l]);
					dacum[l].add(M[l], dacum[l]);
					dacumb[l].add(d[l], dacumb[l]);
				}
			}

			//update weights and thresholds
			for (int l = 1; l < layers; l++) {
				for (int i = 0; i < W[l].getRows(); i++) {
					for (int j = 0; j < W[l].getColumns(); j++) {
						double change = dacum[l].position(i, j) * dacumPrev[l].position(i, j);
						double sign = dacum[l].position(i, j) > 0 ? 1 : -1;
						if (change > 0) {
							updates[l].position(i, j, Math.min(updates[l].position(i, j) * nPlus, maxUpdate));
							W[l].increment(i, j, (-sign * updates[l].position(i, j)));
							dacumPrev[l].position(i, j, dacum[l].position(i, j));
						} else if (change < 0) {
							updates[l].position(i, j, Math.max(updates[l].position(i, j) * nMinus, minUpdate));
							dacumPrev[l].position(i, j, 0);
						} else {
							W[l].increment(i, j, (-sign * updates[l].position(i, j)));
							dacumPrev[l].position(i, j, dacum[l].position(i, j));
						}
						dacum[l].position(i, j, 0);
					}

					for (int j = 0; j < b[l].getColumns(); j++) {
						double change = dacumb[l].position(i, j) * dacumbPrev[l].position(i, j);
						double sign = dacumb[l].position(i, j) > 0 ? 1 : -1;
						if (change > 0) {
							updatesb[l].position(i, j, Math.min(updatesb[l].position(i, j) * nPlus, maxUpdate));
							b[l].increment(i, j, (-sign * updatesb[l].position(i, j)));
							dacumbPrev[l].position(i, j, dacumb[l].position(i, j));
						} else if (change < 0) {
							updatesb[l].position(i, j, Math.max(updatesb[l].position(i, j) * nMinus, minUpdate));
							dacumbPrev[l].position(i, j, 0);
						} else {
							b[l].increment(i, j, (-sign * updatesb[l].position(i, j)));
							dacumbPrev[l].position(i, j, dacumb[l].position(i, j));
						}
						dacumb[l].position(i, j, 0);
					}
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
