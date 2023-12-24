package com.apk.signature.Model;

import java.util.ArrayList;

public class SignatureModel {
    ManifestModel manifestModel;
    ArrayList<String> strings, methods;

    int start, end;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
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
