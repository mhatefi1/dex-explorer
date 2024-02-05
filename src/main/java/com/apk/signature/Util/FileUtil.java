package com.apk.signature.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.apk.signature.Util.Util.printRed;

public class FileUtil {
    private static File convertInputStreamToFile(InputStream is, String fileName) {
        OutputStream outputStream = null;
        File file = null;
        try {
            file = new File(fileName);
            outputStream = new FileOutputStream(file);

            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void extractDex(String s) {
        try {
            File file = new File(s);
            if (file.isDirectory()) {
                ArrayList<File> apk = getFileListByFormat(s, ".apk", true);
                for (File f : apk) {
                    extractDex(f.getAbsolutePath());
                }
            } else {
                String name = splitNameFromFormat(s);
                File ext = new File(name + "-");
                //boolean dir = ext.mkdir();
                //if (dir) {
                readDexFilesFromZip(s, ext.getAbsolutePath());
                // } else {
                //    printRed("could not create " + s + " folder");
                // }
            }
        } catch (Exception e) {
            System.out.println("failed");
            e.printStackTrace();
        }
    }

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
            printRed(format + " files list is null");
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
            printRed(format + " files list is null");
        }
        return fileList;
    }

    public ArrayList<File> getFileListByFormat(String path, String format, boolean containNonFormats) {
        if (containNonFormats) {
            return getFileListContainNonFormats(path, format);
        } else {
            return getFileList(path, format);
        }
    }

    public ArrayList<File> getFileListContainNonFormats(String path, String format) {
        ArrayList<File> fileList = new ArrayList<>();
        File f = new File(path);
        File[] listed = f.listFiles();
        if (listed != null) {
            for (File file : listed) {
                if (file.getName().endsWith(format)) {
                    fileList.add(file);
                } else if (!file.getName().contains(".")) {
                    fileList.add(file);
                }
            }
        } else {
            File file = new File(path);
            if (file.getName().endsWith(format)) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public ArrayList<File> getFileList(String path, String format) {
        ArrayList<File> fileList = new ArrayList<>();
        File f = new File(path);
        File[] listed = f.listFiles();
        if (listed != null) {
            for (File file : listed) {
                if (file.getName().endsWith(format)) {
                    fileList.add(file);
                }
            }
        } else {
            File file = new File(path);
            if (file.getName().endsWith(format)) {
                fileList.add(file);
            }
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

    public void writeToFile(String text, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.append(text);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile(File file) {
        String line;
        StringBuilder output = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public ArrayList<File> generateDex(String path) {
        File f = new File(path);
        if (path.endsWith(".apk") || path.endsWith(".zip")) {
            String fileName = f.getName();
            String name = splitNameFromFormat(fileName);
            String folderPath = f.getParent();
            File extractPath = new File(folderPath, name);
            //extractPath.mkdir();
            return readDexFilesFromZip(path, extractPath.getAbsolutePath());
        }
        ArrayList<File> list = new ArrayList<>();
        list.add(f);
        return list;
    }

    public ArrayList<File> readDexFilesFromZip(String zipFilePath, String extractPath) {
        ArrayList<File> list = new ArrayList<>();
        try {
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        File file = new File(extractPath);
                        if (!file.exists()) {
                            boolean mk = file.mkdir();
                            if (mk) {
                                list.add(convertInputStreamToFile(inputStream, extractPath + "\\" + entry.getName()));
                            }
                        } else {
                            list.add(convertInputStreamToFile(inputStream, extractPath + "\\" + entry.getName()));
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return list;
    }

    public String splitNameFromFormat(String s) {
        try {
            int dotIndex = s.lastIndexOf(".");
            if (dotIndex >= 0) {
                return s.substring(0, dotIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String getWorkingFilePath(File f) {
        if (f.isDirectory())
            return f.getAbsolutePath();
        else
            return f.getParent();
    }
}
