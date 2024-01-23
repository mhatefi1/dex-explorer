package com.apk.signature.Util;

import com.apk.signature.Items.Item;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppUtil extends ManifestUtil {
    public AppUtil() {

    }

    public boolean getAddressFromHexString(HashMap<String, byte[]> header, ByteArrayInputStream stream, String text, Item tClass) {
        return getAddressFromHexString(header, stream, text, tClass, 0, 0);
    }

    public boolean getAddressFromHexString(HashMap<String, byte[]> header, ByteArrayInputStream stream, String text, Item tClass, int periodStartIndex, int periodEndIndex) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = super.getDecimalValue(header_ids_size);
        long ids_offset = super.getDecimalValue(header_ids_off);
        if (periodStartIndex < ids_count) {
            String[] splitText = super.splitTwoByTwo(text);
            if (periodEndIndex > ids_count || periodEndIndex == 0) {
                periodEndIndex = (int) ids_count;
            }
            ids_offset = ids_offset + (long) tClass.data_size * periodStartIndex;
            for (int i = periodStartIndex; i <= periodEndIndex; i++) {
                boolean ss = tClass.searchDataByte(header, stream, ids_offset, splitText);
                if (ss) {
                    printYellow("{ hex:" + text);
                    printYellow("  ids_offs: " + super.decimalToStringHex(ids_offset));
                    printYellow("  ids_index: " + i + " }");
                    return true;
                }
                ids_offset = ids_offset + tClass.data_size;
            }
        }
        return false;
    }

    public void factorizeInFolder(String path, Item tClass, boolean utf8) {
        try {
            System.out.println("***********" + tClass.common_file_name + "***********");
            ArrayList<File> temp = new ArrayList<>();
            ArrayList<File> apkFileList = super.getRecursiveFileListByFormat(temp, path, ".apk", true);
            File first_file = apkFileList.get(0);
            ArrayList<String> finall = getFromDexAsArray(first_file, tClass, utf8);
            System.out.println("this item count:" + finall.size());
            for (int i = 1; i < apkFileList.size(); i++) {
                finall = super.removeDupe(finall);
                System.out.println("common items count:" + finall.size());
                System.out.println(apkFileList.get(i));
                ArrayList<String> file_Strings = getFromDexAsArray(apkFileList.get(i), tClass, utf8);
                System.out.println("this item count:" + file_Strings.size());
                finall = super.getCommonOfArrayList(file_Strings, finall);
            }
            finall = super.removeDupe(finall);
            System.out.println("common items count:" + finall.size());
            System.out.println("**********************");
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
                            byte[] bs = IOUtils.toByteArray(inputStream);
                            ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                            HashMap<String, byte[]> header = super.getHeader(stream);
                            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
                            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
                            long ids_count = super.getDecimalValue(header_ids_size);
                            long ids_offset = super.getDecimalValue(header_ids_off);

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

    public void writeToFile(HashMap<String, byte[]> header, ByteArrayInputStream stream, String fileName, Item tClass, boolean utf8) {
        try {
            byte[] header_ids_size = header.get(tClass.header_x_ids_size);
            byte[] header_ids_off = header.get(tClass.header_x_ids_off);
            long ids_count = super.getDecimalValue(header_ids_size);
            long ids_offset = super.getDecimalValue(header_ids_off);

            File f = new File(Util.TEMP_DEX_PATH + "\\" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));

            if (utf8) {
                for (int i = 0; i < ids_count; i++) {
                    String hex = tClass.getDataAsUTF8(header, stream, ids_offset);
                    ids_offset = ids_offset + tClass.data_size;
                    writer.append(hex);
                    writer.append('\n');
                }
            } else {
                for (int i = 0; i < ids_count; i++) {
                    String hex = tClass.getDataAsHex(header, stream, ids_offset);
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

    public void getAll(HashMap<String, byte[]> header, ByteArrayInputStream stream, Item tClass) {
        byte[] header_ids_size = header.get(tClass.header_x_ids_size);
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_count = super.getDecimalValue(header_ids_size);
        long ids_offset = super.getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = tClass.getDataAsHex(header, stream, ids_offset);
            ids_offset = ids_offset + tClass.data_size;
            System.out.println(hex);
        }
    }

    public String getHexByIndex(HashMap<String, byte[]> header, ByteArrayInputStream stream, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = super.getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsHex(header, stream, ids_offset);
    }

    public byte[] getByteByIndex(HashMap<String, byte[]> header, ByteArrayInputStream stream, long index, Item tClass) {
        byte[] header_ids_off = header.get(tClass.header_x_ids_off);
        long ids_offset = super.getDecimalValue(header_ids_off);
        ids_offset = index * tClass.data_size + ids_offset;
        return tClass.getDataAsByte(header, stream, ids_offset);
    }

}
