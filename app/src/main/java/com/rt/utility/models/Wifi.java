package com.rt.utility.models;

import android.net.wifi.ScanResult;

public class Wifi {
    private String name;
    private int strength;
    private ScanResult scanResult;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public Wifi(String _name, int _strength, ScanResult _scanResult){
        this.name = _name;
        this.strength = _strength;
        this.scanResult = _scanResult;
    }
}
