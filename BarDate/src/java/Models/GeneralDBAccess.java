package Models;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;

/**
 * A general connection class holding the connection info
 * @author romand
 * @version 1.0
 */
public class GeneralDBAccess implements Serializable{
    
    /**
     * DB name
     */
    public final static String DB_NAME = "java:global/jdbc/datingdb";

    /**
     * DB class name
     */
    public final static String DB_CLASS_NAME = "org.apache.derby.jdbc.ClientDataSource";

    /**
     * DB url
     */
    public final static String DB_URL = "jdbc:derby://localhost:1527/datingdb";

    /**
     * DB database name
     */
    public final static String DB_DATABASE_NAME = "datingdb";

    /**
     * DB user
     */
    public final static String DB_USER = "roman";

    /**
     * DB password
     */
    public final static String DB_PASSWORD = "test";
    Connection connection;

    /**
     * Initialize the DB access
     */
    public GeneralDBAccess(){
    }
    
    //get all DB tables
    private Set<String> getDBTables(Connection conn) throws SQLException{
        Set<String> set = new HashSet<String>();
        String[] types = {"TABLE"};
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "%", types);
        while (rs.next())
        {
            set.add(rs.getString("TABLE_NAME").toLowerCase());
        }
        return set;
    }
    
    /**
     * The only way to connect to the DB from the app. All other methods and classes call this method.
     * @param dataSource datasource resource to connect to the DB
     * @return connection variable
     * @throws SQLException
     */
    public Connection getConnection(DataSource dataSource) throws SQLException{
        if(dataSource == null){
            throw new SQLException("Unable to obtain DataSource: this.dataSource == null");
        }
        connection = dataSource.getConnection();
        if(connection == null){
            throw new SQLException("Unable to obtain DataSource: connection == null");
        }
        return connection;
    }

    /**
     * Closes the connection
     */
    public void closeConnection(){
        try{
            if(connection != null){
                connection.close();
            }
        }
        catch(SQLException e){
            System.err.println("SQLException: " + e.getMessage() + " closeConnection");
        }
        
    }
}
