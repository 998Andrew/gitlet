package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

/** Class to do remove branch "rm-branch" command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class RemoveBranch {

    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to do rm-branch command. Removes
     * BRANCHNAME pointer from tree branches*/
    public void removeBranch(String branchName) {
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
        if (!gitTree.getBranches().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (gitTree.getCurrentBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        HashMap<String, CommitObject> branches = gitTree.getBranches();
        branches.remove(branchName);
        Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
    }
}
