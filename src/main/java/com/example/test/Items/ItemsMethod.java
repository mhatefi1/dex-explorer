package com.example.test.Items;

import com.example.test.App;
import com.example.test.Util.AppUtil;
import com.example.test.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.Arrays;
import java.util.HashMap;

public class ItemsMethod extends App {

    public final static int method_data_size = 8;
    public final static String header_x_ids_size = "header_method_ids_size";
    public final static String header_x_ids_off = "header_method_ids_off";
    public final static String common_file_name = "commonMethods";
    Util util;
    AppUtil appUtil;

    public ItemsMethod() {
        super(method_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil(util);
    }

    public String getParsedMethodDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getParsedMethodDataAsUTF8(header, raf, start);
    }

    public String getParsedMethodDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        long class_idx = util.getDecimalValue(class_idx_b);
        String class_ = new ItemsType().getTypeByIndex(header, raf, class_idx);

        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        String proto = new ItemsProto().getProtoByIndex(header, raf, proto_idx);

        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);
        long method_idx = util.getDecimalValue(method_idx_b);
        String method = appUtil.getByIndex(header, raf, method_idx, new ItemsString());

        return util.hexStringToString(proto) + "-" + util.hexStringToString(class_) + "-" + util.hexStringToString(method);
    }

    public String getParsedMethodDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getParsedMethodDataAsHex(header, raf, start);
    }

    public String getParsedMethodDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);

        long class_idx = util.getDecimalValue(class_idx_b);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        long method_idx = util.getDecimalValue(method_idx_b);

        String class_ = new ItemsType().getTypeByIndex(header, raf, class_idx);
        String proto = new ItemsProto().getProtoByIndex(header, raf, proto_idx);
        String method = appUtil.getByIndex(header, raf, method_idx, new ItemsString());

        return proto + class_ + method;
    }

    public void getAllParsedMethods(HashMap<String, byte[]> header, RandomAccessFile raf) {
        byte[] header_ids_size = header.get(header_x_ids_size);
        byte[] header_ids_off = header.get(header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);
        for (int i = 0; i < ids_count; i++) {
            String hex = getParsedMethodDataAsHex(header, raf, ids_offset);
            ids_offset = ids_offset + method_data_size;
            System.out.println(hex);
        }
    }

    public void findMethod(HashMap<String, byte[]> header, RandomAccessFile raf, String s) {
        String hexString = util.stringToHexString(s);
        System.out.println("string to hexString:" + hexString);
        byte[] header_ids_size = header.get(header_x_ids_size);
        byte[] header_ids_off = header.get(header_x_ids_off);
        long ids_count = util.getDecimalValue(header_ids_size);
        long ids_offset = util.getDecimalValue(header_ids_off);

        for (int i = 0; i < ids_count; i++) {
            String hex = getDataAsHex(raf, ids_offset);
            if (hex.endsWith(hexString)) {
                System.out.println("method data as hexString:" + hex);
                System.out.println("index:" + i);
                System.out.println("offset:" + util.decimalToStringHex(ids_offset));
                String utf8 = getParsedMethodDataAsUTF8(header, raf, util.decimalToStringHex(ids_offset));
                System.out.println("parsed method data as UTF8:" + utf8);
                System.out.println("****************************************************");
            }
            ids_offset = ids_offset + method_data_size;
        }
    }

    @Override
    public String getDataAsHex(RandomAccessFile raf, String offseString) {
        long start = util.stringHexToDecimal(offseString);
        return getDataAsHex(raf, start);
    }

    @Override
    public String getDataAsHex(RandomAccessFile raf, long start) {
        /*byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);
        return util.byteToStringHex(method_data_b);*/
        HashMap<String, byte[]> header = util.getHeader(raf);
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);

        long class_idx = util.getDecimalValue(class_idx_b);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        long method_idx = util.getDecimalValue(method_idx_b);

        String class_ = new ItemsType().getTypeByIndex(header, raf, class_idx);
        String proto = new ItemsProto().getProtoByIndex(header, raf, proto_idx);
        String method = appUtil.getByIndex(header, raf, method_idx, new ItemsString());

        return proto + class_ + method;
    }
}
