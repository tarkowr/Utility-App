package com.example.utility.models;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class AppItem {
    private UUID id;
    private String name;
    private int resId;
    private Fragment fragment;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public AppItem(String _name, int _resId, Fragment _fragment){
        this.id = UUID.randomUUID();
        this.name = _name;
        this.resId = _resId;
        this.fragment = _fragment;
    }

    @Override
    public String toString(){
        return getName();
    }
}
