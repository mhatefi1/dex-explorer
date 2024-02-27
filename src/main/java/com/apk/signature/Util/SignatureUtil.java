package com.apk.signature.Util;

import com.apk.signature.Model.DBModel;
import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Model.StringModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignatureUtil extends Util {

    /*private ArrayList<String> getComponentList2(String signature) {
        String regex = "!(.*)@(.*)#(.*)\\$(.*)%(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(signature);
        ArrayList<String> list = new ArrayList<>();
        if (matcher.find()) {
            String permissions = matcher.group(1);
            String activities = matcher.group(2);
            String services = matcher.group(3);
            String receivers = matcher.group(4);
            String strings = matcher.group(5);
            list.add(permissions);
            list.add(activities);
            list.add(services);
            list.add(receivers);
            list.add(strings);
        }
        return list;
    }*/

    private ArrayList<String> getComponentList(String signature) {
        String[] tt = signature.split(";");
        ArrayList<String> list = new ArrayList<>();
        try {
            String name = tt[0];
            String permissions = tt[1];
            String activities = tt[2];
            String services = tt[3];
            String receivers = tt[4];
            String strings = tt[5];

            list.add(name);
            list.add(permissions);
            list.add(activities);
            list.add(services);
            list.add(receivers);
            list.add(strings);

            if (tt.length > 6) {
                String flag = tt[6];
                list.add(flag);
            } else {
                list.add("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*public DBModel parseSignatureAsDB(String signature) {
        ArrayList<String> list = getComponentList(signature);
        return new DBModel(list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
    }*/

    public SignatureModel parseSignature(String signature, boolean decodeHex) {
        ArrayList<String> list = getComponentList(signature);
        return createSignatureModel(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6), decodeHex);
    }

    /*public SignatureModel createSignatureModel2(String permissions, String activities, String services, String receivers, String strings) {
        String[] permissions_list = permissions.split(",");
        String[] activities_list = activities.split(",");
        String[] service_list = services.split(",");
        String[] receivers_list = receivers.split(",");
        String[] strings_list = strings.split(",");

        ManifestModel manifestModel = new ManifestModel();

        ArrayList<String> permissionArrayList = new ArrayList<>();
        ArrayList<String> activitiesArrayList = new ArrayList<>();
        ArrayList<String> serviceArrayList = new ArrayList<>();
        ArrayList<String> receiversArrayList = new ArrayList<>();

        for (String s : permissions_list) {
            s = hexStringToUTF8(s);
            permissionArrayList.add(s);
        }
        for (String s : activities_list) {
            s = hexStringToUTF8(s);
            activitiesArrayList.add(s);
        }
        for (String s : service_list) {
            s = hexStringToUTF8(s);
            serviceArrayList.add(s);
        }
        for (String s : receivers_list) {
            s = hexStringToUTF8(s);
            receiversArrayList.add(s);
        }

        //ArrayList<String> stringsArrayList = new ArrayList<>(Arrays.asList(strings_list));
        ArrayList<StringModel> stringModels = new ArrayList<>();
        for (String s : strings_list) {
            String reg = "(.+)\\[(.+)-(.+)]";
            Pattern pattern1 = Pattern.compile(reg);
            Matcher matcher1 = pattern1.matcher(s);
            if (matcher1.find()) {
                s = matcher1.group(1);
                int startIndex = Integer.parseInt(matcher1.group(2));
                int endIndex = Integer.parseInt(matcher1.group(3));
                StringModel model = new StringModel(startIndex, endIndex, s);
                stringModels.add(model);
            } else {
                int startIndex = 0;
                int endIndex = 0;
                StringModel model = new StringModel(startIndex, endIndex, s);
                stringModels.add(model);
            }
        }

        manifestModel.setPermission(permissionArrayList);
        manifestModel.setActivities(activitiesArrayList);
        manifestModel.setServices(serviceArrayList);
        manifestModel.setReceivers(receiversArrayList);

        com.apk.signature.Model.SignatureModel signatureModel = new com.apk.signature.Model.SignatureModel();

        signatureModel.setManifestModel(manifestModel);
        signatureModel.setStringModels(stringModels);

        return signatureModel;
    }*/

    public SignatureModel createSignatureModel(String name, String permissions, String activities, String services, String receivers, String strings, String flags, boolean decodeHex) {
        String[] permissions_list = permissions.split(",");
        String[] activities_list = activities.split(",");
        String[] service_list = services.split(",");
        String[] receivers_list = receivers.split(",");
        String[] strings_list = strings.split(",");

        ManifestModel manifestModel = new ManifestModel();

        ArrayList<String> permissionArrayList = new ArrayList<>();
        ArrayList<String> activitiesArrayList = new ArrayList<>();
        ArrayList<String> serviceArrayList = new ArrayList<>();
        ArrayList<String> receiversArrayList = new ArrayList<>();

        for (String s : permissions_list) {
            if (decodeHex) s = hexStringToUTF8(s);
            permissionArrayList.add(s);
        }
        for (String s : activities_list) {
            if (decodeHex) s = hexStringToUTF8(s);
            activitiesArrayList.add(s);
        }
        for (String s : service_list) {
            if (decodeHex) s = hexStringToUTF8(s);
            serviceArrayList.add(s);
        }
        for (String s : receivers_list) {
            if (decodeHex) s = hexStringToUTF8(s);
            receiversArrayList.add(s);
        }

        ArrayList<StringModel> stringModels = new ArrayList<>();
        for (String s : strings_list) {
            String reg = "(.+)\\[(.+)-(.+)]";
            Pattern pattern1 = Pattern.compile(reg);
            Matcher matcher1 = pattern1.matcher(s);
            if (matcher1.find()) {
                s = matcher1.group(1);
                int startIndex = Integer.parseInt(matcher1.group(2));
                int endIndex = Integer.parseInt(matcher1.group(3));
                StringModel model = new StringModel(startIndex, endIndex, s);
                stringModels.add(model);
            } else {
                int startIndex = 0;
                int endIndex = 0;
                StringModel model = new StringModel(startIndex, endIndex, s);
                stringModels.add(model);
            }
        }

        manifestModel.setPermission(permissionArrayList);
        manifestModel.setActivities(activitiesArrayList);
        manifestModel.setServices(serviceArrayList);
        manifestModel.setReceivers(receiversArrayList);

        SignatureModel signatureModel = new SignatureModel();

        signatureModel.setManifestModel(manifestModel);
        signatureModel.setStringModels(stringModels);
        signatureModel.setName(name);
        signatureModel.setFlags(flags);

        return signatureModel;
    }
}
