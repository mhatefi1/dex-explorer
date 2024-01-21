package com.apk.signature;

import com.apk.signature.ItemB.ItemsClassB;
import com.apk.signature.Items.ItemsClass;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.ItemsRaf.ItemsClassRaf;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppClasses {
    private static final String print_all = "1";
    private static final String get_method_item_from_hex_address = "2";
    private static final String get_hex_address_from_hex_method_item = "3";
    private static final String get_hex_method_from_method_item_index = "4";
    private static final String write_methods_to_file = "5";

    public static void main(String[] args) {
        try {
            Scanner myObj = new Scanner(System.in);
            AppUtil util = new AppUtil();
            ItemsClassB item = new ItemsClassB();
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

                    try {
                        try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                            Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                                    try {
                                        InputStream inputStream = zipFile.getInputStream(entry);
                                        byte[] bs = IOUtils.toByteArray(inputStream);
                                        ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                        HashMap<String, byte[]> header = util.getHeader(stream);

                                        util.getAll(header, stream, item);

                                        stream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    myObj.close();
                }
                case get_method_item_from_hex_address -> {
                    /*System.out.println("Enter hex address:");
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
                                        ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                        HashMap<String, byte[]> header = util.getHeader(stream);

                                        String hexString = item.getClassDataAsHex(stream, address);
                                        String s = item.parseMethodData(header, stream, address);
                                        System.out.println("data hex string:" + hexString);
                                        System.out.println(s);

                                        stream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
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
                                        ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                        HashMap<String, byte[]> header = util.getHeader(stream);

                                        boolean c = util.getAddressFromHexString(header, stream, s.toUpperCase(), item);
                                        if (!c) {
                                            System.out.println("not found");
                                        }

                                        stream.close();
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
                    System.out.println("Enter class index: ");
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
                                        ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                        HashMap<String, byte[]> header = util.getHeader(stream);

                                        String res = util.getHexByIndex(header, stream, Long.parseLong(s), item);
                                        System.out.println(res);

                                        stream.close();
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
                                        ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                        HashMap<String, byte[]> header = util.getHeader(stream);

                                        try {
                                            util.writeToFile(header, stream, file.getName() + "-classes.txt", item, s.equals("1"));
                                            System.out.println("done");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        stream.close();
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
