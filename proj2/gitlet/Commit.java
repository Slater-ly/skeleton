package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     * lalala
     */
    /**
     * The message of this Commit.
     */
    private String message;
    private String timestamp;
    private List<String> mergeParent = new ArrayList<>();
    private String currentBranchName;
    private List<String> parents = new ArrayList<>();
    private String commitId;

    private HashMap<String, String> tree = new HashMap<>();
    private boolean MergeFlag;
    private Date currentTime;

    public Commit() {
    }

    public List<String> getMergeParent() {
        return mergeParent;
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }

    public void setCurrentBranchName(String currentBranchName) {
        this.currentBranchName = currentBranchName;
    }

    public void setMergeParent(String mergeParent) {
        this.mergeParent.add(mergeParent);
    }

    public HashMap<String, String> getfileToFileContent() {
        return tree;
    }
    public void setTree(HashMap<String, String> tree) {
        this.tree = tree;
    }
    public void setCommitId(){
        this.commitId = Utils.sha1(this.message, this.timestamp, new Random().ints(10, 0, 10).mapToObj(Integer::toString).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString());
    }
    public String getCommitId(){
        return this.commitId;
    }


    public List<String> getParents() {
        return parents;
    }


    public Commit(String message) {
        this.message = message;
        this.currentTime = new Date(0);
        this.timestamp = setTheTime(currentTime);
    }

    public void setMergeFlag(boolean mergeFlag) {
        MergeFlag = mergeFlag;
    }

    public boolean isMergeFlag() {
        return MergeFlag;
    }


    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }


    private String setTheTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("-08:00"));
        return sdf.format(date);
    }
//    private static String dateToTimeStamp(Date date) {
//        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
//        return dateFormat.format(date);
//    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setParents(){
        List<String> parents = Repository.getLatestCommit().getParents();
        parents.add(this.commitId);
        this.parents = parents;
    }
    public void setParens(){
        this.parents.add(this.commitId);
    }

    public HashMap<String, String> getTree() {
        return this.tree;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    /* TODO: fill in the rest of this class. */
}
