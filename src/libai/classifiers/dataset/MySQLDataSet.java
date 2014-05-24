package libai.classifiers.dataset;

import java.util.*;
import java.sql.*;
import libai.classifiers.*;
import libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class MySQLDataSet implements DataSet {
    private int outputIndex;
    private int itemCount = -1;
    private String tableName;
    private String rootName;
    private int orderBy;
    private Connection connection;
    private ResultSetMetaData rsMetaData;
    private Set<Attribute> classes = new HashSet<Attribute>();
    private HashMap<Triplet<Integer, Integer, Integer>, HashMap<Attribute, Integer>> cacheFrequencies;
    private HashMap<Triplet<Integer, Integer, Integer>, HashMap<Double, HashMap<Attribute, Integer>>> cacheAccumulatedFrequencies;

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
            if (classes.isEmpty())
                initializeClasses();
            return classes;
        }

        @Override
        public String getAttributeName(int fieldIndex) {
            try {
                return rsMetaData.getColumnName(fieldIndex + 1);
            } catch (SQLException e) {
                return "[" + fieldIndex + "]";
            }
        }
    };

    private MySQLDataSet(int output) {
        outputIndex = output;
        orderBy = output;
        cacheFrequencies = new HashMap<Triplet<Integer, Integer, Integer>, HashMap<Attribute, Integer>>();
        cacheAccumulatedFrequencies = new HashMap<Triplet<Integer, Integer, Integer>, HashMap<Double, HashMap<Attribute, Integer>>>();
    }

    private MySQLDataSet(MySQLDataSet parent, int lo, int hi) {
        this(parent.outputIndex);

        connection = parent.connection;
        this.orderBy = parent.orderBy;
        this.tableName = parent.rootName + System.currentTimeMillis();
        this.rootName = parent.rootName;
        this.rsMetaData = parent.rsMetaData;

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("CREATE VIEW `%s` AS SELECT * FROM `%s` ORDER BY `%s`, `%s` LIMIT ?,?",
                            this.tableName,
                            parent.tableName,
                            parent.metadata.getAttributeName(orderBy),
                            parent.metadata.getAttributeName(outputIndex)),
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
        this.rootName = tableName;
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataSet getSubset(int lo, int hi) {
        return new MySQLDataSet(this, lo, hi);
    }

    @Override
    public int getOutputIndex() {
        return outputIndex;
    }

    @Override
    public int getItemsCount() {
        if (itemCount >= 0)
            return itemCount;

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("SELECT COUNT(*) FROM `%s`",
                            tableName));
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return itemCount = rs.getInt(1);
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
        return sortOver(0, getItemsCount(), fieldIndex);
    }

    @Override
    public Iterable<List<Attribute>> sortOver(final int lo, final int hi, final int fieldIndex) {
        orderBy = fieldIndex;
        return new Iterable<List<Attribute>>() {
            @Override
            public Iterator<List<Attribute>> iterator() {
                try {
                    String query = String.format("SELECT * FROM `%s` ORDER BY `%s`, `%s` LIMIT %d, %d",
                            tableName,
                            metadata.getAttributeName(fieldIndex),
                            metadata.getAttributeName(outputIndex),
                            lo, hi - lo);
                    PreparedStatement stmt = connection.prepareStatement(query);
                    return buildIterator(stmt.executeQuery(), hi - lo);
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
        long seed = System.currentTimeMillis();
        String nameA = tableName+"_a_"+seed;
        String nameB = tableName+"_b_"+seed;
        
        try {
            PreparedStatement stmt = connection.prepareStatement(String.format("CREATE TABLE `%s` select * from `%s` limit 0", nameA, tableName));
            stmt.executeUpdate();
            stmt.close();
            
            stmt = connection.prepareStatement(String.format("CREATE TABLE `%s` SELECT * FROM `%s` LIMIT 0", nameB, tableName));
            stmt.executeUpdate();
            stmt.close();
        
            MySQLDataSet a = new MySQLDataSet(connection, nameA, outputIndex);
            MySQLDataSet b = new MySQLDataSet(connection, nameB, outputIndex);

            HashMap<Attribute, Integer> freq = getFrequencies(0, getItemsCount(), outputIndex);
            for(Attribute output : freq.keySet()){
                
                String baseQuery = "INSERT INTO `%s` SELECT * FROM `%s` WHERE `%s` = '%s' ORDER BY RAND(%d) LIMIT %d, %d";
                int size = (int)(freq.get(output) * proportion);
                String aQuery = String.format(baseQuery, nameA, tableName, metadata.getAttributeName(outputIndex), output.getValue(), seed, 0, size);
                String bQuery = String.format(baseQuery, nameB, tableName, metadata.getAttributeName(outputIndex), output.getValue(), seed, size, getItemsCount());
                
                stmt = connection.prepareStatement(aQuery);
                stmt.executeUpdate();
                stmt.close();
                
                stmt = connection.prepareStatement(bQuery);
                stmt.executeUpdate();
                stmt.close();
            }
            
            return new DataSet[]{a, b};
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterator<List<Attribute>> iterator() {
        Iterator<List<Attribute>> result = null;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("SELECT * FROM `%s`",
                            tableName));
            result = buildIterator(stmt.executeQuery(), getItemsCount());
        } catch (SQLException ex) {

        }
        return result;
    }

    public void clean() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("DROP VIEW IF EXISTS `%s`", tableName));
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeClasses() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    String.format("SELECT DISTINCT(`%s`) FROM `%s`",
                            metadata.getAttributeName(outputIndex),
                            tableName));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classes.add(Attribute.getInstance(rs.getString(1), metadata.getAttributeName(outputIndex)));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO: fix this iterator so it can use the next as next and the the hasnext to just check (without side effects)
    private Iterator<List<Attribute>> buildIterator(final ResultSet rs, final int itemsCount) {
        return new Iterator<List<Attribute>>() {
            int size = itemsCount;

            @Override
            public boolean hasNext() {
                return size > 0;
            }

            @Override
            public List<Attribute> next() {
                try {
                    if (rs.next()) {
                        size--;
                        List<Attribute> record = new ArrayList<Attribute>();
                        for (int i = 0; i < metadata.getAttributeCount(); i++) {
                            String fieldName = metadata.getAttributeName(i);
                            record.add(Attribute.getInstance(rs.getString(fieldName), fieldName));
                        }
                        return record;
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public boolean allTheSameOutput() {
        return metadata.getClasses().size() == 1;
    }

    @Override
    public Attribute allTheSame() {
        try {
            StringBuffer attributes = new StringBuffer();
            for (int i = 0; i < metadata.getAttributeCount(); i++) {
                if (i != outputIndex) {
                    if (i > 0 && outputIndex != 0)
                        attributes.append(',');
                    attributes.append(metadata.getAttributeName(i));
                }
            }

            String query = String.format("SELECT %s, count(*) as size FROM %s group by %s", attributes, tableName, attributes);
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("size") != getItemsCount())
                    return null;
                else {
                    String fieldName = metadata.getAttributeName(outputIndex);
                    String query2 = String.format("SELECT %s, count(*) as count FROM %s GROUP BY %s ORDER BY count DESC LIMIT 1",
                            fieldName, tableName, fieldName);
                    stmt = connection.prepareStatement(query2);
                    rs = stmt.executeQuery();
                    if (rs.next())
                        return Attribute.getInstance(rs.getString(1), fieldName);
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public HashMap<Attribute, Integer> getFrequencies(int lo, int hi, int fieldIndex) {
        Triplet<Integer, Integer, Integer> key = new Triplet<Integer, Integer, Integer>(lo, hi, fieldIndex);
        if (cacheFrequencies.get(key) != null)
            return cacheFrequencies.get(key);

        //if (!metadata.isCategorical(fieldIndex))
        //    throw new IllegalArgumentException("The attribute must be discrete");

        HashMap<Attribute, Integer> freq = new HashMap<Attribute, Integer>();

        String fieldName = metadata.getAttributeName(fieldIndex);
        String query = String.format("SELECT `%s`, count(*) as count FROM (SELECT `%s` FROM `%s` ORDER BY `%s`,`%s` LIMIT %d,%d) as tmp GROUP BY `%s`",
                fieldName, fieldName, tableName, metadata.getAttributeName(orderBy), metadata.getAttributeName(outputIndex), lo, (hi - lo), fieldName);

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                freq.put(Attribute.getInstance(rs.getString(fieldName), fieldName), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cacheFrequencies.put(key, freq);

        return freq;
    }

    public String toString() {
        Iterable<List<Attribute>> r = sortOver(orderBy);
        StringBuffer str = new StringBuffer();
        for (List<Attribute> l : r)
            str.append(l.toString()).append("\n");
        return str.toString();
    }

    @Override
    public void close() {
        try{
            PreparedStatement stmt = connection.prepareStatement(String.format("DROP VIEW `%s`", tableName));
            stmt.executeUpdate();
            stmt.close();
        }catch(SQLException ex){
            try{
                PreparedStatement stmt = connection.prepareStatement(String.format("DROP TABLE `%s`", tableName));
                stmt.executeUpdate();
                stmt.close();
            }catch(SQLException ex2){
                ex2.printStackTrace();
            }
        }
    }

    //TODO needs to be rewritten to exploit the count abilities of sql
    @Override
    public int getFrecuencyOf(Pair<Integer, Attribute>... values) {
        int count = 0;
        for(List<Attribute> record : sortOver(outputIndex)){
            boolean flag = true;
            for(Pair<Integer, Attribute> var : values){
                if(!record.get(var.first).equals(var.second)){
                    flag = false;
                    break;
                }
            }
            if(flag) 
                count++;
        }
        return count;
    }
}
