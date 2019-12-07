package helperPackage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * A collection of function that are not directly related to the functionality of a specific bean, meaning that it is part of some functions in beans, but to make it a method is not contextually right.
 * @author romand
 * @version 1.0
 */
public class HelperFunctions {

    /**
     * A function that converts a string into it's md5 representation
     * @param plain a plain string
     * @return an encoded string
     */
    public static String md5(String plain){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plain.getBytes());
            byte byteData[] = md.digest();
            StringBuffer md5Pass = new StringBuffer();
            for (int i = 0; i < byteData.length; i++)
                md5Pass.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            return md5Pass.toString();
        }
        catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException: " + "I'm sorry, but MD5 is not a valid message digest algorithm" + " HelperFunction.md5");
            return "";
        }
    }
    
    /**
     * Splits a comma delimitered string with spaces into an array
     * @param stringOfData comma delimitered string with spaces
     * @return array of strings
     */
    public static String[] simpleSplitStringToArray(String stringOfData){
        if((stringOfData != null) && (stringOfData.length() >= 3)){
            String[] res = stringOfData.split(",");
            for(int i=0; i<res.length; i++){
                res[i] = res[i].replaceAll("'", "");
            }
            return res;
        }
        String[] defaultString = {stringOfData};
        return defaultString;
    }
    
    /**
     * Splits comma delimitered string into an array
     * @param stringOfData comma delimitered string
     * @return array of strings
     */
    public static String[] javaSplitStringToArray(String stringOfData){
        if((stringOfData != null) && (stringOfData.length() >= 3)){
            String[] res = stringOfData.split(",");
            return res;
        }
        String[] defaultString = {stringOfData};
        return defaultString;
    }
    
    /**
     * Join Array of Strings into a comma separated string
     * @param arr the string array
     * @param derbySQLQuotationStyle Set true if the final string has to be compliant with the DerbySQL quotation style (double single quote)
     * @return
     */
    public static String simpleJoinArrayOfStrings(String[] arr, boolean derbySQLQuotationStyle){
        if (arr == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String n : arr) { 
            if (sb.length() > 0){
                 sb.append(',');
            }
            if(derbySQLQuotationStyle){
                sb.append("''").append(n).append("''");
            }
            else{
                sb.append("'").append(n).append("'");
            }
            
        }
        return sb.toString();
    }

    /**
     * Calculate age from Date
     * @param dateToCalculate the date to calculate from
     * @return an int age
     */
    public static int getAgeFromDate(Date dateToCalculate){
        LocalDate lcDateToCalculate = dateToCalculate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return getAgeFromLocalDate(lcDateToCalculate);
    }

    /**
     * Calculate age from java.sql.Date
     * @param dateToCalculate the date to calculate from
     * @return an int age
     */
    public static int getAgeFromSQLDate(java.sql.Date dateToCalculate){
        LocalDate lcDateToCalculate = dateToCalculate.toLocalDate();
        return getAgeFromLocalDate(lcDateToCalculate);
    }
    private static int getAgeFromLocalDate(LocalDate lcDateToCalculate){
        LocalDate lcNow = LocalDate.now();
        return Period.between(lcDateToCalculate, lcNow).getYears();
    }

    /**
     * Removes unnecessary info from "viewid" string
     * @param str view id string
     * @return string with the file name of the view without .xhtml etc.
     */
    public static String sanitizeViewId(String str){
        if ( (str != null) && (str.length() > 7) && (str.substring(str.length() - 6).equals(".xhtml")) ) {
            str = str.substring(0, str.length() - 6);
            str = str.substring(1);    
        }
        return str;
    }
    
    /**
     * generate random file name for images
     * @param uid user id
     * @return string formated "bd_"+user id+unique id
     */
    public static String generateUniqueUpload(long uid){
        String uniqueID = UUID.randomUUID().toString();
        return "bd_" + uid + "_" + uniqueID;
    }
    
    /**
    * Calculate distance between two points in latitude and longitude taking
    * into account height difference. If you are not interested in height
    * difference pass 0.0. Uses Haversine method as its base.
    * 
    * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
    * el2 End altitude in meters
    * @param lat1 latitude of first location
    * @param el1 elevation of the first location
    * @param lat2 latitude of the second location
    * @param lon2 longitude of the second location
    * @param el2 elevation of the second location
    * @param lon1 longitude of the first location
    * @return Distance in Meters
    */
    public static double calculateDistance(double lat1, double lat2, double lon1,
        double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }
    
    /**
     * Truncate string by length if longer that length
     * @param value the string to truncate
     * @param length the length to truncate
     * @return truncated string or full string depends on the given params
     */
    public static String truncate(String value, int length)
    {
      if (value != null && value.length() > length)
        value = value.substring(0, length);
      return value;
    }
    /**
     * Sanitize String for SQL input
     * @param stringToSanitize String to Sanitize
     * @return Sanitized String
     */    
    public static String sanitizeString(String stringToSanitize){
        return stringToSanitize.replaceAll("'", "");
    }
    
}


/*
CREATE TABLE "MESSAGES"
(    
   "ID" INT not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),   
   "MESSAGE_TO" VARCHAR(10) NOT NULL DEFAULT '',     
   "MESSAGE_FROM" VARCHAR(10) NOT NULL DEFAULT '',     
   "MESSAGE_ACTUAL" VARCHAR(500) NOT NULL DEFAULT '',     
   "MESSAGE_TIMESTAMP" TIMESTAMP,
   "TO_NEW" BOOLEAN
);


CREATE TABLE "MATCHES"
(    
   "ID" INT not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),   
   "LIKE_ACTIVE" BIGINT,     
   "LIKE_OTHER" BIGINT     
);

CREATE TABLE "USERPROFILES"
(    
   "ID" INT not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),   
   "USERID" BIGINT,     
   "NAME" VARCHAR(40) NOT NULL DEFAULT '',
   "DATE_OF_BIRTH" DATE NOT NULL,
   "GENDER" VARCHAR(20) NOT NULL DEFAULT '',
   "LOC_LNG" DOUBLE,
   "LOC_LAT" DOUBLE,
   "LOC_NAME" VARCHAR(50) NOT NULL DEFAULT '',
   "LOC_COUNTRY" VARCHAR(50) NOT NULL DEFAULT '',
   "SMOKING" VARCHAR(20) NOT NULL DEFAULT '',
   "PREFERRED_MIN_AGE" INTEGER,
   "PREFERRED_MAX_AGE" INTEGER,
   "PREFERRED_MAX_RADIUS" INTEGER,    
   "PREFERRED_SMOKING" VARCHAR(100) NOT NULL DEFAULT '',
   "PREFERRED_GENDER" VARCHAR(100) NOT NULL DEFAULT '',
   "PROFILE_IMAGE" VARCHAR(500) NOT NULL DEFAULT '',
   "DESCRIPTION" VARCHAR(2000) NOT NULL DEFAULT '',
   "LAST_TIMESTAMP" TIMESTAMP,
   "REPORTS" BIGINT,
   "REPORTEDBY" VARCHAR(2000) NOT NULL DEFAULT ''
);



CREATE TABLE "USERS"
(    
   "USERID" INT not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),   
   "PASSWORD" VARCHAR(32),     
   "EMAIL" VARCHAR(50),
   "PHONE" VARCHAR(20),
   "IS_ADMIN" BOOLEAN
);
*/