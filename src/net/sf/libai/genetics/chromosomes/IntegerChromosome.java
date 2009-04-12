package net.sf.libai.genetics.chromosomes;

import net.sf.libai.genetics.chromosomes.Chromosome;
import net.sf.libai.genetics.*;
import java.util.Random;

/**
 *	Implementation of a permutation chromosome
 *	@author kronenthaler
 */
public class IntegerChromosome extends Chromosome{
	protected int[] genes;
	
	public IntegerChromosome(){}
	
	protected IntegerChromosome(int length){
		genes=new int[length];
		
		for(int i=0;i<length;genes[i]=i++); 
			
		//generate initial permutation
		for(int i=0;i<length;i++){
			int j=Engine.r.nextInt(length);
			int aux=genes[i];
			genes[i]=genes[j];
			genes[j]=aux;
		}
	}
	
	protected IntegerChromosome(IntegerChromosome b){
		genes=new int[b.genes.length];
		System.arraycopy(b.genes,0,genes,0,genes.length);
	}
	
	public Chromosome[] cross(Chromosome b, int position){
		/*int[] aux=new int[genes.length];
		System.arraycopy(genes,0,aux,0,position);
		
		IntegerChromosome b1=(IntegerChromosome)b;
		for(int i=0,j=position;i<aux.length;i++){
			boolean flag=false;
			for(int k=0;k<j && !flag;k++){
				if(b1.genes[i]==aux[k])
					flag=true;
			}
			if(!flag)
				aux[j++]=b1.genes[i];
		}
		
		return getInstance(aux);*/

		boolean mask[]=new boolean[genes.length];
		for(int i=0;i<mask.length;mask[i++]=Engine.r.nextBoolean());
		
		IntegerChromosome b1=(IntegerChromosome)b;
		IntegerChromosome a1=new IntegerChromosome(genes.length);
		IntegerChromosome a2=new IntegerChromosome(genes.length);
		
		for(int i=0;i<mask.length;i++){
			if(mask[i]) a1.genes[i]=genes[i];
			else a2.genes[i]=b1.genes[i];
		}
		
		for(int i=0;i<mask.length;i++){
			if(!mask[i]){
				//se que es un hueco, buscar el elemento en el padre contrario
				for(int j=0;j<mask.length;j++){
					boolean flag=false;
					for(int k=0;k<mask.length && !flag;k++)
						if(a1.genes[k]==b1.genes[j]) flag=true;
					
					if(!flag){
						a1.genes[i]=b1.genes[j];
						break;
					}
				}
			}else{
				for(int j=0;j<mask.length;j++){
					boolean flag=false;
					for(int k=0;k<mask.length && !flag;k++)
						if(a2.genes[k]==genes[j])
							flag=true;
					
					if(!flag){
						a2.genes[i]=genes[j];
						break;
					}
				}
			}
		}

		return new Chromosome[]{a1,a2};
	}

	public Chromosome mutate(double pm) {
		int ret[]=new int[genes.length];
		System.arraycopy(genes,0,ret,0,ret.length);
		
		for(int i=0;i<genes.length;i++){
			if(Engine.r.nextDouble()<pm){
				int indexb=Engine.r.nextInt(genes.length);
				
				if(i==indexb) continue;
				
				int aux=ret[i];
				ret[i]=ret[indexb];
				ret[indexb]=aux;
			}
		}
		return getInstance(ret);
	}

	protected Chromosome getInstance(int[] genes){
		IntegerChromosome ret=new IntegerChromosome();
		ret.genes=genes;
		return ret;
	}
	
	public Chromosome getCopy() {
		return new IntegerChromosome(this);
	}

	public Chromosome getInstance(int length) {
		return new IntegerChromosome(length);
	}
	
	public String toString(){
		String ret="";
		for(int i=0;i<genes.length;ret+=genes[i++]+" ");
		return ret;
	}
	
	public int[] getGenes(){ return genes; }
}
