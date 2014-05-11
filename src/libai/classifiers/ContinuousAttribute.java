package libai.classifiers;

/**
 *
 * @author kronenthaler
 */
public class ContinuousAttribute extends Attribute {
	private final double value;

	public ContinuousAttribute(double v) {
		value = v;
	}

	public ContinuousAttribute(String name, double v) {
		this.name = name;
		value = v;
	}

	@Override
	public int compareTo(Attribute o) {
		if (value > ((ContinuousAttribute) o).value)
			return 1;
		if (value < ((ContinuousAttribute) o).value)
			return -1;
		return 0;
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "["+name+"]=" + value;
	}
}
