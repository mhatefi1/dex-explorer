package com.example.test.Items;

import com.example.test.Util.AppUtil;
import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.HashMap;

public class ItemsType {
    private final int type_data_size = 4;
    Util util;
    AppUtil appUtil;
    public ItemsType() {
        util = new Util();
        appUtil = new AppUtil(util);
    }

    public String getTypeDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, String offsetString) {
        long start = util.stringHexToDecimal(offsetString);
        return getTypeDataAsHex(header, raf, start);
    }

    public String getTypeDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] type_data_b = util.getBytesOfFile(raf, start, type_data_size);
        long descriptor_idx = util.getDecimalValue(type_data_b);
        return appUtil.getByIndex(header, raf, descriptor_idx, new ItemsString());
    }

    /*public void findString(HashMap<String, byte[]> header, RandomAccessFile raf, String s) {
        String hexString = util.stringToHexString(s);
        byte[] header_string_ids_size = header.get("header_string_ids_size");
        byte[] header_string_ids_off = header.get("header_string_ids_off");
        long string_ids_count = util.getDecimalValue(header_string_ids_size);
        long string_ids_offset = util.getDecimalValue(header_string_ids_off);
        for (int i = 0; i < string_ids_count; i++) {
            String hex = getTypeDataAsHex(header, raf, string_ids_offset);
            if (hex.equals(hexString)) {
                System.out.println(hex);
                System.out.println("index:" + i);
                System.out.println("offset:" + util.decimalToStringHex(string_ids_offset));
                System.out.println("****************************************************");
            }
            string_ids_offset = string_ids_offset + type_data_size;
        }
    }*/

    /*public void writeStringsToFile(HashMap<String, byte[]> header, RandomAccessFile raf, String fileName) {
        try {
            byte[] header_string_ids_size = header.get("header_string_ids_size");
            byte[] header_string_ids_off = header.get("header_string_ids_off");
            long string_ids_count = util.getDecimalValue(header_string_ids_size);
            long string_ids_offset = util.getDecimalValue(header_string_ids_off);

            BufferedWriter writer = new BufferedWriter(new FileWriter(Util.TEMP_DEX_PATH + "\\" + fileName, false));

            for (int i = 0; i < string_ids_count; i++) {
                String hex = getTypeDataAsHex(header, raf, string_ids_offset);
                string_ids_offset = string_ids_offset + type_data_size;
                writer.append(hex);
                writer.append('\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* public void getAllString(HashMap<String, byte[]> header, RandomAccessFile raf) {
        byte[] header_string_ids_size = header.get("header_string_ids_size");
        byte[] header_string_ids_off = header.get("header_string_ids_off");
        long string_ids_count = util.getDecimalValue(header_string_ids_size);
        long string_ids_offset = util.getDecimalValue(header_string_ids_off);
        for (int i = 0; i < string_ids_count; i++) {
            String hex = getTypeDataAsHex(header, raf, string_ids_offset);
            string_ids_offset = string_ids_offset + type_data_size;
            System.out.println(hex);
        }
    }*/

  /*  public void getAddressFromHexString(HashMap<String, byte[]> header, RandomAccessFile raf, String s) {
        byte[] header_type_ids_size = header.get("header_type_ids_size");
        byte[] header_type_ids_off = header.get("header_type_ids_off");
        long type_ids_count = util.getDecimalValue(header_type_ids_size);
        long type_ids_offset = util.getDecimalValue(header_type_ids_off);
        for (int i = 0; i < type_ids_count; i++) {
            String hex = getTypeDataAsHex(raf, type_ids_offset);
            if (hex.equals(s)) {
                System.out.println("type_ids_offset:" + util.decimalToStringHex(type_ids_offset));
                System.out.println("type_value:" + util.hexStringToString(hex));
                System.out.println("type_ids_index:" + i);
                break;
            }
            type_ids_offset = type_ids_offset + type_data_size;
        }
    }*/

    public String getTypeByIndex(HashMap<String, byte[]> header, RandomAccessFile raf, long index) {
        byte[] header_type_ids_off = header.get("header_type_ids_off");
        long type_ids_offset = util.getDecimalValue(header_type_ids_off);
        type_ids_offset = index * type_data_size + type_ids_offset;
        return getTypeDataAsHex(header, raf, type_ids_offset);
    }

   /* public void getCommonStringsInFolder(String path) {
        try {
            ArrayList<File> dexFileList = util.getFileListByFormat(path, ".dex");
            File fist_file = dexFileList.get(0);
            String fileName = fist_file.getName();
            System.out.println(fileName);
            ArrayList<String> finall = getStringsFromDexAsArray(fist_file);

            for (int i = 1; i < dexFileList.size(); i++) {
                System.out.println(dexFileList.get(i).getName());
                ArrayList<String> file_Strings = getStringsFromDexAsArray(dexFileList.get(i));
                finall = util.getCommonOfArrayList(file_Strings, finall);
            }

            util.writeArrayToFile(finall, path + "\\" + "commonStrings" + ".txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*public ArrayList<String> getStringsFromDexAsArray(File f) {
        ArrayList<String> s = new ArrayList<>();
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            HashMap<String, byte[]> header = util.getHeader(raf);
            byte[] header_string_ids_size = header.get("header_string_ids_size");
            byte[] header_string_ids_off = header.get("header_string_ids_off");
            long string_ids_count = util.getDecimalValue(header_string_ids_size);
            long string_ids_offset = util.getDecimalValue(header_string_ids_off);

            for (int i = 0; i < string_ids_count; i++) {
                String hex = getTypeDataAsHex(raf, string_ids_offset);
                string_ids_offset = string_ids_offset + type_data_size;
                s.add(hex);
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }*/

   /* public boolean fileMatch(String path) {
        File f = new File(path);
        boolean flag = false;
        try {
            ArrayList<String> finall = getStringsFromDexAsArray(f);

            String signature_path = "C:\\Users\\sedej\\Desktop\\dexs\\commonStrings.txt";
            File signature_file = new File(signature_path);
            ArrayList<String> signature = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(signature_file));
            String line = reader.readLine();
            while (line != null) {
                signature.add(line);
                line = reader.readLine();
            }
            reader.close();
            flag = contains(finall, signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }*/

  /*  boolean contains(ArrayList<String> list, ArrayList<String> subList) {
        boolean allItemsPresent = true;

        for (String item : subList) {
            if (!list.contains(item)) {
                allItemsPresent = false;
                break;
            }
        }
        return allItemsPresent;
    }*/
}
