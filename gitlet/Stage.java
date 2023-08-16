package gitlet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/** The stage class.
 *  @author Peter Chen
 */

public class Stage implements Serializable {

    /** Creates a new stage. */
    public Stage() {
        ss = new TreeMap<>();
    }

    /** Returns all the file inside  the stage. */
    public ArrayList<String> getFiles() {
        ArrayList<String> files = new ArrayList<>();
        for (Map.Entry<String, String> entry : ss.entrySet()) {
            String fileName = entry.getKey();
            files.add(fileName);
        }
        return files;
    }

    /** Get the treemap.
     * @return that shall not be named.*/
    public TreeMap<String, String> getss() {
        return ss;
    }

    /** Clear the stage. */
    public void clearmap() {
        ss.clear();
    }

    /** Add to the map.
     * @param key is key.
     * @param value is value.*/
    public void addmap(String key, String value) {
        ss.put(key, value);
    }

    /** Remove from the map.
     * @param key is key.*/
    public void rmap(String key) {
        ss.remove(key);
    }

    /** A tree map storing files and information. */
    private TreeMap<String, String> ss;
}
