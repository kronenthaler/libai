package net.sf.libai.genetics.chromosomes;

import net.sf.libai.genetics.chromosomes.Chromosome;
import net.sf.libai.genetics.*;
import java.util.BitSet;
import java.util.Random;

public class BinaryChromosome extends Chromosome{
	protected BitSet genes;		/** my genetic charge */
	
	public BinaryChromosome(){}
	
	protected BinaryChromosome(int length){
		genes=new BitSet(length);
		
		String str=Integer.toBinaryString(Math.abs(Engine.r.nextInt((int)Math.pow(2,length))));
		for(int i=0;i<str.length();i++)
			if(str.charAt(i)=='1') genes.set(i);
	}

	/** initialize this chromosome with another one */
	protected BinaryChromosome(BinaryChromosome c){
		genes=c.genes.get(0,c.genes.size());
	}
	
	/** Split the genes by <i>position</i> and swap lower portion of this with 
	 * the lower portion of b and return the new individual */
	public Chromosome[] cross(Chromosome b,int position){
		BitSet aux=null;
		
		aux=((BinaryChromosome)b).genes.get(0,genes.size());
		aux.set(0,position,false);
		aux.or(genes.get(0,position));
		
		BitSet aux1=null;
		
		aux1=genes.get(0,genes.size());
		aux1.set(0,position,false);
		aux1.or(((BinaryChromosome)b).genes.get(0,position));
		
		return new Chromosome[]{getInstance(aux),getInstance(aux1)};
	}
	
	/** Flip a bit of the genes */
	public Chromosome mutate(double pm){
		BitSet ret=genes.get(0,genes.size());
		for(int i=0,n=genes.size();i<n;i++){
			if(Engine.r.nextDouble()<pm)
				ret.flip(Engine.r.nextInt(n));
		}
		return getInstance(ret);
	}
	
	/** Create a new instance of a Chromosome with this BitSet*/
	protected Chromosome getInstance(BitSet bs){
		BinaryChromosome ret=new BinaryChromosome();
		ret.genes=bs;
		return ret;
	}
	
	public long convert(){
		int index=-1;
		long acum=0;
		while((index=genes.nextSetBit(index+1))!=-1){
			acum+=Math.pow(2,index);
		}
		return acum;
	}

	public Chromosome getCopy() {
		return new BinaryChromosome(this);
	}
	
	public Chromosome getInstance(int length){
		return new BinaryChromosome(length);
	}
}
