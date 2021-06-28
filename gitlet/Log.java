package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/** Class to do log command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Log {

    /** CommitObject to track current commitObject index. */
    private CommitObject current;
    /** Tree Object to hold what has been committed (persists throughout
     * the whole project). */
    private Tree gitTree;

    /** Method to do log command. */
    public void log() {
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
        if (gitTree.getHead() != null) {
            current = gitTree.getHead();
        }
        while (current != null) {
            System.out.println("===");
            System.out.println("commit " + current.getSha1Code());
            System.out.println("Date: " + current.getTimestamp());
            System.out.println(current.getMetaData());
            System.out.println();
            current = current.getParent();
        }
    }
}
