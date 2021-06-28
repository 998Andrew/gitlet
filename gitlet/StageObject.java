package gitlet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/** StageObject class to hold what has been staged after
 * and "add" command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class StageObject implements Serializable {

    /** The Blobs that will actually be staged. */
    private ArrayList<Blob> _actuallyToBeStaged = new ArrayList<>();
    /** HashMap of Filenames and blobs that will be staged during a commit
     * (they have been added). */
    private HashMap<String, Blob> _toBeStaged = new HashMap<String, Blob>();
    /** HashMap of Filenames and blobs that have had rm func called on them. */
    private HashMap<String, Blob> _removedFiles = new HashMap<>();

    /** StageObject constructor. */
    public StageObject() {
    }

    /** Helper accessor function to return HashMap of toBeStaged
     *  filenames/blobs. */
    public HashMap<String, Blob> getBlobsStagingHashMap() {
        return _toBeStaged;
    }

    /** Method to clear HashMap of fileName/blobs. */
    public void clearStagingArea() {
        _removedFiles.clear();
        _toBeStaged.clear();
    }

    /** Method to find new blobs in a commitObject compared to it's PARENTBLOBS
     * and also updates the values of HashMap if files have been altered.
     * returns the complete set of blobs that should be staged. */
    public HashMap<String, Blob> addNewBlobs(HashMap<String,
            Blob> parentBlobs) {
        HashMap<String, Blob> returnHash = new HashMap<>();
        if (parentBlobs.isEmpty()) {
            returnHash = _toBeStaged;
        }
        returnHash.putAll(parentBlobs);
        for (String staged : _toBeStaged.keySet()) {
            if (!parentBlobs.keySet().contains(staged)) {
                returnHash.put(staged, _toBeStaged.get(staged));
            } else if (parentBlobs.keySet().contains(staged)
                    && parentBlobs.get(staged) != _toBeStaged.get(staged)) {
                returnHash.replace(staged, _toBeStaged.get(staged));
            }
        }
        return returnHash;
    }

    /** Method to check if the parent contains a file that should be
     * removed from the next commit's filename/blob HashMap.
     * and return a HashMap of it removed. Takes in PREREMOVED, which
     * is a HashMap of all blobs that were going to be in the commit, and PARENT
     * commitObject which will contain a hashMap of what should be removed. */
    public HashMap<String, Blob> removeBlobs(HashMap<String, Blob> preRemoved,
                                             CommitObject parent) {
        HashMap<String, Blob> returnHash = preRemoved;
        for (String removedFileName : parent.getToBeRemovedBlobs().keySet()) {
            if (returnHash.containsKey(removedFileName)) {
                returnHash.remove(removedFileName);
            }
        }
        return returnHash;
    }
    /** Helper accessor function to return HashMap of files/blobs that ]
     * have had rm (remove) function called on them. */
    public HashMap<String, Blob> getRemovedFiles() {
        return _removedFiles;
    }

    /** Helper accessor function to return whether or not
     * you should commit or not. */
    public boolean shouldNotCommit() {
        boolean returnVal = false;
        if (getRemovedFiles().isEmpty()) {
            System.out.println(getRemovedFiles().keySet());
            returnVal = true;
        } else if (getBlobsStagingHashMap().isEmpty()) {
            System.out.println(getBlobsStagingHashMap().keySet());
            returnVal = true;
        }
        System.out.println(returnVal);
        return returnVal;
    }
}
