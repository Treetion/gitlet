package gitlet;


import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;

public class CommitNode implements Serializable {
    public HashMap<String, String> splitPoints;
    private String parent;
    private HashMap<String, String> content;
    private HashMap<String, String> merges;
    private HashSet<String> children;
    private String commitMSG;
    private LocalDateTime time;

    /**
     * @param parent
     */
    public CommitNode(String parent) {
        this.parent = parent;
        content = new HashMap<String, String>();
        children = new HashSet<String>();
        splitPoints = new HashMap<String, String>();
    }


    /**
     *
     */
    public void setTime() {
        this.time = LocalDateTime.now();
    }

    /**
     * @param child
     */
    public void addChildren(String child) {
        children.add(child);
    }

    /**
     * @param fileName
     * @param repo
     */
    public void add(String fileName, String repo) {
        File newFile = new File(repo + "/" + fileName);
        if (!newFile.exists()) {
            System.out.println("File does not exist");
            return;
        }
        content.put(fileName, FileHelper.objectWriter(newFile));
    }

    /**
     * @param fileName
     */
    public void remove(String fileName) {
        content.remove(fileName);
    }

    /**
     * @return
     */
    public String toString() {
        String result = "";
        DateTimeFormatter formatter = DateTimeFormatter.
                ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeFormatted = this.time;
        result = timeFormatted.format(formatter) + "\n" + commitMSG;
        return result;
    }

    /**
     * @param fileName
     * @param hashID
     * @return
     */
    public boolean contains(String fileName, String hashID) {
        if (content.containsKey(fileName)) {
            return (hashID == content.get(fileName));
        } else {
            return false;
        }
    }

    public String getParent() {
        return parent;
    }

    public HashSet getChildren() {
        return children;
    }

    public String getMSG() {
        return commitMSG;
    }

    public HashMap<String, String> getContent() {
        return content;
    }

    /**
     * @param message
     */
    public void commit(String message) {
        this.commitMSG = message;
        setTime();
    }

    public boolean containFile(String fileName) {
        return content.containsKey(fileName);
    }

    public File fileReturn(String fileName) {
        String hashID = content.get(fileName);
        return (File) FileHelper.objectReader(
                ".gitlet/" + hashID);
    }

    public void transferContent(String fileName, String hashID) {
        content.put(fileName, hashID);
    }

    public String getTime() {
        return time.toString();
    }
}
