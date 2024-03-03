package com.apk.signature;


import com.apk.signature.Items.ItemsClass;
import com.apk.signature.Items.ItemsMethod;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Util.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class AppCommon {

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);

        System.out.println("Enter path:");
        String path = myObj.nextLine();

        System.out.println(
                """
                        Choose your operation\s
                        0 to go back
                        1 to get common strings
                        2 to get common methods
                        3 to get common classes
                        4 to get common permissions, activities, services and receivers
                        5 to get all"""
        );

        AppUtil appUtil = new AppUtil();

        String input = myObj.nextLine();

        switch (input) {
            case "0" -> {
                myObj.close();
                AppMain.main(args);
            }
            case "1" -> {
                ItemsString item = new ItemsString();
                System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                String s = myObj.nextLine();
                myObj.close();
                System.out.println("waite ...");
                try {
                    appUtil.factorizeInFolder(path, item, s.equals("1"));
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "2" -> {
                ItemsMethod item = new ItemsMethod();
                System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                String s = myObj.nextLine();
                myObj.close();
                System.out.println("waite ...");
                try {
                    appUtil.factorizeInFolder(path, item, s.equals("1"));
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "3" -> {
                ItemsClass item = new ItemsClass();
                System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                String s = myObj.nextLine();
                myObj.close();
                System.out.println("waite ...");
                try {
                    appUtil.factorizeInFolder(path, item, s.equals("1"));
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "4" -> {
                myObj.close();
                System.out.println("waite ...");
                try {
                    ArrayList<File> apk_list = new ArrayList<>();
                    apk_list = appUtil.getRecursiveFileListByFormat(apk_list, path, ".apk", true);
                    appUtil.getCommonInManifest(apk_list);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "5" -> {
                System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                String s = myObj.nextLine();
                myObj.close();

                boolean utf8 = s.equals("1");

                System.out.println("waite ...");

                ItemsString itemsString = new ItemsString();
                ItemsMethod itemsMethod = new ItemsMethod();
                ItemsClass itemsClass = new ItemsClass();

                try {
                    ArrayList<File> apk_list = new ArrayList<>();
                    apk_list = appUtil.getRecursiveFileListByFormat(apk_list, path, ".apk", true);
                    appUtil.getCommonInManifest(apk_list);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    appUtil.factorizeInFolder(path, itemsString, utf8);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    appUtil.factorizeInFolder(path, itemsMethod, utf8);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    appUtil.factorizeInFolder(path, itemsClass, utf8);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
