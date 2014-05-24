package libai.common;

/**
 *
 * @author ignacio
 */
public final class Triplet<V extends Comparable, K extends Comparable, Z extends Comparable> {
    /**
     * First element of the tuple.
     */
    public V first;

    /**
     * Second element of the tuple.
     */
    public K second;

    /**
     * Third element of the tuple.
     */
    public Z third;

    /**
     * Constructor.
     *
     * @param x the first element for the tuple.
     * @param y the second element for the tuple.
     * @param z the third element for the tuple.
     */
    public Triplet(V x, K y, Z z) {
        first = x;
        second = y;
        third = z;
    }

    @Override
    public int hashCode() {
        int n = Integer.MAX_VALUE;
        int a = ((first.hashCode() % n) + (second.hashCode() % n)) % n;
        int b = ((a % n) + (third.hashCode() % n)) % n;
        return b;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Triplet))
            return false;

        Triplet<V, K, Z> b = (Triplet<V, K, Z>) o;
        return first.equals(b.first)
                && second.equals(b.second)
                && third.equals(b.third);
    }

    public String toString() {
        return "(" + first + "," + second + "," + third + ")";
    }

}
