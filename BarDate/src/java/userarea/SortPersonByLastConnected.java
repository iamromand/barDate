package userarea;

import SystemObjects.Person;
import java.util.Comparator;

/**
 * A comparator class to compare two people based on their timestamps of last connected
 * @author romand
 * @version 1.0
 */
public class SortPersonByLastConnected implements Comparator<Person> {
    @Override
    public int compare(Person a, Person b)
    { 
        if(a.getLast_connected() == null){
            return -1;
        }
        else if(b.getLast_connected() == null){
            return 1;
        }
        else{
            return (int)(b.getLast_connected().getTime() - a.getLast_connected().getTime());
        }
    }
}