package libai.fuzzy2.modifiers;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class Not extends Modifier {
	@Override
	public double eval(double y) {
		return 1 - y;
	}
}
