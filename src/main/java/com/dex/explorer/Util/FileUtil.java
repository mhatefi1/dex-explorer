package com.dex.explorer.Util;

import java.io.*;
import java.util.ArrayList;

import static com.dex.explorer.Util.Util.print;

public class FileUtil {

    public ArrayList<File> getRecursiveFileListByFormat(ArrayList<File> fileList, String input, String format, boolean containNonFormats) {
        if (containNonFormats) {
            return getRecursiveFileListContainNonFormats(fileList, input, format);
        } else {
            return getRecursiveFileList(fileList, input, format);
        }
    }

    private ArrayList<File> getRecursiveFileListContainNonFormats(ArrayList<File> fileList, String path, String format) {
        File f = new File(path);
        File[] listed = f.listFiles();
        if (listed != null) {
            for (File file : listed) {
                if (file.isDirectory()) {
                    getRecursiveFileListContainNonFormats(fileList, file.getAbsolutePath(), format);
                } else {
                    if (file.getName().endsWith(format)) {
                        fileList.add(file);
                    } else if (!file.getName().contains(".")) {
                        fileList.add(file);
                    }
                }
            }
        } else {
            print(format + " files list is null");
        }
        return fileList;
    }

    private ArrayList<File> getRecursiveFileList(ArrayList<File> fileList, String input, String format) {
        File f = new File(input);
        File[] listed = f.listFiles();
        if (listed != null) {
            for (File file : listed) {
                if (file.isDirectory()) {
                    getRecursiveFileList(fileList, file.getAbsolutePath(), format);
                } else {
                    if (file.getName().endsWith(format)) {
                        fileList.add(file);
                    }
                }
            }
        } else {
            print(format + " files list is null");
        }
        return fileList;
    }

    public void writeArrayToFile(ArrayList<String> arrayList, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));

            for (String string : arrayList) {
                writer.append(string);
                writer.append('\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static File createExploreDirectoryForFile(File sourceFile) {
        File exploreDir = getExploreDirectoryForFile(sourceFile);
        if (!exploreDir.exists()) {
            boolean wasCreated = exploreDir.mkdirs();
            if (wasCreated) {
                print("Successfully created directory.");
            } else {
                print("Failed created directory.");
                return null;
            }
        }
        return exploreDir;
    }

    private static File getExploreDirectoryForFile(File sourceFile) {
        String parentPath = sourceFile.getParent();
        if (parentPath == null) {
            parentPath = ".";
        }

        String fileName = sourceFile.getName();
        String baseName;
        int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex > 0) {
            baseName = fileName.substring(0, lastDotIndex);
        } else {
            baseName = fileName;
        }

        String newDirName = baseName + "-explore";
        return new File(parentPath, newDirName);
    }
}
