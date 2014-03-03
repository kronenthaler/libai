package libai.classifiers.refactor;

import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import libai.classifiers.*;

/**
 *
 * @author kronenthaler
 */
public class MySQLDataSet implements DataSet{
    private int outputIndex;
    private Connection connection;
    private String tableName;
    private ResultSetMetaData rsMetaData;
    private Set<Attribute> classes = new HashSet<Attribute>();
    
    private MetaData metadata = new MetaData(){
        @Override
        public boolean isCategorical(int fieldIndex) {
            try {
                String type = rsMetaData.getColumnClassName(fieldIndex+1); 
                return type.equals("java.lang.String");
            } catch (SQLException ex) {
                return false;
            }
        }

        @Override
        public int getAttributeCount() {
            try {
                return rsMetaData.getColumnCount();
            } catch (SQLException ex) {
                return 0;
            }
        }

        @Override
        public Set<Attribute> getClasses() {
            return classes;
        }
    };
    
    private MySQLDataSet(int output){
        outputIndex = output;
    }
    
    public MySQLDataSet(Connection conn, String tableName, int output){
        this(output);
        
        connection = conn;
        this.tableName = tableName;
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT * from "+tableName, 
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            
            ResultSet rs = stmt.executeQuery();
            rsMetaData = rs.getMetaData();
            rs.close();
            
            stmt = conn.prepareStatement("SELECT DISTINCT("+rsMetaData.getColumnName(output+1)+") from "+tableName);
            rs = stmt.executeQuery();
            while(rs.next()){
                Attribute attr = null;
                try{
                    attr = new ContinuousAttribute(Double.parseDouble(rs.getString(1)));
                }catch(Exception e){
                    attr = new DiscreteAttribute(rs.getString(1));
                }
                classes.add(attr);
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public int getOutputIndex() {
        return outputIndex;
    }

    @Override
    public int getItemsCount() {
        try{
            PreparedStatement stmt = connection.prepareStatement("select count(*) from "+tableName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
                return rs.getInt(1);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public MetaData getMetaData() {
        return metadata;
    }
    
    @Override
    public Iterable<List<Attribute>> sortOver(final int fieldIndex) {
        return new Iterable<List<Attribute>>(){
            @Override
            public Iterator<List<Attribute>> iterator(){
                try{
                    PreparedStatement stmt = connection.prepareStatement("select * from "+tableName+" order by "+rsMetaData.getColumnName(fieldIndex+1));
                    return buildIterator(stmt.executeQuery());
                }catch(SQLException e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
    
    @Override
    public DataSet[] splitKeepingRelation(double proportion) {
        TextFileDataSet a = new TextFileDataSet(outputIndex);
		TextFileDataSet b = new TextFileDataSet(outputIndex);
        
        Iterable<List<Attribute>> sortedData = sortOver(outputIndex);
        Attribute prev = null;
        List<List<Attribute>> buffer = new ArrayList<List<Attribute>>();
        for (List<Attribute> record : sortedData){
            if((prev != null && prev.compareTo(record.get(outputIndex)) != 0)){
                Collections.shuffle(buffer);
                a.addRecords(buffer.subList(0, (int)(buffer.size() * proportion)));
                b.addRecords(buffer.subList((int)(buffer.size() * proportion), buffer.size()));
                buffer.clear();
            }
            
            buffer.add(record);
            prev = record.get(outputIndex);
        }
        
        if(!buffer.isEmpty()){
            Collections.shuffle(buffer);
            a.addRecords(buffer.subList(0, (int)(buffer.size() * proportion)));
            b.addRecords(buffer.subList((int)(buffer.size() * proportion), buffer.size()));
        }
        
		return new DataSet[]{a, b};
    }

    @Override
    public Iterator<List<Attribute>> iterator() {
        try{
            PreparedStatement stmt = connection.prepareStatement("select * from "+tableName);
            return buildIterator(stmt.executeQuery());
        }catch(SQLException ex){
            return null;
        }
    }

    private Iterator<List<Attribute>> buildIterator(final ResultSet rs){
        return new Iterator<List<Attribute>>() {
            int count = 0;
            @Override
            public boolean hasNext() {
                try {
                    return rs.next();
                } catch (SQLException e) {
                    return false;
                }
            }

            @Override
            public List<Attribute> next() {
                try {
                    List<Attribute> record = new ArrayList<Attribute>();
                    for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                        Attribute attr = null;
                        try {
                            attr = new ContinuousAttribute(Double.parseDouble(rs.getString(i)));
                        } catch (NumberFormatException e) {
                            attr = new DiscreteAttribute(rs.getString(i));
                        }
                        record.add(attr);
                    }
                    return record;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
}