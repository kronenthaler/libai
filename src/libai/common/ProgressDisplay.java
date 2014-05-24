package libai.common;

/**
 *
 * @author kronenthaler
 */
public interface ProgressDisplay {
    public void setMinimum(int v);

    public void setMaximum(int v);

    public void setValue(int v);
}
