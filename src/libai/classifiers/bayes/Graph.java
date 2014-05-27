package libai.classifiers.bayes;

import java.util.*;
import libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class Graph {
    private Map<Integer, Set<Integer>> adjacencies;
    
    public Graph(){
        adjacencies = new HashMap<Integer, Set<Integer>>();
    }
    
    public Graph(List<Pair<Integer,Integer>> edges){
        super();
        //add all the edges
    }
    
    public boolean addEdge(Pair<Integer, Integer> edge){
        if(adjacencies.get(edge.first)==null)
            adjacencies.put(edge.first, new HashSet<Integer>());
        return  adjacencies.get(edge.first).add(edge.second);
    }
    
    public boolean removeEdge(Pair<Integer, Integer> edge){
        if(adjacencies.get(edge.first)==null) 
            return false;
        return adjacencies.get(edge.first).remove(edge.second);
    }
    
    public Set<Integer> neighbors(int vertex){
        if(adjacencies.containsKey(vertex))
            return adjacencies.get(vertex);
        return new HashSet<Integer>();
    }
    
    public Set<Integer> getAdjacencyPath(int X, int Y){
        Set<Integer> result = new HashSet<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        ArrayList<Pair<Integer, Pair>> queue = new ArrayList<Pair<Integer, Pair>>();
        queue.add(new Pair<Integer, Pair>(X, null));
        while(!queue.isEmpty()){
            Pair<Integer, Pair> v = queue.remove(0);
            if(v.first == Y){
                Pair<Integer, Pair> tmp = v;
                while(v!=null){
                    result.add(v.first);
                    v = v.second;
                }
                break;
            }
            if(adjacencies.containsKey(v.first)){
                for(Integer z : adjacencies.get(v.first)){
                    if(!visited.contains(z)){
                        visited.add(z);
                        queue.add(new Pair<Integer, Pair>(z,v));
                    }
                }
            }
        }
        return result;
    }
}
