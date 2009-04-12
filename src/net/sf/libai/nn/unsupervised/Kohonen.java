package net.sf.libai.nn.unsupervised;

import java.io.*;
import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.nn.NeuralNetwork;

/**
 *
 * @author kronenthaler
 */
public class Kohonen extends NeuralNetwork{
	private Matrix W[];					//array of weights ijk, with k positions.
	private int[][] map;				//map of the outputs
	private int[] nperlayer;			//array of 3 positions, {#inputs,#rows,#columns}
	private double neighborhood;
	private int stepsx[],stepsy[];

	public Kohonen(){}

	public Kohonen(int[] nperlayer,double _neighborhood,int[] neighboursX,int[] neighboursY){
		this.nperlayer = nperlayer;
		neighborhood = _neighborhood;

		W=new Matrix[nperlayer[1]*nperlayer[2]];
		stepsx=neighboursX;
		stepsy=neighboursY;

		for(int i=0;i<nperlayer[1];i++){
			for(int j=0;j<nperlayer[2];j++){
				W[(i*nperlayer[2])+j]=new Matrix(nperlayer[0],1);
				W[(i*nperlayer[2])+j].setValue(0);
			}
		}
		map = new int[nperlayer[1]][nperlayer[2]];
		for(int i=0;i<map.length;i++)
			Arrays.fill(map[i], -1);
	}

	public Kohonen(int[] nperlayer, double _neighborhood){
		this(nperlayer, _neighborhood, new int[]{0,0,1,-1}, new int[]{-1,1,0,0});
	}

	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int curr_epoch=0,ig=0,jg=0;
		double lamda=neighborhood;
		double alpha1=alpha;

		Random rand = new Random();
		int[] sort=new int[length];
		for(int i=0;i<length;sort[i]=i++);

		Matrix temp=new Matrix(nperlayer[0],1);
		Matrix winner=new Matrix(2,1);

		while(curr_epoch++ < epochs){
			System.out.println("epoch: "+curr_epoch);
			//shuffle
			for(int i=0;i<length;i++){
				int j = rand.nextInt(length);
				int k = sort[i];
				sort[i] = sort[j];
				sort[j] = k;
			}

			for(int k=0;k<length;k++){
				//Who is the winner
				simulate(patterns[sort[k]+offset],winner);

				ig = (int)winner.position(0,0);
				jg = (int)winner.position(1,0);

				//Update winner and neighbors.
				for(int i=0;i<nperlayer[1];i++){
					for(int j=0;j<nperlayer[2];j++){
						Matrix Mij=get(i,j);
						patterns[sort[k]+offset].subtract(Mij,temp);
						temp.multiply(alpha1 * neighbor(i,j,ig,jg),temp);
						Mij.add(temp,Mij);
					}
				}
			}

			//update neighborhood's ratio.
			if(neighborhood >= 0.5)
				neighborhood=lamda*Math.exp(-(float)curr_epoch/(float)epochs);

			//update alpha
			if(alpha1 > 0.001)
				alpha1=alpha*Math.exp(-(float)curr_epoch/(float)epochs);
		}

		expandMap(patterns, answers, offset, length);
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix ret=new Matrix(2,1);
		simulate(pattern,ret);
		return ret;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		double min=Double.MAX_VALUE;
		for(int i=0;i<nperlayer[1];i++){
			for(int j=0;j<nperlayer[2];j++){
				double temp=euclideanDistance2(pattern,get(i,j));
				if(temp < min){
					result.position(0,0,i);
					result.position(1,0,j);
					min=temp;
				}
			}
		}
	}

	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		double error=0;
		
		Matrix winner = new Matrix(2,1);
		for(int i=0;i<length;i++){
			//calculate the winner neuron
			simulate(patterns[i+offset],winner);

			int x = (int)winner.position(0, 0);
			int y = (int)winner.position(1, 0);

			//take the euclidean distance to the nearest neuron in the expected cluster.
			boolean[][] visited=new boolean[map.length][map[0].length];
			
			ArrayList<Pair<Integer,Integer>> q = new ArrayList<Pair<Integer,Integer>>();
			q.add(new Pair<Integer,Integer>(x,y));
			visited[x][y]=true;

			while(!q.isEmpty()){
				Pair<Integer,Integer> current= q.remove(0);

				if(map[current.first][current.second] == (int)answers[i+offset].position(0,0)){
					error += Math.sqrt((current.first-x)*(current.first-x) + (current.second-y)*(current.second-y));
					break;
				}

				for(int k=0;k<stepsx.length;k++){
					int ii=current.first + stepsx[k];
					int ij=current.second + stepsy[k];
					if(ii>=0 && ii<nperlayer[1] && ij>=0 && ij<nperlayer[2] && !visited[ii][ij]){
						q.add(new Pair<Integer,Integer>(ii,ij));
						visited[ii][ij]=true;
					}
				}
			}
		}

		error /= length;
		return error;
	}

	@Override
	public boolean save(String path) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean open(String path) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private Matrix get(int i,int j){ return W[(i*nperlayer[2])+j]; }

	private double neighbor(int i,int j,int ig,int jg){
		return gaussian(distance(i,j,ig,jg),neighborhood*neighborhood);
	}

	private double distance(int i,int j,int ig, int jg){
		return (((i-ig)*(i-ig)) + ((j-jg)*(j-jg)));
	}


	public int[][] getMap(){ return map; }

	private void expandMap(Matrix[] patterns, Matrix[] answers, int offset, int length){
		Matrix winner = new Matrix(2,1);
		System.out.println("labelling...");
		for(int k=0;k<length;k++){
			simulate(patterns[k+offset],winner);

			int i = (int)winner.position(0, 0);
			int j = (int)winner.position(1, 0);

			if(map[i][j]==-1) //no overlapping
				map[i][j]=(int)answers[k+offset].position(0,0); //must have just one position and should be an integer
		}

		//if(!expand) return map;

		ArrayList<Pair<Integer,Integer>> q = new ArrayList<Pair<Integer,Integer>>();

		for(int i=0;i<nperlayer[1];i++)
			for(int j=0;j<nperlayer[2];j++)
				if(map[i][j]!=-1)
					q.add(new Pair<Integer,Integer>(i,j));

		System.out.println("BFS...");
		while(!q.isEmpty()){
			Pair<Integer,Integer> current=q.remove(0);
			int c=map[current.first][current.second];

			for(int k=0;k<stepsx.length;k++){
				int i=current.first + stepsx[k];
				int j=current.second + stepsy[k];
				if(i>=0 && i<nperlayer[1] && j>=0 && j<nperlayer[2] && map[i][j] == -1){
					q.add(new Pair<Integer,Integer>(i,j));
					map[i][j]=c;
				}
			}
		}
	}
}
