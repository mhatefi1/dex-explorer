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

public class BinaryMatchCore {
    private static String dexName;
    private static boolean dexNamePrinted;
    private static String lastHex;
    private static int offset_of_last_hex;
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

    private ArrayList<SignatureModel> generateSignatureList(ManifestModel appManifestModel, ArrayList<SignatureModel> signatureModels) {
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
    }

    public ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signatureModels) {
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
    }

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

    private boolean getStringAddressFromHexString(byte[] stream, int ids_count, String text, Item tClass, int min, int max) {
        long ids_offset = 112;
        try {
            if (min < ids_count) {
                String[] splitText = util.splitTwoByTwo(text);
                if (max > ids_count || max == 0) {
                    max = ids_count;
                }

                while (min <= max) {
                    int m = min + (max - min) / 2;
                    long offset = ids_offset + (long) tClass.data_size * m;

                    //SearchResultStructure ss = searchDataByte(null, stream, offset, splitText);
                    boolean ss = searchDataByte(stream, offset, splitText);
                    if (ss) {
                        if (!dexNamePrinted) {
                            print(dexName + ":");
                            dexNamePrinted = true;
                        }
                        printYellow("{ hex:" + text);
                        printYellow("  ids_index: " + m + " }");
                        return true;
                    }
                    long f1 = util.stringHexToDecimal(lastHex);
                    String s0 = splitText[offset_of_last_hex];
                    long f2 = util.stringHexToDecimal(s0);

                    if (f1 < f2) {
                        min = m + 1;
                    } else {
                        max = m - 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean searchDataByte(byte[] stream, long start, String[] splitText) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(stream, start, ItemsString.string_data_off_size);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);
        while (true) {
            byte[] size_in_utf16_b = util.getBytesOfFile(stream, offset, 1);
            long size_in_utf16_l = util.getDecimalValue(size_in_utf16_b);
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }
        int i = 0;
        for (String s : splitText) {
            byte[] a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(stream, offset, 1);
            String hex = util.getHexValue(a_string_bit_in_MUTF8_format_b);
            if (!hex.equals(s)) {
                //return new SearchResultStructure(false, hex, i);
                lastHex = hex;
                offset_of_last_hex = i;
                return false;
            }
            offset++;
            i++;
        }
        ///return new SearchResultStructure(true);
        return true;
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

    /*private static class SearchResultStructure {
        boolean success;
        String lastHex;
        int offset;

        public SearchResultStructure(boolean success, String lastHex, int offset) {
            this.success = success;
            this.lastHex = lastHex;
            this.offset = offset;
        }

        public SearchResultStructure(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getLastHex() {
            return lastHex;
        }

        public void setLastHex(String lastHex) {
            this.lastHex = lastHex;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }*/
}
