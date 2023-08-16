package gitlet;
import java.io.File;
import java.io.Serializable;

/** The blob class.
 *  @author Peter Chen
 */

public class Blob implements Serializable {

    /** Create a new blob.
     * @param ff is filename. */
    public Blob(File ff) {
        int num = 0;
        String n = ff.getName();
        String filename = n.split("\\.")[0];
        String extension = n.split("\\.")[1];
        f = new File(".gitlet/" + filename + num + "." + extension);
        while (f.exists()) {
            num += 1;
            f = new File(".gitlet/" + filename + num + "." + extension);
        }
        Utils.writeContents(f, Utils.readContentsAsString(ff));
        content = Utils.readContentsAsString(f);
        name = Utils.sha1(Utils.serialize(this));
        File self = new File(".gitlet/blob/" + name);
        Utils.writeContents(self, Utils.serialize(this));
    }

    /** Get the content.
     * @return that shall not be named.*/
    public String getContent() {
        return content;
    }

    /** Get the name.
     * @return that shall not be named.*/
    public String getName() {
        return name;
    }

    /** The file of the blob. */
    private File f;

    /** The sha1 id of the blob. */
    private String name;

    /** The content of the file of the blob. */
    private String content;
}
