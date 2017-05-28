package libai.fuzzy.modifiers;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class Intensify extends Modifier {
	@Override
	public double eval(double y) {
		if (0.0 <= y && y <= 0.5) return 2 * Math.pow(y, 2);
		return 1 - 2 * Math.pow(1-y,2);
	}
}
