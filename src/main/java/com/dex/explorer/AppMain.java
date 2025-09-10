package com.dex.explorer;

import com.dex.explorer.Items.ItemsClass;
import com.dex.explorer.Items.ItemsMethod;
import com.dex.explorer.Items.ItemsString;
import com.dex.explorer.Model.SearchResultModel;
import com.dex.explorer.Util.AppUtil;
import com.dex.explorer.Util.Util;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.dex.explorer.Util.FileUtil.createExploreDirectoryForFile;
import static com.dex.explorer.Util.Util.print;

public class AppMain {

    // Define constants for the command-line arguments
    private static final String CMD_EXPLORE = "explore";
    private static final String CMD_SEARCH = "search";

    public static void main(String[] args) {

        // 1. --- Argument Validation ---
        if (args.length < 2) {
            printUsage();
            return;
        }

        String command = args[0];
        String filePath = args[1];
        File inputFile = new File(filePath);
        String lowerCasePath = filePath.toLowerCase();

        if (!inputFile.exists() || !inputFile.isFile() || (!lowerCasePath.endsWith(".apk") && !lowerCasePath.endsWith(".dex"))) {
            print("Error: Input must be a valid .apk or .dex file -> " + filePath);
            printUsage();
            return;
        }

        // 2. --- Command Dispatcher ---
        // Based on the first argument, call the appropriate handler method.
        switch (command.toLowerCase()) {
            case CMD_EXPLORE:
                handleExploreOperation(inputFile);
                break;

            case CMD_SEARCH:
                // Search command requires a third argument
                if (args.length < 3) {
                    print("Error: The 'search' command requires a term to search for.");
                    printUsage();
                    return;
                }
                String searchTerm = args[2];
                handleSearchOperation(inputFile, searchTerm);
                break;

            default:
                print("Error: Unknown command '" + command + "'");
                printUsage();
                break;
        }
    }

    /**
     * Handles exploring an APK or a single DEX file and writing its contents to a JSON file.
     * @param inputFile The APK or DEX file to analyze.
     */
    private static void handleExploreOperation(File inputFile) {
        print("Starting 'explore' operation for: " + inputFile.getName());
        print("Please wait...");

        File exploreDir = createExploreDirectoryForFile(inputFile);
        if (exploreDir == null) {
            print("Error: Could not create the output directory.");
            return;
        }
        Util.TEMP_DEX_PATH = exploreDir.getAbsolutePath();
        File outputFile = new File(Util.TEMP_DEX_PATH, "items.json");
        AppUtil util = new AppUtil();

        try (JsonWriter writer = new JsonWriter(new FileWriter(outputFile, StandardCharsets.UTF_8))) {
            writer.setIndent("  "); // For pretty-printing
            writer.beginObject(); // {
            writer.name("path").value(inputFile.getAbsolutePath());
            writer.name("dexs").beginArray(); // "dexs": [

            String fileName = inputFile.getName().toLowerCase();
            if (fileName.endsWith(".apk")) {
                // Handle APK file by iterating through its DEX entries
                try (ZipFile zipFile = new ZipFile(inputFile)) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                processDexStreamForExplore(writer, inputStream, entry.getName(), util);
                            }
                        }
                    }
                }
            } else if (fileName.endsWith(".dex")) {
                // Handle a single DEX file
                try (InputStream inputStream = new FileInputStream(inputFile)) {
                    processDexStreamForExplore(writer, inputStream, inputFile.getName(), util);
                }
            }

            writer.endArray(); // ]
            writer.endObject(); // }
            print("\nDone. Results saved to: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            print("An IO error occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Reusable helper to process a DEX stream and write its contents to the JsonWriter.
     */
    private static void processDexStreamForExplore(JsonWriter writer, InputStream dexStream, String dexName, AppUtil util) throws IOException {
        print("Processing DEX: " + dexName);
        writer.beginObject(); // { (dex item)
        writer.name("dex_name").value(dexName);
        try {
            byte[] bs = Util.toByteArray(dexStream);
            HashMap<String, byte[]> header = util.getHeader(bs);

            // Write strings and methods for this dex
            util.writeToFile(header, bs, new ItemsString(), writer);
            util.writeToFile(header, bs, new ItemsMethod(), writer);

        } catch (Exception e) {
            print("Error processing " + dexName + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            writer.endObject(); // } (dex item)
        }
    }


    /**
     * Handles searching for a term within an APK or a single DEX file.
     * @param inputFile The APK or DEX file to search within.
     * @param searchTerm The string to search for.
     */
    private static void handleSearchOperation(File inputFile, String searchTerm) {
        print("Starting 'search' operation for \"" + searchTerm + "\" in: " + inputFile.getName());
        print("Please wait...");

        AppUtil util = new AppUtil();
        boolean foundAny = false;

        try {
            String fileName = inputFile.getName().toLowerCase();
            if (fileName.endsWith(".apk")) {
                // Handle APK file by iterating through its DEX entries
                try (ZipFile zipFile = new ZipFile(inputFile)) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                if (processDexStreamForSearch(inputStream, entry.getName(), searchTerm, util)) {
                                    foundAny = true;
                                }
                            }
                        }
                    }
                }
            } else if (fileName.endsWith(".dex")) {
                // Handle a single DEX file
                try (InputStream inputStream = new FileInputStream(inputFile)) {
                    if (processDexStreamForSearch(inputStream, inputFile.getName(), searchTerm, util)) {
                        foundAny = true;
                    }
                }
            }

            if (!foundAny) {
                print("Search term not found in any DEX file.");
            }
        } catch (IOException e) {
            print("An IO error occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Reusable helper to search a DEX stream for a term and print results.
     * @return true if a result was found, false otherwise.
     */
    private static boolean processDexStreamForSearch(InputStream dexStream, String dexName, String searchTerm, AppUtil util) {
        String hexSearchTerm = util.stringToHexString(searchTerm).toUpperCase();
        List<SearchResultModel> searchResultList = new ArrayList<>();
        boolean found = false;

        try {
            byte[] bs = Util.toByteArray(dexStream);
            HashMap<String, byte[]> header = util.getHeader(bs);

            // Perform the searches
            SearchResultModel stringSearch = util.getAddressFromHexString(header, bs, hexSearchTerm, new ItemsString(), 0, 0);
            SearchResultModel methodSearch = util.getAddressFromHexString(header, bs, hexSearchTerm, new ItemsMethod(), 0, 0);
            SearchResultModel classSearch = util.getAddressFromHexString(header, bs, hexSearchTerm, new ItemsClass(), 0, 0);

            if (stringSearch != null) {
                stringSearch.setDex_name(dexName);
                searchResultList.add(stringSearch);
            }
            if (methodSearch != null) {
                methodSearch.setDex_name(dexName);
                searchResultList.add(methodSearch);
            }
            if (classSearch != null) {
                classSearch.setDex_name(dexName);
                searchResultList.add(classSearch);
            }

            if (!searchResultList.isEmpty()) {
                found = true;
                print("\n--- Found results in: " + dexName + " ---");
                print(new GsonBuilder().setPrettyPrinting().create().toJson(searchResultList));
            }
        } catch (Exception e) {
            print("Error searching in " + dexName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return found;
    }

    /**
     * Prints the usage instructions for the command-line tool.
     */
    private static void printUsage() {
        print("""
               \s
                APK DEX Explorer Tool
                ---------------------
                Usage: java -jar YourApp.jar [command] [file-path] [options]
               \s
                [file-path] can be a path to an apk or a dex file.
               \s
                Commands:
                  explore - Explores the file and writes all strings and methods to a JSON file.
                            Example: java -jar YourApp.jar explore "C:\\path\\to\\your\\app.apk"
                           \s
                  search  - Searches for a specific string within the file's DEX data.
                            Example: java -jar YourApp.jar search "C:\\path\\to\\classes.dex" "your-search-term"
               \s""");
    }
}