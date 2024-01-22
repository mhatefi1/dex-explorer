package com.apk.signature;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.Model.DBModel;
import com.apk.signature.Util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppConvert {

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter signature file or folder");
        String signature_path = myObj.nextLine();
        Util.aapt2Path = Util.setAapt2Path("");
        Util util = new Util();
        File fileSignature = new File(signature_path);
        long start = System.currentTimeMillis();
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt", false);
        System.out.println("**********************************************************");
        SQLiteJDBC jdbc = new SQLiteJDBC(signature_path);
        jdbc.createTable();
        for (File file_j : fileSignatureList) {
            try {
                String signature_txt = util.readFile(file_j);
                DBModel model = new AppConvert().parseSignature(signature_txt);
                String name = util.splitNameFromFormat(file_j.getName());
                model.setName(name);
                System.out.println("id:" + model.getId() + "-getName:" + model.getName() + "-getPermissions:" + model.getPermissions() +
                        "-getActivities:" + model.getActivities() + "-getServices:" + model.getServices() +
                        "-getReceivers:" + model.getReceivers() + "-getStrings:" + model.getStrings() +
                        "-getString_start:" + model.getString_start() + "-getString_end:" + model.getString_end());

                jdbc.insert(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Util.runDuration(start);
    }

    public DBModel parseSignature(String signature) {
        DBModel model = new DBModel();
        String regex = "!(.*)@(.*)#(.*)\\$(.*)%(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(signature);

        if (matcher.find()) {
            int startIndex;
            int endIndex;
            String permissions = matcher.group(1);
            String activities = matcher.group(2);
            String service = matcher.group(3);
            String receivers = matcher.group(4);
            String strings = matcher.group(5);
            model.setPermissions(permissions);
            model.setActivities(activities);
            model.setServices(service);
            model.setReceivers(receivers);
            if (strings.contains("[")) {
                String reg = "(.+)\\[(.+)-(.+)]";
                Pattern pattern1 = Pattern.compile(reg);
                Matcher matcher1 = pattern1.matcher(strings);
                if (matcher1.find()) {
                    strings = matcher1.group(1);
                    startIndex = Integer.parseInt(matcher1.group(2));
                    endIndex = Integer.parseInt(matcher1.group(3));
                    model.setStrings(strings);
                    model.setString_start(startIndex);
                    model.setString_end(endIndex);
                }
            } else {
                model.setStrings(strings);
            }

            return model;
        }
        return null;
    }
}
