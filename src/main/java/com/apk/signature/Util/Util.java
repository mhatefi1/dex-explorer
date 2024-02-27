package com.apk.signature.Util;

import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Model.StringModel;
import net.lingala.zip4j.model.FileHeader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static org.fusesource.jansi.Ansi.ansi;

public class Util extends FileUtil {

    private static final String defaultAapt2Path = "C:\\scanner\\aapt2.exe";
    public static String TEMP_DEX_PATH = "";
    public static String aapt2Path = "";//public static String aapt2Path = "C:\\Users\\sedej\\AppData\\Local\\Android\\Sdk\\build-tools\\34.0.0\\aapt2.exe";
    public static String RESET = "\u001B[0m";
    public static String RED = "\u001B[31m";
    public static String GREEN = "\u001B[32m";
    public static String YELLOW = "\u001B[33m";

    public static String dexName;

    public static long runDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
        return elapsedTime;
    }

    public static void printRed(Object text) {
        //System.out.println(RED + text + RESET);
        AnsiConsole.systemInstall();
        System.out.println(ansi().fgBrightRed().a(text).reset());
        AnsiConsole.systemUninstall();
    }

    public static void printGreen(Object text) {
        //System.out.println(GREEN + text + RESET);
        AnsiConsole.systemInstall();
        System.out.println(ansi().fgBrightGreen().a(text).reset());
        AnsiConsole.systemUninstall();
    }

    public static void printYellow(Object text) {
        //System.out.println(YELLOW + text + RESET);
        AnsiConsole.systemInstall();
        System.out.println(ansi().fgBrightYellow().a(text).reset());
        AnsiConsole.systemUninstall();
    }

    public static void print(Object text) {
        System.out.println(text);
    }

    public static String setAapt2Path(String path) {
        if (path.isEmpty()) {
            return defaultAapt2Path;
        } else {
            if (path.endsWith("aapt2.exe")) {
                return path;
            } else {
                File f = new File(path);
                File[] listed = f.listFiles();
                assert listed != null;
                for (File file : listed) {
                    if (file.getName().endsWith("aapt2.exe")) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        return "";
    }

    public ArrayList<String> getCommonOfArrayList(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (String s : first) {
                for (String s2 : second) {
                    if (s.equals(s2)) {
                        result.add(s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String hexStringToUTF8(String hexString) {
        byte[] bytes = hexStringToByteArray(hexString);
        return new String(bytes, Charset.forName("Cp1252"));
    }

    public byte[] hexStringToByteArray(String hexString) {
        int l = hexString.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public long getDecimalValue(byte[] bytes) {
        String hex = getHexValue(bytes);
        return stringHexToDecimal(hex);
    }

    public String getHexValue(byte[] bytes) {
        byte[] reverse = reverse(bytes);
        return byteToStringHex(reverse);
    }

    public byte[] getBytesOfFile(ByteArrayInputStream inputStream, long offset, long size) {
        byte[] bytes = new byte[(int) size];
        try {
            inputStream.mark(0);
            inputStream.skip(offset);
            inputStream.read(bytes);
            inputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public byte[] getBytesOfFile(byte[] stream, long offset, long size) {
        int to = (int) (offset + size);
        return Arrays.copyOfRange(stream, (int) offset, to);
    }

    public String byteToStringHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public long stringHexToDecimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    public String decimalToStringHex(long value) {
        return Long.toHexString(value);
    }

    public String stringToHexString(String s) {
        return byteToStringHex(s.getBytes());
    }

    public byte[] reverse(byte[] byteArray) {
        byte[] result = new byte[byteArray.length];
        int j = 0;
        for (int i = byteArray.length - 1; i >= 0; i--) {
            result[j] = byteArray[i];
            j++;
        }
        return result;
    }

    public <T> ArrayList<T> removeDupe(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public boolean contains(ArrayList<String> list, ArrayList<String> subList) {
        boolean allItemsPresent = true;

        if (list != null && subList != null) {
            for (String item : subList) {
                if (!list.contains(item)) {
                    allItemsPresent = false;
                    break;
                }
            }
        } else {
            allItemsPresent = false;
        }
        return allItemsPresent;
    }

    public String[] splitTwoByTwo(String text) {
        int splitUnit = 2;
        String[] splitText = new String[text.length() / splitUnit];
        for (int i = 0, j = 0; i < text.length(); i += splitUnit, j++) {
            splitText[j] = text.substring(i, i + splitUnit);
        }
        return splitText;
    }

    public byte[] split(String text) {
        int splitUnit = 2;
        byte[] splitByte = new byte[text.length() / splitUnit];
        for (int i = 0, j = 0; i < text.length(); i += splitUnit, j++) {
            String s = text.substring(i, i + splitUnit);
            splitByte[j] = (byte) Integer.parseInt(s, 16);
        }
        return splitByte;
    }

    public HashMap<String, byte[]> getStringHeader(byte[] stream) {
        HashMap<String, byte[]> header = new HashMap<>();
        try {
            byte[] header_string_ids_size = getBytesOfFile(stream, 56, 4);
            byte[] header_string_ids_off = getBytesOfFile(stream, 60, 4);
            header.put("header_string_ids_size", header_string_ids_size);
            header.put("header_string_ids_off", header_string_ids_off);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    public HashMap<String, byte[]> getHeader(byte[] stream) {

        HashMap<String, byte[]> header = new HashMap<>();

        try {
            /*byte[] header_magic = getBytesOfFile(stream, 0, 8);
            byte[] header_checksum = getBytesOfFile(stream, 8, 4);
            byte[] header_signature = getBytesOfFile(stream, 12, 20);
            byte[] header_file_size = getBytesOfFile(stream, 32, 4);
            byte[] header_header_size = getBytesOfFile(stream, 36, 4);
            byte[] header_endian_tag = getBytesOfFile(stream, 40, 4);
            byte[] header_link_size = getBytesOfFile(stream, 44, 4);
            byte[] header_link_off = getBytesOfFile(stream, 48, 4);
            byte[] header_map_off = getBytesOfFile(stream, 52, 4);*/
            byte[] header_string_ids_size = getBytesOfFile(stream, 56, 4);
            byte[] header_string_ids_off = getBytesOfFile(stream, 60, 4);
            byte[] header_type_ids_size = getBytesOfFile(stream, 64, 4);
            byte[] header_type_ids_off = getBytesOfFile(stream, 68, 4);
            byte[] header_proto_ids_size = getBytesOfFile(stream, 72, 4);
            byte[] header_proto_ids_off = getBytesOfFile(stream, 76, 4);
            byte[] header_field_ids_size = getBytesOfFile(stream, 80, 4);
            byte[] header_field_ids_off = getBytesOfFile(stream, 84, 4);
            byte[] header_method_ids_size = getBytesOfFile(stream, 88, 4);
            byte[] header_method_ids_off = getBytesOfFile(stream, 92, 4);
            byte[] header_class_ids_size = getBytesOfFile(stream, 96, 4);
            byte[] header_class_ids_off = getBytesOfFile(stream, 100, 4);
            byte[] header_data_size = getBytesOfFile(stream, 104, 4);
            byte[] header_data_off = getBytesOfFile(stream, 108, 4);

            /*header.put("header_magic", header_magic);
            header.put("header_checksum", header_checksum);
            header.put("header_signature", header_signature);
            header.put("header_file_size", header_file_size);
            header.put("header_header_size", header_header_size);
            header.put("header_endian_tag", header_endian_tag);
            header.put("header_link_size", header_link_size);
            header.put("header_link_off", header_link_off);
            header.put("header_map_off", header_map_off);*/
            header.put("header_string_ids_size", header_string_ids_size);
            header.put("header_string_ids_off", header_string_ids_off);
            header.put("header_type_ids_size", header_type_ids_size);
            header.put("header_type_ids_off", header_type_ids_off);
            header.put("header_proto_ids_size", header_proto_ids_size);
            header.put("header_proto_ids_off", header_proto_ids_off);
            header.put("header_field_ids_size", header_field_ids_size);
            header.put("header_field_ids_off", header_field_ids_off);
            header.put("header_method_ids_size", header_method_ids_size);
            header.put("header_method_ids_off", header_method_ids_off);
            header.put("header_class_ids_size", header_class_ids_size);
            header.put("header_class_ids_off", header_class_ids_off);
            header.put("header_data_ids_size", header_data_size);
            header.put("header_data_ids_off", header_data_off);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    public void readZip4j(File file, ReadBytesFromZipListener listener) {
        try {
            try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file)) {
                List<FileHeader> fileHeaders = zipFile.getFileHeaders();
                List<FileHeader> fileHeaderCustom = new ArrayList<>();
                boolean countinue = false;
                for (FileHeader fileHeader : fileHeaders) {
                    String name = fileHeader.getFileName();
                    if (name.equals("AndroidManifest.xml")) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(fileHeader);
                            byte[] bs = IOUtils.toByteArray(inputStream);
                            inputStream.close();
                            countinue = listener.onReadManifest(bs);
                        } catch (Exception e) {
                            if (e.getMessage().contains("zip")) {
                                listener.onZipError(e);
                            } else {
                                listener.onManifestError(e);
                            }
                        }
                    } else if (name.endsWith(".dex")) {
                        fileHeaderCustom.add(fileHeader);
                    }
                }
                if (countinue) {
                    for (FileHeader fileHeader : fileHeaderCustom) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(fileHeader);
                            byte[] bs = IOUtils.toByteArray(inputStream);
                            inputStream.close();
                            dexName = fileHeader.getFileName();
                            boolean malware = listener.onReadDex(bs);
                            if (malware) {
                                break;
                            }
                        } catch (Exception e) {
                            if (e.getMessage().contains("zip")) {
                                listener.onZipError(e);
                            } else {
                                listener.onDexError(e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            listener.onZipError(e);
        }
        listener.onEnd();
    }

    public void readZip(File file, ReadBytesFromZipListener listener) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                List<ZipEntry> dexEntries = new ArrayList<>();
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                boolean countinue = false;
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        if (entry.getName().equals("AndroidManifest.xml")) {
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] bs = IOUtils.toByteArray(inputStream);
                                inputStream.close();
                                countinue = listener.onReadManifest(bs);
                            } catch (Exception e) {
                                if (e.getMessage().contains("zip")) {
                                    listener.onZipError(e);
                                } else {
                                    listener.onManifestError(e);
                                }
                            }
                        } else if (entry.getName().endsWith(".dex")) {
                            dexEntries.add(entry);
                        }
                    }
                }
                if (countinue) {
                    for (ZipEntry entry : dexEntries) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] bs = IOUtils.toByteArray(inputStream);
                            inputStream.close();
                            dexName = entry.getName();
                            boolean malware = listener.onReadDex(bs);
                            if (malware) {
                                break;
                            }
                        } catch (Exception e) {
                            if (e.getMessage().contains("zip")) {
                                listener.onZipError(e);
                            } else {
                                listener.onDexError(e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                listener.onZipError(e);
            }
        } catch (Exception e) {
            listener.onZipError(e);
        }
        listener.onEnd();
    }

    public ArrayList<String> readLineByLine(String path) {
        ArrayList<String> argsFileSignatureList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                argsFileSignatureList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return argsFileSignatureList;
    }
}