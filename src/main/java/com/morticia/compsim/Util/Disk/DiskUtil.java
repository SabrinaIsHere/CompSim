package com.morticia.compsim.Util.Disk;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiskUtil {
    public static String masterDir;
    public static Path masterPath;

    private DiskUtil() {}

    public static void init() {
        masterDir = System.getProperty("user.dir") + "/Gamedata";
        masterPath = Paths.get(masterDir);

        File f = new File(masterDir);
        if (!f.exists()) {
            if (!f.mkdir()) {
                System.out.println("Error creating [Gamedata] folder");
            }
        }
    }

    public static boolean isObjectivePath(String path) {
        return path.startsWith(masterDir);
    }

    public static String getObjectivePath(String path) {
        if (isObjectivePath(path)) {
            return path;
        } else if (path.endsWith("/")) {
            return path + masterDir;
        } else {
            return path + "/" + masterDir;
        }
    }

    public static boolean folderExists(String path) {
        File f = new File(getObjectivePath(path));
        return f.exists() && f.isDirectory();
    }

    public static boolean writeFolder(String path) {
        File f = new File(getObjectivePath(path));
        if (!f.exists()) {
            if (!f.mkdir()) {
                System.out.println("Error: could not create [" + getObjectivePath(path) + "] directory");
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean fileExists(String path) {
        File f = new File(getObjectivePath(path));
        return f.exists() && !f.isDirectory();
    }

    public static boolean makeFile(String path) {
        File f = new File(getObjectivePath(path));
        if (!f.exists()) {
            try {
                return f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
