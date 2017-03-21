/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
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

import libai.genetics.chromosomes.Chromosome;

/**
 * Class to handle the fitness specification for the problem.
 *
 * @author kronenthaler
 */
public interface Fitness {
	/**
	 * evaluate the fitness of the passed chromosome.
	 *
	 * @param c {@code c}
	 * @return fitness value for a given Chromosome
	 */
	public double fitness(Chromosome c);

	/**
	 * is this fitness better than the best fitness known?
	 *
	 * @param fitness {@code fitness}
	 * @param best    {@code best}
	 * @return {@code true} is this fitness better than the best fitness and
	 * {@code false} otherwise
	 */
	public boolean isBetter(double fitness, double best);

	/**
	 * the worst possible value OO or -OO
	 *
	 * @return worst possible value OO or -OO
	 */
	public double theWorst();
}