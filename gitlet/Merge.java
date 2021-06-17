package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Class to do merge command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Merge {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;
    /** Commit Object that is the current branch's head Commit. */
    private CommitObject _headCommitCurrentBranch;
    /** Commit Object that is the merge branch's head Commit. */
    private CommitObject _headCommitMergeBranch;
    /** Commit Object that is split point between merge
     * and current branches. */
    private CommitObject _splitPoint;
    /** HashMap of current branch's head Commit fileNames/blobs. */
    private HashMap<String, Blob> _headCommitCurrentBranchBlobs;
    /** HashMap of merge branch's head Commit fileNames/blobs. */
    private HashMap<String, Blob> _headCommitMergeBranchBlobs;
    /** HashMap of split point' fileNames/blobs. */
    private HashMap<String, Blob> _splitPointBlobs;


    /** Method to do merge command. Takes in string MERGEBRANCHNAME
     * which merges files from MERGEBRANCHNAME branch to current branch */
    public void merge(String mergeBranchName) {
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

        checkErrors(mergeBranchName);
        System.out.println("Merged " + mergeBranchName + " into "
                + gitTree.getCurrentBranch() + ".");
        _headCommitMergeBranch = gitTree.getBranches().get(mergeBranchName);
        _headCommitCurrentBranch = gitTree.getBranches()
                .get(gitTree.getCurrentBranch());
        _headCommitCurrentBranchBlobs = _headCommitCurrentBranch
                .getConnectedBlobs();
        _headCommitMergeBranchBlobs = _headCommitMergeBranch
                .getConnectedBlobs();
        File[] workingDirFiles = new File(System.getProperty("user.dir"))
                .listFiles();
        for (File file : workingDirFiles) {
            String fileName = file.getName();
            if (file.isFile()
                    && !stagingArea.getBlobsStagingHashMap()
                    .containsKey(fileName)
                    && !_headCommitCurrentBranchBlobs.containsKey(fileName)) {
                String msg = "There is an untracked file in the way;"
                        + " delete it or add it first.";
                System.out.println(msg);
                System.exit(0);
            }
        }
        findSplit(getheadCommitCurrentBranch(),
                getheadCommitMergeBranch());
        doMerge(getheadCommitCurrentBranch(),
                getheadCommitMergeBranch(), getSplitPoint());
    }

    /** Find Split Point given CURRENTHEAD, MERGEHEAD.
     * NO CRISSCROSSING :(( */
    public void findSplit(CommitObject currentHead,
                                  CommitObject mergeHead) {
        CommitObject currHeadPointer = currentHead;
        CommitObject currMergePointer = mergeHead;
        HashSet<String> visited = new HashSet<>();
        while (currHeadPointer != null) {
            visited.add(currHeadPointer.getSha1Code());
            currHeadPointer = currHeadPointer.getParent();
        }
        while (currMergePointer != null) {
            if (visited.contains(currMergePointer.getSha1Code())) {
                _splitPoint = currMergePointer;
                break;
            }
            currMergePointer = currMergePointer.getParent();
        }
    }

    /** Helper unction to do the Merge fail cases.
     * Takes in CURRBRANCHHEAD, MERGEBRANCHHEAD, and SPLITPOINT,
     * which are commitobjects. */
    public void helperFailCase(CommitObject currBranchHead,
                               CommitObject mergeBranchHead,
                               CommitObject splitPoint) {
        if (splitPoint.getSha1Code().equals(mergeBranchHead.getSha1Code())) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            System.exit(0);
        } else if (splitPoint.getSha1Code().equals(currBranchHead
                .getSha1Code())) {
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    /** Function to do the Merge. Takes in CURRBRANCHHEAD,
     *  MERGEBRANCHHEAD, and SPLITPOINT, which are commitobjects. */
    public void doMerge(CommitObject currBranchHead,
                        CommitObject mergeBranchHead, CommitObject splitPoint) {
        helperFailCase(currBranchHead, mergeBranchHead, splitPoint);
        HashMap<String, Blob> currBlobs = currBranchHead.getConnectedBlobs();
        HashMap<String, Blob> mergeBlobs = mergeBranchHead.getConnectedBlobs();
        HashMap<String, Blob> splitBlobs = splitPoint.getConnectedBlobs();
        ArrayList<String> workingDirFileNames = new ArrayList<>();
        File[] workingDirFiles = new File(System.getProperty("user.dir"))
                .listFiles();
        for (File file : workingDirFiles) {
            workingDirFileNames.add(file.getName());
        }
        if (checkConflict(currBlobs, mergeBlobs, splitBlobs)) {
            System.out.println("Encountered a merge conflict.");
        }
        for (String file : splitBlobs.keySet()) {
            Blob currVersion = currBlobs.get(file);
            Blob mergeVersion = mergeBlobs.get(file);
            Blob splitVersion = splitBlobs.get(file);
            if (!currBlobs.containsKey(file) && !mergeBlobs.containsKey(file)
                && workingDirFileNames.contains(file)) {
                continue;
            } else if (!mergeVersion.equals(splitVersion)
                    && currVersion.getSha1Code().equals(splitVersion)) {
                currBlobs.replace(file, mergeVersion);
                stagingArea.getBlobsStagingHashMap().put(file, mergeVersion);
            } else if (!currVersion.equals(splitVersion)
                    && mergeVersion.equals(splitVersion)) {
                continue;
            } else if (!currVersion.equals(splitVersion)
                    && !mergeVersion.equals(splitVersion)
                    && currVersion.equals(mergeVersion)) {
                continue;
            } else if (currVersion.equals(splitVersion)
                    && !mergeBlobs.containsKey(file)) {
                currBlobs.remove(file);
            } else if (mergeVersion.equals(splitVersion)
                    && !currBlobs.containsKey(file)) {
                continue;
            }
        }
        for (String file : currBlobs.keySet()) {
            if (!splitBlobs.containsKey(file)) {
                continue;
            }
        }
        for (String file : mergeBlobs.keySet()) {
            if (!splitBlobs.containsKey(file)) {
                Checkout checkout = new Checkout();
                String[] args = new
                        String[] {mergeBranchHead.getSha1Code(), "--", file};
                checkout.checkoutCommitFile(args);
                stagingArea.getBlobsStagingHashMap().put(file,
                        mergeBlobs.get(file));
            }
        }
    }

    /** Checks if there is a file that is in conflict in CURRBLOBS,
     * the blobs of the currentbranchhead, MERGEBLOBS, the blobs
     * of the merge branch head, and SPLITBLOBS, the splitpoint.
     * returns true if there is a file in conflict and false otherwise.*/
    public boolean checkConflict(HashMap<String, Blob> currBlobs,
                                 HashMap<String, Blob> mergeBlobs,
                                 HashMap<String, Blob> splitBlobs) {
        boolean inConflict = false;
        for (String file : splitBlobs.keySet()) {
            Blob currVersion = currBlobs.get(file);
            Blob mergeVersion = mergeBlobs.get(file);
            Blob splitVersion = splitBlobs.get(file);

            if (!currVersion.equals(splitVersion)
                && !mergeVersion.equals(splitVersion)
                && !mergeVersion.equals(currVersion)) {
                inConflict = true;
                inConflict(file, currVersion, mergeVersion);
            }
        }
        for (String file: mergeBlobs.keySet()) {
            Blob currVersion = currBlobs.get(file);
            Blob mergeVersion = mergeBlobs.get(file);
            if (!splitBlobs.containsKey(file)
                && !mergeBlobs.get(file).equals(currBlobs.get(file))) {
                inConflict = true;
                inConflict(file, currVersion, mergeVersion);
            }
        }
        for (String file: currBlobs.keySet()) {
            Blob currVersion = currBlobs.get(file);
            Blob mergeVersion = mergeBlobs.get(file);
            if (!splitBlobs.containsKey(file)
                    && !mergeBlobs.get(file).equals(currBlobs.get(file))) {
                inConflict = true;
                inConflict(file, currVersion, mergeVersion);
            }
        }
        return inConflict;
    }

    /** Method to decide what to merge and their contents.
     * Takes in FILENAME, CURRVERSION, MERGEVERSION*/
    public void inConflict(String fileName, Blob currVersion,
                           Blob mergeVersion) {
        File mergeConflictFile = new File(fileName);
        byte[] mergeConflictFileContents;
        Blob currBranchBlob = currVersion;
        Blob mergeBranchBlob = mergeVersion;
        byte[] currBranchContents = new byte[0];
        byte[] mergeBranchContents = new byte[0];

        if (mergeBranchBlob == null) {
            currBranchContents = currBranchBlob.getSerializedData();
        }  else if (currBranchBlob == null) {
            mergeBranchContents = mergeBranchBlob.getSerializedData();
        } else {
            currBranchContents = currBranchBlob.getSerializedData();
            mergeBranchContents = mergeBranchBlob.getSerializedData();
        }
        mergeConflictFileContents = concatByteArrays(currBranchContents,
                mergeBranchContents);
        Utils.writeContents(mergeConflictFile, mergeConflictFileContents);
    }

    /** From stack overflow. TAKES IN byte array CURRCONTENTS which is the
     * contents from currentBranchHead and also MERGECONTENTS which is
     * contents from mergeBranchHead and returns the resulting
     * concatanated byte[]*/
    private byte[] concatByteArrays(byte[] currContents, byte[] mergeContents) {
        byte[] head = "<<<<<<< HEAD\n".getBytes();
        byte[] equals = "=======\n".getBytes();
        byte[] tail = ">>>>>>>\n".getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(head);
            outputStream.write(currContents);
            outputStream.write(equals);
            outputStream.write(mergeContents);
            outputStream.write(tail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    /** Method to check merge command errors.
     * Takes in string MERGEBRANCHNAME*/
    public void checkErrors(String mergeBranchName) {
        if (!stagingArea.getBlobsStagingHashMap().isEmpty()
                || !stagingArea.getRemovedFiles().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(9);
        }
        if (!gitTree.getBranches().containsKey(mergeBranchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (gitTree.getCurrentBranch().equals(mergeBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /** Accessor helper method to return head Commit of
     *  the Tree's current Branch. */
    public CommitObject getheadCommitCurrentBranch() {
        return _headCommitCurrentBranch;
    }

    /** Accessor helper method to return headCommit
     *  of the Tree's merge branch. */
    public CommitObject getheadCommitMergeBranch() {
        return _headCommitMergeBranch;
    }

    /** Accessor helper method to return Split Point. */
    public CommitObject getSplitPoint() {
        return _splitPoint;
    }
}
