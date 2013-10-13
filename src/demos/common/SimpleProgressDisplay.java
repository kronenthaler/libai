package demos.common;

import libai.common.ProgressDisplay;
import javax.swing.JProgressBar;

/**
 *
 * @author kronenthaler
 */
public class SimpleProgressDisplay implements ProgressDisplay{
	protected JProgressBar progress;
	public SimpleProgressDisplay(JProgressBar pb){
		progress = pb;
	}

	@Override
	public void setMinimum(int v) {
		progress.setMinimum(v);
	}

	@Override
	public void setMaximum(int v) {
		progress.setMaximum(v);
	}

	@Override
	public void setValue(int v) {
		progress.setValue(v);
	}
	
}
