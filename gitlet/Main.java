package gitlet;



/* Driver class for Gitlet, the tiny stupid version-control system.
   @author
*/

import java.io.File;

public class Main {
    static String commitTree = "mainTree";
    private static File user = new File(System.getProperty("user.dir"));

    public static CommitTree getTree() {
        return (CommitTree) FileHelper.
                objectReader(".gitlet/" + commitTree);
    }

    public static void writeTree(CommitTree tree) {
        FileHelper.treeWriter(tree);
    }

    public static void initize() {
        File repo = new File(user, ".gitlet");
        if (!repo.exists()) {
            repo.mkdir();
            CommitTree tree = new CommitTree();
            writeTree(tree);
        } else {
            System.out.println("A gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }
    }

    /* Usage: java gitlet.Main ARGS, where ARGS contains
       <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String keyCommand = args[0];
        switch (keyCommand) {
            case "init":
                Helper.init(args);
                break;
            case "add":
                Helper.add(args);
                break;
            case "rm":
                Helper.rm(args);
                break;
            case "log":
                Helper.log(args);
                break;
            case "global-log":
Helper.globalLog(args);

                break;
            case "find":
Helper.find(args);
                break;
            case "status":

                break;
            case "checkout":

                break;
            case "branch":
                File repo8 = new File(".gitlet");
                if (!repo8.exists()) {
                    System.out.println("Not in an initialized "
                            + "gitlet directory.");
                    return;
                } else {
                    if (args.length > 2 || args.length == 1) {
                        System.out.println("Incorrect operands.");
                        return;
                    } else {
                        CommitTree tree = getTree();
                        tree.addBranch(args[1]);
                        writeTree(tree);
                    }
                }
                break;
            case "rm-branch":
                File repo9 = new File(".gitlet");
                if (!repo9.exists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                    return;
                } else {
                    if (args.length > 2 || args.length == 1) {
                        System.out.println("Incorrect operands.");
                        return;
                    } else {
                        CommitTree tree = getTree();
                        tree.removeBranch(args[1]);
                        writeTree(tree);
                    }
                }
                break;
            case "merge":
                File repo10 = new File(".gitlet");
                if (!repo10.exists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                    return;
                } else {
                    if (args.length > 2 || args.length == 1) {
                        System.out.println("Incorrect operands.");
                        return;
                    } else {
                        CommitTree tree = getTree();
                        tree.merge(args[1]);
                        writeTree(tree);
                    }
                }
                break;
            case "commit":
                File repo11 = new File(".gitlet");
                if (!repo11.exists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                    return;
                } else {
                    if (args.length > 2) {
                        System.out.println("Incorrect operands.");
                        return;
                    } else {
                        String commitMSG;
                        if (args.length == 1) {
                            commitMSG = "";
                        } else {
                            commitMSG = args[1];
                        }
                        CommitTree tree = getTree();
                        tree.commit(commitMSG);
                        writeTree(tree);
                    }
                }
                break;
            case "reset":
                File repo12 = new File(".gitlet");
                if (!repo12.exists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                    return;
                } else {
                    if (args.length == 1 || args.length > 2) {
                        System.out.println("Incorrect operands.");
                        return;
                    } else {
                        CommitTree tree = getTree();
                        tree.reset(args[1]);
                        writeTree(tree);
                    }
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                return;
        }
    }

}
