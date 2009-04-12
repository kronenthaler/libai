import net.sf.libai.common.*;
import net.sf.libai.nn.unsupervised.Kohonen;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author kronenthaler
 */
public class DemoKohonen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		int t = 0;
		int r = 0, g = 0, b = 0;
		int delta = 16;

		int n = delta * delta * delta;
		int m = 3; //rgb
		delta = 256 / (delta - 1);
		final int test = n/4;

		final Matrix[] p = new Matrix[n];
		final Matrix[] ex = new Matrix[n];
		int[] classes = new int[n];

		for (r = 0; r <= 255; r += delta) {
			for (g = 0; g <= 255; g += delta) {
				for (b = 0; b <= 255; b += delta) {
					p[t] =new Matrix(3, 1);
					p[t].position(0, 0, r);
					p[t].position(1, 0, g);
					p[t].position(2, 0, b);

					ex[t] = new Matrix(1, 1);
					ex[t].position(0, 0, (classes[t++] = ((r << 16) | (g << 8) | b) & 0x00ffffff));
				}
			}
		}

		p[p.length-1].position(0, 0, 1);
		p[p.length-1].position(1, 0, 1);
		p[p.length-1].position(2, 0, 1);

		ex[p.length-1].position(0, 0, (classes[p.length-1] = ((1 << 16) | (1 << 8) | 1) & 0x00ffffff));
		System.out.println("answ: "+ex[p.length-1]);

		int nperlayer[] = {m, 30, 40};
		final Kohonen net = new Kohonen(nperlayer, 10);
		net.train(p, ex, 1, 40, 0, p.length - test);
		//Matrix<int> result = net->expandMap(n, p, classes, true); //no es necesario pero casi siempre deberia hacerse
		
		final int[][] map = net.getMap();
		System.out.printf("error: %.8f\n", net.error(p, ex, p.length - test, test));

		JFrame frame = new JFrame("map");
		frame.add(new Canvas(){
			public void paint(Graphics gr){
				for(int i=0;i<map.length;i++){
					for(int j=0;j<map[0].length;j++){
						int r=(map[i][j]>>>16)&0x000000ff;
						int g=(map[i][j]>>>8)&0x000000ff;
						int b=map[i][j]&0x000000ff;
						gr.setColor(new Color(r,g,b));
						gr.fillRect(i*5, j*5, 5, 5);
					}
				}
			}
		});
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
               System.exit(0);
            }
        });
		frame.setSize(5*map.length+30,5*map[0].length + 30);
		frame.setVisible(true);
	}
}
