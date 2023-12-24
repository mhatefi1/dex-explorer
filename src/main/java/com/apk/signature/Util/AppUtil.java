package com.apk.signature.Util;

import com.apk.signature.Items.Item;
import com.apk.signature.Model.ManifestModel;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AppUtil {

    public static final String aapt2Path = "C:\\Users\\sedej\\AppData\\Local\\Android\\Sdk\\build-tools\\34.0.0\\aapt2.exe";
    Util util;

    public AppUtil(Util util) {
        this.util = util;
    }

    public boolean getAddressFromHexStringByteByByte(HashMap<String, byte[]> header, RandomAccessFile raf, String text, Item tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        String[] splitText = util.splitTwoByTwo(text);
        for (int i = 0; i < ids_count; i++) {
            boolean ss = searchByteByByte(raf, ids_offset, splitText);
            if (ss) {
                System.out.println("ids_offs: " + util.decimalToStringHex(ids_offset));
                System.out.println("ids_index: " + i);
                return true;
            }
            ids_offset = ids_offset + tClass.data_size;
        }
        return false;
    }

    public boolean getAddressFromHexStringByteByByteInPeriod(HashMap<String, byte[]> header, RandomAccessFile raf, String text, Item tClass, int periodStartIndex, int periodEndIndex) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        if (periodStartIndex < ids_count) {
            String[] splitText = util.splitTwoByTwo(text);
            if (periodEndIndex > ids_count || periodEndIndex == 0) {
                periodEndIndex = (int) ids_count;
            }
            ids_offset = ids_offset + (long) tClass.data_size * periodStartIndex;
            for (int i = periodStartIndex; i <= periodEndIndex; i++) {
                boolean ss = searchByteByByte(raf, ids_offset, splitText);
                if (ss) {
                    System.out.println("ids_offs: " + util.decimalToStringHex(ids_offset));
                    System.out.println("ids_index: " + i);
                    return true;
                }
                ids_offset = ids_offset + tClass.data_size;
            }
        }
        return false;
    }

    public boolean getAddressFromHexStringByteByByte(HashMap<String, byte[]> header, ByteArrayInputStream stream, String text, Item tClass) {

        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        String[] splitText = util.splitTwoByTwo(text);
        for (int i = 0; i < ids_count; i++) {
            boolean ss = searchByteByByte(stream, ids_offset, splitText);
            if (ss) {
                System.out.println("ids_offs: " + util.decimalToStringHex(ids_offset));
                System.out.println("ids_index: " + i);
                return true;
            }
            ids_offset = ids_offset + tClass.data_size;
        }
        return false;
    }

    public boolean searchByteByByte(RandomAccessFile raf, long start, String[] splitText) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(raf, start, 4);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);
        while (true) {
            byte[] size_in_utf16_b = util.getBytesOfFile(raf, offset, 1);
            long size_in_utf16_l = util.getDecimalValue(size_in_utf16_b);
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }

        for (String s : splitText) {
            byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(raf, offset, 1);
            String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
            if (!hex.equals(s)) {
                return false;
            }
            offset++;
        }

        byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(raf, offset, 1);
        String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
        return hex.equals("00");
    }

    public boolean searchByteByByte(ByteArrayInputStream stream, long start, String[] splitText) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(stream, start, 4);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);
        while (true) {
            byte[] size_in_utf16_b = util.getBytesOfFile(stream, offset, 1);
            long size_in_utf16_l = util.getDecimalValue(size_in_utf16_b);
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }

        for (String s : splitText) {
            byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(stream, offset, 1);
            String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
            if (!hex.equals(s)) {
                return false;
            }
            offset++;
        }

        byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(stream, offset, 1);
        String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
        return hex.equals("00");
    }

    public void getCommonInManifest() {
        System.out.println("***********" + "factorizedManifest" + "***********");
        ManifestUtil manifestUtil = new ManifestUtil();

        ArrayList<File> apk_list = util.getFileListByFormat(Util.commonFolder, ".apk");
        File first_file = apk_list.get(0);
        String fileName = first_file.getAbsolutePath();
        System.out.println(fileName);

        String manifest = manifestUtil.dumpManifest(first_file.getPath());
        ManifestModel manifestModel = manifestUtil.matchManifest(manifest);

        ArrayList<String> permission_list = manifestModel.getPermission();
        ArrayList<String> activity_list = manifestModel.getActivities();
        ArrayList<String> service_list = manifestModel.getServices();
        ArrayList<String> receiver_list = manifestModel.getReceivers();

        for (int i = 1; i < apk_list.size(); i++) {
            System.out.println(apk_list.get(i));

            permission_list = util.removeDupe(permission_list);
            activity_list = util.removeDupe(activity_list);
            service_list = util.removeDupe(service_list);
            receiver_list = util.removeDupe(receiver_list);


            String manifest_ = manifestUtil.dumpManifest(apk_list.get(i).getPath());
            ManifestModel manifestModel_ = manifestUtil.matchManifest(manifest_);

            ArrayList<String> permission_list_ = manifestModel_.getPermission();
            ArrayList<String> activity_list_ = manifestModel_.getActivities();
            ArrayList<String> service_list_ = manifestModel_.getServices();
            ArrayList<String> receiver_list_ = manifestModel_.getReceivers();

            permission_list = util.getCommonOfArrayList(permission_list_, permission_list);
            activity_list = util.getCommonOfArrayList(activity_list_, activity_list);
            service_list = util.getCommonOfArrayList(service_list_, service_list);
            receiver_list = util.getCommonOfArrayList(receiver_list_, receiver_list);
        }

        permission_list = util.removeDupe(permission_list);
        activity_list = util.removeDupe(activity_list);
        service_list = util.removeDupe(service_list);
        receiver_list = util.removeDupe(receiver_list);

        util.writeArrayToFile(permission_list, Util.commonFolder + "\\" + "factorizedPermissions" + ".txt");
        util.writeArrayToFile(activity_list, Util.commonFolder + "\\" + "factorizedActivities" + ".txt");
        util.writeArrayToFile(service_list, Util.commonFolder + "\\" + "factorizedServices" + ".txt");
        util.writeArrayToFile(receiver_list, Util.commonFolder + "\\" + "factorizedReceivers" + ".txt");
    }

    public void factorizeInFolder(Item tClass, boolean utf8) {
        try {
            System.out.println("***********" + tClass.common_file_name + "***********");
            ArrayList<File> fileList = new ArrayList<>();
            util.extractDex(Util.commonFolder);
            ArrayList<File> dexFileList = util.getRecursiveFileListByFormat(fileList, Util.commonFolder, ".dex");
            File first_file = dexFileList.get(0);
            String fileName = first_file.getAbsolutePath();
            System.out.println(fileName);
            ArrayList<String> finall = getFromDexAsArray(first_file, tClass, utf8);
            System.out.println("this items count:" + finall.size());
            for (int i = 1; i < dexFileList.size(); i++) {
                finall = util.removeDupe(finall);
                System.out.println("common items count:" + finall.size());
                System.out.println(dexFileList.get(i));
                ArrayList<String> file_Strings = getFromDexAsArray(dexFileList.get(i), tClass, utf8);
                System.out.println("this items count:" + file_Strings.size());
                finall = util.getCommonOfArrayList(file_Strings, finall);
            }
            finall = util.removeDupe(finall);
            System.out.println("common items count:" + finall.size());
            System.out.println("**********************");
            util.writeArrayToFile(finall, Util.commonFolder + "\\" + tClass.common_file_name + ".txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFromDexAsArray(File f, Item tClass, boolean utf8) {
        ArrayList<String> s = new ArrayList<>();
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            HashMap<String, byte[]> header = util.getHeader(raf);
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = util.getDecimalValue(header_ids_size);
            long ids_offset = util.getDecimalValue(header_ids_off);

            if (utf8) {
                for (int i = 0; i < ids_count; i++) {
                    String data = tClass.getDataAsUTF8(header, raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    s.add(data);
                }
            } else {
                for (int i = 0; i < ids_count; i++) {
                    String data = tClass.getDataAsHex(header, raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    s.add(data);
                }
            }

            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public boolean fileMatch(String path, Item tClass) {
        File f = new File(path);
        boolean flag = false;
        try {
            ArrayList<String> finals = getFromDexAsArray(f, tClass, false);

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
            System.out.println(finals.size() + "+" + signature.size());
            flag = util.contains(finals, signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    public void writeToFile(HashMap<String, byte[]> header, RandomAccessFile raf, String fileName, Item tClass, boolean utf8) {
        try {
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = util.getDecimalValue(header_ids_size);
            long ids_offset = util.getDecimalValue(header_ids_off);

            File f = new File(Util.TEMP_DEX_PATH + "\\" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));

            if (utf8) {
                for (int i = 0; i < ids_count; i++) {
                    String hex = tClass.getDataAsUTF8(header, raf, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    writer.append(hex);
                    writer.append('\n');
                }
            } else {
                for (int i = 0; i < ids_count; i++) {
                    String hex = tClass.getDataAsHex(header, raf, ids_offset);
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

    public void getAll(HashMap<String, byte[]> header, RandomAccessFile raf, Item tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(header, raf, ids_offset);
            ids_offset = ids_offset + tClass.data_size;
            System.out.println(hex);
        }
    }

    public boolean getAddressFromHexString(HashMap<String, byte[]> header, RandomAccessFile raf, String s, Item tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        boolean result = false;
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(header, raf, ids_offset);
            if (hex.equals(s)) {
                System.out.println("ids_offs: " + util.decimalToStringHex(ids_offset));
                System.out.println("ids_index: " + i);
                result = true;
                break;
            }
            ids_offset = ids_offset + tClass.data_size;
        }
        return result;
    }

    public boolean getAddressFromHexStringInPeriod(HashMap<String, byte[]> header, RandomAccessFile raf, String s, Item tClass, int start, int end) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        boolean result = false;
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(header, raf, ids_offset);
            if (hex.equals(s)) {
                System.out.println("ids_offs: " + util.decimalToStringHex(ids_offset));
                System.out.println("ids_index: " + i);
                result = true;
                break;
            }
            ids_offset = ids_offset + tClass.data_size;
        }
        return result;
    }

    public String getByIndex(HashMap<String, byte[]> header, RandomAccessFile raf, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = util.getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsHex(header, raf, ids_offset);
    }

}
