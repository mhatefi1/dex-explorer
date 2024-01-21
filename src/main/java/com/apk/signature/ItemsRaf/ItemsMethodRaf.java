package com.apk.signature.ItemsRaf;

import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.util.Arrays;
import java.util.HashMap;

public class ItemsMethodRaf extends ItemRaf {

    public final static int method_data_size = 8;
    public final static String header_x_ids_size = "header_method_ids_size";
    public final static String header_x_ids_off = "header_method_ids_off";
    public final static String common_file_name = "factorizedMethods";
    Util util;
    AppUtil appUtil;

    public ItemsMethodRaf() {
        super(method_data_size, header_x_ids_size, header_x_ids_off, common_file_name);
        util = new Util();
        appUtil = new AppUtil();
    }

    private int compareBytes(String[] splitText, byte[] bytes, int size) {
        int l = bytes.length;
        int j = 0;
        for (int i = size; i < splitText.length; i++) {
            if (j < l) {
                byte[] b = new byte[1];
                b[0] = bytes[j];
                String hex = util.getHexValue(b);
                if (!hex.equals(splitText[i])) {
                    return 0;
                }

                j++;
            }
        }
        return size + l;
    }

    @Override
    public boolean searchDataByte(HashMap<String, byte[]> header, RandomAccessFile raf, long start, String[] splitText) {
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        long class_idx = util.getDecimalValue(class_idx_b);
        byte[] class_ = appUtil.getByteByIndex(header, raf, class_idx, new ItemsTypeRaf());
        int contains = compareBytes(splitText, class_, 0);
        if (contains == 0) {
            return false;
        } else {
            byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
            long proto_idx = util.getDecimalValue(proto_idx_b);
            byte[] proto = appUtil.getByteByIndex(header, raf, proto_idx, new ItemsProtoRaf());
            int contains1 = compareBytes(splitText, proto, contains);
            if (contains1 == 0) {
                return false;
            } else {
                byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);
                long method_idx = util.getDecimalValue(method_idx_b);
                byte[] method = appUtil.getByteByIndex(header, raf, method_idx, new ItemsStringRaf());
                int contains3 = compareBytes(splitText, method, contains1);
                return contains3 == splitText.length;
            }
        }
    }

    @Override
    public byte[] getDataAsByte(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        long class_idx = util.getDecimalValue(class_idx_b);
        byte[] class_ = appUtil.getByteByIndex(header, raf, class_idx, new ItemsTypeRaf());

        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        byte[] proto = appUtil.getByteByIndex(header, raf, proto_idx, new ItemsProtoRaf());

        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);
        long method_idx = util.getDecimalValue(method_idx_b);
        byte[] method = appUtil.getByteByIndex(header, raf, method_idx, new ItemsStringRaf());

        int size = class_.length + proto.length + method.length;
        byte[] result = new byte[size];
        int j = 0;
        int i = 0;
        while (i < class_.length) {
            result[i] = class_[j];
            j++;
            i++;
        }
        j = 0;
        while (i < proto.length) {
            result[i] = proto[j];
            j++;
            i++;
        }
        j = 0;
        while (i < method.length) {
            result[i] = proto[j];
            j++;
            i++;
        }

        return result;
    }

    @Override
    public String getDataAsHex(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        long class_idx = util.getDecimalValue(class_idx_b);
        String class_ = appUtil.getHexByIndex(header, raf, class_idx, new ItemsTypeRaf());

        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        String proto = appUtil.getHexByIndex(header, raf, proto_idx, new ItemsProtoRaf());

        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);
        long method_idx = util.getDecimalValue(method_idx_b);
        String method = appUtil.getHexByIndex(header, raf, method_idx, new ItemsStringRaf());

        return class_ + proto + method;
    }

    @Override
    public String getDataAsUTF8(HashMap<String, byte[]> header, RandomAccessFile raf, long start) {
        byte[] method_data_b = util.getBytesOfFile(raf, start, method_data_size);

        byte[] class_idx_b = Arrays.copyOfRange(method_data_b, 0, 1);
        long class_idx = util.getDecimalValue(class_idx_b);
        String class_ = appUtil.getHexByIndex(header, raf, class_idx, new ItemsTypeRaf());

        byte[] proto_idx_b = Arrays.copyOfRange(method_data_b, 2, 3);
        long proto_idx = util.getDecimalValue(proto_idx_b);
        String proto = appUtil.getHexByIndex(header, raf, proto_idx, new ItemsProtoRaf());

        byte[] method_idx_b = Arrays.copyOfRange(method_data_b, 4, 7);
        long method_idx = util.getDecimalValue(method_idx_b);
        String method = appUtil.getHexByIndex(header, raf, method_idx, new ItemsStringRaf());

        return util.hexStringToUTF8(class_) + util.hexStringToUTF8(proto) + "-" + util.hexStringToUTF8(method);
    }
}
