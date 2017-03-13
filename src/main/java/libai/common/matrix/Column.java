package libai.common.matrix;

import java.util.Random;

/**
 * Created by kronenthaler on 12/03/2017.
 */
public class Column extends Matrix {
	private static final long serialVersionUID = 836410303513443555L;

	public Column(int r) {
		super(r, 1);
	}

	public Column(int r, boolean identity) {
		super(r, 1, true);
	}

	public Column(int r, double[] data) {
		super(r, 1, data);
	}

	public Column(Matrix copy) {
		super(copy);
	}

	@Override
	public Row transpose() {
		Row result = new Row(rows);
		transpose(result);
		return result;
	}

	public void transpose(Row result) {
		System.arraycopy(matrix, 0, result.matrix, 0, matrix.length);
	}

	public static Column random(int r) {
		return random(r, true);
	}

	public static Column random(int r, boolean signed) {
		return random(r, signed, getDefaultRandom());
	}

	public static Column random(int r, boolean signed, Random rand) {
		Column ret = new Column(r);
		ret.fill(signed, rand);
		return ret;
	}

}
