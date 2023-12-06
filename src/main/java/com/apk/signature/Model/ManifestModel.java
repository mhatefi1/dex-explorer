package com.apk.signature.Model;

import java.util.ArrayList;

public class ManifestModel {
    ArrayList<String> permission;
    ArrayList<String> activities;
    ArrayList<String> services;
    ArrayList<String> receivers;

    public ManifestModel() {
    }

    public ArrayList<String> getPermission() {
        return permission;
    }

    public void setPermission(ArrayList<String> permission) {
        this.permission = permission;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<String> activities) {
        this.activities = activities;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public ArrayList<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(ArrayList<String> receivers) {
        this.receivers = receivers;
    }
}

