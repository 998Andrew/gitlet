package gitlet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/** CommitObject class.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class CommitObject implements Serializable {

    /** CommitObject's message/metadata. */
    private String _metadata;
    /** CommitObject's timestamp for when it was created. */
    private String _timestamp;
    /** CommitObject's sha1Code. */
    private String _sha1Code;
    /** CommitObject's parent which is also a commitObject. */
    private CommitObject _parent;
    /** HashMap to connect the fileName to a Blob
     * that is contained in this commitObject. */
    private HashMap<String, Blob> _connectedBlobs = new HashMap<String, Blob>();
    /** HashMap to contain the fileName and Blob
     * that should be removed from next commitObject due to
     * "rm command". */
    private HashMap<String, Blob> _toBeRemovedBlobs = new HashMap<>();
    /** Commit object's constructor. Takes in METADATA, SHA1, TIMESTAMP,
     * PARENT, and HashMap of BLOBS connecting to filenames. */
    public CommitObject(String metadata, String sha1,
                        String timestamp, CommitObject parent,
                        HashMap<String, Blob> blobs) {
        this._metadata = metadata;
        this._sha1Code = sha1;
        this._timestamp = timestamp;
        this._parent = parent;
        this._connectedBlobs = blobs;
    }

    /** Helper accessor method to return CommitObject metadata/message. */
    public String getMetaData() {
        return _metadata;
    }
    /** Helper accessor method to return CommitObject's timeStamp. */
    public String getTimestamp() {
        return _timestamp;
    }
    /** Helper accessor method to return CommitObject's sha1code. */
    public String getSha1Code() {
        return _sha1Code;
    }
    /** Helper accessor method to return CommitObject's'parent. */
    public CommitObject getParent() {
        return _parent;
    }
    /** Helper accessor method to return CommitObject's HashMap of fileNames
     * connected to Blob objects . */
    public HashMap<String, Blob> getConnectedBlobs() {
        return _connectedBlobs;
    }

    /** Helper accessor method to return CommitObject blobs from HashMap. */
    public Set getBlobKeys() {
        return _connectedBlobs.keySet();
    }

    /** Helper accessor method to return CommitObject's HashMap of fileNames
     * connected to Blob objects . */
    public HashMap<String, Blob> getToBeRemovedBlobs() {
        return _toBeRemovedBlobs;
    }
}
