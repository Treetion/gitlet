package gitlet;


import java.io.*;


public class FileHelper {
    private static File user = new File(System.getProperty("user.dir"));

    public static String objectWriter(Object obj) {
        try {
            byte[] toCheck = serialize(obj);
            String hashID = Utils.sha1(toCheck);
            File outfile = new File(".gitlet/" + hashID);
            outfile.createNewFile();
            Utils.writeContents(outfile, toCheck);
            return hashID;
        } catch (IOException e) {
            return null;
        }
    }

    public static void treeWriter(Object obj) {
        File outfile = new File(".gitlet/mainTree");
        byte[] toCheck = serialize(obj);
        Utils.writeContents(outfile, toCheck);
    }

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw new Error("Internal error serializing commit.");
        }
    }

    /**
     * @param fileName
     * @return
     */
    public static Object objectReader(String fileName) {
        Object obj;
        File inFile = new File(fileName);
        try {
            ObjectInputStream inp = new ObjectInputStream(
                    new FileInputStream(inFile));
            obj = inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;
    }

    /**
     * @param fileName
     * @param repo
     * @return
     */
    public static String fileSaver(String fileName, String repo) {
        try {
            byte[] toCheck = Utils.readContents(new File(fileName));
            String hashID = Utils.sha1(toCheck);
            File outFile = new File(repo + "/" + hashID);
            outFile.createNewFile();
            Utils.writeContents(outFile, toCheck);
            return hashID;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param fileName
     * @return
     */
    public static String fileHashID(String fileName) {
        File toRead = new File(fileName);
        return Utils.sha1(Utils.readContents(toRead));
    }
}
