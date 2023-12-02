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
    public String getDataAsHex(RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getDataAsHex(raf, start);
    }

    @Override
    public String getDataAsHex(RandomAccessFile raf, long start) {
        byte[] class_data_b = util.getBytesOfFile(raf, start, class_data_size);
        return util.byteToStringHex(class_data_b);
    }

    public String parseClassData(HashMap<String, byte[]> header, RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        byte[] method_data_b = util.getBytesOfFile(raf, start, class_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);

        long class_idx = util.getDecimalValue(class_idx_b);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        long method_idx = util.getDecimalValue(method_idx_b);

        String class_ = new ItemsType().getTypeByIndex(header, raf, class_idx);
        String proto = new ItemsProto().getProtoByIndex(header, raf, proto_idx);
        String method = appUtil.getByIndex(header, raf, method_idx,new ItemsString());

        return util.hexStringToString(proto) + "\n" + util.hexStringToString(class_) + "\n" + util.hexStringToString(method);
    }
}
