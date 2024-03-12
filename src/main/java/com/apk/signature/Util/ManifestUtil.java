package com.apk.signature.Util;

import com.apk.signature.Model.ManifestModel;
import com.apk.signature.Model.ManifestModel2;
import com.apk.signature.Model.SignatureModel;
import com.apk.signature.Model.SignatureModel2;
import fr.xgouchet.axml.customized.Attribute;
import fr.xgouchet.axml.customized.CompressedXmlParser;
import org.apache.pdfbox.contentstream.operator.graphics.AppendRectangleToPath;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ManifestUtil extends Util {
    public ManifestModel matchManifestNew(File file) {
        try {
            byte[] bs = getManifestBytes(file);
            if (bs == null) return null;
            ManifestModel appManifestModel = new ManifestModel();
            ArrayList<String> permission_list = new ArrayList<>();
            ArrayList<String> activity_list = new ArrayList<>();
            ArrayList<String> service_list = new ArrayList<>();
            ArrayList<String> receiver_list = new ArrayList<>();
            boolean success = new CompressedXmlParser().parse(bs, (localName, attrs) -> {
                switch (localName) {
                    case "uses-permission":
                    case "permission":
                        setAttribute(attrs, permission_list);
                        break;
                    case "activity":
                        setAttribute(attrs, activity_list);
                        break;
                    case "service":
                        setAttribute(attrs, service_list);
                        break;
                    case "receiver":
                        setAttribute(attrs, receiver_list);
                        break;
                }
            });
            if (!success) return null;
            appManifestModel.setPermission(permission_list);
            appManifestModel.setActivities(activity_list);
            appManifestModel.setServices(service_list);
            appManifestModel.setReceivers(receiver_list);
            return appManifestModel;
        } catch (Exception e) {
            printRed(e.getMessage());
            return null;
        }
    }

    public ManifestModel calcManifest(byte[] bs) {
        ArrayList<String> permission_list = new ArrayList<>();
        ArrayList<String> activity_list = new ArrayList<>();
        ArrayList<String> service_list = new ArrayList<>();
        ArrayList<String> receiver_list = new ArrayList<>();

        new CompressedXmlParser().parse(bs, (localName, attrs) -> {
            switch (localName) {
                case "uses-permission":
                case "permission":
                    //Log.d(MainActivity.tag, "startElement localName:" + localName);
                    setAttribute(attrs, permission_list);
                    break;
                case "activity":
                    //Log.d(MainActivity.tag, "startElement localName:" + localName);
                    setAttribute(attrs, activity_list);
                    break;
                case "service":
                    //Log.d(MainActivity.tag, "startElement localName:" + localName);
                    setAttribute(attrs, service_list);
                    break;
                case "receiver":
                    //Log.d(MainActivity.tag, "startElement localName:" + localName);
                    setAttribute(attrs, receiver_list);
                    break;
            }
        });

        ManifestModel appManifestModel = new ManifestModel();
        appManifestModel.setPermission(permission_list);
        appManifestModel.setActivities(activity_list);
        appManifestModel.setServices(service_list);
        appManifestModel.setReceivers(receiver_list);
        return appManifestModel;
    }

    public ArrayList<String> calcManifest2(byte[] bs) {
        ArrayList<String> permission_list = new ArrayList<>();

        new CompressedXmlParser().parse(bs, (localName, attrs) -> {
            /*switch (localName) {
                case "uses-permission", "permission", "service", "receiver", "activity":
                    //Log.d(MainActivity.tag, "startElement localName:" + localName);
                    setAttribute(attrs, permission_list);
                    break;
            }*/
            setAttributeInHex(attrs, permission_list);
        });

        //ManifestModel2 appManifestModel = new ManifestModel2();
        //appManifestModel.setAll(permission_list);
        //return appManifestModel;
        return permission_list;
    }

    public ArrayList<SignatureModel> compareAppManifestWithSignatures(ArrayList<SignatureModel> signature_list, ManifestModel appManifestModel) {
        ArrayList<SignatureModel> manifestMatchedSignatures = new ArrayList<>();

        boolean permissionMatch, activitiesMatch, serviceMatch, receiverMatch;
        ManifestModel signatureManifestModel;

        for (SignatureModel model : signature_list) {
            try {
                signatureManifestModel = model.getManifestModel();
            } catch (Exception e) {
                continue;
            }

            boolean permissionEmpty = signatureManifestModel.getPermission().get(0).isEmpty(),
                    activitiesEmpty = signatureManifestModel.getActivities().get(0).isEmpty(),
                    serviceEmpty = signatureManifestModel.getServices().get(0).isEmpty(),
                    receiverEmpty = signatureManifestModel.getReceivers().get(0).isEmpty();

            if (permissionEmpty) {
                permissionMatch = true;
            } else {
                permissionMatch = super.contains(appManifestModel.getPermission(), signatureManifestModel.getPermission());
            }

            if (activitiesEmpty) {
                activitiesMatch = true;
            } else {
                activitiesMatch = super.contains(appManifestModel.getActivities(), signatureManifestModel.getActivities());
            }

            if (serviceEmpty) {
                serviceMatch = true;
            } else {
                serviceMatch = super.contains(appManifestModel.getServices(), signatureManifestModel.getServices());
            }

            if (receiverEmpty) {
                receiverMatch = true;
            } else {
                receiverMatch = super.contains(appManifestModel.getReceivers(), signatureManifestModel.getReceivers());
            }

            boolean manifestMatch = permissionMatch && activitiesMatch && serviceMatch && receiverMatch;

            if (manifestMatch) {
                manifestMatchedSignatures.add(model);
            }
        }
        return manifestMatchedSignatures;
    }

    public ArrayList<SignatureModel2> compareAppManifestWithSignatures2(ArrayList<SignatureModel2> signature_list, ArrayList<String> appManifest) {
        ArrayList<SignatureModel2> manifestMatchedSignatures = new ArrayList<>();
        boolean permissionMatch;
        for (SignatureModel2 model : signature_list) {
            boolean permissionEmpty = model.getManifests().isEmpty();
            if (permissionEmpty) {
                permissionMatch = true;
            } else {
                permissionMatch = super.containsIgnoreCase(appManifest, model.getManifests());
            }
            if (permissionMatch) {
                manifestMatchedSignatures.add(model);
            }
        }
        return manifestMatchedSignatures;
    }

    private byte[] getManifestBytes(File file) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().equals("AndroidManifest.xml")) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        byte[] bs = toByteArray(inputStream);
                        inputStream.close();
                        return bs;
                    }
                }
            }
        } catch (ZipException | IndexOutOfBoundsException e) {
            printYellow(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setAttribute(Attribute[] atts, ArrayList<String> list) {
        if (atts != null) {
            for (Attribute att : atts) {
                if (att.getName().equals("name") || att.getName().isEmpty()) {
                    list.add(att.getValue());
                    break;
                }
            }
        }
    }

    private void setAttributeInHex(Attribute[] atts, ArrayList<String> list) {
        if (atts != null) {
            for (Attribute att : atts) {
                if (att.getName().equals("name") || att.getName().isEmpty()) {
                    list.add(stringToHexString(att.getValue()));
                    break;
                }
            }
        }
    }

    public void getCommonInManifest(ArrayList<File> apk_list) {
        System.out.println("***********" + "factorizedManifest" + "***********");
        File first_file = apk_list.get(0);
        String fileName = first_file.getAbsolutePath();
        System.out.println(fileName);

        // String manifest = manifestUtil.dumpManifest(first_file);
        //ManifestModel manifestModel = manifestUtil.matchDumpedManifest(manifest);
        ManifestModel manifestModel = matchManifestNew(first_file);
        if (manifestModel == null) return;
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

            ManifestModel manifestModel_ = matchManifestNew(apk_list.get(i));
            if (manifestModel_ == null) return;
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
                String manifest = decodeManifestFull(file1);
                print(manifest);
            }
        } else {
            printRed("file not found");
        }
    }

    private String decodeManifestFull(File file1) {
        try {
            byte[] bs = getManifestBytes(file1);
            if (bs == null) return "";
            fr.xgouchet.axml.full.CompressedXmlParser parser = new fr.xgouchet.axml.full.CompressedXmlParser();
            org.w3c.dom.Document document = parser.parseDOM(bs);
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /*private ArrayList<SignatureModel> compareAppManifestWithSignatures(ArrayList<SignatureModel> signature_list, ManifestModel appManifestModel) {
        ArrayList<SignatureModel> manifestMatchedSignatures = new ArrayList<>();

        boolean permissionMatch, activitiesMatch, serviceMatch, receiverMatch;
        ManifestModel signatureManifestModel;

        for (SignatureModel model : signature_list) {
            try {
                signatureManifestModel = model.getManifestModel();
            } catch (Exception e) {
                continue;
            }

            boolean permissionEmpty = signatureManifestModel.getPermission().get(0).isEmpty(),
                    activitiesEmpty = signatureManifestModel.getActivities().get(0).isEmpty(),
                    serviceEmpty = signatureManifestModel.getServices().get(0).isEmpty(),
                    receiverEmpty = signatureManifestModel.getReceivers().get(0).isEmpty();

            if (permissionEmpty) {
                permissionMatch = true;
            } else {
                permissionMatch = super.contains(appManifestModel.getPermission(), signatureManifestModel.getPermission());
            }

            if (activitiesEmpty) {
                activitiesMatch = true;
            } else {
                activitiesMatch = super.contains(appManifestModel.getActivities(), signatureManifestModel.getActivities());
            }

            if (serviceEmpty) {
                serviceMatch = true;
            } else {
                serviceMatch = super.contains(appManifestModel.getServices(), signatureManifestModel.getServices());
            }

            if (receiverEmpty) {
                receiverMatch = true;
            } else {
                receiverMatch = super.contains(appManifestModel.getReceivers(), signatureManifestModel.getReceivers());
            }

            boolean manifestMatch = permissionMatch && activitiesMatch && serviceMatch && receiverMatch;

            if (manifestMatch) {
                manifestMatchedSignatures.add(model);
                //boolean manifestEmpty = permissionEmpty && activitiesEmpty && serviceEmpty && receiverEmpty;
                //if (!manifestEmpty) break;
            }
        }
        return manifestMatchedSignatures;
    }*/
    /*public ManifestModel matchDumpedManifest(String input) {
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
    }*/
    /*public ManifestModel matchDecodedManifest(String input) {
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
    }*/
    /*public String parseManifest(File file) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().equals("AndroidManifest.xml")) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        byte[] bs = util.toByteArray(inputStream);
                        inputStream.close();
                        return new XMLReader().decompressXML(bs);
                    }
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "not found";
    }*/
    /*public String decodeManifest(File file) {
        try {
            try (ZipFile zipFile = new ZipFile(file.getAbsolutePath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().equals("AndroidManifest.xml")) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(entry);
                            byte[] bs = util.toByteArray(inputStream);
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
    }*/
    /*public String dumpManifest(File file) {
        String command = Util.aapt2Path + " dump xmltree --file AndroidManifest.xml " + file.getAbsolutePath();
        return runCMD(command);
    }*/
    /* public String runCMD(String command) {
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
        }/*
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
    }*/

}
