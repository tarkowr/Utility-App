package com.example.utility.models;

import java.util.Date;
import java.util.UUID;

public class User {
    private UUID uuid;
    private String username;
    private Date createdDate;

    public UUID getId() {
        return this.uuid;
    }

    public void setId(UUID _id){
        if (this.uuid == null){
            this.uuid = _id;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date _createdDate){
        this.createdDate = _createdDate;
    }

    public User(UUID _id){
        setId(_id);
    }

    public User (String _username){
        this.username = _username;
        this.uuid = UUID.randomUUID();
        this.createdDate = new Date();
    }
}
