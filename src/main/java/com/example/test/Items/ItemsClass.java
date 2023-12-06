package com.example.test.Items;

import com.example.test.App;
import com.example.test.Util.AppUtil;
import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.Arrays;
import java.util.HashMap;

public class ItemsClass extends App {

    public final static int class_data_size = 32;
    public final static String header_x_ids_size = "header_class_ids_size";
    public final static String header_x_ids_off = "header_class_ids_off";
    public final static String common_file_name = "commonClasses";
    Util util;
    AppUtil appUtil;
    public ItemsClass() {
        super(class_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil(util);
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getDataAsHex(header,raf, start);
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] class_data_b = util.getBytesOfFile(raf, start, class_data_size);
        return util.byteToStringHex(class_data_b);
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, String offseString) {
        return null;
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        return null;
    }
}
