package helperPackage;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A helper class to print a map in a user friendly way
 * @author romand
 */
public class PrettyPrintingMap<K, V> {
    private Map<K, V> map;

    /**
     *
     * @param map
     */
    public PrettyPrintingMap(Map<K, V> map) {
        this.map = map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<K, V> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();

    }
}