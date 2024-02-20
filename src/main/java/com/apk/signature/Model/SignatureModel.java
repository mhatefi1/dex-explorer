package com.apk.signature.Model;

import java.util.ArrayList;

public class SignatureModel {
    public String name;
    public ManifestModel manifestModel;
    public ArrayList<String> strings;
    public ArrayList<StringModel> stringModels;

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

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }
}
