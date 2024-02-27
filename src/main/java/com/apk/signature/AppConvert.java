package com.apk.signature;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.Model.DBModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.SignatureUtil;
import com.apk.signature.Util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class AppConvert {

    public static void main(String[] args) {
        String signature_path;
        if (args.length > 0 && args[0].equals("8")) {
            signature_path = args[1];
        } else {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Enter signature file or folder");
            signature_path = myObj.nextLine();
        }

        Util util = new Util();
        File fileSignature = new File(signature_path);
        long start = System.currentTimeMillis();
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt", false);
        System.out.println("**********************************************************");
        SQLiteJDBC jdbc = new SQLiteJDBC(fileSignature.getParent());
        jdbc.createTable();


        for (File file : fileSignatureList) {
            try {
                ArrayList<String> signs = util.readLineByLine(file.getAbsolutePath());
                for (String s : signs) {
                    SignatureModel signatureModel1 = new SignatureUtil().parseSignature(s, false);
                    if (signatureModel1 != null) {
                        jdbc.insert(signatureModel1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*for (File file_j : fileSignatureList) {
            try {
                String signature_txt = util.readFile(file_j);
                DBModel model = new SignatureUtil().parseSignatureAsDB(signature_txt);
                String name = util.splitNameFromFormat(file_j.getName());
                model.setName(name);
                System.out.println("id:" + model.getId() + "-getName:" + model.getName() + "-getPermissions:" + model.getPermissions() +
                        "-getActivities:" + model.getActivities() + "-getServices:" + model.getServices() +
                        "-getReceivers:" + model.getReceivers() + "-getStrings:" + model.getStrings());

                jdbc.insert(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        Util.runDuration(start);
    }
}
