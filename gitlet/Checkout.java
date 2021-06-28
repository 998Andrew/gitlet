package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/** Class to do checkout command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Checkout {

    /** Stage Object to hold what has been added (persists throughout
     * the whole project). */
    private StageObject stagingArea;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;
    /** Commit Object that is the head of the Branch to be checked out. */
    private CommitObject _checkoutHeadCommit;


    /** Method to checkout files/blobs from Tree object gitTree and
     * return/replace them in working directory. Takes in ARGS,
     * which is a list of 3 potential types of checkout commands*/
    public void checkout(String [] args) {
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

        if (args.length == 1) {
            checkoutBranch(args);
        } else if (args.length == 2) {
            checkoutHeadFile(args);
        } else if (args.length == 3) {
            checkoutCommitFile(args);
        } else {
            System.exit(0);
        }
    }

    /** Method to checkout the whole branch. ARGS
     * is a 1 String  array where ARGS[0] is branch name.
     * This is #3 in checkout spec. */
    public void checkoutBranch(String [] args) {
        String branchName = args[0];
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
        if (!gitTree.getBranches().containsKey(branchName)) {
            System.out.println("No such branch exists."); System.exit(0);
        }
        if (gitTree.getCurrentBranch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File[] workingDirFiles = new File(System.getProperty("user.dir"))
                .listFiles();
        for (File file : workingDirFiles) {
            String fileName = file.getName();
            if (fileName.equals(".DS_Store") || fileName.equals(".gitlet")) {
                continue;
            }
            if (!gitTree.getHead().getConnectedBlobs()
                    .containsKey(fileName)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it or add it first.");
                System.exit(0);
            }
        }
        CommitObject branchCommit = gitTree.getBranches().get(branchName);
        HashMap<String, Blob> checkedbranchCommitFileSet = branchCommit
                .getConnectedBlobs();
        for (String fileName: checkedbranchCommitFileSet.keySet()) {
            Blob fileContents = checkedbranchCommitFileSet.get(fileName);
            byte [] blobSerializedData = fileContents.getSerializedData();
            Utils.writeContents(new File(fileName), blobSerializedData);
        }
        CommitObject currentBranchHead = gitTree.getHead();
        for (String fileName : currentBranchHead.getConnectedBlobs().keySet()) {
            if (!checkedbranchCommitFileSet.containsKey(fileName)) {
                Utils.restrictedDelete(fileName);
            }
        }
        gitTree.setBranch(branchName); stagingArea.clearStagingArea();
        Utils.writeObject(new File(".gitlet/staging/stagingArea/"),
                stagingArea);
        Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
    }

    /** Method to checkout the file from current
     * head commit in tree. ARGS
     * is an array of 2 strings where ARGS[1] is file name.
     * This is #1 in checkout spec. */
    public void checkoutHeadFile(String [] args) {
        String fileName = args[1];
        if (!args[0].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (gitTree.getHead() != null) {
            CommitObject head = gitTree.getHead();
            if (!head.getConnectedBlobs().keySet().contains(fileName)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            Blob fileContents = head.getConnectedBlobs().get(fileName);
            byte [] blobSerializedData = fileContents.getSerializedData();
            Utils.writeContents(new File(fileName), blobSerializedData);
        }
    }

    /** Method to checkout the file from a specific commit. ARGS
     * is a array of 3 Strings where ARGS[0] is the commit ID
     * and ARGS[2] is file name to be checked out.
     * This is #2 in checkout spec. */
    public void checkoutCommitFile(String [] args) {
        String commitID = args[0];
        if (!args[1].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        ArrayList<String> commitIDs = new ArrayList<>();
        String fileName = args[2];
        CommitObject current = gitTree.getHead();
        while (current != null) {
            if (current.getSha1Code().equals(commitID)
                    || current.getSha1Code().
                    regionMatches(0, commitID, 0, commitID.length())) {
                commitID = current.getSha1Code();
                CommitObject rightCommit = current;
                if (!rightCommit.getConnectedBlobs()
                        .keySet().contains(fileName)) {
                    System.out.println("File does not exist in that commit.");
                    System.exit(0);
                }
                Blob fileCont = rightCommit.getConnectedBlobs().get(fileName);
                byte[] blobSerialData = fileCont.getSerializedData();
                Utils.writeContents(new File(fileName),
                        blobSerialData);
            }
            commitIDs.add(current.getSha1Code());
            current = current.getParent();
        }
        if (!commitIDs.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }
}
