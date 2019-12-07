package SystemObjects;

import java.sql.Timestamp;

/**
 * An object representation of a message
 * @author romand
 * @version 1.0
 */
public class Message {
    private int from;
    private int to;
    private String message;
    private Timestamp timestamp;
    private String side;

    /**
     * Getter to get from who the message
     * @return from who the message
     */
    public int getFrom() {  return from;    }

    /**
     * Setter to set from who the message
     * @param from from who the message
     */
    public void setFrom(int from) { this.from = from;   }

    /**
     * Getter to get to who the message
     * @return to who the message
     */
    public int getTo() {    return to;  }

    /**
     * Setter to set to who the message
     * @param to to who the message
     */
    public void setTo(int to) { this.to = to;   }

    /**
     * Getter to get the actual message
     * @return the actual message
     */
    public String getMessage() {    return message; }

    /**
     * Setter to set the actual message
     * @param message the actual message
     */
    public void setMessage(String message) {    this.message = message; }

    /**
     * Getter to get the timestamp of the message
     * @return timestamp of the message
     */
    public Timestamp getTimestamp() {  return timestamp;   }

    /**
     * Setter to set the timestamp of the message
     * @param timestamp timestamp of the message
     */
    public void setTimestamp(Timestamp timestamp) {    this.timestamp = timestamp; }

    /**
     * Getter for the direction of the message. When creating a message we are able to set whether the message is outgoing or incoming to a specific user.
     * @return the direction of the message
     */
    public String getSide() {   return side;    }

    /**
     * Setter for the direction of the message. When creating a message we are able to set whether the message is outgoing or incoming to a specific user.
     * @param side the direction of the message
     */
    public void setSide(String side) {  this.side = side;   }

    /**
     * Create a new message instance with default timetamp (now)
     * @param from from who the message
     * @param to to who the message
     * @param message the actual message
     * @param side the direction of the message
     */
    public Message(int from, int to, String message, String side) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.side = side;
    }

    /**
     * Create a new message instance passing timestamp
     * @param from from who the message
     * @param to to who the message
     * @param message the actual message
     * @param timestamp the timestamp of the message
     * @param side the direction of the message
     */
    public Message(int from, int to, String message, Timestamp timestamp, String side) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
        this.side = side;
    }
    
    /**
     * Method to calculate when the message was creates
     * @return a string representation of the age of the message
     */
    public String ago(){
        Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
        long diff = timestampNow.getTime() - getTimestamp().getTime();
        long diffInSeconds = diff / 1000;
        long diffInMinutes = diffInSeconds / 60;
        long diffInHours = diffInMinutes / 60;
        long diffInDays = diffInHours / 60;
        long diffInWeeks = diffInDays / 7;
        long diffInMonths = diffInDays / 30;
        long diffInYears = diffInDays /365;
        if(diffInSeconds < 60){
            return "<1 min";
        }
        else if(diffInMinutes < 60){
            return diffInMinutes+" min";
        }
        else if (diffInHours < 24){
            return diffInHours+" hrs";
        }
        else if (diffInDays < 7){
            return diffInWeeks+" wks";
        }
        else if (diffInDays < 30){
            return diffInMonths+" mnt";
        }
        else{
            return diffInYears+" yrs";
        }
    }
    
}
