package com.apk.signature.Model;

public class DBModel {
    public int id;
    public String name;
    public String permissions;
    public String activities;
    public String services;
    public String receivers;
    public String strings;

    public DBModel(String permissions, String activities, String services, String receivers, String strings) {
        this.permissions = permissions;
        this.activities = activities;
        this.services = services;
        this.receivers = receivers;
        this.strings = strings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getStrings() {
        return strings;
    }

    public void setStrings(String strings) {
        this.strings = strings;
    }

}
