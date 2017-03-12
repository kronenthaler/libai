package libai.common.matrix;

/**
 * Created by kronenthaler on 12/03/2017.
 */
public class Column extends Matrix {
	public Column(int r) {
		super(r, 1);
	}

	public Column(int r, boolean identity){
		super(r, 1, true);
	}

	public Column(int r, double[] data) {
		super(r, 1, data);
	}

	public Column(Matrix copy) {
		super(copy);
	}

	public Row transpose(){
		Row result = new Row(rows);
		transpose(result);
		return result;
	}

	public void transpose(Row result){
		System.arraycopy(matrix, 0, result.matrix, 0, matrix.length);
	}
}
