package SystemObjects;

import static helperPackage.HelperFunctions.getAgeFromSQLDate;
import static java.lang.Double.parseDouble;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import userManagment.ProfileHandlerBean;

/**
 * An object representation of a person in a system
 * @author romand
 */
public class Person {
    //person attributes
    private long personid;
    private String name;
    private String smoking;
    private String gender;
    private String prefered_min_age;
    private String prefered_max_age;
    private String prefered_max_radius;
    private String prefered_gender;
    private String prefered_smoking;
    private String date_of_birth;
    private String description;
    
    private String loc_country;
    private String loc_name;
    private Double loc_lat;
    private Double loc_lng;

    private Timestamp last_connected;


    private int age;
    private String profile_image;
 
    /**
     * Getter to get a person id
     * @return person id
     */
    public long getPersonid() { return personid;    }

    /**
     * Setter to set person id
     * @param personid person id
     */
    public void setPersonid(long personid) {    this.personid = personid;   }

    /**
     * Getter to get a person name
     * @return person name
     */
    public String getName() { return name;    }

    /**
     * Setter to set person name
     * @param name person name
     */
    public void setName(String name) {    this.name = name;   }

    /**
     * Getter to get person smoking status
     * @return person smoking status
     */
    public String getSmoking() {    return smoking; }

    /**
     * Setter to set person smoking status
     * @param smoking person smoking status
     */
    public void setSmoking(String smoking) {    this.smoking = smoking; }

    /**
     * Getter to get person gender
     * @return person gender
     */
    public String getGender() { return gender;  }

    /**
     * Setter to set person gender
     * @param gender person gender
     */
    public void setGender(String gender) {  this.gender = gender;   }

    /**
     * Getter to get the preferred min age
     * @return person preferred min age
     */
    public String getPrefered_min_age() {   return prefered_min_age;    }

    /**
     * Setter to set person preferred min age
     * @param prefered_min_age person preferred min age
     */
    public void setPrefered_min_age(String prefered_min_age) {  this.prefered_min_age = prefered_min_age;   }

    /**
     * Getter to get the preferred max age
     * @return person preferred max age
     */
    public String getPrefered_max_age() {   return prefered_max_age;    }

    /**
     * Setter to set person preferred max age
     * @param prefered_max_age person preferred max age
     */
    public void setPrefered_max_age(String prefered_max_age) {  this.prefered_max_age = prefered_max_age;   }

    /**
     * Getter to get the preferred max radius
     * @return persons preferred max radius
     */
    public String getPrefered_max_radius() {    return prefered_max_radius; }

    /**
     * Setter to set person preferred max radius
     * @param prefered_max_radius person preferred max radius
     */
    public void setPrefered_max_radius(String prefered_max_radius) {    this.prefered_max_radius = prefered_max_radius; }

    /**
     * Getter to get the preferred gender
     * @return person preferred gender
     */
    public String getPrefered_gender() {    return prefered_gender; }

    /**
     * Setter to set person preferred gender
     * @param prefered_gender person preferred gender
     */
    public void setPrefered_gender(String prefered_gender) {    this.prefered_gender = prefered_gender;}

    /**
     * Getter to get the preferred smoking status
     * @return person preferred smoking status
     */
    public String getPrefered_smoking() {   return prefered_smoking;    }

    /**
     * Setter to set person preferred smoking status
     * @param prefered_smoking person preferred smoking status
     */
    public void setPrefered_smoking(String prefered_smoking) {  this.prefered_smoking = prefered_smoking;   }

    /**
     * Getter to get person date of birth
     * @return person date of birth
     */
    public String getDate_of_birth() {  return date_of_birth;   }

    /**
     * Setter to set person date of birth
     * @param date_of_birth person date of birth
     */
    public void setDate_of_birth(String date_of_birth) {    this.date_of_birth = date_of_birth; }

    /**
     * Getter to get person age
     * @return person age
     */
    public int getAge() {    return age; }

    /**
     * Setter to set person age
     * @param age person age
     */
    public void setAge(int age) {    this.age = age; }

    /**
     * Getter to get person profile image
     * @return person profile image
     */
    public String getProfile_image() {  return profile_image;   }

    /**
     * Setter to set person profile image
     * @param profile_photo person profile image
     */
    public void setProfile_image(String profile_photo) {    this.profile_image = profile_photo; }
    
    /**
     * Getter to get person location country
     * @return person location country
     */
    public String getLoc_country() {
        return loc_country;
    }

    /**
     * Setter to set person location country
     * @param loc_country person location country
     */
    public void setLoc_country(String loc_country) {
        this.loc_country = loc_country;
    }

    /**
     * Getter to get person location name
     * @return person location name
     */
    public String getLoc_name() {
        return loc_name;
    }

    /**
     * Setter to set person location name
     * @param loc_name person location name
     */
    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    /**
     * Getter to get person location latitude
     * @return person location latitude
     */
    public Double getLoc_lat() {
        return loc_lat;
    }

    /**
     * Setter to set person location latitude
     * @param loc_lat person location latitude
     */
    public void setLoc_lat(Double loc_lat) {
        this.loc_lat = loc_lat;
    }

    /**
     * Getter to get person location longitude
     * @return person location longitude
     */
    public Double getLoc_lng() {
        return loc_lng;
    }

    /**
     * Setter to set person location longitude
     * @param loc_lng person location longitude
     */
    public void setLoc_lng(Double loc_lng) {
        this.loc_lng = loc_lng;
    }

    /**
     * Getter to get person description
     * @return person description
     */
    public String getDescription() {return description;}

    /**
     * Setter to set person description
     * @param description person description
     */
    public void setDescription(String description) {this.description = description;}
    
    /**
     * Getter to get person last connected time
     * @return last connected time
     */
    public Timestamp getLast_connected() {
        return last_connected;
    }

    /**
     * Setter to set when person was last connected
     * @param last_connected the time when the person was last connected
     */
    public void setLast_connected(Timestamp last_connected) {
        this.last_connected = last_connected;
    }
    
    /**
     * Initialize object person
     * @param personid person id
     * @param name person name
     * @param smoking person smoking status
     * @param gender person gender
     * @param prefered_min_age person preferred min age
     * @param prefered_max_age person preferred max age
     * @param prefered_max_radius person preferred max radius
     * @param prefered_gender person preferred gender
     * @param prefered_smoking person preferred smoking
     * @param dateOfBirth person date of birth
     * @param profileImage person profile image
     * @param description person description
     * @param loc_country person location country
     * @param loc_name person location name
     * @param loc_lat person location latitude
     * @param loc_lng person location longitude
     * @param last_connected person last connected timestamp
     */
    public Person(long personid,
                    String name,
                    String smoking,
                    String gender,
                    String prefered_min_age,
                    String prefered_max_age,
                    String prefered_max_radius,
                    String prefered_gender,
                    String prefered_smoking,
                    Date dateOfBirth,
                    String profileImage,
                    String description,
                    String loc_country,
                    String loc_name,
                    String loc_lat,
                    String loc_lng,
                    Timestamp last_connected){
        this.personid = personid;
        this.name = name;
        this.smoking = smoking;
        this.gender = gender;
        this.prefered_min_age = prefered_min_age;
        this.prefered_max_age = prefered_max_age;
        this.prefered_max_radius = prefered_max_radius;
        this.prefered_gender = prefered_gender;
        this.prefered_smoking = prefered_smoking;
        this.prefered_max_radius = prefered_max_radius;
        this.profile_image = profileImage;
        if((this.profile_image == null)||(this.profile_image.equals(""))){
            this.profile_image = ProfileHandlerBean.getDefaultImage();
        }
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        this.date_of_birth = df.format(dateOfBirth);
        this.age = getAgeFromSQLDate(dateOfBirth);
        this.description = description;
        this.loc_country = loc_country;
        this.loc_name = loc_name;
        this.loc_lat = parseDouble(loc_lat);
        this.loc_lng = parseDouble(loc_lng);
        this.last_connected = last_connected;
    }
    
    public String toString(){
        return "ID: " + this.personid + 
                " Name: " + this.name +
                " Smoking: " + this.smoking +
                " Gender: " + this.gender +
                " Preferred min age: " + this.prefered_min_age +
                " Preferred max age: " + this.prefered_max_age +
                " Preferred max radius: " + this.prefered_max_radius +
                " Preferred gender: " + this.prefered_gender +
                " Preferred smoking: " + this.prefered_smoking +
                " Date of birth: " + this.date_of_birth +
                " Age: " + this.age +
                " loc_country: " + this.loc_country +
                " loc_name: " + this.loc_name +
                " loc_lat: " + this.loc_lat +
                " loc_lng: " + this.loc_lng + 
                " last_connected: " + this.last_connected;
    }
    
}