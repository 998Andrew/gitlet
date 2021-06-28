package gitlet;
import java.io.File;
import java.util.HashSet;

/** Class to do Global Log ("global-log") command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Find {

    /** HashSet to hold commitID that have matching messages.*/
    private HashSet<String> matchingCommitIDs = new HashSet<>();

    /** Method to do find ("find") command looking for commits
     * with the same MESSAGE and printing their IDs.*/
    public void find(String message) {
        File[] commits = new File(".gitlet/commits/").listFiles();

        for (File file: commits) {
            CommitObject currentCommit = Utils.readObject(file,
                    CommitObject.class);
            if (currentCommit.getMetaData().equals(message)) {
                matchingCommitIDs.add(currentCommit.getSha1Code());
            }
        }
        if (matchingCommitIDs.size() == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        for (String id : matchingCommitIDs) {
            System.out.println(id);
        }
    }
}
