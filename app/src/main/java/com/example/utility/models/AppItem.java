package com.example.utility.models;
import java.util.UUID;

public class AppItem {
    private UUID id;
    private String name;
    private int resId;
    private Class<?> activity;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public AppItem(String _name, int _resId, Class<?> _activity){
        this.id = UUID.randomUUID();
        this.name = _name;
        this.resId = _resId;
        this.activity = _activity;
    }

    @Override
    public String toString(){
        return getName();
    }
}
