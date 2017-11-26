package edu.uncc.mysocialapp.pojo;

import java.io.Serializable;

/**
 * Created by hemva on 18-Nov-17.
 */

public class Friend implements Serializable{

    private String uid;
    private String username;
    private String requestStatus;

    public Friend() {
    }

    public Friend(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
