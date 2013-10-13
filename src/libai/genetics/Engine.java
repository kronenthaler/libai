package libai.genetics;

import javax.swing.JProgressBar;
import libai.genetics.chromosomes.Chromosome;
import java.util.Random;

/**
 * Engine class provides complete algorithm to evolve populations of
 * chormosomes, regardless of these kind. This implementation of the genetic
 * algorithm contemplates the ellitism variant for the selection. The mutation
 * and cross are more chromosome-dependent that the algorithm-dependent,
 * therefore, chromosomes are instantiated for its class and evaluateds through
 * the Fitness instance.
 *
 * @author kronenthaler
 */
public class Engine {
	/**
	 * The current population
	 */
	private Chromosome population[];
	/**
	 * The offsprings of the current population
	 */
	private Chromosome newpopulation[];
	/**
	 * The chrmosomes selected to crossing
	 */
	private Chromosome toCross[];
	/**
	 * The best solution so far.
	 */
	private Chromosome best;//population
	/**
	 * Utility random instance.
	 */
	public static Random r;
	/**
	 * The fitness evaluator
	 */
	private Fitness evaluator;
	/**
	 * The size of each chromosome
	 */
	private int chromosomeSize;
	/**
	 * The cross probablilty
	 */
	private double pc = .6;
	/**
	 * The mutation probablilty
	 */
	private double pm = .01;
	private JProgressBar progress;

	/**
	 * Constructor. Initialize a population of
	 * <code>individuals</code> of type
	 * <code>chromotype</code>. Each chromosome has a length of
	 * <code>_chromosomeSize</code>, with a crossing probability of
	 * <code>_pc</code> and a mutation probability of
	 * <code>_pm</code>. To evaluate the fitness of each chromosome will use
	 * <code>_evaluator</code>.
	 *
	 * @param chromotype	The class for the chromosomes
	 * @param individuals	The number of individuals for this population
	 * @param _chromosomeSize	The size of each chromosome
	 * @param _pc	The crossing probability.
	 * @param _pm	The mutation probability.
	 * @param _evaluator	The fitness evaluator.
	 */
	public Engine(Class chromotype, int individuals, int _chromosomeSize, double _pc, double _pm, Fitness _evaluator) {
		evaluator = _evaluator;
		chromosomeSize = _chromosomeSize;
		pc = _pc;
		pm = _pm;

		population = new Chromosome[individuals];
		newpopulation = new Chromosome[individuals];
		toCross = new Chromosome[individuals];
		r = new Random();

		try {
			best = (Chromosome) chromotype.newInstance();
			best.setFitness(evaluator.theWorst());
			best.setFitnessReal(evaluator.theWorst());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		initialize(chromosomeSize);
	}

	/**
	 * Initialize population with size
	 * <code>chromosomeSize</code>
	 *
	 * @param chromosomeSize The size of the chromosome.
	 */
	protected void initialize(int chromosomeSize) {
		for (int i = 0; i < population.length; i++)
			population[i] = best.getInstance(chromosomeSize);
	}

	/**
	 * Roulette determinate individuals to cross
	 *
	 * @param maximum The maximum value for the fitness in this population.
	 */
	private void roulette(double maximum) {
		Chromosome current = null;
		double q = 0;
		for (int i = 0; i < population.length; i++) {
			current = population[i];

			double temp = (current.getFitness() / maximum);
			current.setFitness(q + (1 - temp));
			current.setChance(r.nextDouble());

			q += temp;
		}

		for (int j = 0; j < population.length; j++) {
			if (best == population[j]) {
				toCross[j] = best;
			} else {
				for (int i = 1; i < population.length; i++) {
					if (population[j].getChance() > 0 && population[j].getChance() <= population[i].getFitness()) {
						toCross[j] = population[0];
						break;
					} else if (population[j].getChance() > population[i - 1].getFitness() && population[j].getChance() <= population[i].getFitness()) {
						toCross[j] = population[i];
						break;
					}
				}
			}
		}
	}

	/**
	 * Cross population. Elitish style.
	 */
	private void cross() {
		int a = 0, b = 0, pos = 0, j = 0, i = 0;
		boolean wait = false;
		for (i = 0; i < population.length; i++) {
			if (toCross[i].getChance() < pc && !wait) {
				a = i;
				wait = true;
				pos = Math.abs(r.nextInt(chromosomeSize));
			} else if (toCross[i].getChance() < pc && wait) {
				b = i;
				wait = false;
				Chromosome childs[] = toCross[a].cross(toCross[b], pos);
				newpopulation[j++] = childs[0];//toCross[a].cross(toCross[b],pos);
				newpopulation[j++] = childs[1];//toCross[b].cross(toCross[a],pos);
			} else
				newpopulation[j++] = toCross[i].getCopy();
		}
		if (wait) //one individual missing
			newpopulation[j] = toCross[a].getCopy();
	}

	/**
	 * Mutate random genes in each chromosome
	 */
	private void mutate() {
		for (int i = 0; i < population.length; i++) {
			if (newpopulation[i] != best)
				newpopulation[i] = newpopulation[i].mutate(pm);
		}
		population = newpopulation;
	}

	/**
	 * Evolve the population for
	 * <code>ages</code>
	 *
	 * @return The best chromosome for all these epochs.
	 */
	public Chromosome evolve(int ages) {
		if (progress != null) {
			progress.setMaximum(ages - 1);
			progress.setMinimum(0);
			progress.setValue(0);
		}

		double maximum = 0;
		for (int iter = 0; iter < ages; iter++) {
			Chromosome current = null;
			maximum = 0;

			//eval fitness
			for (int i = 0; i < population.length; i++) {
				current = population[i];
				current.setFitness(evaluator.fitness(current));
				current.setFitnessReal(current.getFitness());
				maximum += current.getFitness(); //store to calculate roulette
				if (evaluator.isBetter(current.getFitnessReal(), best.getFitnessReal()))
					best = current;
			}

			roulette(maximum);
			cross();
			mutate(); //mutate and swap

			if (progress != null)
				progress.setValue(iter);
		}

		return best; //the best individual
	}

	public void setProgressBar(JProgressBar _progress) {
		progress = _progress;
	}
}