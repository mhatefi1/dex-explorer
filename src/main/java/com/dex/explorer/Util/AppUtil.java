package com.dex.explorer.Util;

import com.dex.explorer.Items.Item;
import com.dex.explorer.Items.ItemsClass;
import com.dex.explorer.Items.ItemsMethod;
import com.dex.explorer.Items.ItemsString;
import com.dex.explorer.Model.SearchResultModel;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppUtil extends Util {
    public AppUtil() {

    }

    public SearchResultModel getAddressFromHexString(HashMap<String, byte[]> header, byte[] stream, String text, Item tClass, int periodStartIndex, int periodEndIndex) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = getDecimalValue(header_ids_size);
        long ids_offset = getDecimalValue(header_ids_off);
        if (periodStartIndex < ids_count) {
            String[] splitText = splitTwoByTwo(text);
            if (periodEndIndex > ids_count || periodEndIndex == 0) {
                periodEndIndex = (int) ids_count;
            }
            ids_offset = ids_offset + (long) tClass.data_size * periodStartIndex;
            for (int i = periodStartIndex; i <= periodEndIndex; i++) {
                boolean ss = tClass.searchDataByte(header, stream, ids_offset, splitText);
                if (ss) {
                    String type;
                    if (tClass instanceof ItemsString)
                        type = "string";
                    else if (tClass instanceof ItemsMethod)
                        type = "method";
                    else if (tClass instanceof ItemsClass)
                        type = "class";
                    else
                        type = "unknown";
                    return new SearchResultModel(type, text, Util.hexStringToUTF8(text), decimalToStringHex(ids_offset), i);
                }
                ids_offset = ids_offset + tClass.data_size;
            }
        }
        return null;
    }

    public void factorizeInFolder(String path, Item tClass, boolean utf8) {
        try {
            print("***********" + tClass.common_file_name + "***********");
            ArrayList<File> temp = new ArrayList<>();
            ArrayList<File> apkFileList = super.getRecursiveFileListByFormat(temp, path, ".apk", true);
            File first_file = apkFileList.get(0);
            ArrayList<String> finall = getFromDexAsArray(first_file, tClass, utf8);
            print("this item count:" + finall.size());
            for (int i = 1; i < apkFileList.size(); i++) {
                finall = super.removeDupe(finall);
                print("common items count:" + finall.size());
                print(apkFileList.get(i));
                ArrayList<String> file_Strings = getFromDexAsArray(apkFileList.get(i), tClass, utf8);
                print("this item count:" + file_Strings.size());
                finall = super.getCommonOfArrayList(file_Strings, finall);
            }
            finall = super.removeDupe(finall);
            print("common items count:" + finall.size());
            print("**********************");
            super.writeArrayToFile(finall, path + "\\" + tClass.common_file_name + ".txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFromDexAsArray(File file, Item tClass, boolean utf8) {
        ArrayList<String> s = new ArrayList<>();
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] stream = toByteArray(inputStream);
                            //ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                            HashMap<String, byte[]> header = super.getHeader(stream);
                            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
                            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
                            long ids_count = getDecimalValue(header_ids_size);
                            long ids_offset = getDecimalValue(header_ids_off);

                            if (utf8) {
                                for (int i = 0; i < ids_count; i++) {
                                    String data = tClass.getDataAsUTF8(header, stream, ids_offset);
                                    ids_offset = ids_offset + tClass.data_size;
                                    s.add(data);
                                }
                            } else {
                                for (int i = 0; i < ids_count; i++) {
                                    String data = tClass.getDataAsHex(header, stream, ids_offset);
                                    ids_offset = ids_offset + tClass.data_size;
                                    s.add(data);
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
        return s;
    }
    
    public void writeToFile(HashMap<String, byte[]> header, byte[] stream, Item tClass, JsonWriter writer) {
        try {
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = getDecimalValue(header_ids_size);
            long ids_offset = getDecimalValue(header_ids_off);

            String itemKey;
            if (tClass instanceof ItemsString) {
                itemKey = "strings";
            } else if (tClass instanceof ItemsMethod) {
                itemKey = "methods";
            } else if (tClass instanceof ItemsClass) {
                itemKey = "classes";
            } else {
                itemKey = "unknown.txt";
            }

            writer.name(itemKey).beginArray();
            for (int i = 0; i < ids_count; i++) {
                String hex = tClass.getDataAsUTF8(header, stream, ids_offset);
                ids_offset = ids_offset + tClass.data_size;
                writer.value(hex);
            }
            writer.endArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHexByIndex(HashMap<String, byte[]> header, byte[] stream, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsHex(header, stream, ids_offset);
    }

    public static byte[] getByteByIndex(HashMap<String, byte[]> header, byte[] stream, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsByte(header, stream, ids_offset);
    }

}
