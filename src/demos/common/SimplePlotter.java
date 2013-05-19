package demos.common;

import libai.common.Pair;
import libai.common.Plotter;
import java.awt.*;
import java.util.ArrayList;

public class SimplePlotter extends Canvas implements Plotter{
	private ArrayList<Pair<Integer, Double>> errors;
	private double min, max;
	private int pixels = 3;

	public SimplePlotter() {
		errors = new ArrayList<Pair<Integer, Double>>();
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
	}

	@Override
	public synchronized void paint(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		Rectangle r = g.getClipBounds();
		int x = (int) r.getX();
		int w = getWidth();
		int h = getHeight();

		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.lightGray);
		g.drawLine(10, 10, 10, h - 20);	  //y-axis
		g.drawLine(10, h - 20, w - 20, h - 20); //zero

		//normalizar los valores a 0-1, segun min y max.
		if (errors.size() > w) { //estirar la ventana...
			Dimension d = new Dimension(errors.size(), h); //ancho igual a la cantidad de elementos * 3pixels
			setSize(d);
			setPreferredSize(d);
			invalidate();
		}

		if (!errors.isEmpty()) {
			g.setColor(Color.blue);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//g.setStroke(new BasicStroke(2));
			//y = a*t + (1-t)*b
			double t = (errors.get(x).second - min) / (max - min);
			int y = (int) (10 * t + (1 - t) * (h - 20));
			int inc = 1;
			for (int i = x + 1, n = Math.min(i + (int) r.getWidth(), errors.size()); i < n; i += inc) {
				t = (errors.get(i).second - min) / (max - min);
				int y1 = (int) (10 * t + (1 - t) * (h - 20));
				g.drawLine(i + 10, y, (i + inc) + 10, y1);
				y = y1;
			}
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void setError(int epoch, double error) {
		errors.add(new Pair<Integer, Double>(epoch, error));
		min = Math.min(min, error);
		max = Math.max(max, error);

		//System.out.println(error);
		//invalidate();
		//repaint();
	}
}