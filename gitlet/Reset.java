package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/** Class to do reset command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Reset {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;
    /** CommitObject that I reset to. */
    private CommitObject resetCommit;

    /** Method to do reset command. checks out all the files in
     * given COMMITID, removes tracked files, changes head to COMMITID
     * clears stagingArea.
     */
    public void reset(String commitID) {
        try {
            File tree = new File(".gitlet/branch/myTree/");
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(tree));
            gitTree = (Tree) in.readObject(); in.close();
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
        try {
            File stage = new File(".gitlet/staging/stagingArea/");
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(stage));
            stagingArea = (StageObject) in.readObject(); in.close();
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
        File[] commits = new File(".gitlet/commits/").listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();
        for (File file: commits) {
            CommitObject currentCommit = Utils.readObject(file,
                    CommitObject.class);
            String toAdd = currentCommit.getSha1Code(); fileNames.add(toAdd);
            if (toAdd.equals(commitID)) {
                resetCommit = currentCommit;
            }
        }
        if (!fileNames.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File[] workingDirFiles = new File(System.getProperty("user.dir"))
                .listFiles();
        for (File file : workingDirFiles) {
            String fileName = file.getName();
            if (fileName.equals(".DS_Store") || fileName.equals(".gitlet")) {
                continue;
            }
            if (!gitTree.getHead().getConnectedBlobs().containsKey(fileName)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it or add it first.");
                System.exit(0);
            }
        }
        for (String fileName : gitTree.getHead().getConnectedBlobs().keySet()) {
            if (!resetCommit.getConnectedBlobs().containsKey(fileName)) {
                Remove remove = new Remove();
                remove.remove(fileName);
            }
        }
        reserializeBlobs();
        gitTree.addBranches(gitTree.getCurrentBranch(),
                resetCommit);
        stagingArea.clearStagingArea();
        Utils.writeObject(new File(".gitlet/staging/stagingArea/"),
                stagingArea);
        Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
    }

    /** Helper function to reserialize files of RESETCOMMIT and
     * write them into working directory. */
    public void reserializeBlobs() {
        for (String fileName : resetCommit.getConnectedBlobs().keySet()) {
            Blob fileContents = resetCommit.getConnectedBlobs().get(fileName);
            byte [] blobSerializedData = fileContents.getSerializedData();
            Utils.writeContents(new File(fileName), blobSerializedData);
        }
    }
}
