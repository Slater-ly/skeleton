package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TimeZone;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /**
     * The message of this Commit.
     */
    private String message;
    private String treeSha1;
    private String timestamp;
    private String parent;

    public Commit(String message, String parent, String treeSha1) {
        this.parent = parent;
        this.message = message;
        this.treeSha1 = treeSha1;
        this.timestamp = setTheTime();


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

    private String setTheTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss zzz, EEEE, d MMMM yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(new Date());
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Commit() {
    }

    public void setParent(String parent) {
        this.parent = parent;
        this.timestamp = setTheTime();
    }
    /* TODO: fill in the rest of this class. */
}
