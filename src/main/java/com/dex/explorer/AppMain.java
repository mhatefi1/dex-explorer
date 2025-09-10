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
    private static final String CMD_WRITE_ALL = "write";
    private static final String CMD_SEARCH = "search";

    public static void main(String[] args) {

        // 1. --- Argument Validation ---
        if (args.length < 2) {
            printUsage();
            return;
        }

        String command = args[0];
        String filePath = args[1];
        File apkFile = new File(filePath);

        if (!apkFile.exists() || !apkFile.isFile()) {
            print("Error: File does not exist or is not a valid file -> " + filePath);
            return;
        }

        // 2. --- Command Dispatcher ---
        // Based on the first argument, call the appropriate handler method.
        switch (command.toLowerCase()) {
            case CMD_WRITE_ALL:
                handleWriteOperation(apkFile);
                break;

            case CMD_SEARCH:
                // Search command requires a third argument
                if (args.length < 3) {
                    print("Error: The 'search' command requires a term to search for.");
                    printUsage();
                    return;
                }
                String searchTerm = args[2];
                handleSearchOperation(apkFile, searchTerm);
                break;

            default:
                print("Error: Unknown command '" + command + "'");
                printUsage();
                break;
        }
    }

    /**
     * Handles the logic for writing all strings and methods to a JSON file.
     * Corresponds to the original "1" option.
     * @param apkFile The APK file to analyze.
     */
    private static void handleWriteOperation(File apkFile) {
        print("Starting 'write' operation for: " + apkFile.getName());
        print("Please wait...");

        File exploreDir = createExploreDirectoryForFile(apkFile);
        if (exploreDir == null) {
            print("Error: Could not create the output directory.");
            return;
        }
        Util.TEMP_DEX_PATH = exploreDir.getAbsolutePath();
        File outputFile = new File(Util.TEMP_DEX_PATH, "items.json");

        AppUtil util = new AppUtil();

        try (ZipFile zipFile = new ZipFile(apkFile);
             JsonWriter writer = new JsonWriter(new FileWriter(outputFile, StandardCharsets.UTF_8))) {

            writer.setIndent("  "); // For pretty-printing
            writer.beginObject(); // {
            writer.name("path").value(apkFile.getAbsolutePath());
            writer.name("dexs").beginArray(); // "dexs": [

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                    print("Processing DEX file: " + entry.getName());

                    writer.beginObject(); // { (dex item)
                    writer.name("dex_name").value(entry.getName());

                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        byte[] bs = Util.toByteArray(inputStream);
                        HashMap<String, byte[]> header = util.getHeader(bs);

                        // Write strings, methods, and classes for this dex
                        util.writeToFile(header, bs, new ItemsString(), writer);
                        util.writeToFile(header, bs, new ItemsMethod(), writer);

                    } catch (Exception e) {
                        print("Error processing " + entry.getName() + ": " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        writer.endObject(); // } (dex item)
                    }
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
     * Handles the logic for searching for a specific term within the APK's DEX files.
     * Corresponds to the original "2" option.
     * @param apkFile The APK file to search within.
     * @param searchTerm The string to search for.
     */
    private static void handleSearchOperation(File apkFile, String searchTerm) {
        print("Starting 'search' operation for \"" + searchTerm + "\" in: " + apkFile.getName());
        print("Please wait...");

        AppUtil util = new AppUtil();
        String hexSearchTerm = util.stringToHexString(searchTerm).toUpperCase();
        boolean foundAny = false;

        try (ZipFile zipFile = new ZipFile(apkFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        byte[] bs = Util.toByteArray(inputStream);
                        HashMap<String, byte[]> header = util.getHeader(bs);

                        // Perform the searches
                        SearchResultModel stringSearch = util.getAddressFromHexString(header, bs, hexSearchTerm, new ItemsString(), 0, 0);
                        SearchResultModel methodSearch = util.getAddressFromHexString(header, bs, hexSearchTerm, new ItemsMethod(), 0, 0);
                        SearchResultModel classSearch = util.getAddressFromHexString(header, bs, hexSearchTerm, new ItemsClass(), 0, 0);

                        List<SearchResultModel> searchResultList = new ArrayList<>();
                        if (stringSearch != null) {
                            stringSearch.setDex_name(entry.getName());
                            searchResultList.add(stringSearch);
                        }
                        if (methodSearch != null) {
                            methodSearch.setDex_name(entry.getName());
                            searchResultList.add(methodSearch);
                        }
                        if (classSearch != null) {
                            classSearch.setDex_name(entry.getName());
                            searchResultList.add(classSearch);
                        }

                        if (!searchResultList.isEmpty()) {
                            foundAny = true;
                            print("\n--- Found results in: " + entry.getName() + " ---");
                            print(new GsonBuilder().setPrettyPrinting().create().toJson(searchResultList));
                        }

                    } catch (Exception e) {
                        print("Error searching in " + entry.getName() + ": " + e.getMessage());
                        e.printStackTrace();
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
     * Prints the usage instructions for the command-line tool.
     */
    private static void printUsage() {
        print("""
               \s
                APK DEX Explorer Tool
                ---------------------
                Usage: java -jar YourApp.jar [command] [apk-file-path] [options]
               \s
                Commands:
                  write   - Explores the APK and writes all strings and methods to a JSON file.
                            Example: java -jar YourApp.jar write "C:\\path\\to\\your\\app.apk"
                           \s
                  search  - Searches for a specific string within the APK's DEX files.
                            Example: java -jar YourApp.jar search "C:\\path\\to\\your\\app.apk" "your-search-term"
               \s""");
    }
}

/*
```

        ### How to Compile and Run

1.  **Compile:** If you're using Maven, you can create an executable JAR with dependencies by adding the `maven-assembly-plugin` to your `pom.xml`. If compiling manually, make sure all the required libraries (Gson, JAnsi, etc.) are in your classpath.
        ```bash
    # Assuming you have a standard Maven project structure
mvn clean package
        ```

        2.  **Run from Command Line:** After building the JAR (e.g., `YourApp-1.0-SNAPSHOT-jar-with-dependencies.jar`), you can run it like this from your terminal:

        * **To write all items to `items.json`:**
        ```bash
java -jar target/YourApp-1.0-SNAPSHOT-jar-with-dependencies.jar write "C:\Users\You\Downloads\some_app.apk"
        ```

        * **To search for a specific term (e.g., "MainActivity"):**
        ```bash
java -jar target/YourApp-1.0-SNAPSHOT-jar-with-dependencies.jar search "C:\Users\You\Downloads\some_app.apk" "MainActivity"

*/