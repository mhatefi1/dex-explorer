package com.apk.signature.Util;

import com.apk.signature.Items.Item;
import com.apk.signature.Items.ItemsString;
import com.apk.signature.Model.*;

import java.io.File;
import java.util.ArrayList;

import static com.apk.signature.Util.Util.*;

public class BinaryMatchCoreNewManifest {
    private static boolean dexNamePrinted;
    AppUtil util = new AppUtil();
    ArrayList<String> not_zip = new ArrayList<>();
    ArrayList<String> compress_mode = new ArrayList<>();
    ArrayList<String> split_zip = new ArrayList<>();
    ArrayList<String> unknown = new ArrayList<>();
    ArrayList<String> manifest = new ArrayList<>();
    ArrayList<String> dex = new ArrayList<>();
    ArrayList<String> notMatch = new ArrayList<>();
    long f1, f2;
    private int totalFiles, totalApk;
    private Unscannable unscannableModel = new Unscannable();

    public ArrayList<SignatureModel> getSigModels(File fileSignature) {
        ArrayList<File> fileSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".txt", false);
        //ArrayList<File> dbSignatureList = util.getFileListByFormat(fileSignature.getAbsolutePath(), ".db", false);
        ArrayList<SignatureModel> signatureModels = new ArrayList<>();

        for (File file : fileSignatureList) {
            try {
                ArrayList<String> signs = util.readLineByLine(file.getAbsolutePath());
                for (String s : signs) {
                    SignatureModel SignatureModel = new SignatureUtil().parseSignatureNewManifest(s, false);
                    if (SignatureModel != null) {
                        signatureModels.add(SignatureModel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*for (File file : dbSignatureList) {
            SQLiteJDBC jdbc = new SQLiteJDBC(file);
            ArrayList<SignatureModel> dbList = jdbc.select();
            signatureModels.addAll(dbList);
        }*/
        return signatureModels;
    }

    public ArrayList<MalwareModel> match(ArrayList<File> fileTargetList, ArrayList<SignatureModel> signature_list) {
        ArrayList<MalwareModel> malwareModels = new ArrayList<>();
        ManifestUtil util1 = new ManifestUtil();
        ItemsString itemsString = new ItemsString();
        totalFiles = fileTargetList.size();
        for (int i = 0; i < totalFiles; i++) {
            File file_i = fileTargetList.get(i);
            Util.print(i + 1 + "/" + totalFiles + " ***" + file_i.getAbsolutePath() + "***");
            util1.readZip(file_i, new ReadBytesFromZipListener() {
                ArrayList<SignatureModel> manifestMatchedSignatures = new ArrayList<>();
                boolean apk = true;

                @Override
                public boolean onReadManifest(byte[] bs) {
                    try {
                        ArrayList<String> appManifest = util1.calcManifestInNew(bs);
                        if (appManifest == null) {
                            return false;
                        }
                        manifestMatchedSignatures = util1.compareAppManifestWithSignaturesInNew(signature_list, appManifest);
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
                                stringMatch = getStringAddressFromHexString(bs, (int) ids_count, stringModel.getString(),
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
                    printYellow("onZipError:" + e.getMessage());
                    String error = e.getMessage();
                    if (error.contains("compress")) {
                        compress_mode.add(file_i.getAbsolutePath());
                    } else if (error.contains("split")) {
                        split_zip.add(file_i.getAbsolutePath());
                    } else if (error.contains("zip END header not found")) {
                        apk = false;
                        not_zip.add(file_i.getAbsolutePath());
                    } else {
                        unknown.add(file_i.getAbsolutePath());
                    }
                }

                @Override
                public void onManifestError(Exception e) {
                    printYellow("onManifestError:" + e.getMessage());
                    manifest.add(file_i.getAbsolutePath());
                }

                @Override
                public void onDexError(Exception e) {
                    printYellow("onDexError:" + e.getMessage());
                    dex.add(file_i.getAbsolutePath());
                }

                @Override
                public void onEnd(MatchStateEnum.state state) {
                    if (apk) totalApk++;
                    if (state == MatchStateEnum.state.NOT_MATCH) notMatch.add(file_i.getAbsolutePath());
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
        setNotMatch(notMatch);
        return malwareModels;
    }

    private boolean getStringAddressFromHexString(byte[] stream, int ids_count, String text, Item tClass, int min, int max) {
        long ids_offset = 112;

        if (min < ids_count) {
            //String[] splitText = util.splitTwoByTwo(text);
            byte[] splitByte = util.split(text);
            if (max > ids_count || max == 0) {
                max = ids_count;
            }

            while (min <= max) {
                int m = min + (max - min) / 2;
                long offset = ids_offset + (long) tClass.data_size * m;
                //boolean ss = searchDataByte(stream, offset, splitText);
                boolean ss = searchDataByte(stream, offset, splitByte);
                if (ss) {
                    if (!dexNamePrinted) {
                        print(dexName + ":");
                        dexNamePrinted = true;
                    }
                    printYellow("{ hex:" + text);
                    printYellow("  ids_index: " + m + " }");
                    return true;
                }
                if (f1 < f2) {
                    min = m + 1;
                } else {
                    max = m - 1;
                }
            }
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
            if (!hex.equalsIgnoreCase(s)) {
                f1 = util.stringHexToDecimal(hex);
                String s0 = splitText[i];
                f2 = util.stringHexToDecimal(s0);
                return false;
            }
            offset++;
            i++;
        }
        return true;
    }

    private boolean searchDataByte(byte[] stream, long start, byte[] splitByte) {
        byte[] first_offset_of_string_data_b = util.getBytesOfFile(stream, start, ItemsString.string_data_off_size);
        long offset = util.getDecimalValue(first_offset_of_string_data_b);
        while (true) {
            byte size_in_utf16_b = util.getBytesOfFile(stream, offset, 1)[0];
            long size_in_utf16_l = size_in_utf16_b & 0xFF;
            offset++;
            if (size_in_utf16_l < 127) {
                break;
            }
        }

        for (byte s : splitByte) {
            byte a_string_bit_in_MUTF8_format_b = util.getBytesOfFile(stream, offset, 1)[0];
            if (a_string_bit_in_MUTF8_format_b != s) {
                f1 = a_string_bit_in_MUTF8_format_b & 0xFF;
                f2 = s & 0xFF;
                return false;
            }
            offset++;
        }
        return true;
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

    public ArrayList<String> getDex() {
        return dex;
    }

    public void setDex(ArrayList<String> dex) {
        this.dex = dex;
    }

    public ArrayList<String> getNotMatch() {
        return notMatch;
    }

    public void setNotMatch(ArrayList<String> notMatch) {
        this.notMatch = notMatch;
    }
}
