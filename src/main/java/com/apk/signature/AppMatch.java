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

        System.out.println("Enter aapt2 file path:");
        Util.aapt2Path = myObj.nextLine();

        Util util = new Util();
        AppUtil appUtil = new AppUtil(util);
        ManifestUtil manifestUtil = new ManifestUtil();

        File fileTarget = new File(target_path);
        File fileSignature = new File(signature_path);
        long start = System.currentTimeMillis();
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

            ArrayList<String> manifestMatchedSignatures = new ArrayList<>();

            boolean permissionMatch, activitiesMatch, serviceMatch, receiverMatch;
            String signature_txt1;
            SignatureModel signatureModel1;
            ManifestModel signatureManifestModel1;
            for (File file_j : fileSignatureList) {
                try {
                    signature_txt1 = util.readFile(file_j);
                    signatureModel1 = new AppMatch().parseSignature(signature_txt1);
                    signatureManifestModel1 = signatureModel1.getManifestModel();
                } catch (Exception e) {
                    continue;
                }

                boolean permissionEmpty = signatureManifestModel1.getPermission().get(0).isEmpty(),
                        activitiesEmpty = signatureManifestModel1.getActivities().get(0).isEmpty(),
                        serviceEmpty = signatureManifestModel1.getServices().get(0).isEmpty(),
                        receiverEmpty = signatureManifestModel1.getReceivers().get(0).isEmpty();

                if (permissionEmpty) {
                    permissionMatch = true;
                } else {
                    permissionMatch = util.contains(appManifestModel.getPermission(), signatureManifestModel1.getPermission());
                }

                if (activitiesEmpty) {
                    activitiesMatch = true;
                } else {
                    activitiesMatch = util.contains(appManifestModel.getActivities(), signatureManifestModel1.getActivities());
                }

                if (serviceEmpty) {
                    serviceMatch = true;
                } else {
                    serviceMatch = util.contains(appManifestModel.getServices(), signatureManifestModel1.getServices());
                }

                if (receiverEmpty) {
                    receiverMatch = true;
                } else {
                    receiverMatch = util.contains(appManifestModel.getReceivers(), signatureManifestModel1.getReceivers());
                }
                boolean manifestMatch = permissionMatch && activitiesMatch && serviceMatch && receiverMatch;

                if (manifestMatch) {
                    manifestMatchedSignatures.add(file_j.getAbsolutePath());
                }
            }

            ArrayList<File> dexFiles = util.generateDex(file_i.getAbsolutePath());
            boolean result = false;
            for (File dexFile : dexFiles) {
                Util.TEMP_DEX_PATH = util.getWorkingFilePath(dexFile);
                boolean stringMatch = false;
                try {
                    RandomAccessFile raf = new RandomAccessFile(dexFile, "r");
                    HashMap<String, byte[]> header = util.getHeader(raf);
                    ItemsString itemsString = new ItemsString();

                    for (String s : manifestMatchedSignatures) {
                        File file = new File(s);
                        String signature_txt = util.readFile(file);
                        SignatureModel signatureModel = new AppMatch().parseSignature(signature_txt);
                        ArrayList<String> strings = signatureModel.getStrings();

                        for (String str : strings) {
                            stringMatch = appUtil.getAddressFromHexStringByteByByteInPeriod(header, raf, str.toUpperCase(), itemsString, signatureModel.getStart(), signatureModel.getEnd());
                            if (stringMatch) {
                                break;
                            }
                        }

                        result = stringMatch;

                        if (result) {
                            System.out.println("!!!this is malware!!!" + " matched by: " + file.getName());
                            break;
                        } /*else {
                            System.out.println("permissionMatch:" + permissionMatch + "-activitiesMatch:" + activitiesMatch +
                                    "-serviceMatch:" + serviceMatch + "-receiverMatch:" + receiverMatch +
                                    "-stringMatch:" + stringMatch + "-methodMatch:" + methodMatch);
                        }*/
                    }
                    if (result)
                        break;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        Util.runDuration(start);
    }

    public SignatureModel parseSignature(String signature) {
        //String regex = "!(.*)@(.*)#(.*)\\$(.*)%(.*)\\^(.*)";
        String regex = "!(.*)@(.*)#(.*)\\$(.*)%(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(signature);

        if (matcher.find()) {
            int startIndex = 0;
            int endIndex = 0;
            String permissions = matcher.group(1);
            String activities = matcher.group(2);
            String service = matcher.group(3);
            String receivers = matcher.group(4);
            String strings = matcher.group(5);
            if (strings.contains("[")) {
                String reg = "(.+)\\[(.+)-(.+)]";
                Pattern pattern1 = Pattern.compile(reg);
                Matcher matcher1 = pattern1.matcher(strings);
                if (matcher1.find()) {
                    strings = matcher1.group(1);
                    startIndex = Integer.parseInt(matcher1.group(2));
                    endIndex = Integer.parseInt(matcher1.group(3));
                }
            }
            //String methods = matcher.group(6);


            String[] permissions_list = permissions.split(",");
            String[] activities_list = activities.split(",");
            String[] service_list = service.split(",");
            String[] receivers_list = receivers.split(",");
            String[] strings_list = strings.split(",");
            //String[] methods_list = methods.split(",");

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
            //ArrayList<String> methodArrayList = new ArrayList<>(Arrays.asList(methods_list));

            manifestModel.setPermission(permissionArrayList);
            manifestModel.setActivities(activitiesArrayList);
            manifestModel.setServices(serviceArrayList);
            manifestModel.setReceivers(receiversArrayList);

            SignatureModel signatureModel = new SignatureModel();

            signatureModel.setManifestModel(manifestModel);
            signatureModel.setStrings(stringsArrayList);
            signatureModel.setStart(startIndex);
            signatureModel.setEnd(endIndex);
            //signatureModel.setMethods(methodArrayList);

            return signatureModel;
        }
        return null;
    }
}
