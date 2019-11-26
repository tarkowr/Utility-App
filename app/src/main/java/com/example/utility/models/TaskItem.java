package com.example.utility.models;

import java.util.Date;
import java.util.UUID;

public class TaskItem {
    private UUID id;
    private String title;
    private Date dateCreated;
    private Boolean completed;

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    public TaskItem(String _title){
        this.title = _title;
        this.id = UUID.randomUUID();
        this.dateCreated = new Date();
        this.completed = false;
    }
}
