package userarea;

import Models.ChatDB;
import Models.GeneralDBAccess;
import Models.ProfileDB;
import Models.SearchDB;
import SystemObjects.Person;
import static helperPackage.HelperFunctions.calculateDistance;
import static helperPackage.HelperFunctions.simpleSplitStringToArray;
import java.io.Serializable;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
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


@Named("searchResults")
@SessionScoped

/**
 * This bean manages the communication between the search, liked and single profile view and the DB
 * @author romand
 * @version 1.0
 */
public class SearchResults implements Serializable {
    
    //currentLikedId to allow the bean to take care of different asspects of the liking process
    private long currentLikedId = -1;
    
    //connection the the DB models
    SearchDB sdb;
    ChatDB cdb;
    ProfileDB pdb;
    
    //session to hold id of the connected user
    @Inject private ConnectionBean cbean;
    
    //add datasource resource
    @Resource(lookup="java:global/jdbc/datingdb")
    DataSource dataSource;
    
    /**
     * Getter method to return id of the connected user
     * @return id of the connected user
     */
    public long getUid(){    return cbean.getUid();   }

    /**
     * Getter method to return id of the last person that was liked by the user
     * @return id of the last person that was liked by the user
     */
    public long getCurrentLikedId() {   return currentLikedId;  }

    /**
     * Setter method to return id of the last person that was liked by the user
     * @param currentLikedId id of the last person that was liked by the user
     */
    public void setCurrentLikedId(long currentLikedId) {    this.currentLikedId = currentLikedId;   }
    

    /**
     * Creates a new instance of searchResults and initializes the DB connections
     */
    public SearchResults() {
        sdb = new SearchDB();
        cdb = new ChatDB();
        pdb = new ProfileDB();
    }
    
    /**
     * Gets a list of all people relevant for the search page
     * @return Arraylist of all people relevant to search page
     */
    public ArrayList<Person> getPeople(){
        return getPeopleInternal();
    }

    /**
     * Gets a list of all people relevant for the liked page
     * @return Arraylist of all people relevant to the liked page
     */
    public ArrayList<Person> getLikedPeople(){
        return getPeopleInternal();
    }


    private ArrayList<Person> getPeopleInternal(){
        Person me = sdb.getPerson(dataSource, "" + getUid());
        ArrayList<Person> people = sdb.getPeople(dataSource, "" + getUid());
        
        Comparator sortbyLastConnected = new SortPersonByLastConnected();
        Collections.sort(people,sortbyLastConnected);
        //we now have a person "me" and an array list of all the other people sorted by last connected
        
        ArrayList<Person> filteredPeople = new ArrayList<>();
        //for loop adds into filtered people all people that are compatible to the user
        for(Person person : people){
            if(pdb.getProfileStage(dataSource, person.getPersonid()) == ProfileDB.PROFILE_GREEN_LIGHT){
                if(testIfCompatible(me, person) && testIfCompatible(person, me)){
                    filteredPeople.add(person);
                }
            }
            
        }
        return filteredPeople;
    }
    
    //core compatibiliy function between two people. Has "if"s that check age, gender and other compatibilities.
    private boolean testIfCompatible(Person me, Person other){
        boolean genderFits = false;
        if(other.getAge() < parseInt(me.getPrefered_min_age()) || other.getAge() > parseInt(me.getPrefered_max_age())){
            return false;
        }
        for(String pgender : simpleSplitStringToArray(me.getPrefered_gender())){
            if(other.getGender().equals(pgender)){
                genderFits = true;
                break;
            }
        }
        if(!genderFits){
            return false;
        }
        if(!me.getLoc_country().equals(other.getLoc_country())){
            return false;
        }
        double distance = calculateDistance(me.getLoc_lat(), other.getLoc_lat(), me.getLoc_lng(), other.getLoc_lng(), 0.0, 0.0);
        distance /= 1000; //translate from meters to km
        if(parseDouble(me.getPrefered_max_radius()) < distance){
            return false;
        }
        if((me.getPrefered_smoking().equals("smoking") && (other.getSmoking().equals("no"))) || (me.getPrefered_smoking().equals("nonsmoking") && (!other.getSmoking().equals("no")))){
            return false;
        }
        return true;
    }

    /**
     * Gets single person from person Id
     * @param personId the id of the person to get
     * @return the Person from the id
     */
    public Person getPerson(String personId){
        return sdb.getPerson(dataSource, personId);
    }
        
    /**
     * Method to check if the other person is liked by the person
     * @param other_id other person id
     * @param this_id current person id
     * @return true is liked. false otherwise.
     */
    public boolean isLiked(int other_id, int this_id){
        return sdb.isLiked(dataSource, other_id, this_id);
    }
    
    /**
     * Method to check if current connected user likes the other user.
     * @param oid the other user id
     * @return "Liked" if liked, "Like" is not. Special case - when both like each other returns "Mutual Like".
     */
    public String isLiked(String oid){
        int id = Integer.parseInt(oid);
        if(this.isLiked(id, (int)getUid())){
            if(this.isLiked((int)getUid(), id)){
                return "Mutual Like";
            }
            else{
                return "Liked";
            }
            
        }
        return "Like";
    }

    /**
     * Method to test if connected user has messages from other user
     * @param oid other user id
     * @return true is has new messages. false otherwise
     */
    public boolean isNewMessages(String oid){
        return cdb.getIfNewMessagesFromPerson(dataSource, ""+getUid(), oid);
    }

    /**
     * Method to return the relevant string to show on the chat button
     * @param oid the id of the profile where the chat button located
     * @return "Chat (new)" when has new messages. "Chat" otherwise.
     */
    public String chatString(String oid){
        boolean messages = cdb.getIfNewMessagesFromPerson(dataSource, ""+getUid(), oid);
        if(messages){
            return "Chat (new)";
        }
        return "Chat";
    }
    
    /**
     * A flip flop method to like or unlike another user by the current connected user
     * @param id the id of the user to like or unlike.
     */
    public void likeUnlike(long id){
        sdb.like_unlike(dataSource, getUid(), id);
    }
    
    /**
     * Method to report a user
     * @param id the id of the user to report
     */
    public void report(long id){
        pdb.report(dataSource, getUid(), id);
    }

    /**
     * Method to test if user is reported and change string on the report button
     * @param id the potentially reported user
     * @return "Reported" if user reported. "Report" otherwise.
     */
    public String isReported(long id){
        if(pdb.isReported(dataSource, id, getUid())){
            return "Reported";
        }
        else{
            return "Report";
        }
    }    
}
