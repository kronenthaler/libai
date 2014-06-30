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
    public CountTree skipThisNode;
    private int attributeCount;
    
    private CountTree(){
        children = new ArrayList<CountTree>();
        root = null;
        count = 0;
        index = -1;
    }
    
    public CountTree(DataSet ds){
        this();
        int i=0;
        attributeCount = ds.getMetaData().getAttributeCount();
        for(List<Attribute> record : ds){
            System.err.println(i++);
            add(record);
            count++;
        }
    }
    
    private CountTree(Attribute a, int i, int c){
        this();
        root = a;
        index = i;
        attributeCount = c;
    }
    
    public void add(List<Attribute> v){
        add(v, 0);
    }
    
    private void add(List<Attribute> v, int index){
        //System.err.println(v+" "+index);
        if(index >= v.size())
            return;
        
        //just process the given index.
        Attribute a = v.get(index);

        //insert in the skipThisNode child too.
        /*if(skipThisNode==null)
            skipThisNode = new CountTree(null, index, attributeCount);
        skipThisNode.count++;
        skipThisNode.add(v, index+1);*/
        
        //look in the existing children tree.
        for(CountTree c : children){
            if(c.index == index && c.root.equals(a)){
                c.count++;
                c.add(v, index+1);
                return;
            }
        }

        //if it's the first time trying this node, create that child.
        CountTree node = new CountTree(a, index, attributeCount);
        children.add(node);
        node.count++;
        node.add(v, index+1);
    }
    
    public int getCount(Pair<Integer,Attribute>... values){
        Arrays.sort(values);
        return getCount(0, values);
    }    
    private int getCount(int currentValue, Pair<Integer,Attribute>... values){
        if(isLeaf())
            return count;
        
        if(currentValue >= values.length)
            return count;
        
        if(values[currentValue].first != index+1){
            int acum = 0;
            for(CountTree c : children){
                acum += c.getCount(currentValue, values);
            }
            return acum;
        }else{
            for(CountTree c : children){
                if(c.root.equals(values[currentValue].second)){
                    return c.getCount(currentValue+1, values);
                }
            }
        }
        
        return 0;
    }
    
    public boolean isLeaf(){
        return children == null || children.isEmpty();
    }
    
    public String toString(){
        return toString("");
    }
    
    private String toString(String deep){
        StringBuilder str = new StringBuilder();
        str.append(deep+root+"["+index+"] = "+count);
        str.append(" {\n");
        for(CountTree c : children){
            str.append(deep+c.toString(deep+" "));
        }
        
        if(skipThisNode!=null)
            str.append(deep+skipThisNode.toString(deep+" "));
        
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
