import net.sf.libai.nn.supervised.*;
import net.sf.libai.nn.unsupervised.*;
import net.sf.libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class DemoCompetitive {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		int n=6;
		int m=2;
		int l=3;
		Matrix[] p=new Matrix[n];
		Matrix[] t=new Matrix[n];
		for(int i=0;i<n;i++){
			p[i]=new Matrix(m,1);
			t[i]=new Matrix(l,1);
			t[i].setValue(0);
		}

		p[0].position(0,0,-1);
		p[0].position(1,0,6);
		p[1].position(0,0,1);
		p[1].position(1,0,6);
		p[2].position(0,0,6);
		p[2].position(1,0,2);
		p[3].position(0,0,6);
		p[3].position(1,0,-1);
		p[4].position(0,0,-5);
		p[4].position(1,0,3);
		p[5].position(0,0,-3);
		p[5].position(1,0,-6);

		t[0].position(0,0,1);
		t[1].position(0,0,1);
		t[2].position(1,0,1);
		t[3].position(1,0,1);
		t[4].position(2,0,1);
		t[5].position(2,0,1);

		Competitive net = new Competitive(m,l);
		
		net.train(p,t,0.1,10000);
		
		System.out.printf("Error: %.8f\n",net.error(p, t));

		for(int i=0;i<n;i++)
			System.out.println(net.simulate(p[i]));

    }

}
