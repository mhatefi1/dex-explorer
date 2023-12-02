package com.example.test.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReadZip {


    private static File convertInputStreamToFile(InputStream is, String fileName) {
        OutputStream outputStream = null;
        File file = null;
        try {
            file = new File(fileName);
            outputStream = new FileOutputStream(file);

            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public ArrayList<File> readDexFilesFromZip(String zipFilePath,String extractPath) {
        ArrayList<File> list = new ArrayList<>();
        try {
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        list.add(convertInputStreamToFile(inputStream, extractPath + "\\" + entry.getName()));
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return list;
    }
}
