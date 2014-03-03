package libai.experimental;


import java.io.*;
import java.util.*;
import java.math.*;
import java.sql.*;
import libai.classifiers.refactor.*;
import libai.classifiers.bayes.*;
import libai.classifiers.Attribute;

/**
 *
 * @author kronenthaler
 */
public class BayesTest {
	public static void main(String arg[]) throws Exception{
		DataSet ds1 = new TextFileDataSet(new File("iris.data"), 4);
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iris", "root", "r00t");
        DataSet ds2 = new MySQLDataSet(conn, "iris", 4);
        try{
            new NaiveBayes().train(ds2).save(new File("Nb-mysql.txt"));
            new NaiveBayes().train(ds1).save(new File("Nb-text.txt"));
        }catch(Exception e){
            e.printStackTrace();
        }
		/*System.out.println(
				new NaiveBayes().train(ds2).eval(new DataRecord(new ContinuousAttribute(6), new ContinuousAttribute(130), new ContinuousAttribute(8))));*/
	}
}
