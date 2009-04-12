package net.sf.libai.genetics;

import net.sf.libai.genetics.chromosomes.Chromosome;

public interface Fitness {
	/** evaluate the fitnes of the passed chromosome. */
	public double fitness(Chromosome c);

	/** is this fitness better than the best fitness known? */
	public boolean isBetter(double fitness,double best); 
	
	/** the worst posible value OO or -OO */
	public double theWorst();
}