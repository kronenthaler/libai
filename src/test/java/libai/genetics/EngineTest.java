/*
 * MIT License
 *
 * Copyright (c) 2017 Federico Vera <https://github.com/dktcoding>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package libai.genetics;

import javax.swing.JProgressBar;
import libai.genetics.chromosomes.BinaryChromosome;
import libai.genetics.chromosomes.Chromosome;
import libai.genetics.chromosomes.IntegerChromosome;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class EngineTest {

	public EngineTest() {
	}

	@Test
	public void testDemoBinary() {
		int MaxVal = 1;
		int MinVal = 0;
		int Nbits = (int) Math.ceil(Math.log10(1 + ((MaxVal - MinVal) / 1.e-5)) / Math.log10(2));

		Engine engine = new Engine(BinaryChromosome.class, 200, Nbits, 0.6, 0.01, new Fitness() {
			@Override
			public double fitness(Chromosome c) {
				double x = ((BinaryChromosome) c).decode(0, 1);
				return Math.abs(Math.exp(-x) - x); //=>0 si son iguales,
			}

			@Override
			public boolean isBetter(double fitness, double best) {
				return fitness < best;
			}

			@Override
			public double theWorst() {
				return Double.MAX_VALUE;
			}
		});

		engine.setProgressBar(new JProgressBar());
		BinaryChromosome best = (BinaryChromosome) engine.evolve(5000);

		double x = best.decode(0, 1);
		assertEquals(Math.exp(-x), x, 1e-3);

		//Test toString()
		String bStr = best.toString();
		String oStr = Long.toString(Long.reverse(best.decode()) >>> (64-bStr.length()), 2);
		assertTrue(bStr.endsWith(oStr));
	}

	@Test
	public void testDemoPermutation() {
		Fitness fitnessImpl = new Fitness() {
			@Override
			public double fitness(Chromosome c1) {
				IntegerChromosome c = (IntegerChromosome) c1;
				int g[] = c.getGenes();
				int count = g.length;
				for (int i = 0; i < g.length; i++) {
					if (g[i] == i)
						count--;
				}
				return count;
			}

			@Override
			public boolean isBetter(double fitness, double best) {
				return fitness < best;
			}

			@Override
			public double theWorst() {
				return Double.MAX_VALUE;
			}
		};
		Engine engine = new Engine(IntegerChromosome.class, 200, 10, 0.6, 0.01, fitnessImpl);

		engine.setProgressBar(new JProgressBar());

		//We can't guarantee that it will learn the function, but at least we can check that
		//the number of misplaced values decreases
		IntegerChromosome first = (IntegerChromosome)engine.evolve(1);
		IntegerChromosome last  = (IntegerChromosome)engine.evolve(2000);
		assertTrue(fitnessImpl.fitness(first) > fitnessImpl.fitness(last));

		//Test toString()
		String out = "";
		int[] g = last.getGenes();
		for (int i = 0; i < g.length; i++) {
			out += g[i] + " ";
		}
		assertEquals(out, last.toString());
	}

}
