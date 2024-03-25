package com.apk.signature;

import com.apk.signature.Util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.print;

public class AppTest {

    public static void main(String[] args) {
        jniManifest jni = new jniManifest();
        ArrayList<String> d = new ArrayList<>();

        try {
            try (ZipFile zipFile = new ZipFile(new File(args[0]).getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        if (entry.getName().equals("AndroidManifest.xml")) {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] bs = toByteArray(inputStream);
                            inputStream.close();
                            d = jni.calcManifestInNew(bs);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String ss : d) {
            print(new Util().hexStringToUTF8(ss));
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) output.write(buffer, 0, bytesRead);
        byte[] bs = output.toByteArray();
        output.flush();
        output.close();
        return bs;
    }
}
