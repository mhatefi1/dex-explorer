package com.example.test;

import com.example.test.Model.ManifestModel;
import com.example.test.Util.ManifestUtil;
import com.example.test.Util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class AppMain {
    private static final String aapt2Path = "C:\\Users\\sedej\\AppData\\Local\\Android\\Sdk\\build-tools\\34.0.0\\aapt2.exe";

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.println(
                "Choose your operation " + "\n" +
                        "1 to string module" + "\n" +
                        "2 to method module" + "\n" +
                        "3 to class module" + "\n" +
                        "4 to extract dex files from apk" + "\n" +
                        "5 to get common activities,services and receivers"
        );

        String input = myObj.nextLine();

        switch (input) {
            case "1" -> {
                AppString.main(args);
                myObj.close();
            }
            case "2" -> {
                AppMethod.main(args);
                myObj.close();
            }
            case "3" -> {
                AppClasses.main(args);
                myObj.close();
            }
            case "4" -> {
                System.out.println("Enter file path: ");
                String s = myObj.nextLine();
                myObj.close();
                System.out.println("waite ...");
                try {
                    Util util = new Util();
                    util.extractDex(s);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "5" -> {
                System.out.println("Enter folder path: ");
                String s = myObj.nextLine();
                myObj.close();
                System.out.println("waite ...");
                try {
                    Util util = new Util();
                    ManifestUtil manifestUtil = new ManifestUtil();

                    ArrayList<File> apk_list = util.getFileListByFormat(s, ".apk");
                    File first_file = apk_list.get(0);
                    String fileName = first_file.getAbsolutePath();
                    System.out.println(fileName);

                    String manifest = manifestUtil.dumpManifest(aapt2Path, first_file.getPath());
                    ManifestModel manifestModel = manifestUtil.matchManifest(manifest);

                    ArrayList<String> activity_list = manifestModel.getActivities();
                    ArrayList<String> service_list = manifestModel.getServices();
                    ArrayList<String> receiver_list = manifestModel.getReceivers();


                    for (int i = 1; i < apk_list.size(); i++) {
                        System.out.println(apk_list.get(i));

                        activity_list = util.removeDupe(activity_list);
                        service_list = util.removeDupe(service_list);
                        receiver_list = util.removeDupe(receiver_list);


                        String manifest_ = manifestUtil.dumpManifest(aapt2Path, apk_list.get(i).getPath());
                        ManifestModel manifestModel_ = manifestUtil.matchManifest(manifest_);

                        ArrayList<String> activity_list_ = manifestModel_.getActivities();
                        ArrayList<String> service_list_ = manifestModel_.getActivities();
                        ArrayList<String> receiver_list_ = manifestModel_.getActivities();

                        activity_list = util.getCommonOfArrayList(activity_list_, activity_list);
                        service_list = util.getCommonOfArrayList(service_list_, service_list);
                        receiver_list = util.getCommonOfArrayList(receiver_list_, receiver_list);
                    }

                    activity_list = util.removeDupe(activity_list);
                    service_list = util.removeDupe(service_list);
                    receiver_list = util.removeDupe(receiver_list);

                    util.writeArrayToFile(activity_list, s + "\\" + "commonActivities" + ".txt");
                    util.writeArrayToFile(service_list, s + "\\" + "commonServices" + ".txt");
                    util.writeArrayToFile(receiver_list, s + "\\" + "commonReceivers" + ".txt");


                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
