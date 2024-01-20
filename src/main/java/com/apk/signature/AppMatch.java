package com.apk.signature;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Model.MalwareModel;
import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.ScanResult;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Util.AppUtil;
import com.apk.signature.Util.ManifestUtil;
import com.apk.signature.Util.Util;
import com.google.gson.Gson;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.printGreen;
import static com.apk.signature.Util.Util.printRed;

public class AppMatch {
    private static int totalFiles, totalApk;
    Util util = new Util();

    public static void main(String[] args) {
        String signature_path;
        String target_path;
        ArrayList<File> AllTargetFilePath = new ArrayList<>();
        AppMatch appMatch = new AppMatch();
        if (args.length > 0 && args[0].equals("6")) {
            signature_path = args[1];
            target_path = args[2];
            File file = new File(target_path);
            if (file.getName().endsWith(".txt")) {
                AllTargetFilePath = appMatch.getArgsFileSignatureList(target_path);
            } else {
                AllTargetFilePath.add(file);
            }
        } else {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Enter signature file or folder");
            signature_path = myObj.nextLine();

            System.out.println("Enter target file or folder");
            target_path = myObj.nextLine();
        }

        File fileSignature = new File(signature_path);
        if (!fileSignature.exists()) {
            printRed("signature path doesn't exist");
            System.exit(0);
        }

        File targetFile = new File(target_path);
        if (!targetFile.exists()) {
            printRed("target path doesn't exist");
            System.exit(0);
        }
        Util.aapt2Path = Util.setAapt2Path("");

        Util util = new Util();

        ArrayList<SignatureModel> signatureModels = appMatch.getSigModel(fileSignature);

        if (AllTargetFilePath.isEmpty()) {
            AllTargetFilePath.add(targetFile);
        }

        long start = System.currentTimeMillis();
        ArrayList<MalwareModel> malwareList = new ArrayList<>();
        for (File f : AllTargetFilePath) {
            ArrayList<File> targetFileList = new ArrayList<>();
            if (f.isDirectory()) {
                targetFileList = util.getRecursiveFileListByFormat(targetFileList, f.getAbsolutePath(), "");
                targetFileList = util.getRecursiveFileListByFormat(targetFileList, f.getAbsolutePath(), ".apk");
            } else {
                targetFileList.add(f);
            }
            ArrayList<MalwareModel> list = appMatch.match(targetFileList, signatureModels);
            malwareList.addAll(list);
        }

        long time = Util.runDuration(start);
        ScanResult scanResult = new ScanResult();
        scanResult.setTotalFiles(totalFiles);
        scanResult.setTotalApk(totalApk);
        scanResult.setTotalSignature(signatureModels.size());
        scanResult.setTotalMalware(malwareList.size());
        scanResult.setTotalTime(time);
        scanResult.setMalwareList(malwareList);

        Gson gson = new Gson();
        String json = gson.toJson(scanResult);

        System.out.println(json);
        new Util().writeToFile(json, System.getProperty("user.dir") + "\\report-" + System.currentTimeMillis() + ".json");
    }

    private ArrayList<SignatureModel> getSigModel(File fileSignature) {
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt");
        ArrayList<File> dbSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".db");
        ArrayList<SignatureModel> signatureModels = new ArrayList<>();

        for (File file : fileSignatureList) {
            try {
                String signature_txt1 = util.readFile(file);
                SignatureModel signatureModel1 = new AppMatch().parseSignature(signature_txt1);
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
        return signatureModels;
    }

    private ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signatureModels) {
        AppUtil appUtil = new AppUtil(util);
        ArrayList<MalwareModel> malwareModels = new ArrayList<>();
        ManifestUtil manifestUtil = new ManifestUtil();
        ItemsString itemsString = new ItemsString();
        for (File file_i : fileTargetList) {
            System.out.println("**********************************************************");
            System.out.println(file_i.getAbsolutePath());
            MalwareModel malwareModel = new MalwareModel();
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

            boolean stringMatch = false;
            boolean isMalware = false;
            String result_detailes = "";
            try {
                try (ZipFile zipFile = new ZipFile(file_i.getAbsolutePath())) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] bs = IOUtils.toByteArray(inputStream);
                                ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                HashMap<String, byte[]> header = util.getHeader(stream);

                                for (SignatureModel signatureModel : manifestMatchedSignatures) {
                                    ArrayList<String> strings = signatureModel.getStrings();

                                    for (String str : strings) {
                                        stringMatch = appUtil.getAddressFromHexString(header, stream, str.toUpperCase(),
                                                itemsString, signatureModel.getStart(), signatureModel.getEnd());
                                        if (!stringMatch) {
                                            break;
                                        }
                                    }

                                    isMalware = stringMatch;
                                    if (isMalware) {
                                        result_detailes = "!!!this is malware!!!" + " matched by: " + signatureModel.getName();
                                        malwareModel.setMalwareFamily(signatureModel.getName());
                                        malwareModel.setAppName(file_i.getAbsolutePath());
                                        malwareModels.add(malwareModel);
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
                    }
                    totalApk++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isMalware) {
                printRed(result_detailes);
            } else {
                printGreen("Clean");
            }

            totalFiles++;
        }
        return malwareModels;
    }

    private ArrayList<File> getArgsFileSignatureList(String path) {
        ArrayList<File> argsFileSignatureList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                File file = new File(line);
                argsFileSignatureList.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return argsFileSignatureList;
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
