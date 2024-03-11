package com.apk.signature.Util;

import com.apk.signature.Model.MatchStateEnum;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.fusesource.jansi.Ansi.ansi;

public class Util extends FileUtil {
    public static String TEMP_DEX_PATH = "";
    public static String dexName;

    public static long runDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        //System.out.println("Elapsed time: " + endTime - startTime + " milliseconds");
        return endTime - startTime;
    }

    public static void printRed(Object text) {
        //AnsiConsole.systemInstall();
        System.out.println(ansi().fgBrightRed().a(text).reset());
        //AnsiConsole.systemUninstall();
    }

    public static void printGreen(Object text) {
        //AnsiConsole.systemInstall();
        System.out.println(ansi().fgBrightGreen().a(text).reset());
        //AnsiConsole.systemUninstall();
    }

    public static void printYellow(Object text) {
        //AnsiConsole.systemInstall();
        System.out.println(ansi().fgBrightYellow().a(text).reset());
        //AnsiConsole.systemUninstall();
    }

    public static void print(Object text) {
        System.out.println(text);
    }

    /*public static String setAapt2Path(String path) {
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
    }*/

    public String getArrayAsString(ArrayList<String> list) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            res.append(list.get(i));
            if (i != list.size() - 1) {
                res.append(",");
            }
        }
        return res.toString();
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

    /*public byte[] getBytesOfFile(ByteArrayInputStream inputStream, long offset, long size) {
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
    }*/

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
        ArrayList<T> newList = new ArrayList<>();
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
            /*byte[] header_type_ids_size = getBytesOfFile(stream, 64, 4);
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
            byte[] header_data_off = getBytesOfFile(stream, 108, 4);*/

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
            /*header.put("header_type_ids_size", header_type_ids_size);
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
            header.put("header_data_ids_off", header_data_off);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    /*public void readZip4j(File file, ReadBytesFromZipListener listener) {
        try {
            try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file)) {
                List<FileHeader> fileHeaders = zipFile.getFileHeaders();
                List<FileHeader> fileHeaderCustom = new ArrayList<>();
                boolean continue = false;
                for (FileHeader fileHeader : fileHeaders) {
                    String name = fileHeader.getFileName();
                    if (name.equals("AndroidManifest.xml")) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(fileHeader);
                            byte[] bs = toByteArray(inputStream);
                            inputStream.close();
                            continue = listener.onReadManifest(bs);
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
                if (continue) {
                    for (FileHeader fileHeader : fileHeaderCustom) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(fileHeader);
                            byte[] bs = toByteArray(inputStream);
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
    }*/

    public void readZip(File file, ReadBytesFromZipListener listener) {
        MatchStateEnum.state state = MatchStateEnum.state.NOT_MATCH;
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                List<ZipEntry> dexEntries = new ArrayList<>();
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                boolean continue_ = false;
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        if (entry.getName().equals("AndroidManifest.xml")) {
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] bs = toByteArray(inputStream);
                                inputStream.close();
                                continue_ = listener.onReadManifest(bs);
                            } catch (Exception e) {
                                state = MatchStateEnum.state.ERROR;
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
                if (continue_) {
                    for (ZipEntry entry : dexEntries) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] bs = toByteArray(inputStream);
                            inputStream.close();
                            dexName = entry.getName();
                            boolean malware = listener.onReadDex(bs);
                            if (malware) {
                                state = MatchStateEnum.state.MALWARE;
                                break;
                            }
                        } catch (Exception e) {
                            state = MatchStateEnum.state.ERROR;
                            if (e.getMessage().contains("zip")) {
                                listener.onZipError(e);
                            } else {
                                listener.onDexError(e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                state = MatchStateEnum.state.ERROR;
                listener.onZipError(e);
            }
        } catch (Exception e) {
            listener.onZipError(e);
        }
        listener.onEnd(state);
    }

    /*public void readZip(File file) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        if (entry.getName().endsWith(".dex")) {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] bs = toByteArray(inputStream);
                            byte[] stringSection = getStringSection(bs);
                            String path = "Z:\\fff\\test.txt";
                            writeToFile(stringSection, path);
                            inputStream.close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private ArrayList<byte[]> chunkBytes(byte[] bs) {
        ArrayList<byte[]> list = new ArrayList<>();
        int length = bs.length;
        int start_offset = 0;
        int limit = 1048576;
        int lastOffsetOfMain = length - 1;
        int end_offset = limit - 1;
        if (limit >= length) {
            list.add(bs);
        } else {
            while (start_offset < lastOffsetOfMain) {
                while (bs[end_offset] != 0) {
                    end_offset--;
                }
                int size = end_offset - start_offset + 1;
                byte[] index = getBytesOfFile(bs, start_offset, size);
                list.add(index);
                start_offset = start_offset + size;
                end_offset = end_offset + limit;
                if (end_offset > length) {
                    end_offset = lastOffsetOfMain;
                }
            }
        }
        return list;
    }

    private byte[] getStringSection(byte[] bs) {
        long from_offset = 112;
        byte[] header_ids_size = getBytesOfFile(bs, 56, 4);
        long ids_count = getDecimalValue(header_ids_size);


        long to_offset = from_offset + (long) 4 * (ids_count - 1);

        long start_string_section = getDecimalValue(getBytesOfFile(bs, from_offset, 4));
        long end_string_section = getDecimalValue(getBytesOfFile(bs, to_offset, 4));
        long offset = end_string_section;

        do {
            end_string_section = getBytesOfFile(bs, offset, 1)[0];
            offset++;
        } while (end_string_section != 0);

        long size = offset - start_string_section;
        return getBytesOfFile(bs, start_string_section, size);
    }

    public void FF() {
        try {
            Util util3 = new Util();
            String parent = "C:\\Users\\sedej\\Downloads\\MEmu Download\\aoo\\app-test1\\";
            String path3 = parent + "classes.dex";
            String path4 = parent + "classes.txt";
            try (InputStream inputStream = new FileInputStream(path3)) {
                byte[] bs = util3.toByteArray(inputStream);
                byte[] stringSection = util3.getStringSection(bs);
                util3.writeToFile(stringSection, path4);
                ArrayList<byte[]> list = chunkBytes(stringSection);
                for (int i = 0; i < list.size(); i++) {
                    byte[] bs_i = list.get(i);
                    util3.writeToFile(bs_i, parent + "c_" + i + ".txt");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toByteArray(InputStream input) throws IOException {
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