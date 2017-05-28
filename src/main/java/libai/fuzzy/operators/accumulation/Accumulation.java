package libai.fuzzy.operators.accumulation;

import libai.fuzzy.operators.Operator;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public abstract class Accumulation extends Operator {
	public static final Accumulation MAX = new MaxMethod();
	public static final Accumulation SUM = new SumMethod();

	@Override
	public double neutral() {
		return 0;
	}

	public static Accumulation fromString(String name){
		if (MAX.toString().equalsIgnoreCase(name))
			return MAX;
		if (SUM.toString().equalsIgnoreCase(name))
			return SUM;

		throw new UnsupportedOperationException("Unsupported Accumulation: "+name);
	}
}
