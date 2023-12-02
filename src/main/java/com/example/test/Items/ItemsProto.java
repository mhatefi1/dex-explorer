package com.example.test.Items;

import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.Arrays;
import java.util.HashMap;

public class ItemsProto {
    private final int proto_data_size = 12;
    Util util;

    public ItemsProto() {
        util = new Util();
    }

    public String getProtoDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, String offsetString) {
        long start = util.stringHexToDecimal(offsetString);
        return getProtoDataAsHex(header, raf, start);
    }

    public String getProtoDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] proto_data_b = util.getBytesOfFile(raf, start, proto_data_size);

        byte[] shorty_idx_b = Arrays.copyOfRange(proto_data_b, 0, 3);
        byte[] return_type_idx_b = Arrays.copyOfRange(proto_data_b, 4, 7);
        byte[] parameters_off_b = Arrays.copyOfRange(proto_data_b, 8, 11);
        long shorty_idx = util.getDecimalValue(shorty_idx_b);
        long return_type_idx = util.getDecimalValue(return_type_idx_b);
        long parameters_off = util.getDecimalValue(parameters_off_b);
        return new ItemsType().getTypeByIndex(header, raf, return_type_idx);
    }

    public String getProtoByIndex(HashMap<String, byte[]> header, RandomAccessFile raf, long index) {
        byte[] header_proto_ids_off = header.get("header_proto_ids_off");
        long proto_ids_offset = util.getDecimalValue(header_proto_ids_off);
        proto_ids_offset = index * proto_data_size + proto_ids_offset;
        return getProtoDataAsHex(header, raf, proto_ids_offset);
    }
}
