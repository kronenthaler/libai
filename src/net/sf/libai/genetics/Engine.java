package net.sf.libai.genetics;

import net.sf.libai.genetics.chromosomes.Chromosome;
import java.util.Random;

public class Engine{
	private Chromosome population[],newpopulation[],toCross[],best;//population
	public static Random r;
	private Fitness evaluator;
	private int chromosomeSize;
	private double pc=.6,pm=.01;//probability of cross and mutation respectively.
	
	public Engine(Class chromotype, int individuals, int _chromosomeSize, double _pc, double _pm, Fitness _evaluator){
		evaluator=_evaluator;
		chromosomeSize=_chromosomeSize;
		pc=_pc;
		pm=_pm;
		
		population=new Chromosome[individuals];
		newpopulation=new Chromosome[individuals];
		toCross=new Chromosome[individuals];
		r=new Random();
		
		try{
			best=(Chromosome)chromotype.newInstance();
			best.setFitness(evaluator.theWorst());
			best.setFitnessReal(evaluator.theWorst());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		initialize(chromosomeSize);
	}
	
	/** Initialize population */
	protected void initialize(int chromosomeSize){
		for(int i=0;i<population.length;i++)
			population[i]=best.getInstance(chromosomeSize);
	}
	
	/** Roulette determinate individuals to cross */
	private void roulette(double maximum){
		Chromosome current=null;
		double q=0;
		for(int i=0;i<population.length;i++){
			current=population[i];

			double temp=(current.getFitness()/maximum);
			current.setFitness(q + (1 - temp));
			current.setChance(r.nextDouble());

			q+=temp;
		}
		
		for(int j=0;j<population.length;j++){
			if(best==population[j]){
				toCross[j]=best;
			}else{
				for(int i=1;i<population.length;i++){
					if(population[j].getChance()>0 && population[j].getChance()<=population[i].getFitness()){
						toCross[j]=population[0];
						break;
					}else if(population[j].getChance()>population[i - 1].getFitness() && population[j].getChance()<=population[i].getFitness()){
						toCross[j]=population[i];
						break;
					}
				}
			}
		}
	}
	
	/** Cross population. Elitish style. */
	/* revisar el elitismo de esta formula porque deberia tomar en cuenta el fitness no el chance*/
	private void cross(){
		int a=0,b=0,pos=0,j=0,i=0;
		boolean wait=false;
		for(i=0;i<population.length;i++){
			if(toCross[i].getChance()<pc && !wait){
				a=i;wait=true;
				pos=Math.abs(r.nextInt(chromosomeSize));
			}else if(toCross[i].getChance()<pc && wait){
				b=i;wait=false;
				Chromosome childs[]=toCross[a].cross(toCross[b],pos);
				newpopulation[j++]=childs[0];//toCross[a].cross(toCross[b],pos);
				newpopulation[j++]=childs[1];//toCross[b].cross(toCross[a],pos);
			}else 
				newpopulation[j++]=toCross[i].getCopy();
		}
		if(wait) //one individual missing
			newpopulation[j]=toCross[a].getCopy();
	}
	
	/** Mutate random bits in each chromosome */
	private void mutate(){
		for(int i=0;i<population.length;i++){
			if(newpopulation[i]!=best)
				newpopulation[i]=newpopulation[i].mutate(pm);
		}
		population=newpopulation;
	}
	
	/** Evolve the population */
	public Chromosome evolve(int ages){
		double maximum=0;
		for(int iter=0;iter<ages;iter++){
			Chromosome current=null;
			maximum=0;
			
			//eval fitness
			for(int i=0;i<population.length;i++){
				current=population[i];
				current.setFitness(evaluator.fitness(current));
				current.setFitnessReal(current.getFitness());
				maximum+=current.getFitness(); //store to calculate roulette
				if(evaluator.isBetter(current.getFitnessReal(), best.getFitnessReal()))
					best=current;
			}
			
			roulette(maximum);
			cross();
			mutate(); //mutate and swap
		}
		
		return best; //the best individual
	}
}