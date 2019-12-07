/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userManagment;

import Models.AdminDB;
import Models.GeneralDBAccess;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.faces.view.ViewScoped;
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

@Named("adminBean")
@ViewScoped

/**
 * This bean manages the communication between the admin view and the DB
 * @author romand
 * @version 1.0
 */
public class AdminBean implements Serializable{
    
    //connection to the model
    AdminDB adb;
    //add datasource resource
    @Resource(lookup=GeneralDBAccess.DB_NAME) DataSource dataSource;
    
    /**
     * Initialize the bean and establish connection to the model
     */
    public AdminBean() {
        adb = new AdminDB();
    }
    
    /**
     * Generates a list of reports with number of reports and user id
     * @return A double arraylist with relevant info
     */
    public ArrayList<ArrayList<Integer>> seeReports(){
        ArrayList<ArrayList<Integer>> reports = adb.seeReports(dataSource);
        return reports;
    }
    
    /**
     * Method to delete a user
     * @param uid user id to delete
     */
    public void deleteUser(long uid){
        adb.deleteUser(dataSource, uid);
    }

    /**
     * A method to get the general number of users
     * @return String of number of users
     */
    public String getNumberOfUsers(){
        int number = adb.getNumberOfUsers(dataSource);
        if(number == -1){
            return "N/A";
        }
        return ""+number;
    }

    /**
     * A method to get the general number of profiles
     * @return String of number of profiles
     */
    public String getNumberOfProfiles(){
        int number = adb.getNumberOfProfiles(dataSource);
        if(number == -1){
            return "N/A";
        }
        return ""+number;
    }

    /**
     * A method to get the general number of likes in the system
     * @return String of number of likes in the system
     */
    public String getNumberOfLikes(){
        int number = adb.getNumberOfLikes(dataSource);
        if(number == -1){
            return "N/A";
        }
        return ""+number;
    }

    /**
     * A method to get the general number of messages in the system
     * @return String of number of messages in the system
     */
    public String getNumberOfMessages(){
        int number = adb.getNumberOfMessages(dataSource);
        if(number == -1){
            return "N/A";
        }
        return ""+number;
    }
}
