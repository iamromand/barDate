package userManagment;

import Models.GeneralDBAccess;
import Models.ProfileDB;
import Models.UsersDB;
import static helperPackage.HelperFunctions.generateUniqueUpload;
import static helperPackage.HelperFunctions.getAgeFromDate;
import static helperPackage.HelperFunctions.sanitizeViewId;
import static helperPackage.HelperFunctions.simpleJoinArrayOfStrings;
import static helperPackage.HelperFunctions.simpleSplitStringToArray;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.http.Part;
import javax.sql.DataSource;

@DataSourceDefinition(
        name = GeneralDBAccess.DB_NAME,
        className = GeneralDBAccess.DB_CLASS_NAME,
        url = GeneralDBAccess.DB_URL,
        databaseName = GeneralDBAccess.DB_DATABASE_NAME,
        user = GeneralDBAccess.DB_USER,
        password = GeneralDBAccess.DB_PASSWORD
)

@Named("profileHandlerBean")
@SessionScoped

/**
 * This bean manages the communication between user actions and the profile DB
 * @author romand
 * @version 1.0
 */
public class ProfileHandlerBean implements Serializable{
    
    //connection the the DB
    ProfileDB pdb;
    UsersDB udb;
    
    //session to hold id of the connected user
    @Inject private ConnectionBean cbean;
    
    //add datasource resource
    @Resource(lookup=GeneralDBAccess.DB_NAME) DataSource dataSource;

    //local variable to hold the user id
    private long uid = -1;
    
    //profile variables for a user
    private String name;
    private String dateofbirth;
    private String gender;
    private String locationLng;
    private String locationLat;
    private String locationName;
    private String locationCountry;
    private String smoking;
    private String description;
    private String profileImage;
    
    //p in the beginning of the variable is for "preferred"
    private String pminage;
    private String pmaxage;
    private String pmaxradius;
    //holds an array of preferred genders
    private String[] pgender;
    private String psmoking;
    
    //a list to populate the select box in front using a loop
    private final Map<Integer, Integer> listOfAges;
    //a list to populate the gernder list in front using a loop
    private final Map<String, String> listOfGenders;
    
    /*halding profile images require a delicate approach:
    (1) an local folder to store the images
    (2) the eventual url from where the files will be accessed
    (3) a default image if no image set
    (4) bean variable to get the image stream
    */
    private final static String UPLOADS_PATH = System.getProperty("user.home") + File.separator + "BarDate" + File.separator + "uploads" + File.separator;
    private final static String UPLOADS_URL = "uploads/";
    private final static String DEFAULT_IMAGE = UPLOADS_URL + "2019/default.jpg";
    private Part imageFile;
    
    /**
     * Getter method to return local user id stored in the bean
     * @return user id
     */
    public long getUid() {
        if(this.uid == -1){
            this.uid = cbean.getUid();
        }
        return this.uid;
    }
    /**
     * Setter method to set local user id stored in the bean
     * @param uid the user id of the current user
     */
    public void setUid(long uid) {    this.uid = uid; }
    /**
     * Getter method to return name stored in the bean
     * @return name
     */
    public String getName() {    return name; }
    /**
     * Setter method to set name stored in the bean
     * @param name the name of the current user
     */
    public void setName(String name) {    this.name = name; }
    /**
     * Getter method to return location name stored in the bean
     * @return location name
     */
    public String getLocationName() {    return locationName; }
    /**
     * Setter method to set location name stored in the bean
     * @param locationName the location name for the current user
     */
    public void setLocationName(String locationName) {    this.locationName = locationName; }
    /**
     * Getter method to return location country stored in the bean
     * @return location country
     */
    public String getLocationCountry() {    return locationCountry; }
    /**
     * Setter method to set location country stored in the bean
     * @param locationCountry the location country for the current user
     */
    public void setLocationCountry(String locationCountry) {    this.locationCountry = locationCountry; }
    /**
     * Getter method to return location longitude stored in the bean
     * @return location longitude
     */
    public String getLocationLng() {    return locationLng; }
    /**
     * Setter method to set location longitude stored in the bean
     * @param locationLng the location longitude for the current user
     */
    public void setLocationLng(String locationLng) {    this.locationLng = locationLng; }
    /**
     * Getter method to return location latitude stored in the bean
     * @return location latitude
     */
    public String getLocationLat() {    return locationLat; }
    /**
     * Setter method to set location latitude stored in the bean
     * @param locationLat the location latitude for the current user
     */
    public void setLocationLat(String locationLat) {    this.locationLat = locationLat; }
    /**
     * Getter method to return date of birth stored in the bean
     * @return date of birth
     */
    public String getDateofbirth() {    return dateofbirth; }
    /**
     * Setter method to set date of birth stored in the bean
     * @param dateofbirth the date of birth of the current user
     */
    public void setDateofbirth(String dateofbirth) {    this.dateofbirth = dateofbirth; }
    /**
     * Getter method to return smoking status stored in the bean
     * @return smoking status
     */
    public String getSmoking() {    return smoking; }
    /**
     * Setter method to set smoking status stored in the bean
     * @param smoking the smoking status of the current user
     */
    public void setSmoking(String smoking) {    this.smoking = smoking; }
    /**
     * Getter method to return gender stored in the bean
     * @return gender
     */
    public String getGender() { return gender;  }
    /**
     * Setter method to set gender stored in the bean
     * @param gender the gender of the current user
     */
    public void setGender(String gender) {  this.gender = gender;   }
    /**
     * Getter method to return description stored in the bean
     * @return description
     */
    public String getDescription() {    return description; }
    /**
     * Setter method to set description stored in the bean
     * @param description the description of the current user
     */
    public void setDescription(String description) {    this.description = description; }
    
    /**
     * Getter method to return preferred minimum age stored in the bean
     * @return preferred minimum age
     */
    public String getPminage() {    return pminage; }
    /**
     * Setter method to set preferred minimum age stored in the bean
     * @param pminage the preferred minimum age for the current user
     */
    public void setPminage(String pminage) {    this.pminage = pminage; }
    /**
     * Getter method to return preferred maximum age stored in the bean
     * @return preferred maximum age
     */
    public String getPmaxage() {    return pmaxage; }
    /**
     * Setter method to set preferred maximum age stored in the bean
     * @param pmaxage the preferred maximum age for the current user
     */
    public void setPmaxage(String pmaxage) {    this.pmaxage = pmaxage; }
    /**
     * Getter method to return preferred maximum radius willing to travel stored in the bean
     * @return preferred maximum radius willing to travel
     */
    public String getPmaxradius() { return pmaxradius;  }
    /**
     * Setter method to set preferred maximum radius stored in the bean
     * @param pmaxradius the preferred maximum radius for the current user
     */
    public void setPmaxradius(String pmaxradius) {  this.pmaxradius = pmaxradius;   }
    /**
     * Getter method to return preferred genders stored in the bean
     * @return an array of gender strings
     */
    public String[] getPgender() {    return pgender; }
    /**
     * Setter method to set preferred genders stored in the bean
     * @param pgender the preferred genders for the current user
     */
    public void setPgender(String[] pgender) {    this.pgender = pgender; }
    /**
     * Getter method to return preferred smoking status stored in the bean
     * @return preferred smoking status
     */
    public String getPsmoking() {   return psmoking;    }
    /**
     * Setter method to set preferred smoking status stored in the bean
     * @param psmoking the preferred smoking status for the current user
     */
    public void setPsmoking(String psmoking) {  this.psmoking = psmoking;   }
    /**
     * Getter method to return a list of ages to populate the view
     * @return list of ages
     */
    public Map<Integer, Integer> getListOfAges(){return this.listOfAges;}
    /**
     * Getter method to return a list of genders to populate the view
     * @return preferred minimum age
     */
    public Map<String, String> getListOfGenders(){return this.listOfGenders;}
    
    /**
     * Getter method to return the profile image url
     * @return profile image url
     */
    public String getProfileImage() {   return profileImage;    }
    /**
     * Setter method to set the profile image url stored in the bean
     * @param profileImage the profile image url for the current user
     */
    public void setProfileImage(String profileImage) {  this.profileImage = profileImage;   }
    /**
     * Getter method to return default image url
     * @return default image url
     */
    public static String getDefaultImage() {  return DEFAULT_IMAGE;   }
    /**
     * Getter method to handle image stream from the view
     * @return Part type stream of the image
     */
    public Part getImageFile() {    return imageFile;   }
    /**
     * Setter method to handle image stream from the view
     * @param imageFile Part type representation of the image
     */
    public void setImageFile(Part imageFile) {  this.imageFile = imageFile; }
    
    /**
     * Create a new instance of ProfileHandlerBean.
     * (1) initialize DB connection.
     * (2) initialize view components (list of ages and list of genders)
     */
    public ProfileHandlerBean() {
        pdb = new ProfileDB();
        udb = new UsersDB();
        this.listOfAges = new HashMap<>();
        this.listOfGenders = new LinkedHashMap<>();
        for(int i=18; i<100; i++){  listOfAges.put(i,i);    }
        String gendersLabels[] = {"Male", "Female", "Other"};
        for(String genderLabel : gendersLabels){
            listOfGenders.put(genderLabel.toLowerCase().replaceAll("\\s+",""), genderLabel);
        }
    }

    /**
     * Populate all the field from DB
     */
    public void populateFields(){
        if(this.getUid() != -1){
            Map<String, String> res = pdb.getAllFields(dataSource, this.getUid());
            //System.out.println(new PrettyPrintingMap<String, String>(res));
            if(res != null){
                setName(res.get("name"));
                String dateOfBirth = res.get("dateOfBirth");
                try{ 
                    //translate date format between display version and sql accepted version
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateValue = input.parse(dateOfBirth);
                    SimpleDateFormat output = new SimpleDateFormat("dd.MM.yyyy");
                    dateOfBirth = output.format(dateValue);

                }
                catch(ParseException pe){
                    System.err.println("ParseExeption: " + pe.getMessage() + " populateFields");
                }
                catch(NullPointerException npe){
                    System.err.println("NullPointerException: " + npe.getMessage() + " populateFields");
                }
                setDateofbirth(dateOfBirth);
                setGender(res.get("gender"));
                setLocationName(res.get("locationName"));
                setLocationCountry(res.get("locationCountry"));
                setLocationLat(res.get("locationLat"));
                setLocationLng(res.get("locationLng"));
                setSmoking(res.get("smoking"));
                setPgender(simpleSplitStringToArray(res.get("pGender")));
                setPsmoking(res.get("pSmoking"));
                setPmaxradius(res.get("pMaxRadius"));
                setPminage(res.get("pMinAge"));
                setPmaxage(res.get("pMaxAge"));
                String profileImage = res.get("profileImage");
                if((profileImage != null) && (profileImage != "")){
                    setProfileImage(res.get("profileImage"));
                }
                else{
                    setProfileImage(DEFAULT_IMAGE);
                }
                setDescription(res.get("description"));
                
            }
        }
        
    }

    /**
     * On profile change re-save all fields
     * @return in some cases the return is required to get back to the dashboard
     */
    public String updateProfile(){
        Map<String, String> toSave = new HashMap<>();
        toSave.put("uid", ""+getUid());
        toSave.put("name", getName());
        toSave.put("dateOfBirth", getDateofbirth());
        toSave.put("gender", getGender());
        toSave.put("locationLng", getLocationLng());
        toSave.put("locationLat", getLocationLat());
        toSave.put("locationName", getLocationName());
        toSave.put("locationCountry", getLocationCountry());
        toSave.put("smoking", getSmoking());
        toSave.put("pMinAge", getPminage());
        toSave.put("pMaxAge", getPmaxage());
        toSave.put("pMaxRadius", getPmaxradius());
        toSave.put("pSmoking", getPsmoking());
        toSave.put("pGender", simpleJoinArrayOfStrings(getPgender(), true));
        toSave.put("description", getDescription());
        if(!getProfileImage().equals(DEFAULT_IMAGE)){
            toSave.put("profileImage", getProfileImage());
        }
        if(pdb.saveProfileInformation(dataSource, toSave) != false){
            return "dashboard";
        }
        return "";
    }

    /**
     * A method to redirect user that are connected to their personal area, not letting regular users see admin page, not letting not connected users see dashboard area.
     * @throws IOException
     */
    public void redirectFilter() throws IOException{
        //String version = FacesContext.class.getPackage().getImplementationVersion();
        this.populateFields();
        FacesContext ctx = FacesContext.getCurrentInstance();
        String viewId = ctx.getViewRoot().getViewId();
        viewId = sanitizeViewId(viewId);
        switch(viewId){
            case "adminArea":
                if((!cbean.isConnected()) || (!this.isAdmin())){
                    this.redirectTo("index");
                }
                break;
            case "index":
            case "login":
            case "register":
            case "sessionExpired":
                if(cbean.isConnected()){
                    this.redirectTo("dashboard");
                }
                break;
            case "yourInfo":
            case "preferredInfo":
            default:
                if(cbean.isConnected()){
                    long uid = this.getUid();
                    int pStage = pdb.getProfileStage(dataSource, uid);
                    //pStage is the stage in which a registered user is - had he conleted the whole profile, only his user info or non at all and he just registered.
                    if ((!viewId.equals("yourInfo")) && (pStage == pdb.PROFILE_REQUIRE_INFO)){
                        this.redirectTo("yourInfo");
                    }
                    if ((!viewId.equals("preferredInfo")) && (pStage == pdb.PROFILE_REQUIRE_PREFERRED)){
                        this.redirectTo("preferredInfo");
                    }
                    else{
                        pdb.updateOnlineStatus(dataSource, uid);
                        if((viewId.equals("yourInfo") || viewId.equals("preferredInfo")) && (pStage == pdb.PROFILE_GREEN_LIGHT)){
                            this.redirectTo("dashboard");
                        }
                    }
                }
                else{
                    this.redirectTo("index");
                }
        }
    }
    
    /**
     * Method to check if current user is admin
     * @return true if admin, false otherwise
     */
    public boolean isAdmin(){
        return udb.isAdmin(dataSource, this.getUid());
    }
    //a method to redirect user to specified view
    // @param location the string of the name of the file to be redirected to
    private void redirectTo(String location) throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/"+location+".xhtml");
    }
    
    /**
     * A method to validate that the ages are consistent and make sense
     * @param event a default variable that comes with the function to get the components that we need to test
     */
    public void validateAges(ComponentSystemEvent event) {
            FacesContext fc = FacesContext.getCurrentInstance();
            UIComponent components = event.getComponent();
            UIInput uiInputMinAge = (UIInput) components.findComponent("pMinAgeInputText");
            UIInput uiInputMaxAge = (UIInput) components.findComponent("pMaxAgeInputText");
            String minage = uiInputMinAge.getLocalValue() == null ? "" : uiInputMinAge.getLocalValue().toString();
            String maxage = uiInputMaxAge.getLocalValue() == null ? "" : uiInputMaxAge.getLocalValue().toString();
            String minageId = uiInputMinAge.getClientId();
            String maxageId = uiInputMaxAge.getClientId();

            if (minage.isEmpty()){
                FacesMessage msg = new FacesMessage("Please specify minimum preferred age");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fc.addMessage(minageId, msg);
                fc.renderResponse();
            }
            else if (maxage.isEmpty()){
                FacesMessage msg = new FacesMessage("Please specify maximum preferred age");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fc.addMessage(maxageId, msg);
                fc.renderResponse();
            }
            else if(parseInt(minage) > parseInt(maxage)){
                FacesMessage msg = new FacesMessage("Maximum age cannot be lower than minimum age");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fc.addMessage(maxageId, msg);
                fc.renderResponse();
            }
        }
        
    /**
     * A method to validate that date of birth is in the required format and is between 18 and 99.
     * @param context the context into where to input the error if needed
     * @param comp the component that is being validated
     * @param value the value of the field to be validated
     */
    public void validateDateOfBirth(FacesContext context, UIComponent comp, Object value){
            Date dob = null;
            int errorType = 0;
            try{
                String dateToValidate = (String) value;
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                formatter.setLenient(false);
                dob = formatter.parse(dateToValidate);
            }
            catch (ParseException ex) {
                errorType = 1;
            }
            if(errorType == 0){
                int age = getAgeFromDate(dob);
                if(age < 18){
                    errorType = 2;
                }
                else if(age > 99){
                    errorType = 3;
                }
            }
            if(errorType != 0){
                String errorMessage;
                switch(errorType){
                    case 1:
                        errorMessage = "Doesn't seem like a correct date";
                        break;
                    case 2:
                        errorMessage = "We are sorry - the service available only to 18+ years old.";
                        break;
                    case 3:
                        errorMessage = "Doesn't seem like a correct date";
                        break;
                    default:
                        errorMessage = "Doesn't seem like a correct date";
                        break;
                }
                FacesMessage message = new FacesMessage(errorMessage);
                context.addMessage(comp.getClientId(context), message);
                throw new ValidatorException(message);
            }
        }
        
    /**
     * If the user is passed the registration stages and is already in the system, they are encouraged to add a profile image. This sets the body class to noImage for front handling.
     * @return body class string with relevant info
     */
    public String getLoggedInButNoProfilePic(){
            if(cbean.isConnected()){
                if((this.getProfileImage() == null) || (this.getProfileImage().equals("")) || (this.getProfileImage().equals(this.DEFAULT_IMAGE))){
                    long uid = this.getUid();
                    int pStage = pdb.getProfileStage(dataSource, uid);
                    if (pStage == pdb.PROFILE_GREEN_LIGHT){
                        return "noImage";
                    }
                }
            }
            return "";
        }
        
    /**
     * Method to save an uploaded image
     */
    public void saveImage() {
            
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            int year = cal.get(Calendar.YEAR);
            //year holdes the current year so we can split the images into yearly folders and not have it all in one folder.
            String uploadFolderPath = UPLOADS_PATH + year;
            String uploadDir = UPLOADS_URL + year + "/";
            new File(uploadFolderPath).mkdirs();
            
            try (InputStream input = imageFile.getInputStream()) {
                //test file is indeed image
                if(imageFile.getContentType().equals("image/jpeg") || imageFile.getContentType().equals("image/png")){
                    String fileExtention = "jpg";
                    if(imageFile.getContentType().equals("image/png")){
                        fileExtention = "png";
                    }
                    //generate unique so not to overwrite previous uploads
                    String fullName = generateUniqueUpload(this.getUid())+"."+fileExtention;
                    String uploadUrl = uploadDir + fullName;
                    //put the file into the folder
                    Files.copy(input, new File(uploadFolderPath, fullName).toPath());
                    //set profile image in the bean
                    setProfileImage(uploadUrl);
                    //update DB with iamge information
                    updateProfile();
                    FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard.xhtml");
                    FacesContext.getCurrentInstance().responseComplete();
                }
            }
            catch (IOException e) {
                System.err.println("IOException: " + e.getMessage() + " saveImage");
            }
        }

}
