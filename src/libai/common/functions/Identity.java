package libai.common.functions;

/**
 * Identity function f(x) = x; The first derivate of I(x), I'(x) = 1.
 *
 * @author kronenthaler
 */
public class Identity implements Function {
    /**
     * Singleton variable for the first derivate.
     */
    private static Function derivate;

    public double eval(double x) {
        return x;
    }

    public Function getDerivate() {
        if (derivate == null) {
            derivate = new Function() {
                public double eval(double x) {
                    return 1;
                }

                public Function getDerivate() {
                    return null;
                }
            };
        }

        return derivate;
    }
}
