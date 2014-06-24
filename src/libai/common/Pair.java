package libai.common;

/**
 * Pair class allows to keep two elements of possibly different types in the
 * same structure and can be used for keep pairs key-value.
 *
 * @author kronenthaler
 */
public final class Pair<V extends Comparable, K> implements Comparable<Pair> {
    /**
     * First element of the pair (the 'key').
     */
    public V first;
    /**
     * Second element of the pair (the 'value').
     */
    public K second;

    /**
     * Constructor.
     *
     * @param x the first element for the pair.
     * @param y the second element for the pair.
     */
    public Pair(V x, K y) {
        first = x;
        second = y;
    }

    /**
     * Compare two pairs by the first element. The first element type must
     * implement the Comparable interface.
     *
     * @param o The pair to compare with.
     * @return -1 if this is less than o, 0 if are equals, 1 if this is greater
     * than o.
     */
    public int compareTo(Pair o) {
        return first.compareTo(o.first);
    }

    public String toString() {
        return "(" + first + "," + second + ")";
    }
}
