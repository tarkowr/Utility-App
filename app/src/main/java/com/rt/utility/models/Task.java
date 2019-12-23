package com.rt.utility.models;

import java.util.Date;
import java.util.UUID;

public class Task {
    private UUID id;
    private String name;
    private Boolean completed;
    private Date entered;

    public Task(String _name){
        this.id = UUID.randomUUID();
        this.name = _name;
        completed = false;
        entered = new Date();
    }

    public UUID GetId(){
        return this.id;
    }

    public String GetName(){
        return this.name;
    }

    public Boolean IsCompleted(){
        return this.completed;
    }

    public void ToggleCompleted(){
        this.completed = !this.completed;
    }

    public Date GetEnteredDate(){
        return this.entered;
    }

    @Override
    public String toString() {
        return GetName();
    }
}
