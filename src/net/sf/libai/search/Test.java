package net.sf.libai.search;

import java.util.ArrayList;

/**
 *
 * @author kronenthaler
 */
public class Test {

    /**
     * @param args the command line arguments
     */
	static State target = new Node("12345678.",null,'\0',0);
    public static void main(String[] args) {
        State init = new Node(".87645321",null,'\0',0);
		System.out.println(init.getHeuristicCost());
		/*long begin = System.currentTimeMillis();
		AStar a = new AStar();
		Node ans = (Node)a.search(init);
		if(ans != null){
			ans.printSolution();
			System.out.println("\nCost: "+ans.getCost());
		}else
			System.out.println("no solution");
		long end = System.currentTimeMillis();

		System.out.println("time: "+(end-begin));

		begin = System.currentTimeMillis();
		BFS bfs = new BFS();
		ans = (Node)bfs.search(init);
		if(ans != null){
			ans.printSolution();
			System.out.println("\nCost: "+ans.getCost());
		}else
			System.out.println("no solution");
		end = System.currentTimeMillis();
		System.out.println("time: "+(end-begin));
		//*/
	}

	static class Node extends State{
		String table = "";
		Node parent = null;
		char move = ' ';
		int point = 0;
		int g,h;

		static int stepsx[]={0,0,1,-1};
		static int stepsy[]={1,-1,0,0};
		static char dir[] = {'d','u','r','l'};
		//down, up, right, left

		Node(String t, Node p, char m, int cost){
			table = t;
			parent = p;
			move = m;
			point = table.indexOf('.');
			g = cost;
			h = 0;
			for(int i=0;i<3;i++){
				for(int j=0;j<3;j++){
					int c = table.charAt(i*3+j);
					if(c>='1' && c<='8'){
						c -= '0' - 1;
						int er = c/3;
						int ec = c%3;
						h += Math.abs(i-er) + Math.abs(j-ec);
					}else{
						h += Math.abs(i-2) + Math.abs(j-2);
					}
				}
			}
		}

		public void printSolution(){
			if(parent!=null)
				parent.printSolution();
			System.out.print(move);
		}

		@Override
		public double getCost() {
			return g;
		}

		@Override
		public double getHeuristicCost(){
			return h; //h
		}

		@Override
		public ArrayList<State> getCandidates() {
			//do until 4 moves.
			ArrayList<State> candidates = new ArrayList<State>();
			int row = point / 3;
			int col = point % 3;
			for(int i=0;i<4;i++){
				if(i==0 && row == 2) continue;
				if(i==1 && row == 0) continue;
				if(i==3 && col == 0) continue;
				if(i==2 && col == 2) continue;
				char t = table.charAt((row+stepsy[i])*3 +(col+stepsx[i]));
				String newstate = table.replace('.', '#').replace(t, '.');
				newstate = newstate.replace('#',t);
				candidates.add(new Node(newstate,this,dir[i], g+1));
			}
			return candidates;
		}

		@Override
		public int compareTo(State o) {
			Node n = (Node)o;
			return (int)((getCost()+getHeuristicCost()) - (n.getCost()+n.getHeuristicCost()));
		}

		@Override
		public boolean equals(Object o){
			return table.equals(((Node)o).table);
		}

		@Override
		public String toString(){ 
			return table+" ("+g+")";
		}

		@Override
		public int hashCode() {
			return table.hashCode();
		}

		@Override
		public boolean isSolution(){
			return table.equals(((Node)target).table);
		}
	}
}
