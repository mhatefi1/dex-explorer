package com.apk.signature;

import com.apk.signature.Items.ItemsMethod;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.ManifestUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppMatch {
    Util util = new Util();

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter signature file or folder");
        String signature_path = myObj.nextLine();

        System.out.println("Enter target file or folder");
        String target_path = myObj.nextLine();

        Util util = new Util();
        AppUtil appUtil = new AppUtil(util);
        ManifestUtil manifestUtil = new ManifestUtil();

        File fileTarget = new File(target_path);
        File fileSignature = new File(signature_path);

        ArrayList<File> fileTargetList = util.getFileListByFormat(fileTarget.getAbsolutePath(), ".apk");
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt");

        if (fileTargetList.isEmpty()) {
            fileTargetList = util.getFileListByFormat(fileTarget.getAbsolutePath(), ".dex");
        }

        for (File file_i : fileTargetList) {
            System.out.println("**********************************************************");
            System.out.println(file_i.getAbsolutePath());
            String manifest = manifestUtil.dumpManifest(file_i.getAbsolutePath());
            ManifestModel appManifestModel = manifestUtil.matchManifest(manifest);

            for (File file_j : fileSignatureList) {
                String signature_txt = util.readFile(file_j);
                SignatureModel signatureModel = new AppMatch().parseSignature(signature_txt);
                ManifestModel signatureManifestModel = signatureModel.getManifestModel();
                boolean permissionMatch = util.contains(appManifestModel.getPermission(), signatureManifestModel.getPermission());
                boolean activitiesMatch = util.contains(appManifestModel.getActivities(), signatureManifestModel.getActivities());
                boolean serviceMatch = util.contains(appManifestModel.getServices(), signatureManifestModel.getServices());
                boolean receiverMatch = util.contains(appManifestModel.getReceivers(), signatureManifestModel.getReceivers());

                ArrayList<String> strings = signatureModel.getStrings();
                ArrayList<String> methods = signatureModel.getMethods();

                File dexFile = util.generateDex(file_i.getAbsolutePath()).get(0);
                Util.TEMP_DEX_PATH = util.getWorkingFilePath(dexFile);
                boolean stringMatch = false, methodMatch = false;
                try {
                    RandomAccessFile raf = new RandomAccessFile(dexFile, "r");
                    HashMap<String, byte[]> header = util.getHeader(raf);
                    ItemsString itemsString = new ItemsString();
                    ItemsMethod itemsMethod = new ItemsMethod();

                    for (String s : strings) {
                        stringMatch = appUtil.getAddressFromHexString(header, raf, s.toUpperCase(), itemsString);
                        if (stringMatch) {
                            break;
                        }
                    }
                    for (String m : methods) {
                        methodMatch = appUtil.getAddressFromHexString(header, raf, m.toUpperCase(), itemsMethod);
                        if (methodMatch) {
                            break;
                        }
                    }

                    boolean result = permissionMatch && activitiesMatch && serviceMatch && receiverMatch && stringMatch && methodMatch;

                    if (result) {
                        System.out.println("!!!this is malware!!!");
                    } else {
                        System.out.println("permissionMatch:" + permissionMatch + "-activitiesMatch:" + activitiesMatch +
                                "-serviceMatch:" + serviceMatch + "-receiverMatch:" + receiverMatch +
                                "-stringMatch:" + stringMatch + "-methodMatch:" + methodMatch);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public SignatureModel parseSignature(String signature) {
        String regex = "!(.*)@(.*)#(.*)\\$(.*)%(.*)\\^(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(signature);

        if (matcher.find()) {
            String permissions = matcher.group(1);
            String activities = matcher.group(2);
            String service = matcher.group(3);
            String receivers = matcher.group(4);
            String strings = matcher.group(5);
            String methods = matcher.group(6);

            String[] permissions_list = permissions.split(",");
            String[] activities_list = activities.split(",");
            String[] service_list = service.split(",");
            String[] receivers_list = receivers.split(",");
            String[] strings_list = strings.split(",");
            String[] methods_list = methods.split(",");

            ManifestModel manifestModel = new ManifestModel();

            ArrayList<String> permissionArrayList = new ArrayList<>();
            ArrayList<String> activitiesArrayList = new ArrayList<>();
            ArrayList<String> serviceArrayList = new ArrayList<>();
            ArrayList<String> receiversArrayList = new ArrayList<>();

            for (String s : permissions_list) {
                s = util.hexStringToUTF8(s);
                permissionArrayList.add(s);
            }
            for (String s : activities_list) {
                s = util.hexStringToUTF8(s);
                activitiesArrayList.add(s);
            }
            for (String s : service_list) {
                s = util.hexStringToUTF8(s);
                serviceArrayList.add(s);
            }
            for (String s : receivers_list) {
                s = util.hexStringToUTF8(s);
                receiversArrayList.add(s);
            }

            ArrayList<String> stringsArrayList = new ArrayList<>(Arrays.asList(strings_list));
            ArrayList<String> methodArrayList = new ArrayList<>(Arrays.asList(methods_list));

            manifestModel.setPermission(permissionArrayList);
            manifestModel.setActivities(activitiesArrayList);
            manifestModel.setServices(serviceArrayList);
            manifestModel.setReceivers(receiversArrayList);

            SignatureModel signatureModel = new SignatureModel();

            signatureModel.setManifestModel(manifestModel);
            signatureModel.setStrings(stringsArrayList);
            signatureModel.setMethods(methodArrayList);

            return signatureModel;
        }
        return null;
    }
}
