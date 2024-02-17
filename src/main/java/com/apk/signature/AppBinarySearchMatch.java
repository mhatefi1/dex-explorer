package com.apk.signature;

import com.apk.signature.Model.MalwareModel;
import com.apk.signature.Model.ScanResult;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.BinaryMatchCore;
import com.apk.signature.Util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static com.apk.signature.Util.Util.printGreen;
import static com.apk.signature.Util.Util.printRed;

public class AppBinarySearchMatch {
    public static void main(String[] args) {
        String signature_path;
        String target_path;
        ArrayList<File> AllTargetFilePath = new ArrayList<>();
        BinaryMatchCore matchCore = new BinaryMatchCore();
        if (args.length > 0 && args[0].equals(AppMain.SCAN)) {
            signature_path = args[1];
            target_path = args[2];
            File file = new File(target_path);
            if (file.getName().endsWith(".txt")) {
                AllTargetFilePath = matchCore.getArgsFileSignatureList(target_path);
            } else {
                AllTargetFilePath.add(file);
            }
        } else {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Enter signature file or folder");
            signature_path = myObj.nextLine();

            System.out.println("Enter target file or folder");
            target_path = myObj.nextLine();
        }

        File fileSignature = new File(signature_path);
        if (!fileSignature.exists()) {
            printRed("signature path doesn't exist");
            System.exit(0);
        }

        File targetFile = new File(target_path);
        if (!targetFile.exists()) {
            printRed("target path doesn't exist");
            System.exit(0);
        }
        Util.aapt2Path = Util.setAapt2Path("");

        Util util = new Util();

        ArrayList<SignatureModel> signatureModels = matchCore.getSigModels(fileSignature);

        if (AllTargetFilePath.isEmpty()) {
            AllTargetFilePath.add(targetFile);
        }

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
        ScanResult scanResult = new ScanResult();
        scanResult.setTotalFiles(matchCore.getTotalFiles());
        scanResult.setTotalApk(matchCore.getTotalApk());
        scanResult.setTotalSignature(signatureModels.size());
        scanResult.setTotalMalware(malwareList.size());
        scanResult.setTotalUnscannable(matchCore.getUnscannable());
        scanResult.setUnscannableList(matchCore.getUnscannables());
        scanResult.setTotalTime(time);
        scanResult.setMalwareList(malwareList);

        Gson gson = new Gson();
        String json = gson.toJson(scanResult);

        printGreen(json);
        util.writeToFile(json, System.getProperty("user.dir") + "\\report-" + System.currentTimeMillis() + ".json");

    }
}
