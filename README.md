# DEX Explorer

A lightweight, command-line Java utility to inspect and analyze Android `.dex` files.

This tool can **explore** a file to dump all strings and methods into a structured JSON, or **search** for a specific string, method, or class name. It works directly with both file types, automatically handles all `classes.dex` files in an APK, and saves its output to a clean `<file-name>-explore` directory.

## Usage

### 1. Explore & Dump Data

This command creates a detailed `items.json` file from the input.

**Syntax:**
```bash
java -jar target/YourApp.jar dump "[path-to-apk-or-dex]"
```
**Example:**
```bash
java -jar target/YourApp.jar explore "C:\\path\\to\\your\\app.apk"
```
### 2. Search for a Term

This command searches the file and prints any findings directly to the console.

**Syntax:**
```bash
java -jar target/YourApp.jar search "[path-to-apk-or-dex]" "[searchTerm]"
```
**Example:**
```bash
java -jar target/YourApp.jar search "C:\\path\\to\\classes.dex" "MainActivity"
```