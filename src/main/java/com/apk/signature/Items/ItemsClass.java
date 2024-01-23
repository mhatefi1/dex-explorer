package com.apk.signature.Items;

import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class ItemsClass extends Item {

    public final static int class_data_size = 32;
    public final static String header_x_ids_size = "header_class_ids_size";
    public final static String header_x_ids_off = "header_class_ids_off";
    public final static String common_file_name = "factorizedClasses";
    Util util;
    AppUtil appUtil;

    public ItemsClass() {
        super(class_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil();
    }

    @Override
    public boolean searchDataByte(HashMap<String, byte[]> header, byte[] stream, long start, String[] splitText) {
        byte[] class_data_b = util.getBytesOfFile(stream, start, class_data_size);
        //return util.compareBytes(splitText, class_data_b);
        return false;
    }

    @Override
    public byte[] getDataAsByte(HashMap<String, byte[]> header, byte[] stream, long start) {
        return util.getBytesOfFile(stream, start, class_data_size);
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, byte[] stream, long start) {
        byte[] class_data_b = util.getBytesOfFile(stream, start, class_data_size);
        return util.byteToStringHex(class_data_b);
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, byte[] stream, long start) {
        String hex = getDataAsHex(header, stream, start);
        return util.hexStringToUTF8(hex);
    }
}
