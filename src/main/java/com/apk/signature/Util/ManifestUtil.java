package com.apk.signature.Util;

import com.apk.signature.Model.ManifestModel;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManifestUtil {

    public ManifestModel matchManifest(String input) {
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
                    System.out.println(match);
                    permission_list.add(match);
                }
                case "activity" -> {
                    match = matcher.group(3);
                    System.out.println(match);
                    activity_list.add(match);
                }
                case "service" -> {
                    match = matcher.group(3);
                    System.out.println(match);
                    service_list.add(match);
                }
                case "receiver" -> {
                    match = matcher.group(3);
                    System.out.println(match);
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

    public String dumpManifest(String aapt, String path) {
        String command = aapt + " dump xmltree --file AndroidManifest.xml " + path;
        return runCMD(command);
    }


    public String runCMD(String command) {
        String res;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            System.out.println("Command output:\n" + output);
            System.out.println("Exit code: " + exitCode);

            res = output.toString();
        } catch (Exception e) {
            res = e.getMessage();
            e.printStackTrace();
        }
        return res;
    }

}
