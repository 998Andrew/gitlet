package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/** Class to do commit command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Commit {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to create new commitObject and
     * commit files/blobs from stagingArea object by
     * adding them in Tree object gitTree. Takes in String MESSAGE,
     * which is a message that becomes the new commitObject's
     * metadata. */
    public void commit(String message) {

        if (message == null || message.length() == 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        try {
            File stage = new File(".gitlet/staging/stagingArea/");
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(stage));
            stagingArea = (StageObject) in.readObject();
            in.close();
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }

        try {
            File tree = new File(".gitlet/branch/myTree/");
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(tree));
            gitTree = (Tree) in.readObject();
            in.close();
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }

        if (stagingArea.getRemovedFiles().isEmpty()
            && stagingArea.getBlobsStagingHashMap().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        String currentTimestamp = new SimpleDateFormat("E MMM d HH:mm:ss y Z")
                .format(new Date());
        CommitObject parent = gitTree.getHead();
        HashMap<String, Blob> nonRMCommitBlobs = stagingArea.
                addNewBlobs(parent.getConnectedBlobs());
        HashMap<String, Blob> copyNonRMBlobs = deepCopyHash(nonRMCommitBlobs);
        HashMap<String, Blob> rMCommitBlobs = stagingArea
                .removeBlobs(copyNonRMBlobs, parent);
        HashMap<String, Blob> actualCommitBlobs = deepCopyHash(rMCommitBlobs);
        String blobsSha1 = actualCommitBlobs.values().toString();
        String commitsha1 = Utils.sha1(currentTimestamp + message + blobsSha1);
        CommitObject myCommit = new CommitObject(message, commitsha1,
                currentTimestamp, parent, actualCommitBlobs);
        gitTree.setHead(myCommit);
        stagingArea.clearStagingArea();
        Utils.writeObject(new File(".gitlet/staging/stagingArea/"),
                stagingArea);
        Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
        Utils.writeObject(new File(".gitlet/commits/" + commitsha1), myCommit);
    }

    /** Method to deepCopy HashMap ORIGINAL and return the deepCopy
     * of that HashMap. Needed for persistence. */
    public HashMap<String, Blob> deepCopyHash(HashMap<String, Blob> original) {
        HashMap<String, Blob> deepCopy = new HashMap<>();
        for (HashMap.Entry<String, Blob> entry : original.entrySet()) {
            deepCopy.put(entry.getKey(), entry.getValue());
        }
        return deepCopy;
    }
}
