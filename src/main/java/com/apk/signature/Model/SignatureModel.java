package com.apk.signature.Model;

import java.util.ArrayList;

public class SignatureModel {
    public String name;
    public ManifestModel manifestModel;
    public ArrayList<String> strings;
    public ArrayList<StringModel> stringModels;

    public ArrayList<String> manifests;

    public String flags;

    public SignatureModel() {
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

    public ManifestModel getManifestModel() {
        return manifestModel;
    }

    public void setManifestModel(ManifestModel manifestModel) {
        this.manifestModel = manifestModel;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public ArrayList<String> getManifests() {
        return manifests;
    }

    public void setManifests(ArrayList<String> manifests) {
        this.manifests = manifests;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
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
