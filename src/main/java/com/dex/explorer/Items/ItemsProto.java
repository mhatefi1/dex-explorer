package com.dex.explorer.Items;

import com.dex.explorer.Util.AppUtil;
import com.dex.explorer.Util.Util;

import java.util.Arrays;
import java.util.HashMap;

public class ItemsProto extends Item {
    public static final int proto_data_size = 12;
    public final static String header_x_ids_size = "header_proto_ids_size";
    public final static String header_x_ids_off = "header_proto_ids_off";
    public final static String common_file_name = "factorizedProto";
    Util util;
    AppUtil appUtil;

    public ItemsProto() {
        super(proto_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil();
    }

    @Override
    public boolean searchDataByte(HashMap<String, byte[]> header, byte[] stream, long start, String[] splitText) {
        return false;
    }

    @Override
    public byte[] getDataAsByte(HashMap<String, byte[]> header, byte[] stream, long start) {
        byte[] proto_data_b = util.getBytesOfFile(stream, start, proto_data_size);

        byte[] shorty_idx_b = Arrays.copyOfRange(proto_data_b, 0, 3);
        byte[] return_type_idx_b = Arrays.copyOfRange(proto_data_b, 4, 7);
        byte[] parameters_off_b = Arrays.copyOfRange(proto_data_b, 8, 11);
        long shorty_idx = util.getDecimalValue(shorty_idx_b);
        long return_type_idx = util.getDecimalValue(return_type_idx_b);
        long parameters_off = util.getDecimalValue(parameters_off_b);
        return appUtil.getByteByIndex(header, stream, return_type_idx, new ItemsType());
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, byte[] stream, long start) {
        byte[] proto_data_b = util.getBytesOfFile(stream, start, proto_data_size);

        byte[] shorty_idx_b = Arrays.copyOfRange(proto_data_b, 0, 3);
        byte[] return_type_idx_b = Arrays.copyOfRange(proto_data_b, 4, 7);
        byte[] parameters_off_b = Arrays.copyOfRange(proto_data_b, 8, 11);
        long shorty_idx = util.getDecimalValue(shorty_idx_b);
        long return_type_idx = util.getDecimalValue(return_type_idx_b);
        long parameters_off = util.getDecimalValue(parameters_off_b);
        return appUtil.getHexByIndex(header, stream, return_type_idx, new ItemsType());
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, byte[] stream, long start) {
        return null;
    }
}
