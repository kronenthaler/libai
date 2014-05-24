package libai.fuzzy.sets;

import libai.fuzzy.Variable;
import java.util.ArrayList;

/**
 * Fuzzy set with only one value not equal to zero. The kernel an the support
 * are the same set, containing one single value.
 *
 * @author kronenthaler
 */
public class Singleton implements FuzzySet {
    private Variable p;
    private ArrayList<Double> support;

    public Singleton(Variable _p) {
        p = _p;
        support = new ArrayList<Double>();
        support.add(p.getValue());
    }

    public double eval(Variable s) {
        return eval(s.getValue());
    }

    public double eval(double s) {
        return s == p.getValue() ? 1.0 : 0.0;
    }

    public ArrayList<Double> getSupport() {
        return support;
    }

    public ArrayList<Double> getKernel() {
        return support;
    }

    public String toString() {
        return "Singleton(" + p + ")";
    }
}
