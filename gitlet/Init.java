package gitlet;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/** Class to initialize ("init") GitLet.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Init {

    /** Method to initialize ("init") GitLet. */
    public void init() throws IOException {
        if (new File(".gitlet/").exists()) {
            System.out.println("A Gitlet version-control system"
                    + " already exists in the current directory.");
            System.exit(0);
        } else {
            new File(".gitlet/").mkdir();
            new File(".gitlet/commits/").mkdir();
            new File(".gitlet/blobs/").mkdir();
            new File(".gitlet/staging/").mkdir();
            new File(".gitlet/branch/").mkdir();
            new File(".gitlet/staging/stagingArea/")
                    .createNewFile();
            String initialEpoch = new SimpleDateFormat("E MMM d HH:mm:ss y Z")
                    .format(new Date(0));
            String initialMessage = "initial commit";
            String initialSha1 = Utils.sha1(initialEpoch
                    + initialMessage);
            HashMap<String, Blob> initialHashNull = new HashMap<>();
            CommitObject firstCommit = new CommitObject(initialMessage,
                    initialSha1, initialEpoch, null, initialHashNull);
            Tree gitTree = new Tree(firstCommit, firstCommit);
            gitTree.setBranch("master");
            gitTree.addBranches("master", firstCommit);
            StageObject stagingArea = new StageObject();
            Utils.writeObject(new File(".gitlet/commits/"
                    + firstCommit.getSha1Code()), firstCommit);
            Utils.writeObject(new File(".gitlet/staging/stagingArea/"),
                    stagingArea);
            Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
        }
    }
}
