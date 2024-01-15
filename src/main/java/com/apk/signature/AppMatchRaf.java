package com.apk.signature;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.ItemsRaf.ItemsStringRaf;
import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.ManifestUtil;
import com.apk.signature.Util.Util;
import org.apache.pdfbox.io.RandomAccessFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.apk.signature.Util.Util.printGreen;
import static com.apk.signature.Util.Util.printRed;

public class AppMatchRaf {
    Util util = new Util();

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter signature file or folder");
        String signature_path = myObj.nextLine();

        System.out.println("Enter target file or folder");
        String target_path = myObj.nextLine();

        Util.aapt2Path = Util.setAapt2Path("");

        Util util = new Util();
        AppUtil appUtil = new AppUtil(util);
        ManifestUtil manifestUtil = new ManifestUtil();

        File fileTarget = new File(target_path);
        File fileSignature = new File(signature_path);
        long start = System.currentTimeMillis();
        ArrayList<File> fileTargetList = new ArrayList<>();
        fileTargetList = util.getRecursiveFileListByFormat(fileTargetList, fileTarget.getAbsolutePath(), "");
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt");
        ArrayList<File> dbSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".db");
        ArrayList<SignatureModel> signatureModels = new ArrayList<>();

        for (File file : fileSignatureList) {
            try {
                String signature_txt1 = util.readFile(file);
                SignatureModel signatureModel1 = new AppMatchRaf().parseSignature(signature_txt1);
                signatureModel1.setName(file.getName());
                signatureModels.add(signatureModel1);
            } catch (Exception ignored) {
            }
        }

        for (File file : dbSignatureList) {
            SQLiteJDBC jdbc = new SQLiteJDBC(file);
            ArrayList<SignatureModel> dbList = jdbc.select();
            signatureModels.addAll(dbList);
        }

        if (fileTargetList.isEmpty()) {
            fileTargetList = util.getFileListByFormat(fileTarget.getAbsolutePath(), ".dex");
        }

        for (File file_i : fileTargetList) {
            System.out.println("**********************************************************");
            System.out.println(file_i.getAbsolutePath());
            String manifest = manifestUtil.dumpManifest(file_i.getAbsolutePath());
            ManifestModel appManifestModel = manifestUtil.matchManifest(manifest);

            ArrayList<SignatureModel> manifestMatchedSignatures = new ArrayList<>();

            boolean permissionMatch, activitiesMatch, serviceMatch, receiverMatch;
            for (SignatureModel signatureModel : signatureModels) {
                ManifestModel signatureManifestModel = signatureModel.getManifestModel();

                boolean permissionEmpty = signatureManifestModel.getPermission().get(0).isEmpty(),
                        activitiesEmpty = signatureManifestModel.getActivities().get(0).isEmpty(),
                        serviceEmpty = signatureManifestModel.getServices().get(0).isEmpty(),
                        receiverEmpty = signatureManifestModel.getReceivers().get(0).isEmpty();

                if (permissionEmpty) {
                    permissionMatch = true;
                } else {
                    permissionMatch = util.contains(appManifestModel.getPermission(), signatureManifestModel.getPermission());
                }

                if (activitiesEmpty) {
                    activitiesMatch = true;
                } else {
                    activitiesMatch = util.contains(appManifestModel.getActivities(), signatureManifestModel.getActivities());
                }

                if (serviceEmpty) {
                    serviceMatch = true;
                } else {
                    serviceMatch = util.contains(appManifestModel.getServices(), signatureManifestModel.getServices());
                }

                if (receiverEmpty) {
                    receiverMatch = true;
                } else {
                    receiverMatch = util.contains(appManifestModel.getReceivers(), signatureManifestModel.getReceivers());
                }
                boolean manifestMatch = permissionMatch && activitiesMatch && serviceMatch && receiverMatch;

                if (manifestMatch) {
                    manifestMatchedSignatures.add(signatureModel);
                }
            }

            ArrayList<File> dexFiles = util.generateDex(file_i.getAbsolutePath());
            boolean isMalware = false;
            String result_detailes = "";
            for (File dexFile : dexFiles) {
                Util.TEMP_DEX_PATH = util.getWorkingFilePath(dexFile);
                boolean stringMatch = false;
                try {
                    RandomAccessFile raf = new RandomAccessFile(dexFile, "r");
                    HashMap<String, byte[]> header = util.getHeader(raf);
                    ItemsStringRaf itemsString = new ItemsStringRaf();

                    for (SignatureModel signatureModel : manifestMatchedSignatures) {
                        ArrayList<String> strings = signatureModel.getStrings();

                        for (String str : strings) {
                            stringMatch = appUtil.getAddressFromHexString(header, raf, str.toUpperCase(), itemsString, signatureModel.getStart(), signatureModel.getEnd());
                            if (!stringMatch) {
                                break;
                            }
                        }

                        isMalware = stringMatch;

                        if (isMalware) {
                            result_detailes = "!!!this is malware!!!" + " matched by: " + signatureModel.getName();
                            break;
                        }
                    }
                    if (isMalware) {
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isMalware) {
                printRed(result_detailes);
            } else {
                printGreen("Clean");
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
            String services = matcher.group(3);
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
            return util.createSignatureModel(permissions, activities, services, receivers, strings, startIndex, endIndex);
        }
        return null;
    }
}
