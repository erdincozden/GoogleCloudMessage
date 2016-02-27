package com.googlecloudmessage.app.model;

import java.io.Serializable;

/**
 * Created by erdinc on 2/18/16.
 */
public class Message implements Serializable {

    String id,message,createdAt;
    User user;

    public Message() {
    }

    public Message(String id,String message,  String createdAt, User user) {
        this.message = message;
        this.id = id;
        this.createdAt = createdAt;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
