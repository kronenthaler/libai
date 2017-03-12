package libai.common.matrix;

/**
 * Created by kronenthaler on 12/03/2017.
 */
public class Row extends Matrix {
	public Row(int c) {
		super(1, c);
	}

	public Row(int c, double[] data) {
		super(1, c, data);
	}

	public Row(Row copy) {
		super(copy);
	}

	public Column transpose(){
		Column result = new Column(columns);
		transpose(result);
		return result;
	}

	public void transpose(Column result){
		System.arraycopy(matrix, 0, result.matrix, 0, matrix.length);
	}
}
