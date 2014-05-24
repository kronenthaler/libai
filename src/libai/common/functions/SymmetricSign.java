package libai.common.functions;

/**
 * Symmetric Signum function F(x) = -1 if x &lt; 0 or 1 if x &gt;= 1 This
 * function is not derivable.
 *
 * @author kronenthaler
 */
public class SymmetricSign implements Function {
    public double eval(double x) {
        return x < 0 ? -1 : 1;
    }

    public Function getDerivate() {
        return null;
    }
}
