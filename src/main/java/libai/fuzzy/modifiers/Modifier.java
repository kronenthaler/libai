package libai.fuzzy.modifiers;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public abstract class Modifier {
	public static final Modifier EXTREMELY = new Extremely();
	public static final Modifier INTESIFY = new Intensify();
	public static final Modifier MORE_OR_LESS = new MoreOrLess();
	public static final Modifier NOT = new Not();
	public static final Modifier PLUS = new Plus();
	public static final Modifier SOMEWHAT = new Somewhat();
	public static final Modifier VERY = new Very();

	public abstract double eval(double y);

	public static Modifier fromString(String name){
		if (EXTREMELY.toString().equalsIgnoreCase(name))
			return EXTREMELY;
		if (INTESIFY.toString().equalsIgnoreCase(name))
			return INTESIFY;
		if (MORE_OR_LESS.toString().equalsIgnoreCase(name))
			return MORE_OR_LESS;
		if (NOT.toString().equalsIgnoreCase(name))
			return NOT;
		if (PLUS.toString().equalsIgnoreCase(name))
			return PLUS;
		if (SOMEWHAT.toString().equalsIgnoreCase(name))
			return SOMEWHAT;
		if (VERY.toString().equalsIgnoreCase(name))
			return VERY;

		throw new UnsupportedOperationException("Unsupported Modifier: "+name);
	}

	@Override
	public String toString() { return getClass().getSimpleName().toLowerCase(); }
}
