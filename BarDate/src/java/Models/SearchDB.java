package Models;

import SystemObjects.Person;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 * A model class to handle DB interactions for search and liked
 * @author romand
 * @version 1.0
 */
public class SearchDB implements Serializable{

    //the variable holding the ability to get connection to the db
    private GeneralDBAccess gdba;
    
    /**
     * Initialize class and the DB connection
     */
    public SearchDB(){
        gdba = new GeneralDBAccess();
    }
    
    /**
     * Return info for single person and wrap it into the person object
     * @param ds datasource resource to connect to the DB
     * @param personId the id of the person
     * @return person object representing the person
     */
    public Person getPerson(DataSource ds, String personId){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES WHERE USERID = "+personId+" ORDER BY USERID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            while(rowSet.next()) {
                Person person = new Person( rowSet.getInt("USERID"),
                                            rowSet.getString("NAME"),
                                            rowSet.getString("SMOKING"),
                                            rowSet.getString("GENDER"),
                                            rowSet.getString("PREFERRED_MIN_AGE"),
                                            rowSet.getString("PREFERRED_MAX_AGE"),
                                            rowSet.getString("PREFERRED_MAX_RADIUS"),
                                            rowSet.getString("PREFERRED_GENDER"),
                                            rowSet.getString("PREFERRED_SMOKING"),
                                            rowSet.getDate("DATE_OF_BIRTH"),
                                            rowSet.getString("PROFILE_IMAGE"),
                                            rowSet.getString("DESCRIPTION"),
                                            rowSet.getString("LOC_COUNTRY"),
                                            rowSet.getString("LOC_NAME"),
                                            rowSet.getString("LOC_LAT"),
                                            rowSet.getString("LOC_LNG"),
                                            rowSet.getTimestamp("LAST_TIMESTAMP")
                );
                return person;
            }
            return null;
        }
        catch (SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getPerson");
            return null;
        }
        finally{
            gdba.closeConnection();
        }
    }
    
    /**
     * Gets a list of all people except connected user
     * @param ds datasource resource to connect to the DB
     * @param personId current connected user
     * @return an arraylist of all people
     */
    public ArrayList<Person> getPeople(DataSource ds, String personId){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES WHERE USERID != "+personId+" ORDER BY USERID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            ArrayList<Person> list_of_persons = new ArrayList<Person>();
            while(rowSet.next()) {
                Person person = new Person( rowSet.getInt("USERID"),
                                            rowSet.getString("NAME"),
                                            rowSet.getString("SMOKING"),
                                            rowSet.getString("GENDER"),
                                            rowSet.getString("PREFERRED_MIN_AGE"),
                                            rowSet.getString("PREFERRED_MAX_AGE"),
                                            rowSet.getString("PREFERRED_MAX_RADIUS"),
                                            rowSet.getString("PREFERRED_GENDER"),
                                            rowSet.getString("PREFERRED_SMOKING"),
                                            rowSet.getDate("DATE_OF_BIRTH"),
                                            rowSet.getString("PROFILE_IMAGE"),
                                            rowSet.getString("DESCRIPTION"),
                                            rowSet.getString("LOC_COUNTRY"),
                                            rowSet.getString("LOC_NAME"),
                                            rowSet.getString("LOC_LAT"),
                                            rowSet.getString("LOC_LNG"),
                                            rowSet.getTimestamp("LAST_TIMESTAMP")
                );
                list_of_persons.add(person);
            }
            return list_of_persons;
        }
        catch (SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getPeople");
            return null;
        }
        finally{
            gdba.closeConnection();
        }
    }

    /**
     * Test if a person is liked by other person
     * @param ds datasource resource to connect to the DB
     * @param other_id the person to be liked
     * @param this_id the one that liked
     * @return true is test successful. false otherwise.
     */
    public boolean isLiked(DataSource ds, int other_id, int this_id){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromMatches = connection.prepareStatement(
                "SELECT * FROM MATCHES WHERE LIKE_OTHER="+other_id+" AND LIKE_ACTIVE="+this_id+" ORDER BY ID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromMatches.executeQuery());
            while(rowSet.next()) {
                return true;
            }
            return false;
        }
        catch (SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " isLiked");
            return false;
        }
        finally{
            gdba.closeConnection();
        }    
    }
    
    /**
     * A toggle method to like and unlike the other user
     * @param ds datasource resource to connect to the DB
     * @param me the current connected user
     * @param who_to_like_id the other user
     */
    public void like_unlike(DataSource ds, long me, long who_to_like_id){
        try{
            Connection connection = gdba.getConnection(ds);
            if(!isLiked(ds, (int)who_to_like_id, (int)me)){
                PreparedStatement addEntry = connection.prepareStatement(
                        "INSERT INTO MATCHES " +
                        "(LIKE_ACTIVE, LIKE_OTHER)" + 
                        "VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
                    addEntry.setString(1, ""+me);
                    addEntry.setString(2, ""+who_to_like_id);
                    int affectedRows = addEntry.executeUpdate();
                }
            else{
                PreparedStatement addEntry = connection.prepareStatement(
                        "DELETE FROM MATCHES " +
                        "WHERE LIKE_ACTIVE=? AND LIKE_OTHER=?");
                    addEntry.setString(1, ""+me);
                    addEntry.setString(2, ""+who_to_like_id);
                    addEntry.executeUpdate();
                }
                
            }
        
        catch (SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " like_unlike");
        }
        finally{
            gdba.closeConnection();
        }
    }
    
}
