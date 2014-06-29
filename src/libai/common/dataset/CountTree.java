package libai.common.dataset;

import java.util.*;
import libai.classifiers.Attribute;
import libai.common.Pair;

/**
 * TODO: branch and bound 
 * @author kronenthaler
 */
public class CountTree {
    protected int count; //top level count = ds.getItemCount();
    protected Attribute root; //root for the top level.
    protected int index;
    protected List<CountTree> children;
    
    private CountTree(){
        children = new ArrayList<CountTree>();
        root = null;
        count = 0;
    }
    
    public CountTree(DataSet ds){
        this();
        int i=0;
        for(List<Attribute> record : ds){
            System.err.println(i++);
            add(record);
            count++;
        }
    }
    
    private CountTree(Attribute a, int i){
        this();
        root = a;
        index = i;
    }
    
    public int add(List<Attribute> v){
        return add(v, -1);
    }
    
    private int add(List<Attribute> v, int index){
        //System.err.println("index: "+index);
        //use integers instead of attribute.toString();
        //for(Attribute a : v){
        for(int i=index+1;i<v.size();i++){
            Attribute a = v.get(i);
            
            boolean counted = false;
            for(CountTree c : children){
                if(c.index == i && c.root.equals(a)){
                    c.count++;
                    c.add(v, i);
                    counted = true;
                }
            }
            
            if(!counted){
                CountTree node = new CountTree(a, i);
                
                children.add(node);
                node.count++;
                node.add(v, i);
            }
        }
        
        return count;
    }
    
    public int getCount(Pair<Integer,Attribute>... values){
        Arrays.sort(values);
        
        CountTree base = this;
        for(Pair<Integer,Attribute> value : values){
            for(CountTree child : base.children){
                if(child.index == value.first && 
                   child.root.equals(value.second)){
                    base=child;
                }
            }
        }
        return base.count;
    }
    
    public boolean isLeaf(){
        return children == null || children.isEmpty();
    }
    
    public String toString(){
        return toString("");
    }
    
    private String toString(String deep){
        StringBuilder str = new StringBuilder();
        str.append(deep+root+" = "+count);
        str.append(" {\n");
        for(CountTree c : children){
            str.append(deep+c.toString(deep+" "));
        }
        str.append(deep+"}\n");
        return str.toString();
    }
    
    public int leaves(){
        if(isLeaf()) return 1;
        int count = 0;
        for(CountTree c : children){
            count+=c.leaves();
        }
        return count;
    }
}
