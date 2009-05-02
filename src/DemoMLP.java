import javax.swing.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;
import net.sf.libai.nn.supervised.*;
/**
 *
 * @author kronenthaler
 */
public class DemoMLP {
	static double f(double x){
		return Math.sin(x) + Math.cos(x);
	}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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

		int n=40;
		int m=1;
		int l=1;
		int test=12;
		Matrix []p=new Matrix[n+test];
		Matrix []t=new Matrix[n+test];
		double delta=0.1;
		double x=0;
		for(int i=0;i<n;i++,x+=delta){
			p[i]=new Matrix(m,1);
			t[i]=new Matrix(l,1);

			p[i].position(0,0,x);
			t[i].position(0,0,f(x));
		}

		delta=0.33;
		x=0;
		for(int i=n;i<n+test && x<4;i++,x+=delta){
			p[i]=new Matrix(m,1);
			t[i]=new Matrix(l,1);

			p[i].position(0,0,x);
			t[i].position(0,0,f(x));
		}

		int nperlayer[]={m,4,l};
		MLP net = new MLP(nperlayer,
							new Function[]{NeuralNetwork.identity,NeuralNetwork.sigmoid,NeuralNetwork.identity},
							-0.4);
		net.setPlotter(plotter);
		net.train(p, t, 0.2,50000, 0, n);

		System.out.printf("Error: %.8f\n",net.error(p,t,n,test));

		for(int i=n;i<p.length;i++){
			System.out.println(net.simulate(p[i]).position(0, 0) +" vs "+ t[i].position(0, 0));
		}

		System.exit(0);
	}

}
