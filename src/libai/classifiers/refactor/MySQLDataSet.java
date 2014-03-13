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
public class MySQLDataSet implements DataSet {
    private int outputIndex;
    private String tableName;
    private int orderBy;
    private Connection connection;
    private ResultSetMetaData rsMetaData;
    private Set<Attribute> classes = new HashSet<Attribute>();

    private MetaData metadata = new MetaData() {
        @Override
        public boolean isCategorical(int fieldIndex) {
            try {
                String type = rsMetaData.getColumnClassName(fieldIndex + 1);
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
        
        @Override
        public String getAttributeName(int fieldIndex) {
            try{
                return rsMetaData.getColumnName(fieldIndex + 1);
            }catch(SQLException e){
                return "["+fieldIndex+"]";
            }
        }
    };

    private MySQLDataSet(int output) {
        outputIndex = output;
        orderBy = output;
    }

    private MySQLDataSet(MySQLDataSet parent, int lo, int hi) {
        this(parent.outputIndex);

        connection = parent.connection;
        this.orderBy = parent.orderBy;
        this.tableName = parent.tableName + System.currentTimeMillis();
        this.rsMetaData = parent.rsMetaData;
        
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("CREATE VIEW `%s` AS SELECT * FROM `%s` ORDER BY `%s` LIMIT ?,?",
                            this.tableName,
                            parent.tableName,
                            parent.rsMetaData.getColumnName(orderBy + 1)),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            stmt.setInt(1, lo);
            stmt.setInt(2, hi - lo);
            stmt.executeUpdate();

            initializeClasses();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MySQLDataSet(Connection conn, String tableName, int output) {
        this(output);

        connection = conn;
        this.tableName = tableName;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM `%s`",
                            tableName),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);

            ResultSet rs = stmt.executeQuery();
            rsMetaData = rs.getMetaData();
            rs.close();

            initializeClasses();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override 
    public DataSet getSubset(int lo, int hi){
        return new MySQLDataSet(this, lo, hi);
    }
    
    @Override
    public int getOutputIndex() {
        return outputIndex;
    }

    @Override
    public int getItemsCount() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("SELECT COUNT(*) FROM `%s`",
                            tableName));
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
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
        orderBy = fieldIndex;
        return new Iterable<List<Attribute>>() {
            @Override
            public Iterator<List<Attribute>> iterator() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            String.format("SELECT * FROM `%s` ORDER BY `%s`",
                                    tableName,
                                    rsMetaData.getColumnName(fieldIndex + 1)));
                    return buildIterator(stmt.executeQuery());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
    
    /* TODO change the implementation to return datasets from the same type */
    @Override
    public DataSet[] splitKeepingRelation(double proportion) {
        TextFileDataSet a = new TextFileDataSet(outputIndex);
        TextFileDataSet b = new TextFileDataSet(outputIndex);

        Iterable<List<Attribute>> sortedData = sortOver(outputIndex);
        Attribute prev = null;
        List<List<Attribute>> buffer = new ArrayList<List<Attribute>>();
        for (List<Attribute> record : sortedData) {
            if ((prev != null && prev.compareTo(record.get(outputIndex)) != 0)) {
                Collections.shuffle(buffer);
                a.addRecords(buffer.subList(0, (int) (buffer.size() * proportion)));
                b.addRecords(buffer.subList((int) (buffer.size() * proportion), buffer.size()));
                buffer.clear();
            }

            buffer.add(record);
            prev = record.get(outputIndex);
        }

        if (!buffer.isEmpty()) {
            Collections.shuffle(buffer);
            a.addRecords(buffer.subList(0, (int) (buffer.size() * proportion)));
            b.addRecords(buffer.subList((int) (buffer.size() * proportion), buffer.size()));
        }

        return new DataSet[]{a, b};
    }

    @Override
    public Iterator<List<Attribute>> iterator() {
        Iterator<List<Attribute>> result = null;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("SELECT * FROM `%s`",
                            tableName));
            result = buildIterator(stmt.executeQuery());
            stmt.close();
        } catch (SQLException ex) {
            
        }
        return result;
    }
    
    public void clean(){
        try{
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("DROP VIEW IF EXISTS `%s`", tableName));
            stmt.executeUpdate();
            stmt.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    private void initializeClasses() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("SELECT DISTINCT(`%s`) FROM `%s`",
                            rsMetaData.getColumnName(outputIndex + 1),
                            tableName));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classes.add(Attribute.getInstance(rs.getString(1)));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Iterator<List<Attribute>> buildIterator(final ResultSet rs) {
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
