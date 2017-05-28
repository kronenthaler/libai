package libai.fuzzy.operators.activation;

import libai.fuzzy.operators.Operator;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public abstract class ActivationMethod extends Operator {
	public static final ActivationMethod MIN = new MamdaniActivation();
	public static final ActivationMethod PROD = new LarsenActivation();

	@Override
	public double neutral() {
		return 1;
	}

	public static ActivationMethod fromString(String name){
		if (MIN.toString().equalsIgnoreCase(name))
			return MIN;
		if (PROD.toString().equalsIgnoreCase(name))
			return PROD;

		throw new UnsupportedOperationException("Unsupported ActivationMethod: "+name);
	}
}