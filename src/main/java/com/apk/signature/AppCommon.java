package com.apk.signature;

import com.apk.signature.Items.ItemsClass;
import com.apk.signature.Items.ItemsMethod;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;

import java.util.Scanner;

public class AppCommon {

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);

        System.out.println("Enter path:");
        Util.commonFolder = myObj.nextLine();

        System.out.println(
                "Choose your operation " + "\n" +
                        "0 to go back" + "\n" +
                        "1 to get common strings" + "\n" +
                        "2 to get common methods" + "\n" +
                        "3 to get common classes" + "\n" +
                        "4 to get common permissions, activities, services and receivers" + "\n" +
                        "5 to get all"
        );

        Util util = new Util();
        AppUtil appUtil = new AppUtil(util);

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
                    appUtil.factorizeInFolder(item, s.equals("1"));
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
                    appUtil.factorizeInFolder(item, s.equals("1"));
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
                    appUtil.factorizeInFolder(item, s.equals("1"));
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
                    appUtil.getCommonInManifest();
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

                ItemsString itemsString = new ItemsString();
                ItemsMethod itemsMethod = new ItemsMethod();
                ItemsClass itemsClass = new ItemsClass();

                try {
                    appUtil.getCommonInManifest();
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    appUtil.factorizeInFolder(itemsString, utf8);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    appUtil.factorizeInFolder(itemsMethod, utf8);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    appUtil.factorizeInFolder(itemsClass, utf8);
                    System.out.println("done");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
