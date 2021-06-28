package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Andrew Liu 3032695577 cs61b-alt
 *  @Collaborators: Alice Wang: aliceywang1@berkeley.edu, Kevin Chai:
 *  kevinchai0@berkeley.edu.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        List<String> commands = Arrays.asList("init", "add", "commit", "log",
                "checkout", "rm", "global-log", "find", "branch", "rm-branch",
                "status", "reset", "merge");
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        } else if (!commands.contains(args[0])) {
            System.out.println("No command with that name exists.");
            System.exit(0);
        } else if (args[0].equals("init")) {
            Init firstInit = new Init(); firstInit.init();
        } else if (!new File(".gitlet/").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        } else if (args[0].equals("add")) {
            Add addObj = new Add(); addObj.add(args[1]);
        } else if (args[0].equals("commit")) {
            if (args.length  == 1) {
                System.out.println("Please enter a commit message.");
                System.exit(0);
            }
            Commit commit = new Commit(); commit.commit(args[1]);
        } else if (args[0].equals("log")) {
            Log log = new Log(); log.log();
        } else if (args[0].equals("checkout")) {
            Checkout checkout = new Checkout();
            String[] checkoutArgs = Arrays.copyOfRange(args, 1, args.length);
            checkout.checkout(checkoutArgs);
        } else if (args[0].equals("rm")) {
            Remove remove = new Remove();
            remove.remove(args[1]);
        } else if (args[0].equals("global-log")) {
            GlobalLog globalLog = new GlobalLog(); globalLog.getGlobalLog();
        } else if (args[0].equals("find")) {
            Find find = new Find();
            find.find(args[1]);
        } else if (args[0].equals("branch")) {
            Branch branch = new Branch();
            branch.addBranch(args[1]);
        } else if (args[0].equals("rm-branch")) {
            RemoveBranch removeBranch = new RemoveBranch();
            removeBranch.removeBranch(args[1]);
        } else if (args[0].equals("status")) {
            Status status = new Status();
            status.getStatus();
        } else if (args[0].equals("reset")) {
            Reset reset = new Reset();
            reset.reset(args[1]);
        } else if (args[0].equals("merge")) {
            Merge merge = new Merge();
            merge.merge(args[1]);
        } else {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }
}
