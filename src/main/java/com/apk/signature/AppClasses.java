package com.apk.signature;

import com.apk.signature.Items.ItemsClass;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;
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

    public static void main(String[] args) {
        try {
            Util util = new Util();
            AppUtil appUtil = new AppUtil(util);
            File dexFile = util.generateDex(args[0]).get(0);
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
                            write_methods_to_file + " write dex classes to file");

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
                    boolean c = appUtil.getAddressFromHexString(header, raf, s.toUpperCase(), item);
                    if (!c) {
                        System.out.println("not found");
                    }
                }
                case get_hex_method_from_method_item_index -> {
                    System.out.println("Enter class index: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    String res = appUtil.getHexByIndex(header, raf, Long.parseLong(s), item);
                    System.out.println(res);
                }
                case write_methods_to_file -> {
                    System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        appUtil.writeToFile(header, raf, dexFile.getName() + "-classes.txt", item, s.equals("1"));
                        System.out.println("done");
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
