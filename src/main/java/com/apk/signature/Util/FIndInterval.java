package com.apk.signature.Util;

import com.apk.signature.Items.ItemsString;
import com.google.gson.Gson;
import org.apache.pdfbox.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FIndInterval {
    AppUtil util = new AppUtil();

    public void find(String string, String path) {
        ArrayList<File> files = new ArrayList<>();
        files = util.getRecursiveFileListByFormat(files, path, ".apk", true);
        ResultModel model = getListOfIndexes(string, files);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Util.printGreen(json);
    }

    private ResultModel getListOfIndexes(String string, ArrayList<File> files) {
        ResultModel model = new ResultModel();
        int min = 0, max = 0;
        ArrayList<Integer> ints = new ArrayList<>();
        ArrayList<String> notMatchedApks = new ArrayList<>();
        int totalFiles = 0, totalApks = 0;
        for (File file : files) {
            Util.print("********************" + file.getAbsolutePath() + "********************");
            boolean matched = false;
            totalFiles++;
            try {
                try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    totalApks++;
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] bs = util.toByteArray(inputStream);
                                HashMap<String, byte[]> header = util.getHeader(bs);
                                int c = util.getAddressFromHexString(header, bs, string, new ItemsString(), 0, 0);
                                if (c != -1) {
                                    if (ints.isEmpty()) {
                                        min = c;
                                        model.setMinimumApkName(file.getAbsolutePath());
                                        model.setMinimumDexName(entry.getName());
                                    } else {
                                        for (int i : ints) {
                                            if (c <= i) {
                                                min = c;
                                                model.setMinimumApkName(file.getAbsolutePath());
                                                model.setMinimumDexName(entry.getName());
                                            }
                                        }
                                    }
                                    if (c > max) {
                                        max = c;
                                        model.setMaximumApkName(file.getAbsolutePath());
                                        model.setMaximumDexName(entry.getName());
                                    }
                                    ints.add(c);
                                    matched = true;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!matched) {
                notMatchedApks.add(file.getAbsolutePath());
            }
        }
        model.setTotalFiles(totalFiles);
        model.setTotalApks(totalApks);
        model.setMinimumIndex(min);
        model.setMaximumIndex(max);
        model.setNotMatchedApks(notMatchedApks);
        return model;
    }

    private static class ResultModel {
        public String minimumApkName, minimumDexName;
        public String maximumApkName, maximumDexName;
        public ArrayList<String> notMatchedApks;
        int minimumIndex;
        int maximumIndex;

        int totalFiles, totalApks;
        int notMatchedApksSize;

        public ResultModel() {

        }

        public void setTotalFiles(int totalFiles) {
            this.totalFiles = totalFiles;
        }

        public void setTotalApks(int totalApks) {
            this.totalApks = totalApks;
        }

        public void setNotMatchedApksSize(int notMatchedApksSize) {
            this.notMatchedApksSize = notMatchedApksSize;
        }

        public void setMaximumApkName(String maximumApkName) {
            this.maximumApkName = maximumApkName;
        }

        public void setMaximumDexName(String maximumDexName) {
            this.maximumDexName = maximumDexName;
        }

        public void setMinimumApkName(String minimumApkName) {
            this.minimumApkName = minimumApkName;
        }

        public void setMinimumDexName(String minimumDexName) {
            this.minimumDexName = minimumDexName;
        }

        public void setMinimumIndex(int minimumIndex) {
            this.minimumIndex = minimumIndex;
        }

        public void setMaximumIndex(int maximumIndex) {
            this.maximumIndex = maximumIndex;
        }

        public void setNotMatchedApks(ArrayList<String> notMatchedApks) {
            setNotMatchedApksSize(notMatchedApks.size());
            this.notMatchedApks = notMatchedApks;
        }
    }
}
