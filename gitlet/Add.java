package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/** Class to add files from working directory into Staging Area.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Add {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;

    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to add files/blobs from working directory
     * PATH to stagingArea object. */
    public void add(String path) {

        if (!new File(path).exists()) {
            System.out.println("File does not exist.");
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
        Blob theBlob = new Blob(path);
        String theBlobSha1 = theBlob.getSha1Code();
        Utils.writeObject(new File(".gitlet/blobs/"
                + theBlob.getSha1Code()), theBlob);
        CommitObject headCommit = gitTree.getHead();

        if ((!stagingArea.getBlobsStagingHashMap().containsKey(path))
                || (!stagingArea.getBlobsStagingHashMap()
                .containsValue(theBlob))) {
            stagingArea.getBlobsStagingHashMap().put(path, theBlob);
            Utils.writeObject(new File(".gitlet/staging/"
                    + theBlob.getSha1Code()), theBlob);
        }
        if ((stagingArea.getBlobsStagingHashMap().containsKey(path))
                && (!stagingArea.getBlobsStagingHashMap()
                .containsValue(theBlob))) {
            stagingArea.getBlobsStagingHashMap().replace(path, theBlob);
            Utils.writeObject(new File(".gitlet/staging/"
                    + theBlob.getSha1Code()), theBlob);
        }
        if (headCommit.getConnectedBlobs().containsKey(path)
            && theBlobSha1.equals(headCommit.getConnectedBlobs()
                .get(path).getSha1Code())) {
            stagingArea.getBlobsStagingHashMap().remove(path);
        }
        if (stagingArea.getRemovedFiles().containsKey(path)) {
            stagingArea.getRemovedFiles().remove(path);
        }
        Utils.writeObject(new File(".gitlet/staging/stagingArea/"),
                stagingArea);
    }
}
