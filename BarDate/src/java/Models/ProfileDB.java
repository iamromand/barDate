package Models;

import static helperPackage.HelperFunctions.javaSplitStringToArray;
import static helperPackage.HelperFunctions.sanitizeString;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 * A model class to handle DB interactions for all the profile actions
 * @author romand
 * @version 1.0
 */
public class ProfileDB implements Serializable{

    //the variable holding the ability to get connection to the db
    private GeneralDBAccess gdba;

    /**
     * A variable used in several places to indicate profile stage is before saved any personal data
     */
    public final static int PROFILE_REQUIRE_INFO = 1;

    /**
     * A variable used in several places to indicate profile stage is after saved personal data, but before saved preferences
     */
    public final static int PROFILE_REQUIRE_PREFERRED = 2;

    /**
     * A variable used in several places to indicate profile stage is full
     */
    public final static int PROFILE_GREEN_LIGHT = 3;
    
    private final static int T_INT = 1;
    private final static int T_STRING = 2;
    
    /**
     * A variable used to indicate profile report action
     */
    public final static int REPORT = 1;

    /**
     * A variable used to indicate profile reported
     */
    public final static int IS_REPORTED = 2;
    
    /**
     * Initialize class and the DB connection
     */
    public ProfileDB(){
        gdba = new GeneralDBAccess();
    }
    
    /**
     * Getter to get all profile fields for specific user id
     * @param ds datasource resource to connect to the DB
     * @param uid user id
     * @return Map of all the data about the user
     */
    public Map<String, String> getAllFields(DataSource ds, long uid){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES " + 
                    "WHERE USERID = " + uid + " ORDER BY USERID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            Map<String, String> res = new HashMap<String, String>();
            while(rowSet.next()) {
                res.put("name", rowSet.getString("NAME").toString());
                res.put("dateOfBirth", rowSet.getDate("DATE_OF_BIRTH").toString());
                res.put("gender", rowSet.getString("GENDER"));
                res.put("locationName", rowSet.getString("LOC_NAME"));
                res.put("locationCountry", rowSet.getString("LOC_COUNTRY"));
                res.put("locationLat", rowSet.getString("LOC_LAT"));
                res.put("locationLng", rowSet.getString("LOC_LNG"));
                res.put("smoking", rowSet.getString("SMOKING"));
                res.put("pGender", rowSet.getString("PREFERRED_GENDER"));
                res.put("pSmoking", rowSet.getString("PREFERRED_SMOKING"));
                res.put("pMaxRadius", ""+rowSet.getInt("PREFERRED_MAX_RADIUS"));
                res.put("pMinAge", ""+rowSet.getInt("PREFERRED_MIN_AGE"));
                res.put("pMaxAge", ""+rowSet.getInt("PREFERRED_MAX_AGE"));
                res.put("profileImage", ""+rowSet.getString("PROFILE_IMAGE"));
                res.put("description", ""+rowSet.getString("DESCRIPTION"));
                break;
            }
            return res;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getAllFields");
            return null;
        }
        finally{
            gdba.closeConnection();    
        }
    }
    
    /**
     * Save all given information in the info into the profile
     * @param ds datasource resource to connect to the DB
     * @param info map of all data to save
     * @return true if successful. False otherwise
     */
    public boolean saveProfileInformation(DataSource ds, Map<String, String> info){
        try{
            Connection connection = gdba.getConnection(ds);
            int rowID = -1;
            PreparedStatement addEntry;
            String statement = "";
            //all the columns in the DB
            String[] columns = {"USERID","NAME", "DATE_OF_BIRTH","GENDER","LOC_LNG","LOC_LAT","LOC_NAME","LOC_COUNTRY","SMOKING","PREFERRED_MIN_AGE","PREFERRED_MAX_AGE","PREFERRED_MAX_RADIUS","PREFERRED_SMOKING","PREFERRED_GENDER","PROFILE_IMAGE", "DESCRIPTION"};
            //all the entry titles in the map
            String[] values = {"uid","name", "dateOfBirth","gender","locationLng","locationLat","locationName","locationCountry","smoking","pMinAge","pMaxAge","pMaxRadius","pSmoking","pGender","profileImage", "description"};
            //DB type for each variable
            int[] types = {T_INT,T_STRING, T_STRING,T_STRING,T_STRING,T_STRING,T_STRING,T_STRING,T_STRING,T_INT,T_INT,T_INT,T_STRING,T_STRING,T_STRING, T_STRING};
            if(columns.length != values.length || columns.length != types.length){
                return false;
            }
            
            String uid = info.get("uid");
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES " + 
                    "WHERE USERID = " + uid + " ORDER BY USERID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            //new entry
            if (!rowSet.isBeforeFirst() ) {
                statement = this.generateNewRowStatement(columns, values, types, info);
            }
            else{
                //change entry
                while(rowSet.next()){
                    rowID = rowSet.getInt("ID");
                }
                statement = this.generateExistingRowStatement(columns, values, types, info, rowID);
            }
            addEntry = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            for(int i=0, j=0; i<values.length; i++){
                if(info.get(values[i]) != null){
                    j++;
                    if(types[i] == T_INT){
                        addEntry.setInt(j, Integer.parseInt(info.get(values[i])));    
                    }
                    else{
                        String toAdd = info.get(values[i]);
                        if(values[i].equals("name") || values[i].equals("locationName") || values[i].equals("locationCountry") || values[i].equals("description")){
                            toAdd = sanitizeString(toAdd);
                        }
                        addEntry.setString(j, toAdd);
                    }
                }
                
            }
                
            int affectedRows = addEntry.executeUpdate();
            if (affectedRows == 0) {
               return false;
            }
            return true;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " saveProfileInformation");
            return false;
        }
        finally{
            gdba.closeConnection();    
        }
    }
    
    //generate a statement for inserting new data into userprofiles
    private String generateNewRowStatement(String[] columns, String[] values, int[] types, Map<String, String> info){
        String query_base = "INSERT INTO USERPROFILES (";
        String query_columns = "";
        String query_values = ") VALUES (";
        String query_end = ")";

        for(int i=0; i<columns.length; i++){
            //in debryDB strings has to be surounded with quotes and after every column a comma
            if(info.get(values[i]) != null){
                query_columns += "\""+columns[i]+"\",";
                query_values += "?,";
//                if(types[i] == this.T_INT){
//                    query_values += ""+info.get(values[i])+",";
//                }
//                else{
//                    query_values += "'"+info.get(values[i])+"',";
//                }
            }
        }
        if(query_columns.length() > 0){
            //remove last comma
            query_columns = query_columns.substring(0, query_columns.length() - 1);
            query_values = query_values.substring(0, query_values.length() - 1);
        }
        return query_base + query_columns + query_values + query_end;
    }
    
    //generate a statement for changing data in userprofiles
    private String generateExistingRowStatement(String[] columns, String[] values, int[] types, Map<String, String> info, int rowID){
        String query_base = "UPDATE USERPROFILES SET ";
        String query_where = " WHERE id = " + rowID + "";
        String query_full = "";
        for(int i=0; i<columns.length; i++){
            if(info.get(values[i]) != null){
                //in debryDB strings has to be surounded with quotes and after every column a comma
                query_full += "\""+columns[i]+"\" = ?,";
//                
//                if(types[i] == T_INT){
//                    query_full += ""+info.get(values[i])+",";
//                }
//                else{
//                    query_full += "'"+info.get(values[i])+"',";
//                }
            }
        }
        
        if(query_full.length() > 0){
            //remove last comma
            query_full = query_full.substring(0, query_full.length() - 1);
        }
        return query_base + query_full + query_where;
    }
        
    /**
     * Return profile stage for a given profile
     * @param ds datasource resource to connect to the DB
     * @param uid user id
     * @return the profile stage for a user id
     */
    public int getProfileStage(DataSource ds, long uid){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES " + 
                    "WHERE USERID = " + uid + " ORDER BY USERID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            if (!rowSet.isBeforeFirst() ) { //no lines found
                return this.PROFILE_REQUIRE_INFO;
            } 
            while(rowSet.next()) {
                if(rowSet.getString("NAME").toString() == ""){
                    return this.PROFILE_REQUIRE_INFO;
                }
                else if(rowSet.getInt("PREFERRED_MIN_AGE") == 0){
                    return this.PROFILE_REQUIRE_PREFERRED;
                }
                return this.PROFILE_GREEN_LIGHT;
            }
            return -1;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getProfileStage");
            return -1;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
        
    /**
     * Updates last connected timestamp every use of the app
     * @param ds datasource resource to connect to the DB
     * @param uid user id
     * @return true of success. false otherwise
     */
    public boolean updateOnlineStatus(DataSource ds, long uid){
        try{
            Connection connection = gdba.getConnection(ds);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String statement = "UPDATE USERPROFILES SET \"LAST_TIMESTAMP\" = '" + timestamp + "' WHERE USERID = " + uid + "";
            PreparedStatement addEntry = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            int affectedRows = addEntry.executeUpdate();
            if (affectedRows == 0) {
               return false;
            }
            return true;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " updateOnlineStatus");
            return false;
        }
        finally{
            gdba.closeConnection(); 
        }
    }

    /**
     * Report an abusive user
     * @param ds datasource resource to connect to the DB
     * @param reporter the reporter
     * @param reportee the person being reported
     */
    public void report(DataSource ds, long reporter, long reportee){
        handleReported(ds, reporter, reportee, this.REPORT);
    }
    
    /**
     * Check is user was reported abusive by a user
     * @param ds datasource resource to connect to the DB
     * @param reportee the user to check
     * @param reporter the user
     * @return true is was reported. false otherwise
     */
    public boolean isReported(DataSource ds, long reportee, long reporter){
        return handleReported(ds, reporter, reportee, this.IS_REPORTED);
    }

    /**
     * A method that handles the reporting feature.
     * @param ds datasource resource to connect to the DB
     * @param reporter the reporter
     * @param reportee the person that was reported
     * @param type type can be report of test if reported
     * @return true on success for report, true on user was reported for "is reported". false otherwise.
     */
    public boolean handleReported(DataSource ds, long reporter, long reportee, int type){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getFieldsFromProfile = connection.prepareStatement(
                    "SELECT * FROM USERPROFILES " + 
                    "WHERE USERID = " + reportee + " ORDER BY USERID");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getFieldsFromProfile.executeQuery());
            while(rowSet.next()) {
                int reports = rowSet.getInt("REPORTS");
                String reportedby = rowSet.getString("REPORTEDBY");
                String[] reportedbyArr = javaSplitStringToArray(reportedby);
                //test if wasn't already reported
                if(!Arrays.asList(reportedbyArr).contains(""+reporter)){
                    if(type == this.IS_REPORTED){
                        return false;
                    }
                    else if(type == this.REPORT){
                        reportedby += reports == 0 ? "" + reporter : ","+reporter;//add reported to list of reporters
                        reports++;
                        String statement = "UPDATE USERPROFILES SET \"REPORTS\" = " + reports + ", \"REPORTEDBY\" = '" + reportedby + "' WHERE USERID = " + reportee + "";
                        PreparedStatement addEntry = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
                        int affectedRows = addEntry.executeUpdate();
                        return true;
                    }
                }
                else if(type == this.IS_REPORTED){
                    return true;
                }
                return false;
            }
            return false;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " handleReported");
            return false;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
}
