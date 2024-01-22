package com.apk.signature.Util;

import com.apk.signature.Model.ManifestModel;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ManifestUtil extends Util {

    public ManifestModel matchDumpedManifest(String input) {
        String regex = "E:\\s(permission|uses-permission|receiver|activity|service)[^-].+?(.*\\n)+?.*name\\(.*(?<=\\))=\"([^\"]*)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> permission_list = new ArrayList<>();
        ArrayList<String> activity_list = new ArrayList<>();
        ArrayList<String> service_list = new ArrayList<>();
        ArrayList<String> receiver_list = new ArrayList<>();

        ManifestModel res = new ManifestModel();
        while (matcher.find()) {
            String match;
            switch (matcher.group(1)) {
                case "permission", "uses-permission" -> {
                    match = matcher.group(3);
                    permission_list.add(match);
                }
                case "activity" -> {
                    match = matcher.group(3);
                    activity_list.add(match);
                }
                case "service" -> {
                    match = matcher.group(3);
                    service_list.add(match);
                }
                case "receiver" -> {
                    match = matcher.group(3);
                    receiver_list.add(match);
                }
            }

            res.setPermission(permission_list);
            res.setActivities(activity_list);
            res.setServices(service_list);
            res.setReceivers(receiver_list);
        }
        return res;
    }

    public ManifestModel matchDecodedManifest(String input) {
        String regex = "<(uses-permission|activity|service|receiver).+?name=\"([^\"]+)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> permission_list = new ArrayList<>();
        ArrayList<String> activity_list = new ArrayList<>();
        ArrayList<String> service_list = new ArrayList<>();
        ArrayList<String> receiver_list = new ArrayList<>();

        ManifestModel appManifestModel = new ManifestModel();

        while (matcher.find()) {

            String match;
            switch (Objects.requireNonNull(matcher.group(1))) {
                case "uses-permission": {
                    match = matcher.group(2);
                    permission_list.add(match);
                    break;
                }
                case "activity": {
                    match = matcher.group(2);
                    activity_list.add(match);
                    break;
                }
                case "service": {
                    match = matcher.group(2);
                    service_list.add(match);
                    break;
                }
                case "receiver": {
                    match = matcher.group(2);
                    receiver_list.add(match);
                    break;
                }
            }
        }
        appManifestModel.setPermission(permission_list);
        appManifestModel.setActivities(activity_list);
        appManifestModel.setServices(service_list);
        appManifestModel.setReceivers(receiver_list);

        return appManifestModel;
    }

    public String parseManifest(File file) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().equals("AndroidManifest.xml")) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        byte[] bs = IOUtils.toByteArray(inputStream);
                        inputStream.close();
                        return new XMLReader().decompressXML(bs);
                    }
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "not found";
    }


    public String decodeManifest(File file) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().equals("AndroidManifest.xml")) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] bs = IOUtils.toByteArray(inputStream);
                            return new XMLReader().decompressXML(bs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void getCommonInManifest(ArrayList<File> apk_list) {
        System.out.println("***********" + "factorizedManifest" + "***********");
        ManifestUtil manifestUtil = new ManifestUtil();
        File first_file = apk_list.get(0);
        String fileName = first_file.getAbsolutePath();
        System.out.println(fileName);

        String manifest = manifestUtil.dumpManifest(first_file);
        ManifestModel manifestModel = manifestUtil.matchDumpedManifest(manifest);

        ArrayList<String> permission_list = manifestModel.getPermission();
        ArrayList<String> activity_list = manifestModel.getActivities();
        ArrayList<String> service_list = manifestModel.getServices();
        ArrayList<String> receiver_list = manifestModel.getReceivers();

        for (int i = 1; i < apk_list.size(); i++) {
            System.out.println(apk_list.get(i));

            permission_list = super.removeDupe(permission_list);
            activity_list = super.removeDupe(activity_list);
            service_list = super.removeDupe(service_list);
            receiver_list = super.removeDupe(receiver_list);


            String manifest_ = manifestUtil.dumpManifest(apk_list.get(i));
            ManifestModel manifestModel_ = manifestUtil.matchDumpedManifest(manifest_);

            ArrayList<String> permission_list_ = manifestModel_.getPermission();
            ArrayList<String> activity_list_ = manifestModel_.getActivities();
            ArrayList<String> service_list_ = manifestModel_.getServices();
            ArrayList<String> receiver_list_ = manifestModel_.getReceivers();

            permission_list = super.getCommonOfArrayList(permission_list_, permission_list);
            activity_list = super.getCommonOfArrayList(activity_list_, activity_list);
            service_list = super.getCommonOfArrayList(service_list_, service_list);
            receiver_list = super.getCommonOfArrayList(receiver_list_, receiver_list);
        }

        permission_list = super.removeDupe(permission_list);
        activity_list = super.removeDupe(activity_list);
        service_list = super.removeDupe(service_list);
        receiver_list = super.removeDupe(receiver_list);

        String path = first_file.getParent();
        super.writeArrayToFile(permission_list, path + "\\" + "factorizedPermissions" + ".txt");
        super.writeArrayToFile(activity_list, path + "\\" + "factorizedActivities" + ".txt");
        super.writeArrayToFile(service_list, path + "\\" + "factorizedServices" + ".txt");
        super.writeArrayToFile(receiver_list, path + "\\" + "factorizedReceivers" + ".txt");
    }

    public void decodeMultipleManifest(String s) {
        try {
            File file = new File(s);
            if (file.exists()) {
                ArrayList<File> list = new ArrayList<>();
                if (file.isDirectory()) {
                    list = new Util().getFileListByFormat(file.getAbsolutePath(), ".apk", true);
                } else {
                    list.add(file);
                }
                for (File file1 : list) {
                    System.out.println("**********" + file1.getAbsolutePath() + "**********");
                    String manifest = new ManifestUtil().parseManifest(file1);
                    System.out.println(manifest);
                }
            } else {
                System.out.println("file not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String dumpManifest(File file) {
        String command = Util.aapt2Path + " dump xmltree --file AndroidManifest.xml " + file.getAbsolutePath();
        return runCMD(command);
    }

    public String runCMD(String command) {
        String res = "";

      /*  try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        StringBuilder output = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                        reader.close();
                        res = output.toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).join();



        } catch (Exception e) {
            res = e.getMessage();
            e.printStackTrace();
        }*/
        try {
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            OutputHandler out = new OutputHandler(process.getInputStream(), "UTF-8");
            OutputHandler err = new OutputHandler(process.getErrorStream(), "UTF-8");
            out.join();
            res = out.getText();
            //System.out.println();
            //  err.join();
            // System.out.println("Error:");
            // System.out.println(err.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

}
