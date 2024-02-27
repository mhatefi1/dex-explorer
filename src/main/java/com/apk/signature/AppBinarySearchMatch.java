package com.apk.signature;

import com.apk.signature.Model.MalwareModel;
import com.apk.signature.Model.Report;
import com.apk.signature.Model.ScanResult;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.BinaryMatchCore;
import com.apk.signature.Util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static com.apk.signature.Util.Util.*;

public class AppBinarySearchMatch {
    public static void main(String[] args) {
        String signature_path = "";
        String target_path = "";
        ArrayList<File> AllTargetFilePath = new ArrayList<>();
        BinaryMatchCore matchCore = new BinaryMatchCore();
        boolean arg_sig = false, arg_file = false, report = false;

        if (args != null) {
            int len = args.length;
            if (len >= 2) {
                signature_path = args[1];
                arg_sig = !signature_path.isEmpty();
                if (len >= 3) {
                    report = args[2].equalsIgnoreCase("report");
                    if (!report) {
                        target_path = args[2];
                        arg_file = !target_path.isEmpty();
                    }
                    if (len >= 4) {
                        report = args[3].equalsIgnoreCase("report");
                    }
                }
            }
        } else {
            AppBinarySearchMatch.main(new String[]{""});
        }

        Scanner myObj = new Scanner(System.in);
        if (!arg_sig) {
            System.out.println("Enter signature file or folder");
            signature_path = myObj.nextLine();
            if (signature_path.equalsIgnoreCase("exit")) System.exit(0);
        }
        if (!arg_file) {
            System.out.println("Enter target file or folder");
            target_path = myObj.nextLine();
            if (target_path.equalsIgnoreCase("exit")) System.exit(0);
        }

        File fileSignature = new File(signature_path);
        if (!fileSignature.exists()) {
            printRed("signature path doesn't exist");
            //System.exit(0);
            AppBinarySearchMatch.main(new String[]{""});
        }

        File targetFile = new File(target_path);
        if (!targetFile.exists()) {
            printRed("target path doesn't exist");
            //System.exit(0);
            AppBinarySearchMatch.main(new String[]{"", signature_path});

        }

        File file = new File(target_path);
        if (file.getName().endsWith(".txt")) {
            ArrayList<String> filesPath = new Util().readLineByLine(target_path);
            for (String s : filesPath) {
                AllTargetFilePath.add(new File(s));
            }
        } else {
            AllTargetFilePath.add(file);
        }

        Util util = new Util();

        ArrayList<SignatureModel> signatureModels = matchCore.getSigModels(fileSignature);

        /*if (AllTargetFilePath.isEmpty()) {
            AllTargetFilePath.add(targetFile);
        }*/

        long start = System.currentTimeMillis();
        ArrayList<MalwareModel> malwareList = new ArrayList<>();
        for (File f : AllTargetFilePath) {
            ArrayList<File> targetFileList = new ArrayList<>();
            if (f.isDirectory()) {
                targetFileList = util.getRecursiveFileListByFormat(targetFileList, f.getAbsolutePath(), ".apk", true);
            } else {
                targetFileList.add(f);
            }
            ArrayList<MalwareModel> list = matchCore.match(targetFileList, signatureModels);
            malwareList.addAll(list);
        }

        long time = Util.runDuration(start);
        Gson gson = new Gson();
        ScanResult scanResult = new ScanResult();

        scanResult.setTotalApk(matchCore.getTotalApk());
        scanResult.setTotalMalware(malwareList.size());
        scanResult.setTotalUnscannable(matchCore.getUnscannableModel().getSize());
        scanResult.setTotalTime(time);

        String jsonSummarized = gson.toJson(new Report(scanResult));

        if (report) {
            scanResult.setTotalFiles(matchCore.getTotalFiles());
            scanResult.setTotalSignature(signatureModels.size());
            scanResult.setMalwareList(malwareList);
            scanResult.setUnscannable(matchCore.getUnscannableModel());
            String json = gson.toJson(scanResult);
            util.writeToFile(json, System.getProperty("user.dir") + "\\report-" + System.currentTimeMillis() + ".json");
        }
        printGreen(jsonSummarized);
        AppBinarySearchMatch.main(new String[]{"", signature_path, "", report ? "report" : ""});
    }
}
