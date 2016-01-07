package com.marcusjacobsson.vault.pojos;

/**
 * Created by Marcus Jacobsson on 2015-08-04.
 */
public class PasswordResetRequest {

    private String id;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
