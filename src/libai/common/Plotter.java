package libai.common;

import java.util.*;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author kronenthaler
 */
public interface Plotter {
    public void paint(Graphics g2);

    public void update(Graphics g2);

    public void setError(int epoch, double error);
}
