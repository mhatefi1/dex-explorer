package com.apk.signature.Items;

import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public abstract class Item {
    public int data_size;
    public String header_x_ids_size;
    public String header_x_ids_off;
    public String common_file_name;

    public Item(int data_size, String header_x_ids_size, String header_x_ids_off, String common_file_name) {
        this.data_size = data_size;
        this.header_x_ids_size = header_x_ids_size;
        this.header_x_ids_off = header_x_ids_off;
        this.common_file_name = common_file_name;
    }

    public abstract boolean searchDataByte(HashMap<String, byte[]> header, byte[] stream, long start, String[] splitText);

    public abstract byte[] getDataAsByte(HashMap<String, byte[]> header, byte[] stream, long start);

    public abstract String getDataAsHex(HashMap<String, byte[]> header, byte[] stream, long start);

    public abstract String getDataAsUTF8(HashMap<String, byte[]> header, byte[] stream, long start);

    public String getDataAsHex(HashMap<String, byte[]> header, byte[] stream, String offseString) {
        long start = new Util().stringHexToDecimal(offseString);
        return getDataAsHex(header, stream, start);
    }

    public String getDataAsUTF8(HashMap<String, byte[]> header, byte[] stream, String offseString) {
        long start = new Util().stringHexToDecimal(offseString);
        return getDataAsUTF8(header, stream, start);
    }
}
