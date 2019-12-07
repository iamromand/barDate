package userManagment;

import Models.GeneralDBAccess;
import Models.UsersDB;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

@DataSourceDefinition(
        name = GeneralDBAccess.DB_NAME,
        className = GeneralDBAccess.DB_CLASS_NAME,
        url = GeneralDBAccess.DB_URL,
        databaseName = GeneralDBAccess.DB_DATABASE_NAME,
        user = GeneralDBAccess.DB_USER,
        password = GeneralDBAccess.DB_PASSWORD
)

@Named("userInfoBean")
@SessionScoped

/**
 * This bean manages the communication between user actions and the users DB
 * @author romand
 * @version 1.0
 */
public class UserInfoBean implements Serializable{
    
    private String password;
    private String email;
    private String phone;
    
    //connection the the User DB
    private UsersDB udb;

    //session to hold id of the connected user
    @Inject private ConnectionBean cbean;

    //add datasource resource
    @Resource(lookup=GeneralDBAccess.DB_NAME) DataSource dataSource;
    
    /**
     * Getter method to return password stored in the bean
     * @return password as a string
     */
    public String getPassword(){   return password;   }

    /**
     * Setter to store password in the bean
     * @param password the password of current user
     */
    public void setPassword(String password){   this.password = password;   }

    /**
     * Getter method to return email stored in the bean
     * @return email as a string
     */
    public String getEmail(){   return email;   }

    /**
     * Setter to store email in the bean
     * @param email the email of current user
     */
    public void setEmail(String email){   this.email = email;   }

    /**
     * Getter method to return phone number stored in the bean
     * @return phone number as a string
     */
    public String getPhone(){   return phone;   }

    /**
     * Setter to store phone number in the bean
     * @param phone the phone number of current user
     */
    public void setPhone(String phone){   this.phone = phone;   }
    
    /**
     * Create a new instance of UserInfoBean and initialize DB connection.
     */
    public UserInfoBean(){
        udb = new UsersDB();
    }
    
    /**
     * Registers the user in the database based on inputed data.
     */
    public void register(){
        long uid = udb.register(dataSource, getPassword(), getEmail(), getPhone());
        afterConnectedLogic(uid, "Can't register user", "registerForm");
    }
    
    /**
     * Logs in a user if has correct info.
     */
    public void login(){
        long uid = udb.login(dataSource, this.getEmail(), this.getPassword());
        afterConnectedLogic(uid, "Invalid Username and/or Password", "loginForm");
    }
    
    /**
     * Method that disconnects the user - cleans the connection bean, the session and redirects to homepage.
     * @throws IOException
     */
    public void disconnect() throws IOException{
        cbean.disconnect();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }

    //a method that connects the user in the system (writes into connection bean), and either redirects to dashboard or throws an error
    private void afterConnectedLogic(long uid, String messageOnError, String contextForMessageOnError){
        if(uid != -1){
            cleanData();
            cbean.connect(uid);
        }    
        try{
            if(uid != -1){
                FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard.xhtml");
            }
            FacesContext context = FacesContext.getCurrentInstance(); 
            FacesMessage message = new FacesMessage(messageOnError); 
            context.addMessage(contextForMessageOnError, message); 
        }
        catch(IOException ioe){
            System.err.println("IOException: " + ioe.getMessage() + " afterConnectedLogic");
        }
    }
    
    //cleans data for next use if needed
    private void cleanData(){
        this.setPassword("");
        this.setEmail("");
        this.setPhone("");
    }
    
    /**
     * validates email not in use against the database for registration purposes
     * @param context validator param 
     * @param comp validator param
     * @param value validator param
     * @throws ValidatorException in case the email is in use
     */
    public void validateEmail(FacesContext context, UIComponent comp, Object value){
        String emailToValidate = (String) value;
        if(!udb.validateEmailNotInDB(dataSource, emailToValidate)){
            FacesMessage message = new FacesMessage("Email is already in use");
            context.addMessage(comp.getClientId(context), message);
            throw new ValidatorException(message);
        }
    }
}