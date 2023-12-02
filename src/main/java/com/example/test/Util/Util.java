package com.example.test.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.test.App;
import org.apache.pdfbox.io.RandomAccessFile;

public class Util {

    public static String TEMP_DEX_PATH = "";

    public static String commonFolder = "C:\\Users\\sedej\\Desktop\\remo-test\\Newfolder";
    private final String TEMP_DEX_FOLDER = "\\amn-temp";

    public static void runDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    }

    public String getWorkingFilePath(File f) {
        /*File f = new File(new File(path).getParent() + TEMP_DEX_FOLDER);
        if (!f.exists()) {
            boolean c = f.mkdir();
            System.out.println(f.getAbsolutePath());
            if (!c) {
                return "";
            }
        }
        return f.getAbsolutePath();*/

        //File f = new File(path);
        if (f.isDirectory())
            return f.getAbsolutePath();
        else
            return f.getParent();
    }

    private String splitNameFromFormat(String s) {
        int dotIndex = s.lastIndexOf(".");
        return s.substring(0, dotIndex);
    }

    public File generateDex(String path) {
        File f = new File(path);
        if (path.endsWith(".apk") || path.endsWith(".zip")) {
            String fileName = f.getName();
            String name = splitNameFromFormat(fileName);
            String folderPath = f.getParent();
            File extractPath = new File(folderPath, name);
            extractPath.mkdir();
            return new ReadZip().readDexFilesFromZip(path, extractPath.getAbsolutePath()).get(0);
        }
        return f;
    }

    public ArrayList<String> getCommonOfArrayList(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> result = new ArrayList<>();
        for (String s : first) {
            for (String s2 : second) {
                if (s.equals(s2)) {
                    result.add(s);
                }
            }
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

    public String hexStringToString(String hexString) {
        byte[] bytes = hexStringToByteArray(hexString);
        String st = new String(bytes, Charset.forName("Cp1252"));
        return st;
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

    public byte[] getBytesOfFile(RandomAccessFile raf, String start, String size) {
        long decimal_size = stringHexToDecimal(size);
        long decimal_start = stringHexToDecimal(start);
        byte[] bytes = new byte[(int) decimal_size];
        try {
            raf.seek(decimal_start);
            raf.read(bytes);

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
                    new ReadZip().readDexFilesFromZip(s, ext.getAbsolutePath());
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
            if (file.getName().endsWith(format)) {
                fileList.add(file);
            } else if (file.isDirectory()) {
                getRecursiveFileListByFormat(fileList, file.getAbsolutePath(), format);
            }
        }
        return fileList;
    }

    public ArrayList<File> getFileListByFormat(String input, String format) {
        ArrayList<File> fileList = new ArrayList<>();
        File f = new File(input);
        File[] listed = f.listFiles();
        assert listed != null;
        for (File file : listed) {
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

    public <T> ArrayList<T> removeDupe(ArrayList<T> list){
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
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
/*
    public void getCommonInFolder(String path, App tClass) {
        try {
            ArrayList<File> fileList = new ArrayList<>();
            extractDex(path);
            ArrayList<File> dexFileList = getRecursiveFileListByFormat(fileList,path, ".dex");
            File fist_file = dexFileList.get(0);
            String fileName = fist_file.getName();
            System.out.println(fileName);
            ArrayList<String> finall = getFromDexAsArray(fist_file,tClass);

            for (int i = 1; i < dexFileList.size(); i++) {
                System.out.println(dexFileList.get(i));
                ArrayList<String> file_Strings = getFromDexAsArray(dexFileList.get(i),tClass);
                finall = getCommonOfArrayList(file_Strings, finall);
            }

            writeArrayToFile(finall, path + "\\" + tClass.common_file_name + ".txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFromDexAsArray(File f, App tClass) {
        ArrayList<String> s = new ArrayList<>();
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            HashMap<String, byte[]> header = getHeader(raf);
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = getDecimalValue(header_ids_size);
            long ids_offset = getDecimalValue(header_ids_off);

            for (int i = 0; i < ids_count; i++) {
                String hex = tClass.getDataAsHex(raf, ids_offset);
                ids_offset = ids_offset + tClass.class_data_size;
                s.add(hex);
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public boolean fileMatch(String path,App tClass) {
        File f = new File(path);
        boolean flag = false;
        try {
            ArrayList<String> finall = getFromDexAsArray(f,tClass);

            String signature_path = "C:\\Users\\sedej\\Desktop\\crack\\crack-me2\\amn-temp\\classes.dex-methods.txt";
            File signature_file = new File(signature_path);
            ArrayList<String> signature = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(signature_file));
            String line = reader.readLine();
            while (line != null) {
                signature.add(line);
                line = reader.readLine();
            }
            reader.close();
            System.out.println(finall.size() + "+" + signature.size());
            flag = contains(finall, signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    boolean contains(ArrayList<String> list, ArrayList<String> subList) {
        boolean allItemsPresent = true;

        for (String item : subList) {
            if (!list.contains(item)) {
                allItemsPresent = false;
                break;
            }
        }
        return allItemsPresent;
    }


    public void writeToFile(HashMap<String, byte[]> header, RandomAccessFile raf, String fileName, App tClass) {
        try {
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = getDecimalValue(header_ids_size);
            long ids_offset = getDecimalValue(header_ids_off);

            File f = new File(Util.TEMP_DEX_PATH + "\\" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));

            for (int i = 0; i < ids_count; i++) {
                String hex = tClass.getDataAsHex(raf, ids_offset);
                System.out.println(hex);
                ids_offset = ids_offset + tClass.class_data_size;
                writer.append(hex);
                writer.append('\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAll(HashMap<String, byte[]> header, RandomAccessFile raf, App tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = getDecimalValue(header_ids_size);
        long ids_offset = getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(raf, ids_offset);
            ids_offset = ids_offset + tClass.class_data_size;
            System.out.println(hex);
        }
    }

    public void getAddressFromHexString(HashMap<String, byte[]> header, RandomAccessFile raf, String s,App tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = getDecimalValue(header_ids_size);
        long ids_offset = getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(raf, ids_offset);
            if (hex.equals(s)) {
                System.out.println("ids_offs: " + decimalToStringHex(ids_offset));
                System.out.println("ids_index: " + i);
                break;
            }
            ids_offset = ids_offset + tClass.class_data_size;
        }
    }

    public String getByIndex(HashMap<String, byte[]> header, RandomAccessFile raf, long index, App tClass) {
        /*byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = getDecimalValue(header_ids_size);
        long ids_offset = getDecimalValue(header_ids_off);

        if (index > ids_count) {
            return "out of index";
        } else {
            for (long i = 0; i < ids_count; i++) {
                String hex = tClass.getClassDataAsHex(raf, ids_offset);
                ids_offset = ids_offset + tClass.class_data_size;
                if (i == index) {
                    return "index [" + i + "]:" + hex;
                }
            }
        }
        return "error";*/
       /* byte[] header_ids_off = header.get("header_string_ids_off");
        long ids_offset = getDecimalValue(header_ids_off);
        ids_offset = index * tClass.class_data_size + ids_offset;
        return tClass.getDataAsHex(raf, ids_offset);
    }
*/
}
