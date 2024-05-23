package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    private String treeSha1;
    private String timestamp;
    private String parent;
    private List<String> parents;
    private boolean MergeFlag;
    private Date currentTime;

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public Commit(String message, String parent, String treeSha1) {
        this.parent = parent;
        this.message = message;
        this.treeSha1 = treeSha1;
        this.currentTime = new Date(0);
        this.timestamp = setTheTime(currentTime);
    }

    public void setMergeFlag(boolean mergeFlag) {
        MergeFlag = mergeFlag;
    }

    public boolean isMergeFlag() {
        return MergeFlag;
    }

    public void setTreeSha1(String treeSha1) {
        this.treeSha1 = treeSha1;
    }

    public String getTreeSha1() {
        return treeSha1;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getParent() {
        return parent;
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

    public Commit() {
    }

    public void setParent(String parent) {
        this.parent = parent;
        this.timestamp = setTheTime(new Date());
    }
    /* TODO: fill in the rest of this class. */
}
