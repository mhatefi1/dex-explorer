package com.apk.signature.Util;

import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Util {

    private static final String defaultAapt2Path = "C:\\scanner\\aapt2.exe";
    public static String TEMP_DEX_PATH = "";
    public static String aapt2Path = "";//public static String aapt2Path = "C:\\Users\\sedej\\AppData\\Local\\Android\\Sdk\\build-tools\\34.0.0\\aapt2.exe";
    public static String RESET = "\u001B[0m";
    public static String RED = "\u001B[31m";
    public static String GREEN = "\u001B[32m";
    public static String YELLOW = "\u001B[33m";

    public static void runDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    }

    public static void printRed(String text) {
        //System.out.println(RED + text + RESET);
        System.out.println(text);
    }

    public static void printGreen(String text) {
        //System.out.println(GREEN + text + RESET);
        System.out.println(text);
    }

    public static void printYellow(String text) {
        //System.out.println(YELLOW + text + RESET);
        System.out.println(text);
    }

    public static String setAapt2Path(String path) {
        if (path.isEmpty()) {
            return defaultAapt2Path;
        } else {
            if (path.endsWith("aapt2.exe")) {
                return path;
            } else {
                File f = new File(path);
                File[] listed = f.listFiles();
                assert listed != null;
                for (File file : listed) {
                    if (file.getName().endsWith("aapt2.exe")) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        return "";
    }

    private static File convertInputStreamToFile(InputStream is, String fileName) {
        OutputStream outputStream = null;
        File file = null;
        try {
            file = new File(fileName);
            outputStream = new FileOutputStream(file);

            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public SignatureModel createSignatureModel(String permissions, String activities, String services, String receivers, String strings, int startIndex, int endIndex) {
        String[] permissions_list = permissions.split(",");
        String[] activities_list = activities.split(",");
        String[] service_list = services.split(",");
        String[] receivers_list = receivers.split(",");
        String[] strings_list = strings.split(",");

        ManifestModel manifestModel = new ManifestModel();

        ArrayList<String> permissionArrayList = new ArrayList<>();
        ArrayList<String> activitiesArrayList = new ArrayList<>();
        ArrayList<String> serviceArrayList = new ArrayList<>();
        ArrayList<String> receiversArrayList = new ArrayList<>();

        for (String s : permissions_list) {
            s = hexStringToUTF8(s);
            permissionArrayList.add(s);
        }
        for (String s : activities_list) {
            s = hexStringToUTF8(s);
            activitiesArrayList.add(s);
        }
        for (String s : service_list) {
            s = hexStringToUTF8(s);
            serviceArrayList.add(s);
        }
        for (String s : receivers_list) {
            s = hexStringToUTF8(s);
            receiversArrayList.add(s);
        }

        ArrayList<String> stringsArrayList = new ArrayList<>(Arrays.asList(strings_list));

        manifestModel.setPermission(permissionArrayList);
        manifestModel.setActivities(activitiesArrayList);
        manifestModel.setServices(serviceArrayList);
        manifestModel.setReceivers(receiversArrayList);

        SignatureModel signatureModel = new SignatureModel();

        signatureModel.setManifestModel(manifestModel);
        signatureModel.setStrings(stringsArrayList);
        signatureModel.setStart(startIndex);
        signatureModel.setEnd(endIndex);

        return signatureModel;
    }

    public String getWorkingFilePath(File f) {
        if (f.isDirectory())
            return f.getAbsolutePath();
        else
            return f.getParent();
    }

    public String splitNameFromFormat(String s) {
        try {
            int dotIndex = s.lastIndexOf(".");
            return s.substring(0, dotIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public ArrayList<File> generateDex(String path) {
        File f = new File(path);
        if (path.endsWith(".apk") || path.endsWith(".zip")) {
            String fileName = f.getName();
            String name = splitNameFromFormat(fileName);
            String folderPath = f.getParent();
            File extractPath = new File(folderPath, name);
            extractPath.mkdir();
            return readDexFilesFromZip(path, extractPath.getAbsolutePath());
        }
        ArrayList<File> list = new ArrayList<>();
        list.add(f);
        return list;
    }

    public ArrayList<String> getCommonOfArrayList(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (String s : first) {
                for (String s2 : second) {
                    if (s.equals(s2)) {
                        result.add(s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void writeArrayToFile(ArrayList<String> arrayList, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));

            for (String string : arrayList) {
                writer.append(string);
                writer.append('\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String hexStringToUTF8(String hexString) {
        byte[] bytes = hexStringToByteArray(hexString);
        return new String(bytes, Charset.forName("Cp1252"));
    }

    public byte[] hexStringToByteArray(String hexString) {
        int l = hexString.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public long getDecimalValue(byte[] bytes) {
        String hex = getHexValue(bytes);
        return stringHexToDecimal(hex);
    }

    public String getHexValue(byte[] bytes) {
        byte[] reverse = reverse(bytes);
        return byteToStringHex(reverse);
    }

    public byte[] getBytesOfFile(RandomAccessFile raf, long start, long size) {
        byte[] bytes = new byte[(int) size];
        try {
            raf.seek(start);
            raf.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public byte[] getBytesOfFile(ByteArrayInputStream inputStream, long offset, long size) {
        byte[] bytes = new byte[(int) size];
        try {
            inputStream.mark(0);
            inputStream.skip(offset);
            inputStream.read(bytes);
            inputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public boolean searchInFile(File file, String searchString) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(searchString)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void extractDex(String s) {
        try {
            File file = new File(s);

            if (file.isFile()) {
                String name = splitNameFromFormat(s);
                File ext = new File(name);
                boolean dir = ext.mkdir();
                if (dir) {
                    readDexFilesFromZip(s, ext.getAbsolutePath());
                }
            } else {
                ArrayList<File> apk = getFileListByFormat(s, ".apk");
                for (File f : apk) {
                    extractDex(f.getAbsolutePath());
                }
                ArrayList<File> zip = getFileListByFormat(s, ".zip");
                for (File f : zip) {
                    extractDex(f.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.out.println("failed");
            e.printStackTrace();
        }
    }

    public ArrayList<File> getRecursiveFileListByFormat(ArrayList<File> fileList, String input, String format) {

        File f = new File(input);
        File[] listed = f.listFiles();
        assert listed != null;
        for (File file : listed) {
            if (file.isDirectory()) {
                getRecursiveFileListByFormat(fileList, file.getAbsolutePath(), format);
            } else if (file.getName().endsWith(format)) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public ArrayList<File> getFileListByFormat(String input, String format) {
        ArrayList<File> fileList = new ArrayList<>();
        File f = new File(input);
        File[] listed = f.listFiles();
        if (listed != null) {
            for (File file : listed) {
                if (file.getName().endsWith(format)) {
                    fileList.add(file);
                }
            }
        } else {
            File file = new File(input);
            if (file.getName().endsWith(format)) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public String byteToStringHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public long stringHexToDecimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    public String decimalToStringHex(long value) {
        return Long.toHexString(value);
    }

    public String stringToHexString(String s) {
        return byteToStringHex(s.getBytes());
    }

    public byte[] reverse(byte[] byteArray) {
        byte[] result = new byte[byteArray.length];
        int j = 0;
        for (int i = byteArray.length - 1; i >= 0; i--) {
            result[j] = byteArray[i];
            j++;
        }
        return result;
    }

    public <T> ArrayList<T> removeDupe(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public boolean contains(ArrayList<String> list, ArrayList<String> subList) {
        boolean allItemsPresent = true;

        if (list != null && subList != null) {
            for (String item : subList) {
                if (!list.contains(item)) {
                    allItemsPresent = false;
                    break;
                }
            }
        } else {
            allItemsPresent = false;
        }
        return allItemsPresent;
    }

    public ArrayList<File> readDexFilesFromZip(String zipFilePath, String extractPath) {
        ArrayList<File> list = new ArrayList<>();
        try {
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        list.add(convertInputStreamToFile(inputStream, extractPath + "\\" + entry.getName()));
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return list;
    }

    public String readFile(File file) {
        String line;
        StringBuilder output = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public String[] splitTwoByTwo(String text) {
        int splitUnit = 2;
        String[] splitText = new String[text.length() / splitUnit];
        for (int i = 0, j = 0; i < text.length(); i += splitUnit, j++) {
            splitText[j] = text.substring(i, i + splitUnit);
        }
        return splitText;
    }


    public HashMap<String, byte[]> getHeader(ByteArrayInputStream raf) {

        HashMap<String, byte[]> header = new HashMap<>();

        try {
            byte[] header_magic = getBytesOfFile(raf, 0, 8);
            byte[] header_checksum = getBytesOfFile(raf, 8, 4);
            byte[] header_signature = getBytesOfFile(raf, 12, 20);
            byte[] header_file_size = getBytesOfFile(raf, 32, 4);
            byte[] header_header_size = getBytesOfFile(raf, 36, 4);
            byte[] header_endian_tag = getBytesOfFile(raf, 40, 4);
            byte[] header_link_size = getBytesOfFile(raf, 44, 4);
            byte[] header_link_off = getBytesOfFile(raf, 48, 4);
            byte[] header_map_off = getBytesOfFile(raf, 52, 4);
            byte[] header_string_ids_size = getBytesOfFile(raf, 56, 4);
            byte[] header_string_ids_off = getBytesOfFile(raf, 60, 4);
            byte[] header_type_ids_size = getBytesOfFile(raf, 64, 4);
            byte[] header_type_ids_off = getBytesOfFile(raf, 68, 4);
            byte[] header_proto_ids_size = getBytesOfFile(raf, 72, 4);
            byte[] header_proto_ids_off = getBytesOfFile(raf, 76, 4);
            byte[] header_field_ids_size = getBytesOfFile(raf, 80, 4);
            byte[] header_field_ids_off = getBytesOfFile(raf, 84, 4);
            byte[] header_method_ids_size = getBytesOfFile(raf, 88, 4);
            byte[] header_method_ids_off = getBytesOfFile(raf, 92, 4);
            byte[] header_class_ids_size = getBytesOfFile(raf, 96, 4);
            byte[] header_class_ids_off = getBytesOfFile(raf, 100, 4);
            byte[] header_data_size = getBytesOfFile(raf, 104, 4);
            byte[] header_data_off = getBytesOfFile(raf, 108, 4);

            header.put("header_magic", header_magic);
            header.put("header_checksum", header_checksum);
            header.put("header_signature", header_signature);
            header.put("header_file_size", header_file_size);
            header.put("header_header_size", header_header_size);
            header.put("header_endian_tag", header_endian_tag);
            header.put("header_link_size", header_link_size);
            header.put("header_link_off", header_link_off);
            header.put("header_map_off", header_map_off);
            header.put("header_string_ids_size", header_string_ids_size);
            header.put("header_string_ids_off", header_string_ids_off);
            header.put("header_type_ids_size", header_type_ids_size);
            header.put("header_type_ids_off", header_type_ids_off);
            header.put("header_proto_ids_size", header_proto_ids_size);
            header.put("header_proto_ids_off", header_proto_ids_off);
            header.put("header_field_ids_size", header_field_ids_size);
            header.put("header_field_ids_off", header_field_ids_off);
            header.put("header_method_ids_size", header_method_ids_size);
            header.put("header_method_ids_off", header_method_ids_off);
            header.put("header_class_ids_size", header_class_ids_size);
            header.put("header_class_ids_off", header_class_ids_off);
            header.put("header_data_ids_size", header_data_size);
            header.put("header_data_ids_off", header_data_off);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    public HashMap<String, byte[]> getHeader(RandomAccessFile raf) {

        HashMap<String, byte[]> header = new HashMap<>();

        try {
            byte[] header_magic = getBytesOfFile(raf, 0, 8);
            byte[] header_checksum = getBytesOfFile(raf, 8, 4);
            byte[] header_signature = getBytesOfFile(raf, 12, 20);
            byte[] header_file_size = getBytesOfFile(raf, 32, 4);
            byte[] header_header_size = getBytesOfFile(raf, 36, 4);
            byte[] header_endian_tag = getBytesOfFile(raf, 40, 4);
            byte[] header_link_size = getBytesOfFile(raf, 44, 4);
            byte[] header_link_off = getBytesOfFile(raf, 48, 4);
            byte[] header_map_off = getBytesOfFile(raf, 52, 4);
            byte[] header_string_ids_size = getBytesOfFile(raf, 56, 4);
            byte[] header_string_ids_off = getBytesOfFile(raf, 60, 4);
            byte[] header_type_ids_size = getBytesOfFile(raf, 64, 4);
            byte[] header_type_ids_off = getBytesOfFile(raf, 68, 4);
            byte[] header_proto_ids_size = getBytesOfFile(raf, 72, 4);
            byte[] header_proto_ids_off = getBytesOfFile(raf, 76, 4);
            byte[] header_field_ids_size = getBytesOfFile(raf, 80, 4);
            byte[] header_field_ids_off = getBytesOfFile(raf, 84, 4);
            byte[] header_method_ids_size = getBytesOfFile(raf, 88, 4);
            byte[] header_method_ids_off = getBytesOfFile(raf, 92, 4);
            byte[] header_class_ids_size = getBytesOfFile(raf, 96, 4);
            byte[] header_class_ids_off = getBytesOfFile(raf, 100, 4);
            byte[] header_data_size = getBytesOfFile(raf, 104, 4);
            byte[] header_data_off = getBytesOfFile(raf, 108, 4);

            header.put("header_magic", header_magic);
            header.put("header_checksum", header_checksum);
            header.put("header_signature", header_signature);
            header.put("header_file_size", header_file_size);
            header.put("header_header_size", header_header_size);
            header.put("header_endian_tag", header_endian_tag);
            header.put("header_link_size", header_link_size);
            header.put("header_link_off", header_link_off);
            header.put("header_map_off", header_map_off);
            header.put("header_string_ids_size", header_string_ids_size);
            header.put("header_string_ids_off", header_string_ids_off);
            header.put("header_type_ids_size", header_type_ids_size);
            header.put("header_type_ids_off", header_type_ids_off);
            header.put("header_proto_ids_size", header_proto_ids_size);
            header.put("header_proto_ids_off", header_proto_ids_off);
            header.put("header_field_ids_size", header_field_ids_size);
            header.put("header_field_ids_off", header_field_ids_off);
            header.put("header_method_ids_size", header_method_ids_size);
            header.put("header_method_ids_off", header_method_ids_off);
            header.put("header_class_ids_size", header_class_ids_size);
            header.put("header_class_ids_off", header_class_ids_off);
            header.put("header_data_ids_size", header_data_size);
            header.put("header_data_ids_off", header_data_off);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }
}