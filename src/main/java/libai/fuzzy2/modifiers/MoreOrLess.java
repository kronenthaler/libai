package libai.fuzzy2.modifiers;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class MoreOrLess extends Modifier {
	@Override
	public double eval(double y) {
		return Math.pow(y, 1/3.);
	}

	@Override
	public String toString() { return "MORE_OR_LESS"; }
}
