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
        for (File file : files) {
            Util.print("********************" + file.getAbsolutePath() + "********************");
            try {
                try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                            try {
                                InputStream inputStream = zipFile.getInputStream(entry);
                                byte[] bs = IOUtils.toByteArray(inputStream);
                                HashMap<String, byte[]> header = util.getHeader(bs);
                                int c = util.getAddressFromHexString(header, bs, string, new ItemsString(), 0, 0);
                                ints.add(c);
                                for (int i : ints) {
                                    if (c < i) {
                                        min = c;
                                        model.setMinimumApkName(file.getAbsolutePath());
                                        model.setMinimumDexName(entry.getName());
                                    }
                                }
                                if (c > max) {
                                    max = c;
                                    model.setMaximumApkName(file.getAbsolutePath());
                                    model.setMaximumDexName(entry.getName());
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
        }
        model.setMinimumIndex(min);
        model.setMaximumIndex(max);

        return model;
    }

    private static class ResultModel {
        public String minimumApkName, minimumDexName;
        public String maximumApkName, maximumDexName;
        int minimumIndex, maximumIndex;

        public ResultModel() {

        }

        public String getMaximumApkName() {
            return maximumApkName;
        }

        public void setMaximumApkName(String maximumApkName) {
            this.maximumApkName = maximumApkName;
        }

        public String getMaximumDexName() {
            return maximumDexName;
        }

        public void setMaximumDexName(String maximumDexName) {
            this.maximumDexName = maximumDexName;
        }

        public String getMinimumApkName() {
            return minimumApkName;
        }

        public void setMinimumApkName(String minimumApkName) {
            this.minimumApkName = minimumApkName;
        }

        public String getMinimumDexName() {
            return minimumDexName;
        }

        public void setMinimumDexName(String minimumDexName) {
            this.minimumDexName = minimumDexName;
        }

        public int getMinimumIndex() {
            return minimumIndex;
        }

        public void setMinimumIndex(int minimumIndex) {
            this.minimumIndex = minimumIndex;
        }

        public int getMaximumIndex() {
            return maximumIndex;
        }

        public void setMaximumIndex(int maximumIndex) {
            this.maximumIndex = maximumIndex;
        }
    }

}
