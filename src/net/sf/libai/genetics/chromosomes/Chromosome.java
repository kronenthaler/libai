package net.sf.libai.genetics.chromosomes;

public abstract class Chromosome {
	/** What is my current fitness */
	/** What is my chance to be selected (by roulette)*/
	private double fitness;
	private double chance;
	private double fitnessReal;

	/**
	 *	Split the genes by <i>position</i> and swap lower portion of this with 
	 *	the lower portion of b and return the new individual 
	 */
	public abstract Chromosome[] cross(Chromosome b,int position);
		
	/** 
	 * Flip a bit of the genes
	 */
	public abstract Chromosome mutate(double pm);
	
	/**
	 *	Copy constructor
	 */
	public abstract Chromosome getCopy();
	
	/**
	 *	Generic constructor
	 */
	public abstract Chromosome getInstance(int length);

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * @param fitness the fitness to set
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * @return the chance
	 */
	public double getChance() {
		return chance;
	}

	/**
	 * @param chance the chance to set
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
	 * @param fitnessReal the fitnessReal to set
	 */
	public void setFitnessReal(double fitnessReal) {
		this.fitnessReal = fitnessReal;
	}


}