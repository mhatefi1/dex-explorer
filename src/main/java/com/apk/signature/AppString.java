package com.apk.signature;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import com.apk.signature.Items.ItemsString;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

public class AppString {

    private static final String print_all = "1";
    private static final String get_string_item_from_hex_address = "2";
    private static final String get_hex_address_from_hex_string_item = "3";
    private static final String get_hex_string_from_string_value = "4";
    private static final String get_hex_string_from_string_item_index = "5";
    private static final String write_strings_to_file = "6";
    private static final String compare_dex_file = "7";
    private static final String match_dex_with_signature = "8";

    public static void main(String[] args) {
        try {
            Util util = new Util();
            AppUtil appUtil = new AppUtil(util);
            File dexFile = util.generateDex(args[0]);
            Util.TEMP_DEX_PATH = util.getWorkingFilePath(dexFile);
            RandomAccessFile raf = new RandomAccessFile(dexFile, "r");
            ItemsString item = new ItemsString();
            HashMap<String, byte[]> header = util.getHeader(raf);

            Scanner myObj = new Scanner(System.in);
            System.out.println(
                    "Choose your operation " + "\n" +
                            "0 to go back" + "\n" +
                            print_all + " print all strings " + "\n" +
                            get_string_item_from_hex_address + " search utf8 value by string id offset; (input format:hex string)" + "\n" +
                            get_hex_address_from_hex_string_item + " search string index and offset by string hex value; (input format:hex string)" + "\n" +
                            get_hex_string_from_string_value + " search string index and offset by utf8 value; (input format:utf8 string)" + "\n" +
                            get_hex_string_from_string_item_index + " search string id by index; (input format:number)" + "\n" +
                            write_strings_to_file + " write dex strings to file " + "\n" +
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
                case get_string_item_from_hex_address -> {
                    System.out.println("Enter offset as hex:");
                    String hex_address = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    String utf8 = item.getDataAsUTF8(header, raf, hex_address);
                    System.out.println("data string:" + utf8);
                }
                case get_hex_address_from_hex_string_item -> {
                    System.out.println("Enter hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    appUtil.getAddressFromHexString(header, raf, s, item);
                }
                case get_hex_string_from_string_value -> {
                    System.out.println("Enter string : ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    item.find(header, raf, s);
                }
                case get_hex_string_from_string_item_index -> {
                    System.out.println("Enter index: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    String res = appUtil.getByIndex(header, raf, Long.parseLong(s), item);
                    System.out.println("index [" + s + "]:" + res);
                }
                case write_strings_to_file -> {
                    System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        appUtil.writeToFile(header, raf, dexFile.getName() + "-strings.txt", item, s.equals("1"));
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
                        appUtil.factorizeInFolder(item, s.equals("1"));
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
