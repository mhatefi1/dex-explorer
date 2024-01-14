package com.apk.signature.Model;

public class DBModel {
    public int id;
    public String name;
    public String permissions;
    public String activities;
    public String services;
    public String receivers;
    public String strings;
    public int string_start;
    public int string_end;

    public DBModel() {
    }

    public DBModel(int id, String name, String permissions, String activities, String services, String receivers, String strings, int string_start, int string_end) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
        this.activities = activities;
        this.services = services;
        this.receivers = receivers;
        this.strings = strings;
        this.string_start = string_start;
        this.string_end = string_end;
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

    public int getString_start() {
        return string_start;
    }

    public void setString_start(int string_start) {
        this.string_start = string_start;
    }

    public int getString_end() {
        return string_end;
    }

    public void setString_end(int string_end) {
        this.string_end = string_end;
    }
}
