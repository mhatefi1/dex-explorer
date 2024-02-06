package com.apk.signature;

import com.apk.signature.Items.ItemsString;
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

import static com.apk.signature.Util.Util.print;

public class AppString {

    private static final String print_all = "1";
    private static final String get_string_item_from_hex_address = "2";
    private static final String get_hex_address_from_hex_string_item = "3";
    private static final String get_hex_string_from_string_value = "4";
    private static final String get_hex_string_from_string_item_index = "5";
    private static final String write_strings_to_file = "6";

    public static void main(String[] args) {
        try {
            Scanner myObj = new Scanner(System.in);
            AppUtil util = new AppUtil();
            File file = null;
            ItemsString item = new ItemsString();

            if (args.length > 0) {
                try {
                    file = new File(args[1]);
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
                            print_all + " print all strings " + "\n" +
                            get_string_item_from_hex_address + " search utf8 value by string id offset; (input format:hex string)" + "\n" +
                            get_hex_address_from_hex_string_item + " search string index and offset by string hex value; (input format:hex string)" + "\n" +
                            get_hex_string_from_string_value + " search string index and offset by utf8 value; (input format:utf8 string)" + "\n" +
                            get_hex_string_from_string_item_index + " search string id by index; (input format:number)" + "\n" +
                            write_strings_to_file + " write dex strings to file");

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
                case get_string_item_from_hex_address -> {
                    System.out.println("Enter offset as hex:");
                    String hex_address = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                print(entry.getName());
                                try {
                                    InputStream inputStream = zipFile.getInputStream(entry);
                                    byte[] bs = IOUtils.toByteArray(inputStream);

                                    HashMap<String, byte[]> header = util.getHeader(bs);
                                    String utf8 = item.getDataAsUTF8(header, bs, hex_address);
                                    System.out.println("data string:" + utf8);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                case get_hex_address_from_hex_string_item -> {
                    System.out.println("Enter hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                print(entry.getName());
                                try {
                                    InputStream inputStream = zipFile.getInputStream(entry);
                                    byte[] bs = IOUtils.toByteArray(inputStream);

                                    HashMap<String, byte[]> header = util.getHeader(bs);
                                    int c = util.getAddressFromHexString(header, bs, s.toUpperCase(), item, 0, 0);
                                    if (c == -1) {
                                        System.out.println("not found");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                case get_hex_string_from_string_value -> {
                    System.out.println("Enter string : ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                print(entry.getName());
                                try {
                                    InputStream inputStream = zipFile.getInputStream(entry);
                                    byte[] bs = IOUtils.toByteArray(inputStream);

                                    HashMap<String, byte[]> header = util.getHeader(bs);
                                    String hexString = util.stringToHexString(s);
                                    int c = util.getAddressFromHexString(header, bs, hexString.toUpperCase(), item, 0, 0);
                                    if (c == -1) {
                                        System.out.println("not found");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                case get_hex_string_from_string_item_index -> {
                    System.out.println("Enter index: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                print(entry.getName());
                                try {
                                    InputStream inputStream = zipFile.getInputStream(entry);
                                    byte[] bs = IOUtils.toByteArray(inputStream);

                                    HashMap<String, byte[]> header = util.getHeader(bs);
                                    String res = util.getHexByIndex(header, bs, Long.parseLong(s), item);
                                    System.out.println("index [" + s + "]:" + res);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                case write_strings_to_file -> {
                    System.out.println("Enter 1 to compare as utf8 and 0 as hex string: ");
                    String s = myObj.nextLine();
                    myObj.close();
                    System.out.println("waite ...");
                    try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                print(entry.getName());
                                try {
                                    InputStream inputStream = zipFile.getInputStream(entry);
                                    byte[] bs = IOUtils.toByteArray(inputStream);

                                    HashMap<String, byte[]> header = util.getHeader(bs);
                                    try {
                                        util.writeToFile(header, bs, file.getName() + "-strings.txt", item, s.equals("1"));
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
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
