package com.apk.signature;

import com.apk.signature.Items.ItemsString;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.BinaryMatchCore;
import com.apk.signature.Util.Util;
import fr.xgouchet.axml.customized.Attribute;
import net.lingala.zip4j.model.FileHeader;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.print;

public class AppZip {

    public static void main(String[] args) {
        String input = getInput(args);
        //extractDex(input);
        changeSig("C:\\Users\\sedej\\Desktop\\ms");
    }

    public static void changeSig(String s) {
        try {
            Util util = new AppUtil();
            ArrayList<File> apk = util.getFileListByFormat(s, ".txt", false);
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\sedej\\Desktop\\ms\\res.txt", false));
            for (File file1 : apk) {
                String d = util.readFile(file1);
                writer.append(util.splitNameFromFormat(file1.getName()));
                writer.append(d);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractDex(String s) {
        try {
            Util util = new AppUtil();
            File file = new File(s);
            if (file.isDirectory()) {
                ArrayList<File> apk = util.getFileListByFormat(s, ".apk", true);
                ArrayList<File> zip = util.getFileListByFormat(s, ".zip", true);
                apk.addAll(zip);
                for (File f : apk) {
                    extractDex(f.getAbsolutePath());
                }
            } else {
                String name = util.splitNameFromFormat(s);
                File ext = new File(name + "-");
                print("**********" + s + "**********");
                //readDexFilesFromZip(s);
                //dd(s);
                new AppZip().extractWithZipInputStream(new File(s));
                //new AppZip().extractWithZipInputStream22(new File(s));
            }
        } catch (Exception e) {
            System.out.println("failed");
            e.printStackTrace();
        }
    }

    private static ArrayList<File> readDexFilesFromZip(String zipFilePath) {
        ArrayList<File> list = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                    //print(entry.getName() + "---entry.getMethod():" + entry.getMethod());
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            //print(zipFilePath + ":" + exception.getMessage());
        }
        return list;
    }

    private static String getInput(String[] args) {
        String s4;
        if (args.length > 0) {
            s4 = args[0];
        } else {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Enter file path: ");
            s4 = myObj.nextLine();
            myObj.close();
        }
        return s4;
    }

    public void dfg(String w) {
        String[] w_rep = w.split(";");
        int i = 0;
        for (String s : w_rep) {
            print("i:" + i + "--s:" + s);
            i++;
        }
    }

    /*public void extractWithZipInputStream(File file) {
        LocalFileHeader localFileHeader;
        int readLen;
        byte[] readBuffer = new byte[4096];
        try {
            InputStream inputStream = new FileInputStream(file);
            try (net.lingala.zip4j.io.inputstream.ZipInputStream zipInputStream = new net.lingala.zip4j.io.inputstream.ZipInputStream(inputStream)) {
                while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                    File extractedFile = new File(localFileHeader.getFileName());
                    if (!localFileHeader.isDirectory() && localFileHeader.getFileName().endsWith(".dex")) {
                        try {
                           try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
                               while ((readLen = zipInputStream.read(readBuffer)) != -1) {
                                   outputStream.write(readBuffer, 0, readLen);
                               }
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
    }*/

    public void extractWithZipInputStream(File file) {

        try {
            try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file)) {
                List<FileHeader> fileHeaders = zipFile.getFileHeaders();
                fileHeaders.forEach(fileHeader -> {
                            String name = fileHeader.getFileName();
                            if (name.endsWith(".dex")) {
                                try {
                                    int readLen;
                                    byte[] readBuffer = new byte[4096];
                                    File extractedFile = new File(System.currentTimeMillis() + ".dex");
                                    InputStream inputStream = zipFile.getInputStream(fileHeader);
                                    try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
                                        while ((readLen = inputStream.read(readBuffer)) != -1) {
                                            outputStream.write(readBuffer, 0, readLen);
                                        }
                                    }
                                    inputStream.close();
                                } catch (Exception e) {
                                    print(e.getMessage());
                                }

                            }
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractWithZipInputStream22(File file) {

        try {
            // try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file)) {
            net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file);
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            fileHeaders.forEach(fileHeader -> {
                        String name = fileHeader.getFileName();
                        if (name.endsWith(".dex")) {
                            try {

                                InputStream inputStream = zipFile.getInputStream(fileHeader);
                                byte[] stream = IOUtils.toByteArray(inputStream);
                                inputStream.close();
                                print(name + "--size:" + stream.length);
                                //   boolean check = check(stream);
                            } catch (Exception e) {
                                print(e.getMessage());
                            }

                        } else if (name.equals("AndroidManifest.xml")) {
                                /*ArrayList<String> permission_list = new ArrayList<>();
                                ArrayList<String> activity_list = new ArrayList<>();
                                ArrayList<String> service_list = new ArrayList<>();
                                ArrayList<String> receiver_list = new ArrayList<>();
                                try {
                                    InputStream inputStream = zipFile.getInputStream(fileHeader);
                                    byte[] bs = IOUtils.toByteArray(inputStream);
                                    inputStream.close();
                                    boolean success = new CompressedXmlParser().parse(bs, (uri, localName, qName, attrs) -> {
                                        switch (localName) {
                                            case "uses-permission":
                                            case "permission":
                                                setAttribute(attrs, permission_list);
                                                break;
                                            case "activity":
                                                setAttribute(attrs, activity_list);
                                                break;
                                            case "service":
                                                setAttribute(attrs, service_list);
                                                break;
                                            case "receiver":
                                                setAttribute(attrs, receiver_list);
                                                break;
                                        }
                                    });
                                } catch (IndexOutOfBoundsException e) {
                                    //print(e.getMessage());
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                        }
                    }
            );
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAttribute(Attribute[] atts, ArrayList<String> list) {
        if (atts != null) {
            for (Attribute att : atts) {
                if (att.getName().equals("name") || att.getName().isEmpty()) {
                    list.add(att.getValue());
                    //print(att.getValue());
                    break;
                }
            }
        }
    }


}
