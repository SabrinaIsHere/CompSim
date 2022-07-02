package com.morticia.compsim.Util.Disk;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to make interactions with files easier. Serves primarily as a data wrapper for text editing
 *
 * While using this class keep in mind that the contents variable is public and if it's faster you're free to edit it directly
 *
 * @author Morticia
 * @version 1.0
 * @since 6/29/22
 */

public class DiskFile {
    public String dir;
    public String fileName;
    public String extension;
    public Path path;
    public boolean writable;
    public ExecutionPermissions execPerms;

    public List<String> contents;

    /**
     * Initializer function, pretty straightforward
     *
     * If file already exists contents will be initialized from it
     *
     * @param parentDir The directory of the folder this file is in
     * @param fileName The name of this file, including extensions (.txt,.sh, etc.)
     * @param writable Whether or not data can be written, does not affect actual file just this wrapper
     */
    public DiskFile(String parentDir, String fileName, boolean writable) {
        if (!parentDir.endsWith("/")) {
            parentDir = parentDir + "/";
        }

        this.dir = parentDir + fileName;
        this.fileName = fileName;
        if (fileName.contains(".")) {
            this.extension = fileName.split("\\.")[1];
        } else {
            this.extension = "";
        }
        this.path = Path.of(DiskUtil.getObjectivePath(dir));
        this.writable = writable;
        this.contents = new ArrayList<>();

        File f = path.toFile();

        try {
            if (f.exists() && f.isFile()) {
                // Tries to initialize contents (the buffer) from file if it exists
                readBuffer();
            } else {
                f.createNewFile();
            }
        } catch (Exception e) {
            printError(e);
        }

        // Set permissions, library perms set later
        this.execPerms = new ExecutionPermissions();
        this.execPerms.canExecute = extension.endsWith("lua");
    }

    /**
     * Initializer function, pretty straightforward
     *
     * @param parentDir The directory of the folder this file is in
     * @param fileName The name of this file, including extensions (.txt,.sh, etc.)
     * @param writable Whether or not data can be written, does not affect actual file just this wrapper
     * @param contents The contents to initialize with. Each string is a new line
     */
    public DiskFile(String parentDir, String fileName, boolean writable, List<String> contents) {
        if (!parentDir.endsWith("/")) {
            parentDir = parentDir + "/";
        }

        this.dir = parentDir + fileName;
        this.fileName = fileName;
        this.path = Path.of(dir);
        this.writable = writable;
        this.contents = new ArrayList<>();

        this.contents.addAll(contents);
    }

    /**
     * Get the number of lines currently in the contents
     *
     * @return Number of lines present
     */
    public int getNumLines() {
        return contents.size() + 1;
    }

    /**
     * Gets a specific line and returns it
     *
     * @param line Number of the line to be gotten
     * @return The text correlating to the given line number
     */
    public String getLine(int line) {
        // This is done so I can address line 1 (logical start of text) but maintain standard list indexing
        line--;
        if (line < contents.size() && line > 0) {
            return contents.get(line);
        }
        return "";
    }

    /**
     * Sets the specified line to the specified text
     *
     * @param line The line to set
     * @param text The text to set the line to
     */
    public void setLine(int line, String text) {
        line--;
        if (line < contents.size() && line > 0) {
            contents.set(line, text);
        }
        writeBuffer();
    }

    /**
     * Sets and configures the lines from a string
     *
     * If input string is blank function returns before doing anything
     *
     * @param txt String to configure and set lines from
     */
    public void setLines(String txt) {
        if (txt.isBlank()) {
            return;
        }
        contents = new ArrayList<>();
        String[] lines = txt.split("\n");
        contents.addAll(Arrays.asList(lines));
    }

    /**
     * Returns the contents of the file as a string
     *
     * @return String containing contents of the file
     */
    public String getLines() {
        StringBuilder s = new StringBuilder();
        for (String i : contents) {
            s.append(i).append("\n");
        }
        return s.toString();
    }

    /**
     * Appends text to the specified line
     *
     * @param line Line to be appended to
     * @param text Text to append
     */
    public void appendLine(int line, String text) {
        line--;
        if (line < contents.size() && line > 0) {
            contents.set(line, contents.get(line) + text);
        }
        writeBuffer();
    }

    /**
     * Appends a new line to the end of the file
     *
     * @param text Text contained in that line
     */
    public void appendNewLine(String text) {
        contents.add(text);
        writeBuffer();
    }

    /**
     * Adds specified number of lines to the file with the specified initialization value
     *
     * @param newLines Number of lines to add
     * @param initVal Value the new lines will be initialized to
     */
    public void addLines(int newLines, String initVal) {
        for (int i = 0; i < newLines; i++) {
            contents.add(initVal);
        }
        writeBuffer();
    }

    /**
     * Adds lines to the end of the file, all initialized to empty strings
     *
     * @param newLines Number of new lines to add
     */
    public void addLines(int newLines) {
        addLines(newLines, "");
    }

    /**
     * Removes a line from the file
     *
     * @param line Line to be removed
     */
    public void removeLine(int line) {
        line--;
        if (line < contents.size() && line > 0) {
            contents.remove(line);
        }
        writeBuffer();
    }

    /**
     * Removes specified lines from the file
     *
     * @param start Start of the lines to be removed
     * @param end End of the lines to be removed
     */
    public void removeLines(int start, int end) {
        if (start > 0 && end < contents.size()) {
            start--;
            end--;
            for (int i = start; i <= end; i++) {
                // This might not work, tinker with it if there are issues
                contents.remove(start);
            }
        }
        writeBuffer();
    }

    /**
     * Function to read the contents of the file to the buffer, where it can be manupulated
     *
     * @return Whether or not the operation was successful
     */
    public boolean readBuffer() {
        File f = path.toFile();
        if (f.exists() && f.isFile() && f.canRead()) {
            try {
                FileReader fr = new FileReader(f.getAbsoluteFile());
                BufferedReader br = new BufferedReader(fr);
                this.contents = new ArrayList<>();
                this.contents.addAll(br.lines().toList());
            } catch (Exception e) {
                printError(e);
                contents = new ArrayList<>();
                return false;
            }
        }
        return false;
    }

    /**
     * Function to write the buffer onto the disk, this is automatically called by many text manipulation functions
     *
     * @return Whether or not the operation was successful
     */
    public boolean writeBuffer() {
        File f = path.toFile();
        if (this.writable && f.canWrite()) {
            StringBuilder finStr = new StringBuilder();
            for (String i : this.contents) {
                finStr.append(i).append("\n");
            }

            try {
                FileWriter fw = new FileWriter(f.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(finStr.toString());
                bw.close();
            } catch (Exception e) {
                printError(e);
                return false;
            }

            return true;
        }
        return false;
    }

    public void execute() {
        // Add lib perms stuff
        // TODO: 7/1/22 Add lua stuff
        if (execPerms.canExecute) {

        }
    }

    /**
     * Private function to print some file details about an error the the console
     *
     * @param e Exception to be printed as well
     */
    private void printError(Exception e) {
        System.out.println("Failed: " + this.dir + ": " + e.getMessage());
        e.printStackTrace();
    }
}
