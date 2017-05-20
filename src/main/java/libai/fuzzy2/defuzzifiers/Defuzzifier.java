package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;

import java.awt.*;
import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public abstract class Defuzzifier {
	public static final Defuzzifier MOM = new MeanOfMaxima();
	public static final Defuzzifier COG = new CenterOfGravity();
	public static final Defuzzifier COA = new CenterOfArea();

	public abstract double getValue(List<Point.Double> function);

	public static Defuzzifier fromString(String name){
		if (MOM.toString().equalsIgnoreCase(name))
			return MOM;
		if (COG.toString().equalsIgnoreCase(name))
			return COG;
		if (COA.toString().equalsIgnoreCase(name))
			return COA;

		throw new UnsupportedOperationException("Unsupported Defuzzifier: "+name);
	}
}
