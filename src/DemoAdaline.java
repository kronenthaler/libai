import javax.swing.*;

import net.sf.libai.nn.supervised.Adaline;
import net.sf.libai.common.*;
import net.sf.libai.nn.*;

/**
 *
 * @author kronenthaler
 */
public class DemoAdaline {
	public static void main(String arg[]){
		Plotter plotter = new Plotter();
		JFrame frame = new JFrame("map");
		frame.add(new JScrollPane(plotter));
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
               System.exit(0);
            }
        });
		frame.setSize(500,200);
		frame.setVisible(true);

		int n = 40;
		int t = 10;

		Matrix[] patterns = new Matrix[n+t];
		Matrix[] ans = new Matrix[n+t];

		for(int i=0;i<n;i++){
			patterns[i] = new Matrix(1,1, new double[]{i+1});
			ans[i] = new Matrix(1,1, new double[]{(2*(i+1))+3 });
		}

		for(int i=n;i<n+t;i++){
			patterns[i] = new Matrix(1,1, new double[]{i+1.33});
			ans[i] = new Matrix(1,1, new double[]{(2*(i+1.33))+3 });
		}
		
		NeuralNetwork net = new Adaline(1,1);
		net.setPlotter(plotter);
		net.train(patterns, ans, 0.001, 1000, 0, n);

		System.out.printf("Error: %.8f\n",net.error(patterns, ans, n, t));

		for(int i=n;i<patterns.length;i++){
			System.out.println((i+1)+") "+net.simulate(patterns[i]).position(0,0));
		}
	}
}
