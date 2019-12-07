package Models;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 * A model class to handle DB interactions for admin
 * @author romand
 * @version 1.0
 */
public class AdminDB implements Serializable{

    //the variable holding the ability to get connection to the db
    private GeneralDBAccess gdba;
   
    /**
     * Initialize class and the DB connection
     */
    public AdminDB(){
        gdba = new GeneralDBAccess();
    }
    
    /**
     * Get all the reports from the DB
     * @param ds datasource resource to connect to the DB
     * @return an arraylist of arraylists of all the reports
     */
    public ArrayList<ArrayList<Integer>> seeReports(DataSource ds){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES " + 
                    "WHERE REPORTS > 0 ORDER BY REPORTS DESC");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            ArrayList<ArrayList<Integer>> reports = new ArrayList<ArrayList<Integer>>();
            while(rowSet.next()) {
                ArrayList<Integer> aUser = new ArrayList<Integer>();
                aUser.add(rowSet.getInt("USERID"));
                aUser.add(rowSet.getInt("REPORTS"));
                reports.add(aUser);
            }
            return reports;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " seeReports");
            return null;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
    /**
     * Deletes user from both the userDB and the profileDB. To fully erase user we can also erase their messages and likes, but nothing currently in the system indicate of these "ghost" records.
     * @param ds datasource resource to connect to the DB
     * @param uid user id to delete
     */
    public void deleteUser(DataSource ds, long uid){
        try{
            Connection connection = gdba.getConnection(ds);
            
            PreparedStatement deleteProfile = connection.prepareStatement("DELETE FROM USERPROFILES WHERE USERID = ?");
            deleteProfile.setString(1, ""+uid);
            deleteProfile.executeUpdate();
            
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM USERS WHERE USERID = ?");
            deleteUser.setString(1, ""+uid);
            deleteUser.executeUpdate();
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getProfileStage");
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
    //a simply method to get number of rows in a table
    private int getNumberOfRowsFromTable(DataSource ds, String tableName){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getCount = connection.prepareStatement("SELECT COUNT(*) FROM " + tableName);
            ResultSet rs = getCount.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getNumberOfRowsFromTable");
            return -1;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
    /**
     * Get number of rows in table "users"
     * @param ds datasource resource to connect to the DB
     * @return int of number of rows in table "users"
     */
    public int getNumberOfUsers(DataSource ds){
        return getNumberOfRowsFromTable(ds, "USERS");
    }

    /**
     * Get number of rows in table "userprofiles"
     * @param ds datasource resource to connect to the DB
     * @return int of number of rows in table "userprofiles"
     */
    public int getNumberOfProfiles(DataSource ds){
        return getNumberOfRowsFromTable(ds, "USERPROFILES");
    }

    /**
     * Get number of rows in table "matches"
     * @param ds datasource resource to connect to the DB
     * @return int of number of rows in table "matches"
     */
    public int getNumberOfLikes(DataSource ds){
        return getNumberOfRowsFromTable(ds, "MATCHES");
    }

    /**
     * Get number of rows in table messages
     * @param ds datasource resource to connect to the DB
     * @return int of number of rows in table "messages"
     */
    public int getNumberOfMessages(DataSource ds){
        return getNumberOfRowsFromTable(ds, "MESSAGES");
    }
    
}
