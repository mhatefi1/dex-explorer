package com.example.test.Util;

import com.example.test.App;
import com.example.test.Items.ItemsMethod;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AppUtil {

    Util util;

    public AppUtil(Util util) {
        this.util = util;
    }

    public void getCommonInFolder(App tClass) {
        try {
            ArrayList<File> fileList = new ArrayList<>();
            util.extractDex(Util.commonFolder);
            ArrayList<File> dexFileList = util.getRecursiveFileListByFormat(fileList, Util.commonFolder, ".dex");
            File first_file = dexFileList.get(0);
            String fileName = first_file.getAbsolutePath();
            System.out.println(fileName);
            ArrayList<String> finall = getFromDexAsArray(first_file, tClass);

            for (int i = 1; i < dexFileList.size(); i++) {
                System.out.println(finall.size());
                finall = util.removeDupe(finall);
                System.out.println(finall.size());
                System.out.println(dexFileList.get(i));
                ArrayList<String> file_Strings = getFromDexAsArray(dexFileList.get(i), tClass);
                finall = util.getCommonOfArrayList(file_Strings, finall);
            }
            finall = util.removeDupe(finall);
            System.out.println(finall.size());
            util.writeArrayToFile(finall, Util.commonFolder + "\\" + tClass.common_file_name + ".txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFromDexAsArray(File f, App tClass) {
        ArrayList<String> s = new ArrayList<>();
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            HashMap<String, byte[]> header = util.getHeader(raf);
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = util.getDecimalValue(header_ids_size);
            long ids_offset = util.getDecimalValue(header_ids_off);

            if (tClass instanceof ItemsMethod){
                for (int i = 0; i < ids_count; i++) {
                    String hex = ((ItemsMethod) tClass).getParsedMethodDataAsUTF8(header,raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    s.add(hex);
                }
            } else {
                for (int i = 0; i < ids_count; i++) {
                    String hex = tClass.getDataAsHex(raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    s.add(hex);
                }
            }


            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public boolean fileMatch(String path, App tClass) {
        File f = new File(path);
        boolean flag = false;
        try {
            ArrayList<String> finall = getFromDexAsArray(f, tClass);

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
            long ids_count = util.getDecimalValue(header_ids_size);
            long ids_offset = util.getDecimalValue(header_ids_off);

            File f = new File(Util.TEMP_DEX_PATH + "\\" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));

            if (tClass instanceof ItemsMethod){
                for (int i = 0; i < ids_count; i++) {
                    String hex = ((ItemsMethod) tClass).getParsedMethodDataAsUTF8(header,raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    writer.append(hex);
                    writer.append('\n');
                }
            } else {
                for (int i = 0; i < ids_count; i++) {
                    String hex = tClass.getDataAsHex(raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    writer.append(hex);
                    writer.append('\n');
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAll(HashMap<String, byte[]> header, RandomAccessFile raf, App tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(raf, ids_offset);
            ids_offset = ids_offset + tClass.data_size;
            System.out.println(hex);
        }
    }

    public void getAddressFromHexString(HashMap<String, byte[]> header, RandomAccessFile raf, String s, App tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(raf, ids_offset);
            if (hex.equals(s)) {
                System.out.println("ids_offs: " + util.decimalToStringHex(ids_offset));
                System.out.println("ids_index: " + i);
                break;
            }
            ids_offset = ids_offset + tClass.data_size;
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
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = util.getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsHex(raf, ids_offset);
    }

}
