package edu.uncc.mysocialapp.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hemva on 17-Nov-17.
 */

public class Post implements Serializable {

    private String message;
    private Date time;
    private String uid;

    public Post() {
    }

    public Post(String message, Date time, String uid) {
        this.message = message;
        this.time = time;
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Post{" +
                "message='" + message + '\'' +
                ", time=" + time +
                ", uid='" + uid + '\'' +
                '}';
    }
}
