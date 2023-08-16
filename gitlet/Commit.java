package gitlet;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

/** The commit class.
 *  @author Peter Chen
 */

public class Commit implements Serializable {
    /** Creates a new commit object.
     * @param givenmessage is the commit message.
     * @param parentc is the parent commit*/
    public Commit(String givenmessage, String parentc) {
        message = givenmessage;
        parent = new String[2];
        if (parentc == null) {
            parent = null;
        } else if (parentc.contains(",")) {
            parent[0] = parentc.split(",")[0];
            parent[1] = parentc.split(",")[1];
        } else {
            parent[0] = parentc;
            parent[1] = null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy",
                new Locale("en", "US"));
        Date now = new Date();
        String strDate = sdf.format(now) + " -0800";
        timestamp = strDate;
        if (parentc != null) {
            Commit pare = Utils.readObject
                    (new File(".gitlet/commit/" + parent[0]), Commit.class);
            blobs = pare.getBlobs();
        }
    }

    /** Get the parent(s).
     * @return that shall not be named. */
    public String[] getParent() {
        return parent;
    }

    /** Get the message.
     * @return that shall not be named.*/
    public String getMessage() {
        return message;
    }

    /** Get all the files tracked.
     * @return that shall not be named.*/
    public ArrayList<String> getFiles() {
        ArrayList<String> files = new ArrayList<>();
        for (Map.Entry<String, String> entry : blobs.entrySet()) {
            String fileName = entry.getKey();
            files.add(fileName);
        }
        return files;
    }

    /** Check if a file is tracked here.
     * @param s is the file name.
     * @return that shall not be named.*/
    public boolean containsFile(String s) {
        if (blobs != null) {
            return blobs.containsKey(s);
        }
        return false;
    }

    /** Create the initial commit.
     * @return that shall not be named.*/
    public static Commit init() {
        Commit initial = new Commit("initial commit", null);
        initial.timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        return initial;
    }

    /** Untrack a file.
     * @param s is file name. */
    public void removeF(String s) {
        if (blobs == null) {
            return;
        }
        if (blobs.containsKey(s)) {
            blobs.remove(s);
        }
    }

    /** Get the content stored of a file.
     * @param s is the file name.
     * @return that shall not be named.*/
    public String getContent(String s) {
        if (blobs == null) {
            return null;
        }
        if (blobs.containsKey(s)) {
            File f = new File(".gitlet/blob/" + blobs.get(s));
            Blob b = Utils.readObject(f, Blob.class);
            return b.getContent();
        }
        return "";
    }

    /** Check if two commits are the same commit.
     * @param c is commit.
     * @return that shall not be named.*/
    public boolean same(Commit c) {
        if (c == null) {
            return false;
        }
        if (Utils.sha1(Utils.serialize(this)).
                equals(Utils.sha1(Utils.serialize(c)))) {
            return true;
        }
        return false;
    }

    /** Add and track a file.
     * @param f is file name. */
    public void addFile(String f) {
        Blob b;
        File ff;
        ff = new File(f);
        b = new Blob(ff);
        blobs.put(f, b.getName());
    }

    /** Get the timestamp.
     * @return that shall not be named.*/
    public String getTimestamp() {
        return timestamp;
    }

    /** Get the blobs.
     * @return that shall not be named.*/
    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    /** The time the commit was made. */
    private String timestamp;

    /** The blob of the commit. */
    private TreeMap<String, String> blobs = new TreeMap<>();

    /** The message of the commit. */
    private String message;

    /** The parent(s) of the commit. */
    private String[] parent;
}
