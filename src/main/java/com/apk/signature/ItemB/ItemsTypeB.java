package com.apk.signature.ItemB;

import com.apk.signature.Items.Item;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class ItemsTypeB extends ItemB {
    public final static int type_data_size = 4;

    public final static String header_x_ids_size = "header_type_ids_size";
    public final static String header_x_ids_off = "header_type_ids_off";
    public final static String common_file_name = "factorizedTypes";
    Util util;
    AppUtil appUtil;

    public ItemsTypeB() {
        super(type_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil();
    }

    @Override
    public boolean searchDataByte(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start, String[] splitText) {
        return false;
    }

    @Override
    public byte[] getDataAsByte(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start) {
        byte[] type_data_b = util.getBytesOfFile(stream, start, type_data_size);
        long descriptor_idx = util.getDecimalValue(type_data_b);
        return appUtil.getByteByIndex(header, stream, descriptor_idx, new ItemsStringB());
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start) {
        byte[] type_data_b = util.getBytesOfFile(stream, start, type_data_size);
        long descriptor_idx = util.getDecimalValue(type_data_b);
        return appUtil.getHexByIndex(header, stream, descriptor_idx, new ItemsStringB());
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, ByteArrayInputStream stream, long start) {
        return null;
    }

}
