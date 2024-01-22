package com.apk.signature;

import com.apk.signature.Util.ManifestUtil;
import com.apk.signature.Util.Util;

import java.util.Scanner;

public class AppMain {

    public static void main(String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "1":
                    AppString.main(args);
                    break;
                case "2":
                    AppMethod.main(args);
                    break;
                case "3":
                    AppClasses.main(args);
                    break;
                case "4":
                    String s4 = args[1];
                    System.out.println("waite ...");
                    new Util().extractDex(s4);
                    System.out.println("done");
                    break;
                case "5":
                    AppCommon.main(args);
                    break;
                case "6":
                    AppMatch.main(args);
                    break;
                case "7":
                    AppGenerateSignature.main(args);
                    break;
                case "8":
                    AppConvert.main(args);
                    break;
                case "9":
                    System.out.println("Enter file path: ");
                    String s9 = args[1];
                    new ManifestUtil().decodeMultipleManifest(s9);
                    break;
            }
        } else {

            Scanner myObj = new Scanner(System.in);
            System.out.println(
                    """
                            Choose your operation\s
                            1 to string module
                            2 to method module
                            3 to class module
                            4 to extract dex files from apk
                            5 to get common files
                            6 to match
                            7 to generate signature
                            8 to convert text to sql
                            9 to decode manifest"""
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
                    new Util().extractDex(s);
                    System.out.println("done");
                }
                case "5" -> {
                    AppCommon.main(args);
                    myObj.close();
                }
                case "6" -> {
                    AppMatch.main(args);
                    myObj.close();
                }
                case "7" -> {
                    AppGenerateSignature.main(args);
                    myObj.close();
                }
                case "8" -> {
                    AppConvert.main(args);
                    myObj.close();
                }
                case "9" -> {
                    System.out.println("Enter file path: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    new ManifestUtil().decodeMultipleManifest(s);
                }
            }
        }
    }
}
