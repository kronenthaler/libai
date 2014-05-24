package libai.common.functions;

/**
 * Sigmoid function: f(x) = 1/(1+e^-x) The first derivate of the sigmoid
 * function S(x) S'(x) = S(x)*(1-S(x))
 *
 * @author kronenthaler
 */
public class Sigmoid implements Function {
    private static Function derivate;

    public double eval(double x) {
        return (1.0 / (1.0 + Math.exp(-x)));
    }

    public Function getDerivate() {
        if (derivate == null) {
            final Sigmoid me = this;
            derivate = new Function() {
                public double eval(double x) {
                    double a = me.eval(x);
                    return a * (1.0 - a);
                }

                public Function getDerivate() {
                    return null;
                }
            };
        }

        return derivate;
    }
}
