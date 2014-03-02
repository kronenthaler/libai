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
    private ArrayList<DataRecord> data = new ArrayList<DataRecord>();
    
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
    };
    
    public MySQLDataSet(Connection conn, String tableName, int output){
        outputIndex = output + 1;
        connection = conn;
        this.tableName = tableName;
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT * from "+tableName, 
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            
            ResultSet rs = stmt.executeQuery();
            rsMetaData = rs.getMetaData();
            while(rs.next()){
                Attribute[] attributes = new Attribute[rsMetaData.getColumnCount()];
                for(int i=1;i<=rsMetaData.getColumnCount();i++){
                    Attribute attr = null;
                    try{
                        attr = new ContinuousAttribute(Double.parseDouble(rs.getString(i)));
                    }catch(Exception e){
                        attr = new DiscreteAttribute(rs.getString(i));
                    }
                    attributes[i-1] = attr;
                    if(i == outputIndex)
                        classes.add(attr);
                }
                data.add(new DataRecord(attributes));
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public int getOutputIndex() {
        return outputIndex - 1;
    }

    @Override
    public Set<Attribute> getClasses() {
        return classes;
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
    public DataSet[] splitKeepingRelation(double proportion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<DataRecord> iterator() {
        return data.iterator();
    }
}