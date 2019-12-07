package userarea;

import Models.ChatDB;
import Models.GeneralDBAccess;
import Models.SearchDB;
import SystemObjects.Message;
import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import userManagment.ConnectionBean;

@DataSourceDefinition(
        name = GeneralDBAccess.DB_NAME,
        className = GeneralDBAccess.DB_CLASS_NAME,
        url = GeneralDBAccess.DB_URL,
        databaseName = GeneralDBAccess.DB_DATABASE_NAME,
        user = GeneralDBAccess.DB_USER,
        password = GeneralDBAccess.DB_PASSWORD
)

@Named("chatManager")
@ViewScoped
/**
 * This bean manages the communication between the chat view and the chat model
 * @author romand
 * @version 1.0
 */
public class ChatManager implements Serializable{
    
    //connection to the model
    ChatDB cdb;
    //session to hold id of the connected user
    @Inject private ConnectionBean cbean;
    //holder of the message sent from the user to the bean
    private String actualMessage = "";
    
    /**
     * Getter method to return id of the connected user
     * @return id of the connected user
     */
    public long getUid(){    return cbean.getUid();   }
    
    //add datasource resource
    @Resource(lookup="java:global/jdbc/datingdb")
    DataSource dataSource;
    
    /**
     * A constructor to establish the connection to the model
     */
    public ChatManager(){
        cdb = new ChatDB();
    }

    /**
     * Getter of the message sent from the user to the bean
     * @return the message
     */
    public String getActualMessage() {
        return actualMessage;
    }

    /**
     * Setter of the message sent from the user to the bean
     * @param actualMessage the message
     */
    public void   setActualMessage(String actualMessage) {
        this.actualMessage = actualMessage;
    }
    
    /**
     * Gets the message sent from the user and uses it's connection to the DB to put in into the DB. Finally empties the message for next use.
     */
    public void sendMessage(){
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String other_id = request.getParameter("pid");
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        cdb.addMessageToMessagesDB(dataSource, other_id, ""+getUid(), actualMessage, ts);
        setActualMessage("");
    }
    
    /**
     * Outputs an Arraylist of all the messages between the connected user and the "other_id" user
     * @param other_id the id of the other user
     * @return Arraylist of all the messages between two users
     */
    public ArrayList<Message> getMessages(String other_id){
        ArrayList<Message> messages = cdb.getMessages(dataSource, "" + other_id, "" + getUid());
        return messages;
    }
    
    /**
     * Tests if the user is allowed to chat with another user, by checking if they both like each other.
     * @param pid the id of the other user
     * @return true in case the chat is allowed. false otherwise.
     */
    public boolean allowed(String pid){
        try{
            SearchDB sdb = new SearchDB();
            return (sdb.isLiked(dataSource, parseInt(pid), (int)getUid()) && sdb.isLiked(dataSource, (int)getUid(), parseInt(pid)));
        }
        catch(Exception e){
            return false;
        }
    }
    
    /**
     * A function to find if the user has new messages. Uses getIfNewMessagesInGeneral method of the model. Generally relies on comparing tiemstamps between last page view and last read message.
     * @return true if has new messages. False otherwise.
     */
    public boolean getIfNewMessagesInGeneral(){
        return cdb.getIfNewMessagesInGeneral(dataSource, ""+getUid());
    }

}
