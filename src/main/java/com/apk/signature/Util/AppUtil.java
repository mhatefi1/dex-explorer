package com.apk.signature.Util;

import com.apk.signature.Items.Item;
import com.apk.signature.Model.ManifestModel;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static com.apk.signature.Util.Util.*;

public class AppUtil {

    Util util;

    public AppUtil(Util util) {
        this.util = util;
    }

    public boolean getAddressFromHexString(HashMap<String, byte[]> header, RandomAccessFile raf, String text, Item tClass) {
        return getAddressFromHexString(header, raf, text, tClass, 0, 0);
    }

    public boolean getAddressFromHexString(HashMap<String, byte[]> header, RandomAccessFile raf, String text, Item tClass, int periodStartIndex, int periodEndIndex) {
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
                boolean ss = tClass.searchDataByte(header, raf, ids_offset, splitText);
                if (ss) {
                    printYellow("{ hex:" + text);
                    printYellow("  ids_offs: " + util.decimalToStringHex(ids_offset));
                    printYellow("  ids_index: " + i + " }");
                    return true;
                }
                ids_offset = ids_offset + tClass.data_size;
            }
        }
        return false;
    }

    public void getCommonInManifest(String path) {
        System.out.println("***********" + "factorizedManifest" + "***********");
        ManifestUtil manifestUtil = new ManifestUtil();

        ArrayList<File> apk_list = util.getFileListByFormat(path, ".apk");
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

        util.writeArrayToFile(permission_list, path + "\\" + "factorizedPermissions" + ".txt");
        util.writeArrayToFile(activity_list, path + "\\" + "factorizedActivities" + ".txt");
        util.writeArrayToFile(service_list, path + "\\" + "factorizedServices" + ".txt");
        util.writeArrayToFile(receiver_list, path + "\\" + "factorizedReceivers" + ".txt");
    }

    public void factorizeInFolder(String path, Item tClass, boolean utf8) {
        try {
            System.out.println("***********" + tClass.common_file_name + "***********");
            ArrayList<File> fileList = new ArrayList<>();
            util.extractDex(path);
            ArrayList<File> dexFileList = util.getRecursiveFileListByFormat(fileList, path, ".dex");
            File first_file = dexFileList.get(0);
            String fileName = first_file.getAbsolutePath();
            System.out.println(fileName);
            ArrayList<String> finall = getFromDexAsArray(first_file, tClass, utf8);
            System.out.println("this item count:" + finall.size());
            for (int i = 1; i < dexFileList.size(); i++) {
                finall = util.removeDupe(finall);
                System.out.println("common items count:" + finall.size());
                System.out.println(dexFileList.get(i));
                ArrayList<String> file_Strings = getFromDexAsArray(dexFileList.get(i), tClass, utf8);
                System.out.println("this item count:" + file_Strings.size());
                finall = util.getCommonOfArrayList(file_Strings, finall);
            }
            finall = util.removeDupe(finall);
            System.out.println("common items count:" + finall.size());
            System.out.println("**********************");
            util.writeArrayToFile(finall, path + "\\" + tClass.common_file_name + ".txt");

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

    public String getHexByIndex(HashMap<String, byte[]> header, RandomAccessFile raf, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = util.getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsHex(header, raf, ids_offset);
    }

    public byte[] getByteByIndex(HashMap<String, byte[]> header, RandomAccessFile raf, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = util.getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsByte(header, raf, ids_offset);
    }

}
