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