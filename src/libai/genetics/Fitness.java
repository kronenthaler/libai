package libai.genetics;

import libai.genetics.chromosomes.Chromosome;

/**
 * Class to handle the fitness specification for the problem.
 *
 * @author kronenthaler
 */
public interface Fitness {
	/**
	 * evaluate the fitnes of the passed chromosome.
	 */
	public double fitness(Chromosome c);

	/**
	 * is this fitness better than the best fitness known?
	 */
	public boolean isBetter(double fitness, double best);

	/**
	 * the worst posible value OO or -OO
	 */
	public double theWorst();
}