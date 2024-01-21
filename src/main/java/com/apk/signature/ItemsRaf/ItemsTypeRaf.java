package com.apk.signature.ItemsRaf;

import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.HashMap;

public class ItemsTypeRaf extends ItemRaf {
    public final static int type_data_size = 4;

    public final static String header_x_ids_size = "header_type_ids_size";
    public final static String header_x_ids_off = "header_type_ids_off";
    public final static String common_file_name = "factorizedTypes";
    Util util;
    AppUtil appUtil;

    public ItemsTypeRaf() {
        super(type_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil();
    }

    @Override
    public boolean searchDataByte(HashMap<String, byte[]> header, RandomAccessFile raf, long start, String[] splitText) {
        return false;
    }

    @Override
    public byte[] getDataAsByte(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] type_data_b = util.getBytesOfFile(raf, start, type_data_size);
        long descriptor_idx = util.getDecimalValue(type_data_b);
        return appUtil.getByteByIndex(header, raf, descriptor_idx, new ItemsStringRaf());
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] type_data_b = util.getBytesOfFile(raf, start, type_data_size);
        long descriptor_idx = util.getDecimalValue(type_data_b);
        return appUtil.getHexByIndex(header, raf, descriptor_idx, new ItemsStringRaf());
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        return null;
    }

}
