package com.apk.signature.Util;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Model.MalwareModel;
import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.*;

public class MatchCore {

    AppUtil util = new AppUtil();
    private int totalFiles, totalApk, unscannable;
    private ArrayList<String> unScannedList = new ArrayList<>();

    public ArrayList<SignatureModel> getSigModel(File fileSignature) {
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt");
        ArrayList<File> dbSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".db");
        ArrayList<SignatureModel> signatureModels = new ArrayList<>();

        for (File file : fileSignatureList) {
            try {
                String signature_txt1 = util.readFile(file);
                SignatureModel signatureModel1 = parseSignature(signature_txt1);
                if (signatureModel1 != null) {
                    signatureModel1.setName(file.getName());
                    signatureModels.add(signatureModel1);
                }
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

    public ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signatureModels) {
        ArrayList<MalwareModel> malwareModels = new ArrayList<>();
        ManifestUtil manifestUtil = new ManifestUtil();
        ItemsString itemsString = new ItemsString();
        for (File file_i : fileTargetList) {
            totalFiles++;
            System.out.println("**********************************************************");
            System.out.println(file_i.getAbsolutePath());
            MalwareModel malwareModel = new MalwareModel();
            //String manifest = manifestUtil.dumpManifest(file_i.getAbsolutePath());
            String manifest = manifestUtil.decodeManifest(file_i);
            if (manifest.isEmpty()) {
                unscannable++;
                unScannedList.add(file_i.getAbsolutePath());
                printYellow("Unscannable");
                continue;
            }
            //ManifestModel appManifestModel = manifestUtil.matchDumpedManifest(manifest);
            ManifestModel appManifestModel = manifestUtil.matchDecodedManifest(manifest);

            ArrayList<SignatureModel> manifestMatchedSignatures = new ArrayList<>();

            boolean permissionMatch, activitiesMatch, serviceMatch, receiverMatch;
            for (SignatureModel signatureModel : signatureModels) {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
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
                            totalApk++;
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] bs = IOUtils.toByteArray(inputStream);
                                //ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                                HashMap<String, byte[]> header = util.getHeader(bs);

                                for (SignatureModel signatureModel : manifestMatchedSignatures) {
                                    ArrayList<String> strings = signatureModel.getStrings();

                                    for (String str : strings) {
                                        stringMatch = util.getAddressFromHexString(header, bs, str.toUpperCase(),
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
                }
            } catch (IOException e) {
                e.printStackTrace();
                unscannable++;
                unScannedList.add(file_i.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isMalware) {
                printRed(result_detailes);
            } else {
                printGreen("Clean");
            }
        }
        return malwareModels;
    }

    public ArrayList<File> getArgsFileSignatureList(String path) {
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

    private SignatureModel parseSignature(String signature) {
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

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getTotalApk() {
        return totalApk;
    }

    public void setTotalApk(int totalApk) {
        this.totalApk = totalApk;
    }

    public int getUnscannable() {
        return unscannable;
    }

    public void setUnscannable(int unscannable) {
        this.unscannable = unscannable;
    }

    public ArrayList<String> getUnScannedList() {
        return unScannedList;
    }

    public void setUnScannedList(ArrayList<String> unScannedList) {
        this.unScannedList = unScannedList;
    }

    public AppUtil getUtil() {
        return util;
    }

    public void setUtil(AppUtil util) {
        this.util = util;
    }
}
