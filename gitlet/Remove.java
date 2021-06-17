package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/** Class to do Remove ("rm") command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Remove {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to remove files/blobs from Tree object gitTree head commit and
     * unstage them from stagingArea object. Takes in FILENAME,
     * which is the file to be removed. Removes if tracked in head commit.*/
    public void remove(String fileName) {
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
        CommitObject headCommit = gitTree.getHead();
        if ((!(stagingArea.getBlobsStagingHashMap().containsKey(fileName)))
            && (!(headCommit.getConnectedBlobs().containsKey(fileName)))) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (stagingArea.getBlobsStagingHashMap().containsKey(fileName)) {
            stagingArea.getBlobsStagingHashMap().remove(fileName);
        }
        if (headCommit.getConnectedBlobs().containsKey(fileName)) {
            headCommit.getToBeRemovedBlobs().put(fileName,
                    headCommit.getConnectedBlobs().get(fileName));
            if (!stagingArea.getRemovedFiles().containsKey(fileName)) {
                stagingArea.getRemovedFiles().put(fileName,
                        headCommit.getConnectedBlobs().get(fileName));
            }
            if (new File(fileName).exists()) {
                Utils.restrictedDelete(fileName);
            }
        }
        Utils.writeObject(new File(".gitlet/staging/stagingArea/"),
                stagingArea);
        String headCommitSha1 = headCommit.getSha1Code();
        Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
        Utils.writeObject(new File(".gitlet/commits/" + headCommitSha1),
                headCommit);
    }
}
