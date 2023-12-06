package com.example.test.Items;

import com.example.test.App;
import com.example.test.Util.AppUtil;
import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.HashMap;

public class ItemsType extends App {
    public final static int type_data_size = 4;

    public final static String header_x_ids_size = "header_type_ids_size";
    public final static String header_x_ids_off = "header_type_ids_off";
    public final static String common_file_name = "factorizedTypes";
    Util util;
    AppUtil appUtil;

    public ItemsType() {
        super(type_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getDataAsHex(header, raf, start);
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] type_data_b = util.getBytesOfFile(raf, start, type_data_size);
        long descriptor_idx = util.getDecimalValue(type_data_b);
        return appUtil.getByIndex(header, raf, descriptor_idx, new ItemsString());
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        return null;
    }

    @Override
    public void find(HashMap<String, byte[]> header, RandomAccessFile raf, String s) {

    }
}
