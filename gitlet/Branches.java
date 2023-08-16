package gitlet;
import java.io.Serializable;
import java.util.ArrayList;

/** The branch class.
 *  @author Peter Chen
 */

public class Branches implements Serializable {
    /** Creates a new branch. */
    public Branches() {
        b = new ArrayList<>();
    }

    /** Add to the branch.
     * @param a is branch name*/
    public void addbrranch(String a) {
        b.add(a);
    }

    public ArrayList<String> getB() {
        return b;
    }

    /** Remove the branch.
     * @param a is branch name*/
    public void rmbranch(String a) {
        b.remove(a);
    }

    /** An arraylist holding all branches. */
    private ArrayList<String> b;
}
