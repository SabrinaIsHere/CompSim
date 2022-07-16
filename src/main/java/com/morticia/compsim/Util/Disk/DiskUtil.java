package com.morticia.compsim.Util.Disk;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiskUtil {
    public static String masterDir;
    public static Path masterPath;

    private static final String[] gamedataFolders = {
            "Config",
            "Logs",
            "Machines",
            "Metadata",
            "TemplateDevices",
            "TemplateDisk",
            "TemplateDisk/boot",
            "TemplateDisk/ROM"};

    private static final String MachineDir = "/Machines/";

    private DiskUtil() {}

    /**
     * Initialization function, populates Gamedata automatically if one isn't detected
     */
    public static void init() {
        masterDir = System.getProperty("user.dir") + "/Gamedata";
        masterPath = Paths.get(masterDir);

        // Make sure Gamedata folder exists since everything relies on it
        File f = new File(masterDir);
        if (!f.exists()) {
            if (!f.mkdir()) {
                printError("Gamedata", "dir");
            }
        }

        for (String i : gamedataFolders) {
            writeFolder(i);
        }
    }

    /**
     * Utility function to tell if a given path is relative to Gamedata or objective
     *
     * @param path Path to make determination with
     * @return Whether or not path is objective
     */
    public static boolean isObjectivePath(String path) {
        return path.startsWith(masterDir);
    }

    /**
     * Gets objective path from a provided subjective path
     * <p>
     * Won't break if objective path is supplied
     *
     * @param path Path to make objective
     * @return The objective path
     */
    public static String getObjectivePath(String path) {
        if (isObjectivePath(path)) {
            return path;
        } else if (path.startsWith("/")) {
            return masterDir + path;
        } else {
            return masterDir + "/" + path;
        }
    }

    public static String getRelativePath(String path) {
        if (!isObjectivePath(path)) {
            return path;
        } else {
            return path.replaceFirst(masterDir, "");
        }
    }

    public static String removeObjectivePaths(String text, String machineDesig) {
        return text.replaceAll(masterDir + "/Machines/" + machineDesig + "/Disk", "")
                .replaceAll(masterDir + "/Machines/" + machineDesig, "");
    }

    /**
     * Checks if a folder exists
     *
     * @param path Relative path to folder
     * @return Whether the folder has been found or not
     */
    public static boolean folderExists(String path) {
        File f = new File(getObjectivePath(path));
        return f.exists() && f.isDirectory();
    }

    /**
     * Creates a folder at given path. Name is part of path (i.e. /test/1/folder)
     *
     * @param path Path to folder, including name of folder
     * @return Whether or not folder was successfully created (returns true if the folder exists already)
     */
    public static boolean writeFolder(String path) {
        File f = new File(getObjectivePath(path));
        if (!f.exists()) {
            if (!f.mkdir()) {
                printError(getObjectivePath(path), "Dir");
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    /**
     * Utility function to tell if a given file exists or not
     *
     * @param path Path to file, including name of file
     * @return Whether or not file is found
     */
    public static boolean fileExists(String path) {
        File f = new File(getObjectivePath(path));
        return f.exists() && !f.isDirectory();
    }

    /**
     * Creates a file at given relative path
     *
     * @param path Path to file, including name
     * @return Whether or not file was successfully created
     */
    public static boolean makeFile(String path) {
        File f = new File(getObjectivePath(path));
        if (!f.exists()) {
            try {
                return f.createNewFile();
            } catch (Exception e) {
                printError(getObjectivePath(path), "file");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Copies the contents of a folder (srcDir) to another folder (destDir)
     * <p>
     * If destDir does not exist it is created, credit to stackoverflow for this function
     *
     * @param srcDir Directory to copy from
     * @param destDir Directory to copy to
     * @return Whether or not copy was successful
     */
    public static boolean copyFolder(String srcDir, String destDir) {
        File src = new File(getObjectivePath(srcDir));
        File dest = new File(getObjectivePath(destDir));

        try {
            if(src.isDirectory()){
                if(!dest.exists()){
                    dest.mkdir();
                }

                String[] files = src.list();

                if (files == null) {
                    printError(srcDir, "folder");
                    return false;
                }

                for (String file : files) {
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);

                    copyFolder(srcFile.getPath(), destFile.getPath());
                }

            } else {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0){
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            }
        } catch (Exception e) {
            printError(srcDir + " -> " + destDir, "File/Dir");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean deleteFolder(String path) {
        File f = new File(getObjectivePath(path));
        return f.delete();
    }

    /**
     * Renames a directory to the name provided
     *
     * @param src Directory to rename
     * @param newName New name for the directory
     * @return Whether or not the operation was successful
     */
    public static boolean renameDir(String src, String newName) {
        File f = new File(src);
        return f.renameTo(new File(newName));
    }

    public static File[] getFolderChildren(String path) {
        return new File(getObjectivePath(path)).listFiles();
    }

    /**
     * Populates a machine's data
     *
     * @param machineName Name of the machine to be populating (desig)
     * @return Whether or not the operation was successful
     */
    public static boolean populateMachine(String machineName) {
        String dir = MachineDir + machineName;
        if (!writeFolder(dir)) {
            printError(dir, "folder");
            return false;
        }
        if (!copyFolder("TemplateDisk", dir + "/Disk")) {
            return false;
        }
        return writeFolder(dir + "/Devices");
    }

    private static void printError(String path, String type) {
        System.out.println("Error: [" + type + "] Failed: " + path);
    }
}
