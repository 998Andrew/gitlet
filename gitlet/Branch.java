package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/** Class to do branch command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Branch {

    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to do branch command. adds BRANCHNAME
     * as a branch pointer in tree*/
    public void addBranch(String branchName) {

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
        if (gitTree.getBranches().containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        } else {
            gitTree.addBranches(branchName, gitTree.getHead());
            Utils.writeObject(new File(".gitlet/branch/myTree/"), gitTree);
        }

    }

}
