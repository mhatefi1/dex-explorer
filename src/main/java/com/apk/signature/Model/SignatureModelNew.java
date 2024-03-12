package com.apk.signature.Model;

import java.util.ArrayList;

public class SignatureModelNew {
    public String name;

    public ArrayList<String> strings;

    public ArrayList<String> manifests;
    public ArrayList<StringModel> stringModels;
    public String flags;

    public SignatureModelNew() {
    }

    public ArrayList<StringModel> getStringModels() {
        return stringModels;
    }

    public void setStringModels(ArrayList<StringModel> stringModels) {
        this.stringModels = stringModels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public ArrayList<String> getManifests() {
        return manifests;
    }

    public void setManifests(ArrayList<String> manifests) {
        this.manifests = manifests;
    }

    public String getStringModelsAsString() {
        StringBuilder res = new StringBuilder();
        ArrayList<StringModel> list = getStringModels();
        for (int i = 0; i < list.size(); i++) {
            res.append(list.get(i).getStringModelAsString());
            if (i != list.size() - 1) {
                res.append(",");
            }
        }
        return res.toString();
    }
}
