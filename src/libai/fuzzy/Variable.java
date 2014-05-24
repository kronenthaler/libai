package libai.fuzzy;

/**
 * Wrapper around a double value. This implementation allows, change the type of
 * the double, for other types more precises or maybe any type suitable for
 * embebbed devices without change the structure of the rest of the engine.
 *
 * @author kronenthaler
 */
public class Variable implements Comparable<Variable> {
    /**
     * Value of the variable
     */
    private double value;

    /**
     * Constructor. Creates a new variable with the value v.
     *
     * @param v The initial value for the variable.
     */
    public Variable(double v) {
        setValue(v);
    }

    /**
     * Set a new value v for the variable.
     *
     * @param v The new value for the variable.
     */
    public void setValue(double v) {
        value = v;
    }

    /**
     * Get the current value for the variable.
     *
     * @return The current value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Compare two variables.
     *
     * @param o the variable to compare.
     * @return -1 if this is less than o, 0 if they are equal, 1 if this is
     * grater than o
     */
    public int compareTo(Variable o) {
        return (int) (value - o.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
