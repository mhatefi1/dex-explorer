package com.apk.signature;

import com.apk.signature.Items.ItemsMethod;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppMethod {

    private static final String print_all = "1";
    private static final String get_method_item_from_hex_address = "2";
    private static final String get_hex_address_from_hex_method_item = "3";
    private static final String get_hex_string_from_string_value = "4";
    private static final String get_hex_method_from_method_item_index = "5";
    private static final String write_methods_to_file = "6";

    public static void main(String[] args) {
        try {
            Scanner myObj = new Scanner(System.in);
            AppUtil util = new AppUtil();
            ItemsMethod item = new ItemsMethod();
            File file = null;

            if (args.length > 0) {
                try {
                    file = util.generateDex(args[1]).get(0);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Util.printRed("Enter file path for second argument or run with no argument");
                    System.exit(0);
                }
            } else {
                System.out.println("Enter file path");
                String path = myObj.nextLine();
                file = new File(path);
            }

            System.out.println(
                    "Choose your operation " + "\n" +
                            "0 to go back" + "\n" +
                            print_all + " print all method;" + "\n" +
                            get_method_item_from_hex_address + " search method by offset; (input format:hex string)" + "\n" +
                            get_hex_address_from_hex_method_item + " get method index and offset by method; (input format:hex string)" + "\n" +
                            get_hex_string_from_string_value + " search method info by method name; (input format:utf8 string)" + "\n" +
                            get_hex_method_from_method_item_index + " search method id by index; (input format:number)" + "\n" +
                            write_methods_to_file + " write dex methods to file");

            String input = myObj.nextLine();

            switch (input) {
                case "0" -> {
                    myObj.close();
                    AppMain.main(args);
                }
                case print_all -> {
                    myObj.close();
                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        HashMap<String, byte[]> header = util.getHeader(bs);
                                        util.getAll(header, bs, item);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case get_method_item_from_hex_address -> {
                    System.out.println("Enter hex address:");
                    String address = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        HashMap<String, byte[]> header = util.getHeader(bs);
                                        String hexString = item.getDataAsHex(header, bs, address);
                                        System.out.println(hexString);
                                        System.out.println(util.hexStringToUTF8(hexString));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case get_hex_address_from_hex_method_item -> {
                    System.out.println("Enter hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        HashMap<String, byte[]> header = util.getHeader(bs);
                                        boolean c = util.getAddressFromHexString(header, bs, s.toUpperCase(), item);
                                        if (!c) {
                                            System.out.println("not found");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case get_hex_string_from_string_value -> {
                    System.out.println("Enter method name : ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        HashMap<String, byte[]> header = util.getHeader(bs);
                                        String hexString = util.stringToHexString(s);
                                        boolean c = util.getAddressFromHexString(header, bs, hexString.toUpperCase(), item);
                                        if (!c) {
                                            System.out.println("not found");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case get_hex_method_from_method_item_index -> {
                    System.out.println("Enter index: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        HashMap<String, byte[]> header = util.getHeader(bs);
                                        String res = util.getHexByIndex(header, bs, Long.parseLong(s), item);
                                        System.out.println(res);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case write_methods_to_file -> {
                    System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        HashMap<String, byte[]> header = util.getHeader(bs);
                                        try {
                                            util.writeToFile(header, bs, file.getName() + "-methods.txt", item, s.equals("1"));
                                            System.out.println("done");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
