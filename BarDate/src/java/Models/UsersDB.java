package Models;

import static helperPackage.HelperFunctions.md5;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 * A model class to handle DB interactions for users - connection, registration etc.
 * @author romand
 * @version 1.0
 */
public class UsersDB{

    //the variable holding the ability to get connection to the db
    private final GeneralDBAccess gdba;
    
    /**
     * Initialize class and the DB connection
     */
    public UsersDB(){
        gdba = new GeneralDBAccess();
    }
    
    /**
     * Registers user into the "users" db
     * @param ds datasource resource to connect to the DB
     * @param password password
     * @param email email
     * @param phone phone number
     * @return user id
     */
    public long register(DataSource ds, String password, String email, String phone){

        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement addEntry = connection.prepareStatement(
                "INSERT INTO USERS " +
                "(PASSWORD, EMAIL, PHONE)" + 
                "VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            addEntry.setString(1, md5(password));
            addEntry.setString(2, email);
            addEntry.setString(3, phone);
            int affectedRows = addEntry.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            try (ResultSet generatedKeys = addEntry.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (generatedKeys.getLong(1));
                }
                else {
                    return -1;
                }
            }
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " register");
            return -1;
        }
        finally{
            gdba.closeConnection();    
        }
    }
    
    /**
     * Handles proper login into the system
     * @param ds datasource resource to connect to the DB
     * @param email email
     * @param password password
     * @return user id on successful login. -1 otherwise
     */
    public long login(DataSource ds, String email, String password){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getLogin = connection.prepareStatement(
                    "SELECT * FROM USERS " + 
                    "WHERE EMAIL = '" + email + "'");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getLogin.executeQuery());
            while(rowSet.next()) {
                String dbpassword = rowSet.getString("PASSWORD");
                String enteredPassword = md5(password);
                if(dbpassword.equals(enteredPassword)){
                        return (rowSet.getLong(1));
                    }
            }
            return -1;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " login");
            return -1;
        }
        finally{
            gdba.closeConnection();
        }
    }
    
    /**
     * Tests whether the email is already in the system
     * @param ds datasource resource to connect to the DB
     * @param email email
     * @return true if not in DB. false otherwise.
     */
    public boolean validateEmailNotInDB(DataSource ds, String email){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getEmails = connection.prepareStatement("SELECT * FROM USERS WHERE EMAIL = '" + email + "'");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getEmails.executeQuery());
            if(rowSet.size() > 0){
                return false;
            }
            return true;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " validateEmailNotInDB");
            return false;
        }
        finally{
            gdba.closeConnection();
        }
    }
    
    /**
     * Test to find out if id is of admin
     * @param ds datasource resource to connect to the DB
     * @param id id to check
     * @return true if admin. false otherwise
     */
    public boolean isAdmin(DataSource ds, long id){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getUsers = connection.prepareStatement("SELECT * FROM USERS WHERE (USERID = " + id + " AND IS_ADMIN = 'true')");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getUsers.executeQuery());
            if(rowSet.size() > 0){
                return true;
            }
            return false;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " isAdmin");
            return false;
        }
        finally{
            gdba.closeConnection();
        }
    }
    
}
