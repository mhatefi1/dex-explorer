package com.apk.signature.Util;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.Items.Item;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Model.*;
import net.lingala.zip4j.exception.ZipException;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.*;

public class MatchCore {
    private static boolean dexNamePrinted;
    AppUtil util = new AppUtil();
    ArrayList<String> not_zip = new ArrayList<>();
    ArrayList<String> compress_mode = new ArrayList<>();
    ArrayList<String> split_zip = new ArrayList<>();
    ArrayList<String> unknown = new ArrayList<>();
    ArrayList<String> manifest = new ArrayList<>();
    ArrayList<String> dex = new ArrayList<>();
    private int totalFiles, totalApk;
    private Unscannable unscannableModel = new Unscannable();

    public ArrayList<SignatureModel> getSigModels(File fileSignature) {
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt", false);
        ArrayList<File> dbSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".db", false);
        ArrayList<SignatureModel> signatureModels = new ArrayList<>();

        for (File file : fileSignatureList) {
            try {
                String signature_txt1 = util.readFile(file);
                SignatureModel signatureModel1 = new SignatureUtil().parseSignature(signature_txt1);
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

    /*private ArrayList<SignatureModel> generateSignatureList(ManifestModel appManifestModel, ArrayList<SignatureModel> signatureModels) {
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
        return manifestMatchedSignatures;
    }*/

    public ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signature_list) {
        ArrayList<MalwareModel> malwareModels = new ArrayList<>();
        ManifestUtil util1 = new ManifestUtil();
        ItemsString itemsString = new ItemsString();
        totalFiles = fileTargetList.size();
        for (File file_i : fileTargetList) {
            Util.print("********************" + file_i.getAbsolutePath() + "********************");
            util1.readZip(file_i, new ReadBytesFromZipListener() {
                ArrayList<SignatureModel> manifestMatchedSignatures = new ArrayList<>();
                boolean apk = true;

                @Override
                public boolean onReadManifest(byte[] bs) {
                    try {
                        ManifestModel appManifestModel = util1.calcManifest(bs);
                        if (appManifestModel == null) {
                            return false;
                        }
                        manifestMatchedSignatures = util1.compareAppManifestWithSignatures(signature_list, appManifestModel);
                    } catch (Exception e) {
                        onManifestError(e);
                    }
                    return !manifestMatchedSignatures.isEmpty();
                }

                @Override
                public boolean onReadDex(byte[] bs) {
                    MalwareModel malwareModel = new MalwareModel();
                    boolean stringMatch = false;
                    String result_detailes = "";
                    dexNamePrinted = false;
                    try {
                        byte[] header_ids_size = util.getBytesOfFile(bs, 56, 4);
                        long ids_count = util.getDecimalValue(header_ids_size);
                        for (SignatureModel signatureModel : manifestMatchedSignatures) {
                            ArrayList<StringModel> strings = signatureModel.getStringModels();
                            for (StringModel stringModel : strings) {
                                stringMatch = getStringAddressFromHexString(bs, (int) ids_count, stringModel.getString().toUpperCase(),
                                        itemsString, stringModel.getStart(), stringModel.getEnd());
                                if (!stringMatch) {
                                    break;
                                }
                            }
                            malwareModel.setMalware(stringMatch);
                            if (malwareModel.isMalware()) {
                                result_detailes = "!!!this is malware!!!" + " matched by: " + signatureModel.getName();
                                malwareModel.setMalwareFamily(signatureModel.getName());
                                malwareModel.setAppName(file_i.getAbsolutePath());
                                malwareModels.add(malwareModel);
                                break;
                            }
                        }
                        if (malwareModel.isMalware()) {
                            printRed(result_detailes);
                        }
                    } catch (Exception e) {
                        onDexError(e);
                    }
                    return malwareModel.isMalware();
                }

                @Override
                public void onZipError(Exception e) {
                    printYellow(e.getMessage());
                    String error = e.getMessage();
                    if (error.contains("compress")) {
                        compress_mode.add(file_i.getAbsolutePath());
                    } else if (error.contains("split")) {
                        split_zip.add(file_i.getAbsolutePath());
                    } else if (error.contains("not a zip file")) {
                        apk = false;
                        not_zip.add(file_i.getAbsolutePath());
                    } else {
                        unknown.add(file_i.getAbsolutePath());
                    }
                }

                @Override
                public void onManifestError(Exception e) {
                    printYellow(e.getMessage());
                    manifest.add(file_i.getAbsolutePath());
                }

                @Override
                public void onDexError(Exception e) {
                    printYellow(e.getMessage());
                    dex.add(file_i.getAbsolutePath());
                }

                @Override
                public void onEnd() {
                    if (apk) totalApk++;
                }
            });
        }
        Unscannable unscannable1 = new Unscannable();
        unscannable1.setNot_zip(not_zip);
        unscannable1.setCompress_mode(compress_mode);
        unscannable1.setSplit_zip(split_zip);
        unscannable1.setUnknown(unknown);
        unscannable1.setManifest(manifest);
        unscannable1.setDex(dex);
        setUnscannableModel(unscannable1);
        return malwareModels;
    }
    /*public ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signatureModels) {
        ArrayList<MalwareModel> malwareModels = new ArrayList<>();
        ManifestUtil manifestUtil = new ManifestUtil();
        ItemsString itemsString = new ItemsString();
        for (File file_i : fileTargetList) {
            totalFiles++;
            Util.print("********************" + file_i.getAbsolutePath() + "********************");
            MalwareModel malwareModel = new MalwareModel();
            ManifestModel appManifestModel = manifestUtil.matchManifestNew(file_i);
            if (appManifestModel == null) {
                unscannable++;
                unscannables.add(file_i.getAbsolutePath());
                printYellow("Unscannable");
                continue;
            } else {
                totalApk++;
            }
            ArrayList<SignatureModel> manifestMatchedSignatures = generateSignatureList(appManifestModel, signatureModels);

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
                                dexName = entry.getName();
                                dexNamePrinted = false;
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] stream = IOUtils.toByteArray(inputStream);
                                byte[] header_ids_size = util.getBytesOfFile(stream, 56, 4);
                                long ids_count = util.getDecimalValue(header_ids_size);
                                for (SignatureModel signatureModel : manifestMatchedSignatures) {
                                    ArrayList<StringModel> strings = signatureModel.getStringModels();
                                    for (StringModel stringModel : strings) {
                                        stringMatch = getStringAddressFromHexString(stream, (int) ids_count, stringModel.getString().toUpperCase(),
                                                itemsString, stringModel.getStart(), stringModel.getEnd());
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
                                inputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                unscannable++;
                unscannables.add(file_i.getAbsolutePath());
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
    }*/

    public ArrayList<File> getArgsFileSignatureList(String path) {
        ArrayList<File> argsFileSignatureList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                File file = new File(line);
                argsFileSignatureList.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return argsFileSignatureList;
    }

    private boolean getStringAddressFromHexString(byte[] stream, int ids_count, String text, Item tClass, int periodStartIndex, int periodEndIndex) {
        long ids_offset = 112;
        //  try {
        if (periodStartIndex < ids_count) {
            String[] splitText = util.splitTwoByTwo(text);
            if (periodEndIndex > ids_count || periodEndIndex == 0) {
                periodEndIndex = ids_count;
            }
            ids_offset = ids_offset + (long) tClass.data_size * periodStartIndex;
            for (int i = periodStartIndex; i <= periodEndIndex; i++) {
                boolean ss = tClass.searchDataByte(null, stream, ids_offset, splitText);
                if (ss) {
                    if (!dexNamePrinted) {
                        print(dexName + ":");
                        dexNamePrinted = true;
                    }
                    printYellow("{ hex:" + text);
                    printYellow("  ids_index: " + i + " }");
                    return true;
                }
                ids_offset = ids_offset + tClass.data_size;
            }
        }
        //  } catch (Exception e) {
        //      e.printStackTrace();
        //       return false;
        // }
        return false;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getTotalApk() {
        return totalApk;
    }

    public Unscannable getUnscannableModel() {
        return unscannableModel;
    }

    public void setUnscannableModel(Unscannable unscannableModel) {
        this.unscannableModel = unscannableModel;
    }

    public ArrayList<String> getNot_zip() {
        return not_zip;
    }

    public void setNot_zip(ArrayList<String> not_zip) {
        this.not_zip = not_zip;
    }

    public ArrayList<String> getCompress_mode() {
        return compress_mode;
    }

    public void setCompress_mode(ArrayList<String> compress_mode) {
        this.compress_mode = compress_mode;
    }

    public ArrayList<String> getSplit_zip() {
        return split_zip;
    }

    public void setSplit_zip(ArrayList<String> split_zip) {
        this.split_zip = split_zip;
    }

    public ArrayList<String> getManifest() {
        return manifest;
    }

    public void setManifest(ArrayList<String> manifest) {
        this.manifest = manifest;
    }

    public ArrayList<String> getDex() {
        return dex;
    }

    public void setDex(ArrayList<String> dex) {
        this.dex = dex;
    }
}
