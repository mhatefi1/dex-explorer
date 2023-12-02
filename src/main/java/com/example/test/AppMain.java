package com.example.test;

import com.example.test.Util.Util;

import java.util.Scanner;

public class AppMain {
    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.println(
                "Choose your operation " + "\n" +
                        "1 to string module" + "\n" +
                        "2 to method module" + "\n" +
                        "3 to class module" + "\n" +
                        "4 to extract dex files from apk"
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
        }
    }
}
