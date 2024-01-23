package com.apk.signature.Model;

import java.util.ArrayList;

public class SignatureModel {
    ManifestModel manifestModel;
    ArrayList<String> strings, methods;

    //int start, end;

    String name;

    ArrayList<StringModel> stringModels;

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

    public SignatureModel() {
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

    public ArrayList<String> getMethods() {
        return methods;
    }

    public void setMethods(ArrayList<String> methods) {
        this.methods = methods;
    }
}
