package com.apk.signature.Model;

import java.util.ArrayList;

public class ScanResult {
    public int totalFiles;
    public int totalApk;
    public int totalMalware;
    public int totalSignature;
    public long totalTime;
    ArrayList<MalwareModel> malwareList;

    public ScanResult() {
    }

    public int getTotalSignature() {
        return totalSignature;
    }

    public void setTotalSignature(int totalSignature) {
        this.totalSignature = totalSignature;
    }

    public void print() {

    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getTotalApk() {
        return totalApk;
    }

    public void setTotalApk(int totalApk) {
        this.totalApk = totalApk;
    }

    public int getTotalMalware() {
        return totalMalware;
    }

    public void setTotalMalware(int totalMalware) {
        this.totalMalware = totalMalware;
    }

    public ArrayList<MalwareModel> getMalwareList() {
        return malwareList;
    }

    public void setMalwareList(ArrayList<MalwareModel> malwareList) {
        this.malwareList = malwareList;
    }
}
