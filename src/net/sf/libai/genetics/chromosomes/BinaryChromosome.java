package net.sf.libai.genetics.chromosomes;

import net.sf.libai.genetics.*;
import java.util.BitSet;


/**
 *	The binary form of the chormosome. This chromosome contains a sequence of bits.
 *	The mutation operation are supported by flipping a bit. And the cross by masking
 *	the bits:<br/>
 *	offspring1: (this & mask) | (otherparent & ~mask)<br/>
 *	offspring2: (otherparent & mask) | (this & ~mask)<br/>
 * 
 *	@author kronenthaler
 */
public class BinaryChromosome extends Chromosome{
	/** The genetic charge */
	protected BitSet genes;
	
	public BinaryChromosome(){}
	
	protected BinaryChromosome(int length){
		genes=new BitSet(length);
		
		String str=Integer.toBinaryString(Math.abs(Engine.r.nextInt((int)Math.pow(2,length))));
		for(int i=0;i<str.length();i++)
			if(str.charAt(i)=='1') genes.set(i);
	}

	/**
	 * Initialize this chromosome with another one
	 * @param c The chromosome to copy.
	 */
	protected BinaryChromosome(BinaryChromosome c){
		genes=c.genes.get(0,c.genes.size());
	}
	
	/**
	 *	Split the genes by <i>position</i> and swap lower portion of this with
	 *	the lower portion of b and viceversa to return 2 offsprings.
	 *	@param b chromosome to cross
	 *	@param position Position to split the chromosomes.
	 *	@return A two position array with the new offsprings.
	 */
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
	
	/**
	 *	Flip a bit of the genes. The bits are selected with probability <code>pm</code>
	 *	@param pm Mutation probability.
	 *	@return The new mutated chromosome.
	 */
	public Chromosome mutate(double pm){
		BitSet ret=genes.get(0,genes.size());
		for(int i=0,n=genes.size();i<n;i++){
			if(Engine.r.nextDouble()<pm)
				ret.flip(Engine.r.nextInt(n));
		}
		return getInstance(ret);
	}
	
	/**
	 *	Create a new instance of a Chromosome with this BitSet.
	 *	@param bs The genetic charge to copy.
	 *	@return A new chromosome with the same genetic charge.
	 */
	protected Chromosome getInstance(BitSet bs){
		BinaryChromosome ret=new BinaryChromosome();
		ret.genes=bs;
		return ret;
	}

	/**
	 *	Convert this chromosome in a long value.
	 *	If the chromosome is too large this value can be overflowed.
	 *	@return The integral representation of the chromosome.
	 */
	public long convert(){
		int index=-1;
		long acum=0;
		while((index=genes.nextSetBit(index+1))!=-1){
			acum+=Math.pow(2,index);
		}
		return acum;
	}

	/**
	 *	Clone this chromosome.
	 *	@return A identical chromosome of this.
	 */
	public Chromosome getCopy() {
		return new BinaryChromosome(this);
	}

	/**
	 *	Creates a new chromosome with a length of <code>length</code>
	 *	@param length The length of the new chromosome.
	 *	@return A new instance of length <code>length</code>
	 */
	public Chromosome getInstance(int length){
		return new BinaryChromosome(length);
	}
}
