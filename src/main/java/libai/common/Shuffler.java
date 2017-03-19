package libai.common;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by kronenthaler on 19/03/2017.
 */
public class Shuffler {
	private int[] order;
	private Random random;

	public Shuffler(int length){
		this(length, ThreadLocalRandom.current());
	}

	public Shuffler(int length, Random random){
		order = new int[length];
		for(int i=0;i<length;i++)
			order[i] = i;

		this.random = random;
	}

	public int[] shuffle(){
		for (int i = 0; i < order.length; i++) {
			int j = random.nextInt(order.length);
			int aux = order[i];
			order[i] = order[j];
			order[j] = aux;
		}
		return Arrays.copyOf(order, order.length);
	}
}
