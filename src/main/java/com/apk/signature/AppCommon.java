package com.apk.signature;


import com.apk.signature.ItemsRaf.ItemsClassRaf;
import com.apk.signature.ItemsRaf.ItemsMethodRaf;
import com.apk.signature.ItemsRaf.ItemsStringRaf;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;

import java.util.Scanner;

public class AppCommon {

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);

        System.out.println("Enter path:");
        String path = myObj.nextLine();

        System.out.println(
                "Choose your operation " + "\n" +
                        "0 to go back" + "\n" +
                        "1 to get common strings" + "\n" +
                        "2 to get common methods" + "\n" +
                        "3 to get common classes" + "\n" +
                        "4 to get common permissions, activities, services and receivers" + "\n" +
                        "5 to get all"
        );

        AppUtil appUtil = new AppUtil();

        String input = myObj.nextLine();

        switch (input) {
            case "0" -> {
                myObj.close();
                AppMain.main(args);
            }
            case "1" -> {
                ItemsStringRaf item = new ItemsStringRaf();
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
                ItemsMethodRaf item = new ItemsMethodRaf();
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
                ItemsClassRaf item = new ItemsClassRaf();
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
                //System.out.println("Enter aapt2 file path (default path is C:\\scanner\\aapt2.exe):");
                //Util.aapt2Path = Util.setAapt2Path(myObj.nextLine());
                Util.aapt2Path = Util.setAapt2Path("");
                myObj.close();
                System.out.println("waite ...");
                try {
                    appUtil.getCommonInManifest(path);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "5" -> {
                System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                String s = myObj.nextLine();

                //System.out.println("Enter aapt2 file path (default path is C:\\scanner\\aapt2.exe):");
                //Util.aapt2Path = Util.setAapt2Path(myObj.nextLine());
                Util.aapt2Path = Util.setAapt2Path("");
                myObj.close();

                boolean utf8 = s.equals("1");

                System.out.println("waite ...");

                ItemsStringRaf itemsString = new ItemsStringRaf();
                ItemsMethodRaf itemsMethod = new ItemsMethodRaf();
                ItemsClassRaf itemsClass = new ItemsClassRaf();

                try {
                    appUtil.getCommonInManifest(path);
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
