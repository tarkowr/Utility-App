package com.rt.utility.models;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class AppItem {
    private UUID id;
    private String name;
    private int resId;
    private int minSdk;
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

    public int getMinSdk() {
        return minSdk;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public AppItem(String _name, int _resId, Integer _minSdk, Fragment _fragment){
        this.id = UUID.randomUUID();
        this.name = _name;
        this.resId = _resId;
        this.fragment = _fragment;

        if (_minSdk == null){
            this.minSdk = 0;
        }
    }

    @Override
    public String toString(){
        return getName();
    }
}
