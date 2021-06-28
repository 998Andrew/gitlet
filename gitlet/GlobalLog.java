package gitlet;
import java.io.File;

/** Class to do Global Log ("global-log") command.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class GlobalLog {

    /** Method to do Global Log ("global-log") command.*/
    public void getGlobalLog() {
        File[] commits = new File(".gitlet/commits/").listFiles();

        for (File file: commits) {
            CommitObject currentCommit = Utils.readObject(file,
                    CommitObject.class);
            String fileName = file.getName();
            System.out.println("===");
            System.out.println("commit " + currentCommit.getSha1Code());
            System.out.println("Date: " + currentCommit.getTimestamp());
            System.out.println(currentCommit.getMetaData());
            System.out.println();
        }
    }
}
