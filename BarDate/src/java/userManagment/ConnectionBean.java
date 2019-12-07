package userManagment;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("connectionBean")
@SessionScoped

/**
 * This bean manages holds the connection for the use of other beans
 * @author romand
 * @version 1.0
 */
public class ConnectionBean implements Serializable{

    //the holder of the user id
    private long uid = -1;

    /**
     * Getter to get the user id
     * @return user id
     */
    public long getUid() {  return uid; }

    /**
     * Setter to set the user id
     * @param uid the user id to set
     */
    public void setUid(long uid) {  this.uid = uid; }

    /**
     * Constructor
     */
    public ConnectionBean() {
        
    }
    
    /**
     * The actual method that sets the user id - the recommended way to set user id.
     * @param uid
     */
    public void connect(long uid){
        this.setUid(uid);
    }
    
    /**
     * A method that unsets the user id
     */
    public void disconnect(){
        this.setUid(-1);
    }
    
    /**
     * Checks if the method holds a valid user id that is connected
     * @return true is user connected. false otherwise.
     */
    public boolean isConnected(){
        return (this.getUid() != -1);
    }
    
}
