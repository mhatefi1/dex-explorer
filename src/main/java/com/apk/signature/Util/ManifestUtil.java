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

    public String dumpManifest(String path) {
        String command = Util.aapt2Path + " dump xmltree --file AndroidManifest.xml " + path;
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
            System.out.println("Output:");
            System.out.println(out.getText());
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
