package libai.fuzzy2.modifiers;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class Plus extends Modifier {
	@Override
	public double eval(double y) {
		return Math.pow(y, 1.25);
	}
}
