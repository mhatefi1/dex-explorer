package com.example.test;

import org.apache.pdfbox.io.RandomAccessFile;

public abstract class App {
    public int class_data_size;
    public String header_x_ids_size;
    public String header_x_ids_off;
    public String common_file_name;

    public App(int class_data_size, String header_x_ids_size, String header_x_ids_off, String common_file_name) {
        this.class_data_size = class_data_size;
        this.header_x_ids_size = header_x_ids_size;
        this.header_x_ids_off = header_x_ids_off;
        this.common_file_name = common_file_name;
    }

    public abstract String getDataAsHex(RandomAccessFile raf, String offseString);


    public abstract String getDataAsHex(RandomAccessFile raf, long start);
}
