package com.example.test.Items;

import java.util.HashMap;

import com.example.test.App;
import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

public class ItemsString extends App {


    public final static int string_data_off_size = 4;
    public final static String header_x_ids_size = "header_string_ids_size";
    public final static String header_x_ids_off = "header_string_ids_off";
    public final static String common_file_name = "commonStrings";
    Util util;

    public ItemsString() {
        super(string_data_off_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
    }

    public void findString(HashMap<String, byte[]> header, RandomAccessFile raf, String s) {
        String hexString = util.stringToHexString(s);
        byte[] header_ids_size = header.get("header_string_ids_size");
        byte[] header_ids_off = header.get("header_string_ids_off");
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = getDataAsHex(raf, ids_offset);
            if (hex.equals(hexString)) {
                System.out.println(hex);
                System.out.println("index:" + i);
                System.out.println("offset:" + util.decimalToStringHex(ids_offset));
                System.out.println("****************************************************");
            }
            ids_offset = ids_offset + string_data_off_size;
        }
    }


    public String getStringDataAsUTF8(RandomAccessFile raf, String hex) {
        String hexString = getDataAsHex(raf, hex);
        return util.hexStringToString(hexString);
    }

    @Override
    public String getDataAsHex(RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getDataAsHex(raf, start);
    }

    @Override
    public String getDataAsHex(RandomAccessFile raf, long start) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(raf, start, string_data_off_size);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);
        StringBuilder stringBuilder = new StringBuilder();


        while (true) {
            byte[] size_in_utf16_b = util.getBytesOfFile(raf, offset, 1);
            long size_in_utf16_l = util.getDecimalValue(size_in_utf16_b);
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }

        while (true) {
            byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(raf, offset, 1);
            String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
            if (hex.equals("00")) {
                break;
            }
            stringBuilder.append(hex);
            offset++;
        }

        return stringBuilder.toString();
    }
}
