package com.apk.signature.Util;

import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Model.StringModel;
import org.apache.pdfbox.io.RandomAccessFile;
import org.fusesource.jansi.AnsiConsole;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fusesource.jansi.Ansi.ansi;

public class Util extends FileUtil {

    private static final String defaultAapt2Path = "C:\\scanner\\aapt2.exe";
    public static String TEMP_DEX_PATH = "";
    public static String aapt2Path = "";//public static String aapt2Path = "C:\\Users\\sedej\\AppData\\Local\\Android\\Sdk\\build-tools\\34.0.0\\aapt2.exe";
    public static String RESET = "\u001B[0m";
    public static String RED = "\u001B[31m";
    public static String GREEN = "\u001B[32m";
    public static String YELLOW = "\u001B[33m";

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

    public SignatureModel createSignatureModel(String permissions, String activities, String services, String receivers, String strings) {
        String[] permissions_list = permissions.split(",");
        String[] activities_list = activities.split(",");
        String[] service_list = services.split(",");
        String[] receivers_list = receivers.split(",");
        String[] strings_list = strings.split(",");

        ManifestModel manifestModel = new ManifestModel();

        ArrayList<String> permissionArrayList = new ArrayList<>();
        ArrayList<String> activitiesArrayList = new ArrayList<>();
        ArrayList<String> serviceArrayList = new ArrayList<>();
        ArrayList<String> receiversArrayList = new ArrayList<>();

        for (String s : permissions_list) {
            s = hexStringToUTF8(s);
            permissionArrayList.add(s);
        }
        for (String s : activities_list) {
            s = hexStringToUTF8(s);
            activitiesArrayList.add(s);
        }
        for (String s : service_list) {
            s = hexStringToUTF8(s);
            serviceArrayList.add(s);
        }
        for (String s : receivers_list) {
            s = hexStringToUTF8(s);
            receiversArrayList.add(s);
        }

        //ArrayList<String> stringsArrayList = new ArrayList<>(Arrays.asList(strings_list));
        ArrayList<StringModel> stringModels = new ArrayList<>();
        for (String s : strings_list) {
            String reg = "(.+)\\[(.+)-(.+)]";
            Pattern pattern1 = Pattern.compile(reg);
            Matcher matcher1 = pattern1.matcher(s);
            if (matcher1.find()) {
                s = matcher1.group(1);
                int startIndex = Integer.parseInt(matcher1.group(2));
                int endIndex = Integer.parseInt(matcher1.group(3));
                StringModel model = new StringModel(startIndex, endIndex, s);
                stringModels.add(model);
            }
        }

        manifestModel.setPermission(permissionArrayList);
        manifestModel.setActivities(activitiesArrayList);
        manifestModel.setServices(serviceArrayList);
        manifestModel.setReceivers(receiversArrayList);

        SignatureModel signatureModel = new SignatureModel();

        signatureModel.setManifestModel(manifestModel);
        signatureModel.setStringModels(stringModels);

        //signatureModel.setStrings(stringsArrayList);
        //signatureModel.setStart(startIndex);
        //signatureModel.setEnd(endIndex);

        return signatureModel;
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

    public HashMap<String, byte[]> getHeader(ByteArrayInputStream stream) {

        HashMap<String, byte[]> header = new HashMap<>();

        try {
            byte[] header_magic = getBytesOfFile(stream, 0, 8);
            byte[] header_checksum = getBytesOfFile(stream, 8, 4);
            byte[] header_signature = getBytesOfFile(stream, 12, 20);
            byte[] header_file_size = getBytesOfFile(stream, 32, 4);
            byte[] header_header_size = getBytesOfFile(stream, 36, 4);
            byte[] header_endian_tag = getBytesOfFile(stream, 40, 4);
            byte[] header_link_size = getBytesOfFile(stream, 44, 4);
            byte[] header_link_off = getBytesOfFile(stream, 48, 4);
            byte[] header_map_off = getBytesOfFile(stream, 52, 4);
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

            header.put("header_magic", header_magic);
            header.put("header_checksum", header_checksum);
            header.put("header_signature", header_signature);
            header.put("header_file_size", header_file_size);
            header.put("header_header_size", header_header_size);
            header.put("header_endian_tag", header_endian_tag);
            header.put("header_link_size", header_link_size);
            header.put("header_link_off", header_link_off);
            header.put("header_map_off", header_map_off);
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
}