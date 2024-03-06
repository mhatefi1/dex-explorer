package com.apk.signature.Model;

public class Report {
    int totalApk, totalMalware, totalUnscannable, totalNotMatch;
    long totalTime;

    public Report(ScanResult scanResult) {
        totalApk = scanResult.getTotalApk();
        totalMalware = scanResult.getTotalMalware();
        totalUnscannable = scanResult.getTotalUnscannable();
        totalTime = scanResult.getTotalTime();
        totalNotMatch = scanResult.getTotalNotMatch();
    }
}