package com.apk.signature.Util;

import com.apk.signature.DB.SQLiteJDBC;
import com.apk.signature.Items.Item;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Model.MalwareModel;
import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Model.StringModel;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.*;

public class MatchCore {

    private final ArrayList<String> unscannables = new ArrayList<>();
    AppUtil util = new AppUtil();
    private int totalFiles, totalApk, unscannable;

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

    public ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signatureModels) {
        ArrayList<MalwareModel> malwareModels = new ArrayList<>();
        ManifestUtil manifestUtil = new ManifestUtil();
        ItemsString itemsString = new ItemsString();
        for (File file_i : fileTargetList) {
            totalFiles++;
            Util.print("********************" + file_i.getAbsolutePath() + "********************");
            MalwareModel malwareModel = new MalwareModel();
            String manifest = manifestUtil.decodeManifest(file_i);
            if (manifest.isEmpty()) {
                unscannable++;
                unscannables.add(file_i.getAbsolutePath());
                printYellow("Unscannable");
                continue;
            } else {
                totalApk++;
            }
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
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] stream = IOUtils.toByteArray(inputStream);
                                //HashMap<String, byte[]> header = util.getStringHeader(stream);
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
                                //stream.close();
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

    private boolean getStringAddressFromHexString(byte[] stream, int ids_count, String text, Item tClass, int periodStartIndex, int periodEndIndex) {
        long ids_offset = 112;
        if (periodStartIndex < ids_count) {
            String[] splitText = util.splitTwoByTwo(text);
            if (periodEndIndex > ids_count || periodEndIndex == 0) {
                periodEndIndex = ids_count;
            }
            ids_offset = ids_offset + (long) tClass.data_size * periodStartIndex;
            for (int i = periodStartIndex; i <= periodEndIndex; i++) {
                boolean ss = tClass.searchDataByte(null, stream, ids_offset, splitText);
                if (ss) {
                    printYellow("{ hex:" + text);
                    printYellow("  ids_index: " + i + " }");
                    return true;
                }
                ids_offset = ids_offset + tClass.data_size;
            }
        }
        return false;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getTotalApk() {
        return totalApk;
    }

    public int getUnscannable() {
        return unscannable;
    }

    public ArrayList<String> getUnscannables() {
        return unscannables;
    }

}
