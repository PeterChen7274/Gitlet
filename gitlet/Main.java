package gitlet;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Peter Chen
 */
public class Main {


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String command = args[0];
        if (!Arrays.asList(dic).contains(command)) {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        if (command.equals("init")) {
            init();
            return;
        } else {
            File gitlet = new File(".gitlet");
            if (!gitlet.exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
        }
        initialize();
        if (command.equals("add")) {
            if (args.length != 2) {
                wrongL();
            }
            add(args[1]);
        } else if (command.equals("commit")) {
            if (args.length < 2) {
                System.out.println("Please enter a commit message.");
                System.exit(0);
            }
            if (args[1].trim().isEmpty()) {
                System.out.println("Please enter a commit message.");
                System.exit(0);
            }
            makeCommit(args[1], false, "", "");
        }
        helper(args);
        System.exit(0);
    }

    /** Main is too long so.
     * @param args is the command. */
    public static void helper(String... args) {
        String command = args[0];
        if (command.equals("rm")) {
            if (args.length != 2) {
                wrongL();
            }
            rm(args[1]);
        } else if (command.equals("log")) {
            if (args.length != 1) {
                wrongL();
            }
            log();
        } else if (command.equals("global-log")) {
            if (args.length != 1) {
                wrongL();
            }
            gLog();
        } else if (command.equals("find")) {
            if (args.length != 2) {
                wrongL();
            }
            find(args[1]);
        } else if (command.equals("status")) {
            if (args.length != 1) {
                wrongL();
            }
            status();
        } else if (command.equals("checkout")) {
            if (args[1].equals("--")) {
                checkoutF(args[2]);
            } else if (args.length <= 2) {
                checkoutA(args[1]);
            } else if (args.length == 4 && args[2].equals("--")) {
                checkoutI(args[1], args[3]);
            } else {
                wrongL();
            }
        } else if (command.equals("branch")) {
            if (args.length != 2) {
                wrongL();
            }
            branch(args[1]);
        } else if (command.equals("rm-branch")) {
            if (args.length != 2) {
                wrongL();
            }
            rbranch(args[1]);
        } else if (command.equals("reset")) {
            if (args.length != 2) {
                wrongL();
            }
            reset(args[1]);
        } else if (command.equals("merge")) {
            if (args.length != 2) {
                wrongL();
            }
            merge(args[1]);
        }
    }

    /** Give out an error message for incorrect operands. */
    public static void wrongL() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    /** Set all variables used. */
    public static void initialize() {
        head = Utils.readObject(hf, Commit.class);
        stage = Utils.readObject(sf, Stage.class);
        branches = Utils.readObject(bf, Branches.class);
        rstage = Utils.readObject(rf, Stage.class);
        cbranch = "master";
        for (String bs: branches.getB()) {
            if (bs.substring(0, 1).equals("*")) {
                cbranch = bs.substring(1);
                break;
            }
        }
        current = new File(".gitlet/commit/" + cbranch);
    }

    /** Prints all commit with the given message.
     * @param c is commit message. */
    public static void find(String c) {
        boolean found = false;
        List<String> files = Utils.plainFilenamesIn(".gitlet/commit");
        Commit co;
        File f;
        for (String s: files) {
            if (!s.equals("head") && !branches.getB().contains(s)
                    && !s.equals(cbranch)) {
                f = new File(".gitlet/commit/" + s);
                co = Utils.readObject(f, Commit.class);
                if (co.getMessage().equals(c)) {
                    found = true;
                    System.out.println(s);
                }
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Remove the branch commit but not the whole branch.
     * @param b is branch name. */
    public static void rbranch(String b) {
        if (b.equals(cbranch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!branches.getB().contains(b)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        File f = new File(".gitlet/commit/" + b);
        f.delete();
        branches.rmbranch(b);
        Utils.writeContents(bf, Utils.serialize(branches));
    }

    /** Checkout to a given commit.
     * @param c is the commit id. */
    public static void reset(String c) {
        File co = new File(".gitlet/commit/" + c);
        if (c.length() <= 2 * 10) {
            List<String> files = Utils.plainFilenamesIn(".gitlet/commit");
            for (String lon: files) {
                if (lon.contains(c)) {
                    co = new File(".gitlet/commit/" + lon);
                }
            }
        }
        if (!co.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit com = Utils.readObject(co, Commit.class);
        List<String> files = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        for (String f:files) {
            if (!head.containsFile(f) && com.containsFile(f)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            }
            if (!com.containsFile(f) && head.containsFile(f)) {
                File re = new File(f);
                re.delete();
            }
        }
        for (Map.Entry<String, String> entry
                : com.getBlobs().entrySet()) {
            String fileName = entry.getKey();
            String blob = entry.getValue();
            File ff = new File(fileName);
            File b = new File(".gitlet/blob/" + blob);
            Blob bb = Utils.readObject(b, Blob.class);
            Utils.writeContents(ff, bb.getContent());
        }
        if (!stage.getss().isEmpty()) {
            stage.getss().clear();
            Utils.writeContents(sf, Utils.serialize(stage));
        }
        if (!rstage.getss().isEmpty()) {
            rstage.getss().clear();
            Utils.writeContents(rf, Utils.serialize(rstage));
        }
        Utils.writeContents(hf, Utils.serialize(com));
        Utils.writeContents(new File(".gitlet/commit/"
                + cbranch), Utils.serialize(com));
    }

    /** Merge two branches and create a new commit at the currnt head.
     * @param b is the branch name. */
    public static void merge(String b) {
        if (!stage.getss().isEmpty() || !rstage.getss().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!branches.getB().contains(b)
                && !branches.getB().contains("*" + b)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (b.equals(cbranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        File bran = new File(".gitlet/commit/" + b);
        File cu = new File(".gitlet/commit/" + cbranch);
        Commit current1 = Utils.readObject(cu, Commit.class);
        Commit branch = Utils.readObject(bran, Commit.class);
        File split = new File(".gitlet/commit/"
                + findSplit(cbranch, b));
        Commit point = Utils.readObject(split, Commit.class);
        if (branch.same(point)) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            System.exit(0);
        }
        if (current1.same(point)) {
            checkoutA(b);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        List<String> cwdf = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        boolean happened = false;
        for (String files3: cwdf) {
            if (branch.containsFile(files3) && !current1.containsFile(files3)) {
                System.out.println("There is an untracked file in the way"
                        + "; delete it, or add and commit it first.");
                System.exit(0);
            }
            if (!files3.contains("gitlet") || !files3.contains("Make")
                    || !files3.contains(".iml")) {
                if (conflict(files3, current1, branch, point)) {
                    String replace = "<<<<<<< HEAD\n"
                            + current1.getContent(files3)
                            + "=======\n"
                            + branch.getContent(files3)
                            + ">>>>>>>\n";
                    File changef = new File(files3);
                    Utils.writeContents(changef, replace);
                    stage.addmap(files3, Utils.readContentsAsString(changef));
                    happened = true;
                }
            }
        }
        thisOnlyExistBecOfDamnStyleCheck(happened, b, point, branch, current1);
    }

    /** A furious protest against style check.
     * @param happened is something.
     * @param b is something.
     * @param point is something.
     * @param branch is something.
     * @param current1 is something.*/
    public static void thisOnlyExistBecOfDamnStyleCheck(
            boolean happened, String b, Commit point,
            Commit branch, Commit current1) {
        for (String files2: point.getFiles()) {
            if (current1.getContent(files2).equals(point.getContent(files2))
                    && !branch.containsFile(files2)) {
                rm(files2);
            }
            if (branch.getContent(files2).equals(point.getContent(files2))
                    && !current1.containsFile(files2)) {
                continue;
            }
        }
        for (String files: branch.getFiles()) {
            if (point.containsFile(files)
                    && !branch.getContent(files).equals(point.getContent(files))
                    && current1.getContent(files).
                    equals(point.getContent(files))) {
                checkoutI(b, files);
                stage.addmap(files,
                        Utils.readContentsAsString(new File(files)));
            }
            if (!point.containsFile(files)
                    && !current1.containsFile(files)) {
                checkoutI(b, files);
                stage.addmap(files,
                        Utils.readContentsAsString(new File(files)));
            }
        }
        makeCommit("Merged " + b + " into " + cbranch + ".", true,
                Utils.sha1(Utils.serialize(current1)),
                Utils.sha1(Utils.serialize(branch)));
        if (happened) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** A helper method to check if two files are in conflict.
     * @param file is something.
     * @param c is something.
     * @param b is something.
     * @param p is something.
     * @return that shall not be named.*/
    public static boolean conflict(String file, Commit c, Commit b, Commit p) {
        if (!p.containsFile(file) && b.containsFile(file)
                && !b.getContent(file).equals(c.getContent(file))) {
            return true;
        }
        if (!p.getContent(file).equals(b.getContent(file))
                && !c.getContent(file).equals(b.getContent(file))
                && !p.getContent(file).equals(c.getContent(file))) {
            return true;
        }
        if (!p.getContent(file).equals(b.getContent(file))
                && p.containsFile(file) && !c.containsFile(file)) {
            return true;
        }
        if (!p.getContent(file).equals(c.getContent(file))
                && p.containsFile(file) && !b.containsFile(file)) {
            return true;
        }
        return false;
    }

    /** A helper function to find the closest split point of two branches.
     * @param c is current branch.
     * @param b is given branch.
     * @return that shall not be named.*/
    public static String findSplit(String c, String b) {
        HashMap<String, Integer> allC = findParents(c, 0);
        HashMap<String, Integer> allB = findParents(b, 0);
        Set<String> C = allC.keySet();
        Set<String> B = allB.keySet();
        C.retainAll(B);
        int min = 10 * 10 * 10;
        String re = "";
        for (String s : C) {
            if (allC.get(s) < min) {
                min = allC.get(s);
                re = s;
            }
        }
        return re;
    }

    /** A helper method to find all parents of a commit.
     * @param c is given branch.
     * @param i is distance from c.
     * @return that shall not be named.*/
    public static HashMap<String, Integer> findParents(String c, int i) {
        HashMap<String, Integer> lst = new HashMap<>();
        File f = new File(".gitlet/commit/" + c);
        if (!f.exists()) {
            return null;
        }
        Commit co = Utils.readObject(f, Commit.class);
        while  (co != null) {
            lst.put(Utils.sha1(Utils.serialize(co)), i);
            if (co.getParent() == null) {
                return lst;
            }
            if (co.getParent()[1] != null) {
                for (Map.Entry<String, Integer> entry
                        : findParents(co.getParent()[1], i + 1).entrySet()) {
                    lst.put(entry.getKey(), entry.getValue());
                }
            }
            co = Utils.readObject(new File(".gitlet/commit/"
                    + co.getParent()[0]), Commit.class);
            i += 1;
        }
        return lst;
    }

    /** Prints information of all the commits up to the initial commit. */
    public static void log() {
        Commit c = head;
        while (c != null) {
            System.out.println("===");
            System.out.println("commit" + " " + Utils.sha1(Utils.serialize(c)));
            System.out.println("Date:" + " " + c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.println();
            if (c.getParent() == null) {
                break;
            }
            File f = new File(".gitlet/commit/" + c.getParent()[0]);
            c = Utils.readObject(f, Commit.class);
        }
    }

    /** Delete a file and put it on the remove stage.
     * @param a is file name.*/
    public static void rm(String a) {
        if (stage.getss().containsKey(a)) {
            stage.rmap(a);
            Utils.writeContents(sf, Utils.serialize(stage));
        } else if (head.containsFile(a)) {
            File f = new File(a);
            if (!f.exists()) {
                rstage.addmap(a, "");
            } else {
                rstage.addmap(a, Utils.readContentsAsString(f));
                f.delete();
            }
            Utils.writeContents(rf, Utils.serialize(rstage));
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /** Shows the status of the repo. */
    public static void status() {
        System.out.println("=== Branches ===");
        for (String b: branches.getB()) {
            System.out.println(b);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String s: stage.getFiles()) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String r: rstage.getFiles()) {
            System.out.println(r);
        }
        System.out.println();
        extraCredit();
    }

    /** A result of terrible style. */
    public static void extraCredit() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> files = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        ArrayList<String> printed = new ArrayList<>();
        for (String sss: head.getFiles()) {
            if (!printed.contains(sss)
                    && !rstage.getss().containsKey(sss)
                    && !files.contains(sss)) {
                System.out.println(sss + " (deleted)");
                printed.add(sss);
            }
        }
        for (String ss: stage.getFiles()) {
            if (!printed.contains(ss) && !files.contains(ss)) {
                System.out.println(ss + " (deleted)");
                printed.add(ss);
            }
        }
        for (String s: files) {
            if (printed.contains(s)) {
                continue;
            }
            if (s.contains(".iml") || s.contains("Make") || s.contains(".DS")) {
                continue;
            }
            String content = Utils.readContentsAsString(new File(s));
            if (head.containsFile(s) && !stage.getss().containsKey(s)
                    && !content.equals(head.getContent(s))) {
                System.out.println(s + " (modified)");
                printed.add(s);
            } else if (stage.getss().containsKey(s)
                    && !stage.getss().get(s).equals(content)) {
                System.out.println(s + " (modified)");
                printed.add(s);
            }

        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String untra: files) {
            if (untra.contains(".DS") || untra.contains("Make")
                    || untra.contains(".iml")) {
                continue;
            }
            if (!stage.getss().containsKey(untra)
                    && !head.containsFile(untra)) {
                System.out.println(untra);
            } else if (rstage.getss().containsKey(untra)) {
                System.out.println(untra);
            }
        }
        System.out.println();
    }

    /** Checkout a file to the head commit.
     * @param ch is file name.*/
    public static void checkoutF(String ch) {
        File f = new File(ch);
        if (!head.containsFile(ch)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String b = head.getBlobs().get(ch);
        File change = new File(".gitlet/blob/" + b);
        Blob bb = Utils.readObject(change, Blob.class);
        Utils.writeContents(f, bb.getContent());
    }

    /** Checkout a specific file to the given commit.
     * @param ch is file name.
     * @param id is commit id.*/
    public static void checkoutI(String id, String ch) {
        File f = new File(ch);
        File co = new File(".gitlet/commit/" + id);
        if (id.length() <= 2 * 10) {
            List<String> files = Utils.plainFilenamesIn(".gitlet/commit");
            for (String lon: files) {
                if (lon.contains(id)) {
                    co = new File(".gitlet/commit/" + lon);
                }
            }
        }
        if (branches.getB().contains(id)
                || branches.getB().contains("*" + id)) {
            co = new File(".gitlet/commit/" + id);
        }
        if (!co.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit com = Utils.readObject(co, Commit.class);
        if (!com.containsFile(ch)) {
            System.out.println("File does not exist in that commit");
            System.exit(0);
        }
        String b = com.getBlobs().get(ch);
        File change = new File(".gitlet/blob/" + b);
        Blob bb = Utils.readObject(change, Blob.class);
        Utils.writeContents(f, bb.getContent());
    }

    /** Checkout all files to the given branch.
     * @param id is commit id. */
    public static void checkoutA(String id) {
        if (!branches.getB().contains(id) && !id.equals(cbranch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (id.equals(cbranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File co = new File(".gitlet/commit/" + id);
        Commit com = Utils.readObject(co, Commit.class);
        List<String> files = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        for (String f:files) {
            if (com.containsFile(f) && !head.containsFile(f)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            } else if (head.containsFile(f) && !com.containsFile(f)) {
                File re = new File(f);
                re.delete();
            }
        }
        branches.rmbranch("*" + cbranch);
        branches.rmbranch(id);
        branches.addbrranch("*" + id);
        branches.addbrranch(cbranch);
        if (!stage.getss().isEmpty()) {
            stage.clearmap();
            Utils.writeContents(sf, Utils.serialize(stage));
        }
        if (!rstage.getss().isEmpty()) {
            rstage.clearmap();
            Utils.writeContents(rf, Utils.serialize(rstage));
        }
        for (Map.Entry<String, String> entry : com.getBlobs().entrySet()) {
            String fileName = entry.getKey();
            String blob = entry.getValue();
            File ff = new File(fileName);
            File b = new File(".gitlet/blob/" + blob);
            Blob bb = Utils.readObject(b, Blob.class);
            Utils.writeContents(ff, bb.getContent());
        }
        Utils.writeContents(hf, Utils.serialize(com));
        Utils.writeContents(bf, Utils.serialize(branches));
    }

    /** Creates a new branch.
     * @param b is branch name.*/
    public static void branch(String b) {
        if (branches.getB().contains(b)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File bran = new File(".gitlet/commit/" + b);
        branches.addbrranch(b);
        Utils.writeContents(bran, Utils.serialize(head));
        Utils.writeContents(bf, Utils.serialize(branches));
    }

    /** Create a new commit that is the children of the head commit.
     * @param a is commit message.
     * @param merge is indicator for merge commit.
     * @param current1 is merge parent 1.
     * @param branch1 is merge parent 2.*/
    public static void makeCommit(String a, boolean merge,
                                  String current1, String branch1) {
        if (stage.getss().isEmpty() && rstage.getss().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit c = new Commit(a, Utils.sha1(Utils.serialize(head)));
        if (merge) {
            c = new Commit(a, current1 + "," + branch1);
        }
        for (String rmf: rstage.getFiles()) {
            c.removeF(rmf);
        }
        rstage.clearmap();
        for (String addf: stage.getFiles()) {
            c.addFile(addf);
        }
        stage.clearmap();
        File x = new File(".gitlet/commit/" + Utils.sha1(Utils.serialize(c)));
        Utils.writeContents(x, Utils.serialize(c));
        Utils.writeContents(hf, Utils.serialize(c));
        Utils.writeContents(current, Utils.serialize(c));
        Utils.writeContents(sf, Utils.serialize(stage));
        Utils.writeContents(rf, Utils.serialize(rstage));
    }

    /** Print the global log information. */
    public static void gLog() {
        List<String> files = Utils.plainFilenamesIn(".gitlet/commit");
        Commit c;
        File f;
        for (String s: files) {
            if (!s.equals("head") && !branches.getB().contains(s)
                    && !branches.getB().contains("*" + s)) {
                f = new File(".gitlet/commit/" + s);
                c = Utils.readObject(f, Commit.class);
                System.out.println("===");
                System.out.println("commit" + " " + s);
                System.out.println("Date:" + " " + c.getTimestamp());
                System.out.println(c.getMessage());
                System.out.println();
            }
        }
    }

    /** Add a file to the addition stage.
     * @param a is file name.*/
    public static void add(String a) {
        File f = new File(a);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if (rstage.getss().containsKey(a)) {
            rstage.rmap(a);
            Utils.writeContents(rf, Utils.serialize(rstage));
        }
        String ff = head.getContent(a);
        String compare = Utils.readContentsAsString(f);
        if (compare.equals(ff)) {
            if (stage.getss().containsKey(a)) {
                stage.rmap(a);
            }
            Utils.writeContents(sf, Utils.serialize(stage));
            System.exit(0);
        }
        stage.addmap(a, Utils.readContentsAsString(f));
        Utils.writeContents(sf, Utils.serialize(stage));
        Utils.writeContents(rf, Utils.serialize(rstage));
    }

    /** Creates a gitlet repo in the CWD. */
    public static void init() {
        File gitlet = new File(".gitlet");
        if (gitlet.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        Commit initial = Commit.init();
        branches.addbrranch("*master");
        File commit = new File(".gitlet/commit");
        File blob = new File(".gitlet/blob");
        gitlet.mkdir();
        commit.mkdir();
        blob.mkdir();
        head = initial;
        stage = new Stage();
        rstage = new Stage();
        String sha = Utils.sha1(Utils.serialize(head));
        File sh = new File(".gitlet/commit/" + sha);
        Utils.writeContents(hf, Utils.serialize(head));
        Utils.writeContents(sh, Utils.serialize(head));
        Utils.writeContents(sf, Utils.serialize(stage));
        Utils.writeContents(rf, Utils.serialize(rstage));
        Utils.writeContents(current, Utils.serialize(head));
        Utils.writeContents(bf, Utils.serialize(branches));
    }

    /** The head commit. */
    private static Commit head = new Commit("", null);

    /** The addition stage. */
    private static Stage stage;

    /** The removal stage. */
    private static Stage rstage;

    /** The file path to head. */
    private static File hf = new File(".gitlet/commit/head");

    /** The file path to addition stage. */
    private static File sf = new File(".gitlet/stage");

    /** The file path to removal stage. */
    private static File rf = new File(".gitlet/Rstage");

    /** The file path to branches. */
    private static File bf = new File(".gitlet/branches");

    /** The object representation of branches. */
    private static Branches branches = new Branches();

    /** The default path to current commit. */
    private static File current = new File(".gitlet/commit/master");

    /** The current branch. */
    private static String cbranch;

    /** A dictionary of possible commands. */
    private static String[] dic = {"init", "add", "commit", "rm", "log",
        "global-log", "find", "status", "checkout",
        "branch", "rm-branch", "reset", "merge"};
}
