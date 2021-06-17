package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

/** Class to do branch command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Status {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to do working related to status command.
     * and call printOut function at end*/
    public void getStatus() {
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
        HashMap<String, CommitObject> branches = gitTree.getBranches();
        HashMap<String, Blob> staged = stagingArea.getBlobsStagingHashMap();
        HashMap<String, Blob> removed = stagingArea.getRemovedFiles();
        HashMap<String, String> unstagedModified = new HashMap<>();
        HashMap<String, Blob> untracked = new HashMap<>();
        unstagedModified = checkUnstagedModified();
        untracked = checkUntracked();
        printOut(branches, staged, removed, unstagedModified, untracked);
        String userDir = System.getProperty("user.dir");
        File[] workingDirFiles = new File(userDir).listFiles();

    }

    /** Method to print out status. Takes in HashMaps of BRANCHES
     * STAGED, REMOVED, UNSTAGEDMODIFIED, UNTRACKED
     * which are to be printed out.*/
    public void printOut(HashMap<String, CommitObject> branches,
                         HashMap<String, Blob> staged,
                         HashMap<String, Blob> removed,
                         HashMap<String, String> unstagedModified,
                         HashMap<String, Blob> untracked
                         ) {
        System.out.println("=== Branches ===");
        System.out.println("*" + gitTree.getCurrentBranch());
        for (String branch : branches.keySet()) {
            if (!branch.equals(gitTree.getCurrentBranch())) {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String filename : staged.keySet()) {
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String filename : removed.keySet()) {
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String filename : unstagedModified.keySet()) {
            if (unstagedModified.get(filename).equals("(modified)")) {
                System.out.println(filename + " (modified)");
            }
            if (unstagedModified.get(filename).equals("(deleted)")) {
                System.out.println(filename + " (deleted)");
            }
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String filename : untracked.keySet()) {
            System.out.println(filename);
        }
        System.out.println();
    }

    /** Method to return untracked modified files.*/
    public HashMap<String, String> checkUnstagedModified() {
        String userDir = System.getProperty("user.dir");
        HashMap<String, String> returnHash = new HashMap<>();
        File[] workingDirFiles = new File(userDir).listFiles();
        HashMap<String, Blob> workingDirBlobs = new HashMap<>();
        for (File f: workingDirFiles) {
            if (f.getName().equals(".DS_Store")
                    || f.getName().equals(".gitlet")) {
                continue;
            } else {
                Blob newBlob = new Blob(f.getName());
                workingDirBlobs.put(f.getName(), newBlob);
            }
        }
        HashMap<String, Blob> headBlobs = gitTree.getHead().getConnectedBlobs();
        HashMap<String, Blob> stageBlobsAdd = stagingArea
                .getBlobsStagingHashMap();
        HashMap<String, Blob> stageBlobsRemove = stagingArea
                .getRemovedFiles();
        for (String i: headBlobs.keySet()) {
            boolean inWork = workingDirBlobs.containsKey(i);
            if (!stageBlobsRemove.containsKey(i)
                    && !workingDirBlobs.containsKey(i)) {
                returnHash.put(i, "(deleted)");
            }
            if (inWork) {
                if (!workingDirBlobs.get(i).equals(headBlobs.get(i))
                        && !stageBlobsAdd.containsKey(i)) {
                    returnHash.put(i, "(modified)");
                }
            }
        }
        for (String i: stageBlobsAdd.keySet()) {
            if (!stageBlobsAdd.get(i).equals(workingDirBlobs.get(i))) {
                returnHash.put(i, "(modified)");
            } else if (!workingDirBlobs.containsKey(i)) {
                returnHash.put(i, "(deleted)");
            }
        }
        return returnHash;
    }

    /** Method to return untracked files.*/
    public HashMap<String, Blob> checkUntracked() {
        String userDir = System.getProperty("user.dir");
        HashMap<String, Blob> returnHash = new HashMap<>();
        File[] workingDirFiles = new File(userDir).listFiles();
        HashMap<String, Blob> workingDirBlobs = new HashMap<>();
        for (File f: workingDirFiles) {
            if (f.getName().equals(".DS_Store")
                    || f.getName().equals(".gitlet")) {
                continue;
            } else {
                Blob newBlob = new Blob(f.getName());
                workingDirBlobs.put(f.getName(), newBlob);
            }
        }
        HashMap<String, Blob> headBlobs = gitTree.getHead().getConnectedBlobs();
        HashMap<String, Blob> stageBlobsAdd = stagingArea
                .getBlobsStagingHashMap();
        HashMap<String, Blob> stageBlobsRemove = stagingArea
                .getRemovedFiles();
        for (String i: workingDirBlobs.keySet()) {
            if (i.equals(".DS_Store") || i.equals(".gitlet")) {
                continue;
            } else if (!stageBlobsAdd.containsKey(i)
                    && !headBlobs.containsKey(i)) {
                returnHash.put(i, workingDirBlobs.get(i));
            }
        }
        return returnHash;
    }
}
