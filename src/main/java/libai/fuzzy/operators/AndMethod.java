package libai.fuzzy.operators;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public abstract class AndMethod extends Operator {
	public static final AndMethod PROD = new ProdMethod();
	public static final AndMethod MIN = new MinMethod();

	public static AndMethod fromString(String name){
		if (MIN.toString().equalsIgnoreCase(name))
			return MIN;
		if (PROD.toString().equalsIgnoreCase(name))
			return PROD;

		throw new UnsupportedOperationException("Unsupported AndMethod: "+name);
	}
}
