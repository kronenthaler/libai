package libai.experimental;

import libai.classifiers.trees.*;
import libai.classifiers.*;
import java.io.*;
import java.util.*;
import java.math.*;

/**
 *
 * @author kronenthaler
 */
public class TreesTest {
	String[][] data;
	int output;

	public static void main(String[] args) {
		new TreesTest().doitC45();
	}

	public void doitC45() {
		DataSet ds = new DataSet(0,
				new DataRecord(new DiscreteAttribute("outcome", "no"), new ContinuousAttribute("val", 65)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 64)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 68)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 69)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 70)),
				new DataRecord(new DiscreteAttribute("outcome", "no"), new ContinuousAttribute("val", 71)),
				new DataRecord(new DiscreteAttribute("outcome", "no"), new ContinuousAttribute("val", 72)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 72)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 75)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 75)),
				new DataRecord(new DiscreteAttribute("outcome", "no"), new ContinuousAttribute("val", 80)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 81)),
				new DataRecord(new DiscreteAttribute("outcome", "yes"), new ContinuousAttribute("val", 83)),
				new DataRecord(new DiscreteAttribute("outcome", "no"), new ContinuousAttribute("val", 85)));
		DataSet sets[] = ds.splitKeepingRelation(1);
		sets[0].print("");
		sets[1].print("");
		/*C45 tree = C45.getInstance(ds);
		 tree.print();
		 System.out.println("error: "+tree.error(ds));*/
	}

	public void doitMagic() {
		try {
			DataSet ds = new DataSet(10);
			BufferedReader in = new BufferedReader(new FileReader("magic.data"));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				String toks[] = line.split(",");
				Attribute attrs[] = new Attribute[toks.length];
				for (int i = 0; i < attrs.length; i++) {
					try {
						attrs[i] = new ContinuousAttribute("" + i, Double.parseDouble(toks[i]));
					} catch (NumberFormatException e1) {
						attrs[i] = new DiscreteAttribute("" + i, toks[i]);
					}
				}
				ds.addRecord(new DataRecord(attrs));
			}
			in.close();
			//*/

			//DataSet ret[] = ds.generateTrainningTestSets(0.3);
			//System.out.println(ret[0].getRecordCount()+" "+ret[1].getRecordCount());

			C45 tree = C45.getInstance(ds);
			//tree.print();
			System.out.println("error before prune: " + tree.error(ds));
			tree.prune(ds, C45.QUINLANS_PRUNE);
			System.out.println("error after prune: " + tree.error(ds));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doitPrune() {
		DataSet ds = new DataSet(4, new String[]{"a", "b", "c", "d", "out"},
				new DataRecord(new DiscreteAttribute("t"), new DiscreteAttribute("f"), new DiscreteAttribute("x"), new DiscreteAttribute("x"), new DiscreteAttribute("c1")),
				new DataRecord(new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("x"), new DiscreteAttribute("x"), new DiscreteAttribute("c1")),
				new DataRecord(new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("x"), new DiscreteAttribute("x"), new DiscreteAttribute("c1")),
				new DataRecord(new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("x"), new DiscreteAttribute("x"), new DiscreteAttribute("c1")),
				new DataRecord(new DiscreteAttribute("f"), new DiscreteAttribute("x"), new DiscreteAttribute("f"), new DiscreteAttribute("x"), new DiscreteAttribute("c1")),
				new DataRecord(new DiscreteAttribute("f"), new DiscreteAttribute("x"), new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("c1")),
				new DataRecord(new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("x"), new DiscreteAttribute("x"), new DiscreteAttribute("c2")),
				new DataRecord(new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("x"), new DiscreteAttribute("x"), new DiscreteAttribute("c2")),
				new DataRecord(new DiscreteAttribute("f"), new DiscreteAttribute("x"), new DiscreteAttribute("t"), new DiscreteAttribute("t"), new DiscreteAttribute("c2")),
				new DataRecord(new DiscreteAttribute("f"), new DiscreteAttribute("x"), new DiscreteAttribute("t"), new DiscreteAttribute("f"), new DiscreteAttribute("c2")));

		C45 tree = C45.getInstance(ds);
		tree.prune(ds, C45.LAPLACE_PRUNE);
		System.out.println("after --");
		tree.print();
		System.out.println("error: " + tree.error(ds));
	}

	public void doit() {
		/*DataSet ds = new DataSet(4,
		 new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","no")),
		 new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","no")),
		 new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","no")),
		 new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","no")),
		 new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
		 new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","no")));
		 //*/
		try {
			//tree = C45.getInstancePrune(ds);
			DataSet ds = new DataSet(0,
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 3.0), new ContinuousAttribute("working_hours_per_week", 38), new ContinuousAttribute("statuatory_holidays", 11), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 4.0), new ContinuousAttribute("working_hours_per_week", 35), new ContinuousAttribute("statuatory_holidays", 15), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 4.5), new ContinuousAttribute("working_hours_per_week", 35), new ContinuousAttribute("statuatory_holidays", 11), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 7.0), new ContinuousAttribute("working_hours_per_week", 36), new ContinuousAttribute("statuatory_holidays", 11), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 4.3), new ContinuousAttribute("working_hours_per_week", 38), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 4.0), new ContinuousAttribute("working_hours_per_week", 36), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "half")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 4.5), new ContinuousAttribute("working_hours_per_week", 36), new ContinuousAttribute("statuatory_holidays", 10), new DiscreteAttribute("health_plan_contribution", "half")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 2.8), new ContinuousAttribute("working_hours_per_week", 35), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 5.0), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 11), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 6.9), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 6.4), new ContinuousAttribute("working_hours_per_week", 38), new ContinuousAttribute("statuatory_holidays", 15), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 2.0), new ContinuousAttribute("working_hours_per_week", 35), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 6.0), new ContinuousAttribute("working_hours_per_week", 38), new ContinuousAttribute("statuatory_holidays", 9), new DiscreteAttribute("health_plan_contribution", "*")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 6.0), new ContinuousAttribute("working_hours_per_week", 35), new ContinuousAttribute("statuatory_holidays", 9), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 4.5), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 10), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "good"), new ContinuousAttribute("wage_increase_first_year", 5.0), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "half")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 2.0), new ContinuousAttribute("working_hours_per_week", 38), new ContinuousAttribute("statuatory_holidays", 12), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 4.0), new ContinuousAttribute("working_hours_per_week", 36), new ContinuousAttribute("statuatory_holidays", 11), new DiscreteAttribute("health_plan_contribution", "none")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 3.0), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 10), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 4.0), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 10), new DiscreteAttribute("health_plan_contribution", "none")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 2.8), new ContinuousAttribute("working_hours_per_week", 38), new ContinuousAttribute("statuatory_holidays", 9), new DiscreteAttribute("health_plan_contribution", "none")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 2.0), new ContinuousAttribute("working_hours_per_week", 35), new ContinuousAttribute("statuatory_holidays", 10), new DiscreteAttribute("health_plan_contribution", "full")),
					new DataRecord(new DiscreteAttribute("outcome", "bad"), new ContinuousAttribute("wage_increase_first_year", 4.5), new ContinuousAttribute("working_hours_per_week", 40), new ContinuousAttribute("statuatory_holidays", 10), new DiscreteAttribute("health_plan_contribution", "half")));
			C45 tree = C45.getInstance(new File("prunetest.c45"));
			//*/

			//C45 tree = C45.getInstance(ds);
			tree.setConfidence(0.25);
			tree.prune(ds, C45.QUINLANS_PRUNE);
			System.out.println("after --- ");
			tree.print();
			System.out.println("error: " + tree.error(ds));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printData() {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++)
				System.out.print(data[i][j] + "\t");
			System.out.println("");
		}
	}
}