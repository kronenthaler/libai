import java.io.*;

/**
 *
 * @author kronenthaler
 */
public class MainDemos {
	public static void main(String[] args) {
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			while(true){
				System.out.println("Choose one demo option:");
				System.out.println("1. Demo Adaline");
				System.out.println("2. Demo MLP (Multi Layer Perceptron)");
				System.out.println("3. Demo RBF (Radial Basis Functions)");
				System.out.println("4. Demo LVQ (Learning Quantification Vector)");
				System.out.println("5. Demo Competitive");
				System.out.println("6. Demo Kohonen Self-organizative Maps");

				int option = Integer.parseInt(in.readLine());

				switch(option){
					case 1:
						DemoAdaline.main(null);
					break;
					case 2:
						DemoMLP.main(null);
					break;
					case 3:
						DemoRBF.main(null);
					break;
					case 4:
						DemoLVQ.main(null);
					break;
					case 5:
						DemoCompetitive.main(null);
					break;
					case 6:
						DemoKohonen.main(null);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
