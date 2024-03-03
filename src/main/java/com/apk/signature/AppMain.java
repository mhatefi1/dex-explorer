package com.apk.signature;

import com.apk.signature.Util.FIndInterval;
import com.apk.signature.Util.ManifestUtil;
import com.apk.signature.Util.Util;
import org.fusesource.jansi.AnsiConsole;

import java.util.Scanner;

public class AppMain {

    public static final String STRING_EXPLORER = "1";
    public static final String METHOD_EXPLORER = "2";
    public static final String CLASS_EXPLORER = "3";
    public static final String EXTRACT_DEX = "4";
    public static final String FACTORIZING = "5";
    public static final String SCAN = "6";
    public static final String GENERATE_SIGNATURE = "7";
    public static final String CONVERT_SIGNATURES = "8";
    public static final String DECODE_MANIFEST = "9";
    public static final String GET_STRING_INDEX_INTERVAL = "10";

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        if (args.length > 0) {
            switch (args[0]) {
                case STRING_EXPLORER:
                    AppString.main(args);
                    break;
                case METHOD_EXPLORER:
                    AppMethod.main(args);
                    break;
                case CLASS_EXPLORER:
                    AppClasses.main(args);
                    break;
                case EXTRACT_DEX:
                    String s4 = args[1];
                    System.out.println("waite ...");
                    new Util().extractDex(s4);
                    System.out.println("done");
                    break;
                case FACTORIZING:
                    AppCommon.main(args);
                    break;
                case SCAN:
                    AppBinarySearchMatch.main(args);
                    break;
                case GENERATE_SIGNATURE:
                    AppGenerateSignature.main(args);
                    break;
                case CONVERT_SIGNATURES:
                    AppConvert.main(args);
                    break;
                case DECODE_MANIFEST:
                    String s9 = args[1];
                    new ManifestUtil().decodeMultipleManifest(s9);
                    break;
                case GET_STRING_INDEX_INTERVAL:
                    String s10 = args[1];
                    String path = args[2];
                    new FIndInterval().find(s10, path);
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
                            9 to decode manifest
                            10 to find a string min and max index"""
            );

            String input = myObj.nextLine();

            switch (input) {
                case STRING_EXPLORER -> {
                    AppString.main(args);
                    myObj.close();
                }
                case METHOD_EXPLORER -> {
                    AppMethod.main(args);
                    myObj.close();
                }
                case CLASS_EXPLORER -> {
                    AppClasses.main(args);
                    myObj.close();
                }
                case EXTRACT_DEX -> {
                    System.out.println("Enter file path: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    new Util().extractDex(s);
                    System.out.println("done");
                }
                case FACTORIZING -> {
                    AppCommon.main(args);
                    myObj.close();
                }
                case SCAN -> {
                    AppBinarySearchMatch.main(args);
                    myObj.close();
                }
                case GENERATE_SIGNATURE -> {
                    AppGenerateSignature.main(args);
                    myObj.close();
                }
                case CONVERT_SIGNATURES -> {
                    AppConvert.main(args);
                    myObj.close();
                }
                case DECODE_MANIFEST -> {
                    System.out.println("Enter file path: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    new ManifestUtil().decodeMultipleManifest(s);
                }
                case GET_STRING_INDEX_INTERVAL -> {
                    System.out.println("Enter string: ");
                    String s = myObj.nextLine();
                    System.out.println("Enter file path: ");
                    String path = myObj.nextLine();
                    myObj.close();
                    new FIndInterval().find(s, path);
                }
            }
        }
    }
}
