package gitlet;
import java.io.Serializable;
import java.util.HashMap;

/** Tree class to hold commitObjects after a "commit" command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Tree implements Serializable {

    /** The root of the Tree, should be null (initial commit). */
    private CommitObject _root;
    /** The head of the Tree, wherever the head pointer is. */
    private CommitObject _head;
    /** HashMap of Tree branches, with STRING branchName and
     * Commit Object head's sha1.*/
    private HashMap<String, CommitObject> _listBranches = new HashMap<>();
    /** The current branch of the Tree. */
    private String _branch;

    /** Tree constructor, takes in a ROOT, and a HEAD. The initial
     * commit will set these to NULL. */
    public Tree(CommitObject root, CommitObject head) {
        this._root = root;
        this._head = head;
    }
    /** Method to set the head of the Tree to a NEWHEAD CommitObject. */
    public void setHead(CommitObject newHead) {
        _head = _listBranches.replace(getCurrentBranch(), newHead);
    }
    /** Accessor helper method to return head of the Tree. */
    public CommitObject getHead() {
        return _listBranches.get(getCurrentBranch());
    }
    /** Accessor helper method to return branches of the Tree. */
    public HashMap<String, CommitObject> getBranches() {
        return _listBranches;
    }
    /** Adds a new branch to the tree, setting the head commit object's
     * HEAD to a NEWBRANCHNAME pointer in a HashMap. */
    public void addBranches(String newBranchName, CommitObject head) {
        _listBranches.put(newBranchName, head);
    }
    /** Helper method to set current head branches of the Tree
     * to BRANCHNAME - used during checkout. * */
    public void setBranch(String branchName) {
        _branch = branchName;
    }
    /** Accessor helper method to return current (head)
     * branch name of the Tree. */
    public String getCurrentBranch() {
        return _branch;
    }
}
