package com.example.test;

import com.example.test.Items.ItemsClass;
import com.example.test.Util.AppUtil;
import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class AppClasses {
    private static final String print_all = "1";
    private static final String get_method_item_from_hex_address = "2";
    private static final String get_hex_address_from_hex_method_item = "3";
    private static final String get_hex_method_from_method_item_index = "4";
    private static final String write_methods_to_file = "5";
    private static final String compare_dex_file = "6";
    private static final String match_dex_with_signature = "7";

    public static void main(String[] args){
        try {
            Util util = new Util();
            AppUtil appUtil = new AppUtil(util);
            File dexFile = util.generateDex(args[0]);
            Util.TEMP_DEX_PATH = util.getWorkingFilePath(dexFile);
            RandomAccessFile raf = new RandomAccessFile(dexFile, "r");
            ItemsClass item = new ItemsClass();
            HashMap<String, byte[]> header = util.getHeader(raf);

            Scanner myObj = new Scanner(System.in);
            System.out.println(
                    "Choose your operation " + "\n" +
                            "0 to go back" + "\n" +
                            print_all + " print all classes " + "\n" +
                           // get_method_item_from_hex_address + " print utf-8 class data use hex address " + "\n" +
                            get_hex_address_from_hex_method_item + " print hex address use hex string " + "\n" +
                            get_hex_method_from_method_item_index + " print hex string use index " + "\n" +
                            write_methods_to_file + " write dex classes to file " + "\n" +
                            compare_dex_file + " compare dex files in a folder" + "\n" +
                            match_dex_with_signature + " match dex with signature"
            );

            String input = myObj.nextLine();

            switch (input) {
                case "0" -> {
                    myObj.close();
                    AppMain.main(args);
                }
                case print_all -> {
                    appUtil.getAll(header, raf, item);
                    myObj.close();
                }
                case get_method_item_from_hex_address -> {
                   /* System.out.println("Enter hex address:");
                    String address = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    String hexString = classItems.getClassDataAsHex(raf, address);
                    String s = classItems.parseMethodData(header, raf, address);
                    System.out.println("data hex string:" + hexString);
                    System.out.println(s);*/
                }
                case get_hex_address_from_hex_method_item -> {
                    System.out.println("Enter hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    appUtil.getAddressFromHexString(header, raf, s, item);
                }
                case get_hex_method_from_method_item_index -> {
                    System.out.println("Enter class index: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    String res = appUtil.getByIndex(header, raf, Long.parseLong(s), item);
                    System.out.println(res);
                }
                case write_methods_to_file -> {
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        appUtil.writeToFile(header, raf, dexFile.getName() + "-classes.txt", item);
                        System.out.println("done");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case compare_dex_file -> {
                    System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        appUtil.factorizeInFolder(item,s.equals("1"));
                        System.out.println("done");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case match_dex_with_signature -> {
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        boolean result = appUtil.fileMatch("C:\\Users\\sedej\\Desktop\\crack\\crack-me2\\classes.dex", item);
                        System.out.println("result:" + result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            raf.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
