package com.apk.signature;

import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.Util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class AppGenerateSignature {
    static ArrayList<String> string_list = new ArrayList<>();
    static ArrayList<String> permission_list = new ArrayList<>();
    static ArrayList<String> activity_list = new ArrayList<>();
    static ArrayList<String> service_list = new ArrayList<>();
    static ArrayList<String> receiver_list = new ArrayList<>();


    public static void main(String[] args) {

        SignatureModel signature = new SignatureModel();
        Util util = new Util();
        Scanner myObj = new Scanner(System.in);

        System.out.println("Write permissions or press Enter key if is empty:");
        String permission = myObj.nextLine();
        permission_list = getList(permission, util);

        System.out.println("Write activities or press Enter key if is empty:");
        String activities = myObj.nextLine();
        activity_list = getList(activities, util);

        System.out.println("Write services or press Enter key if is empty:");
        String services = myObj.nextLine();
        service_list = getList(services, util);

        System.out.println("Write receivers or press Enter key if is empty:");
        String receivers = myObj.nextLine();
        receiver_list = getList(receivers, util);

        ManifestModel manifest = new ManifestModel();
        manifest.setPermission(permission_list);
        manifest.setActivities(activity_list);
        manifest.setServices(service_list);
        manifest.setReceivers(receiver_list);

        callString(myObj);

        signature.setManifestModel(manifest);
        signature.setStrings(string_list);

        signature.setManifestModel(manifest);
        signature.setStrings(string_list);

        String result = generateSig(signature);

        write(result, myObj);
    }

    public static void callString(Scanner myObj) {
        System.out.println("Write string or press Enter key if is empty:");
        String string = myObj.nextLine();
        if (!string.isEmpty()) {
            String hex = new Util().stringToHexString(string);
            String interval = "";
            /*System.out.println("is this string complete? [y/n]");
            String state = myObj.nextLine();
            if (state.equals("y")) {
                hex = hex + "00";
            }*/
            System.out.println("Set start index or press Enter key if is empty:");
            String start = myObj.nextLine();
            int start_index;
            if (start.isEmpty()) {
                start_index = 0;
            } else {
                start_index = Integer.parseInt(start);
            }

            System.out.println("Set end index or press Enter key if is empty:");
            String end = myObj.nextLine();
            int end_index;
            if (end.isEmpty()) {
                end_index = 0;
            } else {
                end_index = Integer.parseInt(end);
            }
            if (start_index > 0) {
                interval = "[" + start_index + "-" + end_index + "]";
            }
            string_list.add(hex + interval);
            System.out.println("Do you want add next string? [y/n]");
            String state2 = myObj.nextLine();
            if (state2.equals("y")) {
                callString(myObj);
            }
        }
    }

    public static String generateSig(SignatureModel signature) {
        StringBuilder builder = new StringBuilder();
        ManifestModel manifestModel = signature.getManifestModel();
        builder.append("!");
        generateSignatureItems(manifestModel.getPermission(), builder);
        builder.append("@");
        generateSignatureItems(manifestModel.getActivities(), builder);
        builder.append("#");
        generateSignatureItems(manifestModel.getServices(), builder);
        builder.append("$");
        generateSignatureItems(manifestModel.getReceivers(), builder);
        builder.append("%");
        generateSignatureItems(signature.getStrings(), builder);
        return builder.toString();
    }

    public static void generateSignatureItems(ArrayList<String> arrayList, StringBuilder builder) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            builder.append(arrayList.get(i));
            if (i != size - 1) {
                builder.append(",");
            }
        }
    }

    public static ArrayList<String> getList(String text, Util util) {
        String[] list = text.split(",");
        ArrayList<String> arrayList = new ArrayList<>();
        for (String s : list) {
            String hex = util.stringToHexString(s);
            arrayList.add(hex);
        }
        return arrayList;
    }

    public static void print(ArrayList<String> list) {
        for (String s : list) {
            System.out.println(s);
        }
    }

    public static void write(String text, Scanner myObj) {
        System.out.println("Write path to save signature or press Enter key to save in current folder:");
        Util.printYellow("note: current folder is " + System.getProperty("user.dir"));
        String path = myObj.nextLine();

        System.out.println("Enter signature name:");
        String name = myObj.nextLine();

        name = name + ".txt";
        if (path.isEmpty()) {
            path = System.getProperty("user.dir");
        }
        File file = new File(path, name);
        try {
            boolean created = file.createNewFile();
            if (created) {
                FileWriter myWriter = new FileWriter(file);
                myWriter.write(text);
                myWriter.close();
            } else {
                System.out.println(file.getAbsolutePath() + " Already Exists");
                write(text, myObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
