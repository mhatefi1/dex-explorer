package com.apk.signature.ItemB;

import com.apk.signature.Items.Item;
import com.apk.signature.Util.Util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemsStringB extends ItemB {


    public final static int string_data_off_size = 4;
    public final static String header_x_ids_size = "header_string_ids_size";
    public final static String header_x_ids_off = "header_string_ids_off";
    public final static String common_file_name = "factorizedStrings";
    Util util;

    public ItemsStringB() {
        super(string_data_off_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
    }


    @Override
    public boolean searchDataByte(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start, String[] splitText) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(stream, start, string_data_off_size);
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
        /*byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(raf, offset, 1);
        String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
        return hex.equals("00");*/
        return true;
    }

    @Override
    public byte[] getDataAsByte(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(stream, start, string_data_off_size);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);

        while (true) {
            byte[] size_in_utf16_b = util.getBytesOfFile(stream, offset, 1);
            long size_in_utf16_l = util.getDecimalValue(size_in_utf16_b);
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }
        ArrayList<Byte> list = new ArrayList<>();
        while (true) {
            byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(stream, offset, 1);
            String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
            if (hex.equals("00")) {
                break;
            }
            list.add(a_string_bit_in_MUTF8_format_b[0]);
            offset++;
        }
        byte[] result = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(stream, start, string_data_off_size);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);
        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            byte[] size_in_utf16_b = util.getBytesOfFile(stream, offset, 1);
            long size_in_utf16_l = util.getDecimalValue(size_in_utf16_b);
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }

        while (true) {
            byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(stream, offset, 1);
            String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
            if (hex.equals("00")) {
                break;
            }
            stringBuilder.append(hex);
            offset++;
        }

        return stringBuilder.toString();
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start) {
        String hex = getDataAsHex(header, stream, start);
        return util.hexStringToUTF8(hex);
    }
}
