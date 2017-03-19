package libai.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by kronenthaler on 19/03/2017.
 */
public class ShufflerTest extends Shuffler {
	public ShufflerTest(){
		super(0);
	}

	@Test
	public void testCreateSequentialArray(){
		Shuffler shuffler = new Shuffler(3);

		assertEquals(shuffler.order[0], 0);
		assertEquals(shuffler.order[1], 1);
		assertEquals(shuffler.order[2], 2);
	}

	@Test
	public void testShuffleCreatesACopy(){
		Shuffler shuffler = new Shuffler(3);
		int[] sort = shuffler.shuffle();

		assertNotEquals(sort, shuffler.order);
	}

	@Test
	public void testShuffleArray(){
		Shuffler shuffler = new Shuffler(10);
		int[] sort = shuffler.shuffle();

		int inPlace = 0;
		for(int i=0;i<sort.length;i++){
			if (sort[i] == i)
				inPlace++;
		}

		assertTrue(inPlace < sort.length);
	}

}
