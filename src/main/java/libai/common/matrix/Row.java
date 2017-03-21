package libai.common.matrix;

import java.util.Random;

/**
 * Created by kronenthaler on 12/03/2017.
 */
public class Row extends Matrix {
	private static final long serialVersionUID = 471750854178537656L;

	public Row(int c) {
		super(1, c);
	}

	public Row(int c, double[] data) {
		super(1, c, data);
	}

	public Row(Row copy) {
		super(copy);
	}

	@Override
	public Column transpose() {
		Column result = new Column(columns);
		transpose(result);
		return result;
	}

	public void transpose(Column result) {
		System.arraycopy(matrix, 0, result.matrix, 0, matrix.length);
	}

	public static Row random(int c) {
		return random(c, true);
	}

	public static Row random(int c, boolean signed) {
		return random(c, signed, getDefaultRandom());
	}

	public static Row random(int c, boolean signed, Random rand) {
		Row ret = new Row(c);
		ret.fill(signed, rand);
		return ret;
	}
}
