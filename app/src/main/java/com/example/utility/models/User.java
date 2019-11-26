package com.example.utility.models;

import java.util.Date;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private Date createdDate;

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public User (String _username){
        this.username = _username;
        this.id = UUID.randomUUID();
        this.createdDate = new Date();
    }
}
