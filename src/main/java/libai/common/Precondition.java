package libai.common;

/**
 * Created by kronenthaler on 09/03/2017.
 */
public final class Precondition {
	public static final void check(boolean expression, String message, Object... params){
		if (!expression){
			throw new IllegalArgumentException(String.format(message, params));
		}
	}
}
