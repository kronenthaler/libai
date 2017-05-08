package libai.fuzzy2.operators;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public abstract class OrMethod extends Operator{
	public static final OrMethod PROBOR = new ProbOrMethod();
	public static final OrMethod MAX = new MaxMethod();

	public static OrMethod fromString(String name){
		if (MAX.toString().equalsIgnoreCase(name))
			return MAX;
		if (PROBOR.toString().equalsIgnoreCase(name))
			return PROBOR;

		throw new UnsupportedOperationException("Unsupported OrMethod: "+name);
	}
}
