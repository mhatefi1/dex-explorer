package com.apk.signature.Model;

import java.util.ArrayList;

public class Unscannable {
    public ArrayList<String> not_zip, compress_mode, split_zip, unknown, manifest, dex;

    public Unscannable(ArrayList<String> not_zip, ArrayList<String> compress_mode, ArrayList<String> split_zip, ArrayList<String> unknown, ArrayList<String> manifest, ArrayList<String> dex) {
        this.not_zip = not_zip;
        this.compress_mode = compress_mode;
        this.split_zip = split_zip;
        this.unknown = unknown;
        this.manifest = manifest;
        this.dex = dex;
    }

    public Unscannable() {
    }

    public int getSize() {
        return getNot_zip().size() + getSplit_zip().size() + getCompress_mode().size() + getUnknown().size() + getManifest().size() + getDex().size();
    }

    public ArrayList<String> getNot_zip() {
        return not_zip;
    }

    public void setNot_zip(ArrayList<String> not_zip) {
        this.not_zip = not_zip;
    }

    public ArrayList<String> getCompress_mode() {
        return compress_mode;
    }

    public void setCompress_mode(ArrayList<String> compress_mode) {
        this.compress_mode = compress_mode;
    }

    public ArrayList<String> getSplit_zip() {
        return split_zip;
    }

    public void setSplit_zip(ArrayList<String> split_zip) {
        this.split_zip = split_zip;
    }

    public ArrayList<String> getManifest() {
        return manifest;
    }

    public void setManifest(ArrayList<String> manifest) {
        this.manifest = manifest;
    }

    public ArrayList<String> getDex() {
        return dex;
    }

    public void setDex(ArrayList<String> dex) {
        this.dex = dex;
    }

    public ArrayList<String> getUnknown() {
        return unknown;
    }

    public void setUnknown(ArrayList<String> unknown) {
        this.unknown = unknown;
    }
}
