package Models;

import SystemObjects.Message;
import helperPackage.HelperFunctions;
import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 * A model class to handle DB interactions for chats
 * @author romand
 * @version 1.0
 */
public class ChatDB implements Serializable{
    
//the variable holding the ability to get connection to the db
    private GeneralDBAccess gdba;

    /**
     * Initialize class and the DB connection
     */
    public ChatDB(){
        gdba = new GeneralDBAccess();
    }
    
    /**
     * Add a message to Messages DB
     * @param ds datasource resource to connect to the DB
     * @param to to whom the message is sent
     * @param from from who
     * @param message the actual message
     * @param timestamp timestamp of the sent message
     */
    public void addMessageToMessagesDB(DataSource ds, String to, String from, String message, Timestamp timestamp){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement addMessage = connection.prepareStatement(
                    "INSERT INTO MESSAGES " + 
                    "(MESSAGE_TO, MESSAGE_FROM, MESSAGE_ACTUAL, MESSAGE_TIMESTAMP, TO_NEW)" +
                    "VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            addMessage.setString(1, to);
            addMessage.setString(2, from);
            addMessage.setString(3, HelperFunctions.truncate(HelperFunctions.sanitizeString(message), 500));
            addMessage.setTimestamp(4, timestamp);
            //to new means the message wasn't viewed yet by the other side
            addMessage.setBoolean(5, false);
            int affectedRows = addMessage.executeUpdate();
        }
        catch (SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " addMessageToMessageDB");
        }
        finally{
            gdba.closeConnection();
        }
    }
    
    /**
     * Get all messages between two users
     * @param ds datasource resource to connect to the DB
     * @param from from who
     * @param to to who
     * @return all messages including message direction
     */
    public ArrayList<Message> getMessages(DataSource ds, String from, String to){
        ArrayList<Message> list_of_messages = new ArrayList<Message>();
        
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getMessages = connection.prepareStatement(
                    "SELECT * FROM MESSAGES " + 
                    "WHERE (MESSAGE_FROM = '" + from + "' AND MESSAGE_TO = '" + to + "')"
                     + "OR (MESSAGE_FROM = '" + to + "' AND MESSAGE_TO = '" + from + "') ORDER BY MESSAGE_TIMESTAMP");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getMessages.executeQuery());
            String side = "";
            while(rowSet.next()) {
                //direction of the message for the visual view
                side = "self";
                if(rowSet.getString("MESSAGE_FROM").equals(from)){
                    side = "other";
                }
                Message msg = new Message(
                        parseInt(rowSet.getString("MESSAGE_FROM")),
                        parseInt(rowSet.getString("MESSAGE_TO")),
                        rowSet.getString("MESSAGE_ACTUAL"),
                        rowSet.getTimestamp("MESSAGE_TIMESTAMP"),
                        side);
                list_of_messages.add(msg);
            }
            //to new updated to not to show this as a "new" message
            String statement = "UPDATE MESSAGES SET \"TO_NEW\" = 'true' WHERE (MESSAGE_FROM = '" + from + "' AND MESSAGE_TO = '" + to + "')";
            PreparedStatement addEntry = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            int affectedRows = addEntry.executeUpdate();
            
            return list_of_messages;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getMessages");
            return null;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
    /**
     * Test if there are new messages between two people
     * @param ds datasource resource to connect to the DB
     * @param to to whom
     * @param from from whom
     * @return true if new messages. false otherwise.
     */
    public boolean getIfNewMessagesFromPerson(DataSource ds, String to, String from){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getMessages = connection.prepareStatement(
                    "SELECT * FROM MESSAGES " + 
                    "WHERE (TO_NEW = 'false' AND MESSAGE_TO = '" + to + "' AND MESSAGE_FROM = '" + from + "')");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getMessages.executeQuery());
            String side = "";
            while(rowSet.next()) {
                return true;
            }
            return false;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getIfNewMessagesFromPerson");
            return false;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
    /**
     * Test if new messages in general to a user
     * @param ds datasource resource to connect to the DB
     * @param to the user
     * @return true if new messages. false otherwise.
     */
    public boolean getIfNewMessagesInGeneral(DataSource ds, String to){
        try{
            Connection connection = gdba.getConnection(ds);
            PreparedStatement getMessages = connection.prepareStatement(
                    "SELECT * FROM MESSAGES " + 
                    "WHERE (TO_NEW = 'false' AND MESSAGE_TO = '" + to + "')");
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            rowSet.populate(getMessages.executeQuery());
            while(rowSet.next()) {
                return true;
            }
            return false;
        }
        catch(SQLException sqle){
            System.err.println("SQLException: " + sqle.getMessage() + " getIfNewMessagesInGeneral");
            return false;
        }
        finally{
            gdba.closeConnection(); 
        }
    }
    
}
