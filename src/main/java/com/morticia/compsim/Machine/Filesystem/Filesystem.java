package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.Machine;

/**
 * This could all be handled in the machine, technically, but I thought separating it would help with clutter
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class Filesystem {
    public Machine machine;

    public VirtualFolder root;
    public VirtualFolder events;

    public VirtualFolder currFolder;

    public Filesystem(Machine machine) {
        this.machine = machine;

        // Initializes data from disk recursively
        this.root = new VirtualFolder(this);

        this.currFolder = root;
        this.events = root.getFolder("evn");

        machine.logHandler.log("Filesystem initialized");
    }

    public String getDiskDir() {
        return machine.getMachineDir() + "/Disk";
    }

    public VirtualFolder getfolder(String path) {
        VirtualFolder f;

        path = path.strip();
        if (path.startsWith("/")) {
            f = root;
            path = path.replaceFirst("/", "");
        } else {
            f = currFolder;
        }
        String[] str_list = path.split("/");

        for (int i = 0; i < str_list.length; i++) {
            String i_str = str_list[i];
            if (i + 1 == str_list.length) {
                return f.getFolder(i_str);
            } else {
                f = f.getFolder(i_str);
            }
        }

        return null;
    }

    public VirtualFile getFile(String path) {
        VirtualFolder f;

        path = path.strip();
        if (path.startsWith("/")) {
            f = root;
            path = path.replaceFirst("/", "");
        } else {
            f = currFolder;
        }
        String[] str_list = path.split("/");

        String i_str;
        for (int i = 0; i < str_list.length; i++) {
            i_str = str_list[i];
            if (f == null) {
                System.out.println("Could not find " + i_str + " folder");
                return null;
            }
            if (i + 1 == str_list.length) {
                return f.getFile(i_str);
            } else {
                f = f.getFolder(i_str);
            }
        }

        return null;
    }

    public void executeScript(String path) {
        VirtualFile f = getFile(path);
        if (f != null) {
            f.trueFile.execute(machine);
        } else {
            // TODO: 7/2/22 This should probably just return a boolean but I need this for debugging
            System.out.println("Could not find file at " + path);
        }
    }
}
