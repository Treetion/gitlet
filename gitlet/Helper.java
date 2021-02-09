package gitlet;

import java.io.File;

public class Helper {
    private static File user = new File(System.getProperty("user.dir"));

    public static void init(String... args) {
        if (args.length == 1) {
            Main.initize();
        } else {
            System.out.println("Incorrect operands.");
            return;
        }

    }

    public static void add(String... args) {
        File repo1 = new File(".gitlet");
        if (!repo1.exists()) {
            System.out.println("Not in an "
                    + "initialized gitlet directory.");
            return;
        } else {
            if (args.length > 2 || args.length == 1) {
                System.out.println("Incorrect operands.");
                return;
            }
            CommitTree tree = Main.getTree();
            File toCheck = new File(user, args[1]);
            if (!toCheck.exists()) {
                System.out.println("File does not exist.");
                return;
            } else {
                tree.add(args[1]);
                Main.writeTree(tree);
            }
        }
    }

    public static void rm(String... args) {
        File repo2 = new File(".gitlet");
        if (!repo2.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        } else {
            if (args.length > 2 || args.length == 1) {
                System.out.println("Incorrect operands.");
                return;
            } else {
                CommitTree tree = Main.getTree();
                tree.remove(args[1]);
                Main.writeTree(tree);
            }
        }
    }

    public static void log(String... args) {
        File repo3 = new File(".gitlet");
        if (!repo3.exists()) {
            System.out.println("Not in an "
                    + "initialized gitlet directory.");
            return;
        } else {
            if (args.length > 1) {
                System.out.println("Incorrect operands.");
                return;
            } else {
                CommitTree tree = Main.getTree();
                tree.log();
            }
        }
    }

    public static void globalLog(String... args) {
        File repo4 = new File(".gitlet");
        if (!repo4.exists()) {
            System.out.println("Not in an "
                    + "initialized gitlet directory.");
            return;
        } else {
            if (args.length > 1) {
                System.out.println("Incorrect operands.");
                return;
            } else {
                CommitTree tree = Main.getTree();
                tree.globalLog();
            }
        }
    }

    public static void find(String... args) {
        File repo5 = new File(".gitlet");
        if (!repo5.exists()) {
            System.out.println("Not in an "
                    + "initialized gitlet directory.");
            return;
        } else {
            if (args.length > 2 || args.length == 1) {
                System.out.println("Incorrect operands.");
                return;
            } else {
                CommitTree tree = Main.getTree();
                tree.find(args[1]);
            }
        }
    }

    public static void status(String... args) {
        File repo6 = new File(".gitlet");
        if (!repo6.exists()) {
            System.out.println("Not in an "
                    + "initialized gitlet directory.");
            return;
        } else {
            if (args.length > 1) {
                System.out.println("Incorrect operands.");
                return;
            } else {
                CommitTree tree = Main.getTree();
                tree.status();
            }
        }

    }

    public static void checkout(String... args) {
        File repo7 = new File(".gitlet");
        if (!repo7.exists()) {
            System.out.println("Not in an "
                    + "initialized gitlet directory.");
            return;
        } else {
            if (args.length == 2) {
                CommitTree tree = Main.getTree();
                tree.checkoutBranch(args[1]);
                Main.writeTree(tree);
            } else if (args.length == 3 && args[1].equals("--")) {
                CommitTree tree = Main.getTree();
                tree.checkout(args[2]);
                Main.writeTree(tree);
            } else if (args.length == 4 && args[2].equals("--")) {
                CommitTree tree = Main.getTree();
                tree.checkout(args[1], args[3]);
                Main.writeTree(tree);
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }
}