package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class CommitTree implements Serializable {
    // keep track of the name of the removed files
    private static File user = new File(System.getProperty("user.dir"));
    private HashMap<String, String> branches = new HashMap<String, String>();
    // key: branch name   value: hashCode of the head commit of the branch
    private String root;   // points to the hashCode of a CommitNode
    private HashSet<String> allCommits = new HashSet<String>();
    // stores the hashCodes of all CommitNodes
    private HashMap<String, String> stagingArea = new HashMap<String, String>(); // tracks
    private String head; // points to the name of the head branch
    private HashSet<String> removed = new HashSet<String>();


    public CommitTree() {
        CommitNode newNode = new CommitNode(null);
        newNode.commit("initial commit");
        String toSave = FileHelper.objectWriter(newNode);
        root = toSave;
        allCommits.add(toSave);
        head = "master";
        this.branches.put(head, toSave);

    }

    /**
     * Adds a file to the staging area.
     *
     * @param fileName Name of the file to add
     */

    public void add(String fileName) {
        CommitNode prevCommit = (CommitNode) FileHelper.
                objectReader(".gitlet/" + getCommitID(head));
        String fileHash = FileHelper.fileHashID(fileName);
        // Check if the file already exists unmodified in the head commit
        if (prevCommit.getContent().containsKey(fileName)) {
            if (prevCommit.getContent().get(fileName).equals(fileHash)) {
                return;
            }
        } else {
            // Save file to stagingArea and write file to .gitlet
            removed.remove(fileName);
            stagingArea.put(fileName, fileHash);
            FileHelper.fileSaver(fileName, ".gitlet");
            return;
        }
    }

    /**
     * @param fileName
     * @return
     */
    public void remove(String fileName) {
        boolean committed, staged;
        committed = ((CommitNode) FileHelper
                .objectReader(".gitlet/" + (branches.get(head))))
                .getContent().containsKey(fileName);
        staged = stagingArea.containsKey(fileName); //TODO same filename or same content too?
        if (committed || staged) {
            if (committed) {
                File toRemove = new File(fileName);
                if (toRemove.exists()) {
                    toRemove.delete();
                }
                removed.add(fileName); //save to removed, so we can status it
            }
            if (staged) {
                String fileID = stagingArea.get(fileName);
                File toRemove = new File(".gitlet/" + fileID);
                toRemove.delete();
                //Utils.restrictedDelete(stagingArea.get(fileName));
                // should be delete from .gitlet
                stagingArea.remove(fileName);
            }
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * Helper method for branch function in main
     * Adds a branch at the current head commit, then changes the head branch
     * to the newly created one.
     *
     * @param branchName Name of the new branch
     * @return true if branch successfully added, false otherwise
     */
    public void addBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            branches.put(branchName, getCommitID(head));
            head = branchName;
        }
    }

    /**
     * Removes the branch with the given name.
     *
     * @param branchName Name of the branch you want to remove
     * @return true if successfully removed branch
     */
    public void removeBranch(String branchName) {
        if (branchName.equals(head)) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else {
            branches.remove(branchName);
        }
    }

    /**
     * Finds and prints the IDs of all commits with the given message.
     *
     * @param message The message you want to find
     */
    public void find(String message) {
        String toPrint = "";
        for (String commitID : allCommits) {
            String currentMSG = ((CommitNode) FileHelper.
                    objectReader(".gitlet/" + commitID)).getMSG();
            if (currentMSG.equals(message)) {
                toPrint = toPrint + commitID + "\n";
            }
        }
        if (toPrint.equals("")) {
            toPrint = "Found no commit with that message.";
        }
        System.out.println(toPrint);
    }

    /**
     *
     */
    public void status() {
        //Prints the branches
        System.out.println("=== Branches ===");
        for (String branch : branches.keySet()) {
            String toPrint = branch;
            if (branch.equals(head)) {
                toPrint = "*" + toPrint;
            }
            System.out.println(toPrint);
        }
        System.out.println();

        //Prints the staged files
        System.out.println("=== Staged Files ===");
        for (String file : stagingArea.keySet()) {
            System.out.println(file);
        }
        System.out.println();

        //Prints the removed files
        System.out.println("=== Removed Files ===");
        for (String file : removed) {
            File curr = new File(file);
            if (!curr.exists()) {
                System.out.println(file);
            }
        }
        System.out.println();

        //Prints the optional stuff
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
    }

    /**
     * Starting from the current head commit, prints each commit backwards
     * along the commit tree until the initial commit.
     */
    public void log() {
        String currentId = getCommitID(head);
        CommitNode current = (CommitNode) FileHelper
                .objectReader(".gitlet/" + (currentId));
        System.out.println("===");
        System.out.println("Commit " + currentId + "\n" + current);
        System.out.println();
        while (current.getParent() != null) {
            currentId = current.getParent();
            current = (CommitNode) FileHelper
                    .objectReader(".gitlet/" + (currentId));
            System.out.println("===");
            System.out.println("Commit " + currentId + "\n" + current);
            System.out.println();
        }
    }

    /**
     * Prints out all commits ever made.
     */
    public void globalLog() {
        for (String commitId : allCommits) {
            CommitNode current = (CommitNode)
                    FileHelper.objectReader(".gitlet/" + (commitId));
            System.out.println("===");
            System.out.println("Commit " + commitId + "\n" + current);
            System.out.println();
        }
    }

    /**
     * @param fileName
     */
    public void checkout(String fileName) {
        String commitID = getCommitID(head);
        checkout(commitID, fileName);
    }


    /**
     * @param commitID
     * @param fileName
     */
    public void checkout(String commitID, String fileName) {
        if (!allCommits.contains(fileName)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        CommitNode formerNode = (CommitNode) FileHelper.
                objectReader(".gitlet/" + commitID);
        CommitNode currHead = (CommitNode) FileHelper.objectReader(getCommitID(head));
        if (formerNode.containFile(fileName)) {
            if (!currHead.containFile(fileName)) {
                System.out.println("There is an " +
                        "untracked file in the way; delete it or add it first.");
                return;
            }
            try {
                String fileID = formerNode.getContent().get(fileName);
                byte[] toCheck = Utils.readContents(new
                        File(".gitlet/" + fileID));
                File outFile = new File(fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }
                outFile.createNewFile();
                Utils.writeContents(outFile, toCheck);
            } catch (IOException e) {
                return;
            }
        } else {
            System.out.println("File does not exist in that commit.");
            return;
        }
    }

    /**
     * @param branchName
     */
    public void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else if (branchName.equals(head)) {
            System.out.println("No need to checkout the current branch.");
        } else {
            CommitNode currHead = (CommitNode) FileHelper
                    .objectReader(".gitlet/" + (getCommitID(head)));
            List<String> allFile = Utils.plainFilenamesIn(user);
            CommitNode newHead = (CommitNode) FileHelper
                    .objectReader(".gitlet/" + (getCommitID(branchName)));
            for (String fileName : allFile) {
                if (!currHead.containFile(fileName)) {
                    System.out.println("There is an untracked "
                            + "file in the way; delete it or add it first.");
                    return;
                }
            }
            for (String fileName : currHead.getContent().keySet()) {
                try {
                    String fileID = currHead.getContent().get(fileName);
                    byte[] toCheck = Utils.readContents(new
                            File(".gitlet/" + fileID));
                    File outFile = new File(fileName);
                    outFile.createNewFile();
                    Utils.writeContents(outFile, toCheck);
                } catch (IOException e) {
                    return;
                }
                head = branchName;
                stagingArea.clear();
                removed.clear();
            }
        }
    }

    /**
     * @param otherBranch
     */
    public void merge(String otherBranch) {
        if (stagingArea.size() > 0 || removed.size() > 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (!branches.containsKey(otherBranch)) {
            System.out.println(" A branch with that name does not exist.");
            return;
        }
        if (head == otherBranch) {
            System.out.println(" Cannot merge a branch with itself.");
            return;
        }
        List<String> allFile = Utils.plainFilenamesIn(user);
        CommitNode currHead = (CommitNode) FileHelper
                .objectReader(".gitlet/" + (getCommitID(head)));
        CommitNode newHead = (CommitNode) FileHelper
                .objectReader(".gitlet/" + (getCommitID(otherBranch)));
        for (String fileName : allFile) {
            if (!currHead.containFile(fileName)) {
                System.out.println("There is an untracked "
                        + "file in the way; delete it or add it first.");
                return;
            }
        }
    }

    /**
     * @param branchName
     * @return
     */
    public String getCommitID(String branchName) {
        return branches.get(branchName);
    }

    public void commit(String commitMSG) {
        if (commitMSG.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        } else if (stagingArea.isEmpty() && removed.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        } else {
            CommitNode currHead = (CommitNode) FileHelper
                    .objectReader(".gitlet/" + (getCommitID(head)));
            CommitNode newHead = new CommitNode(getCommitID(head));
            HashMap<String, String> currContent = currHead.getContent();
            for (String fileName : currContent.keySet()) {
                if (!removed.contains(fileName)) {
                    newHead.transferContent(fileName, currContent.get(fileName));
                }
            }
            for (String fileName : stagingArea.keySet()) {
                newHead.transferContent(fileName, stagingArea.get(fileName));
            }
            newHead.commit(commitMSG);
            String idOfnewHead = FileHelper.objectWriter(newHead);
            newHead.splitPoints = currHead.splitPoints;
            currHead.addChildren(idOfnewHead);
            if (currHead.getChildren().size() > 1) {
                newHead.splitPoints.put(
                        currHead.getTime(), getCommitID(head));
            }
            branches.put(head, idOfnewHead);
            allCommits.add(idOfnewHead);
            stagingArea.clear();
            removed.clear();
        }
    }

    public void reset(String commitID) {
        if (!allCommits.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        CommitNode currHead = (CommitNode) FileHelper
                .objectReader(".gitlet/" + (getCommitID(head)));
        CommitNode newHead = (CommitNode) FileHelper
                .objectReader(".gitlet/" + (commitID));
        for (String fileName : newHead.getContent().keySet()) {
            File toBeOverridden = new File(fileName);
            if (toBeOverridden.exists()) {
                String hashID = FileHelper.fileHashID(fileName);
                if (!hashID.equals(newHead.getContent().get(fileName))) {
                    if (!currHead.getContent().containsKey(fileName)) {
                        System.out.println("There is an untracked file in the way;"
                                + " delete it or add it first.");
                        return;
                    }
                }
            }
        }
        for (String fileName : currHead.getContent().keySet()) {
            if ((!newHead.getContent().containsKey(fileName))) {
                Utils.restrictedDelete(fileName);
            }
        }
        branches.put(head, commitID);
        stagingArea.clear();
        removed.clear();
    }
}
