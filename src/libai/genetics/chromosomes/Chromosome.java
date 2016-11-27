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
package libai.genetics.chromosomes;

/**
 * The chromosome abstraction. Keeps the fitness and the chance to be selected
 * by the roulette. Also has the methods to mutate and cross with another
 * chromosome in an specific point.
 *
 * @author kronenthaler
 */
public abstract class Chromosome {
	/**
	 * What is my current fitness
	 */
	private double fitness;
	/**
	 * What is my chance to be selected (by roulette)
	 */
	private double chance;
	private double fitnessReal;

	/**
	 * Split the genes by
	 * <code>position</code> and swap lower portion of this with the lower
	 * portion of
	 * <code>b</code> and return the new individual
	 */
	public abstract Chromosome[] cross(Chromosome b, int position);

	/**
	 * Change one gene of the chromosome
	 */
	public abstract Chromosome mutate(double pm);

	/**
	 * Copy constructor
	 */
	public abstract Chromosome getCopy();

	/**
	 * Generic constructor
	 */
	public abstract Chromosome getInstance(int length);

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * @param fitness The new fitness
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * @return the chance to be selected
	 */
	public double getChance() {
		return chance;
	}

	/**
	 * @param chance The new chance.
	 */
	public void setChance(double chance) {
		this.chance = chance;
	}

	/**
	 * @return the fitnessReal
	 */
	public double getFitnessReal() {
		return fitnessReal;
	}

	/**
	 * @param fitnessReal The new fitnessReal.
	 */
	public void setFitnessReal(double fitnessReal) {
		this.fitnessReal = fitnessReal;
	}
}