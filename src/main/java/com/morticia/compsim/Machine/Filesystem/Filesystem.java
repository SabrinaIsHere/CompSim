package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.Machine;

import java.util.List;

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

    // Like a working directory in linux
    public VirtualFolder currFolder;

    /**
     * Constructor
     *
     * @param machine Machine this filesystem is attached to
     */
    public Filesystem(Machine machine) {
        this.machine = machine;

        // Initializes data from disk recursively
        this.root = new VirtualFolder(this);

        this.currFolder = root;
        this.events = root.getFolder("evn");

        machine.userHandler.root.homeFolder = getFolder("/root");
        if (machine.userHandler.root.homeFolder == null) {
            this.machine.userHandler.root.homeFolder = new VirtualFolder(this, root, "root");
        }

        machine.logHandler.log("Filesystem initialized");
    }

    /**
     * Gets the directory of this machine's disk
     *
     * @return The path to this machines disk
     */
    public String getDiskDir() {
        return machine.getMachineDir() + "/Disk";
    }

    /**
     * Gets the folder at the provided path
     *
     * @param path Path to folder to get
     * @return Folder described by path. Null if folder doesn't exist
     */
    public VirtualFolder getFolder(String path) {
        if (path.equals("/")) {
            return root;
        }

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
            if (f == null) {
                return null;
            } else {
                if (i_str.equals("..")) {
                    if (f.parent != null) {
                        f = f.parent;
                    }
                    continue;
                } else if (i_str.equals(".")) {
                    continue;
                }
                f = f.getFolder(i_str);
            }
        }
        return f;
    }

    /**
     * Get the file at the provided path
     *
     * @param path Path to wanted file
     * @return The file at the provided path
     */
    public VirtualFile getFile(String path) {
        int index = path.lastIndexOf("/");
        String f_path = "";
        if (index < 0) {
            f_path = "/";
        } else {
            f_path = path.substring(0, path.lastIndexOf("/"));
            if (f_path.isBlank()) {
                f_path = "/";
            }
        }
        VirtualFolder f = getFolder(f_path);
        if (f == null) {
            return null;
        } else {
            try {
                return f.getFile(path.substring(path.lastIndexOf("/") + 1));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Gets an object at the given path
     *
     * @param path Path to the object
     * @return The object at the given path
     */
    public FilesystemObject getObject(String path) {
        VirtualFolder folder = getFolder(path);
        if (folder == null) {
            return getFile(path);
        } else {
            return folder;
        }
    }

    /**
     * Adds an object to the given path
     *
     * @param path Path to add to
     * @param o Object to add
     * @return Whether or not the operation was successful
     */
    public boolean addObject(String path, FilesystemObject o) {
        String[] str = path.split("/");
        StringBuilder parent_path_builder = new StringBuilder();
        String name = str[str.length - 1];
        VirtualFolder parent;
        if (str.length < 2) {
            parent = root;
        } else {
            for (int i = 0; i < str.length - 1; i++) {
                parent_path_builder.append(str[i]).append("/");
            }
            parent = getFolder(parent_path_builder.toString());
        }
        if (parent == null) {
            return false;
        } else {
            return parent.addChild(o);
        }
    }

    /**
     * Adds folder to the given path
     *
     * @param path Path to add a folder to
     * @return Whether or not the operation was successful
     */
    public boolean addFolder(String path) {
        String[] str = path.split("/");
        StringBuilder parent_path_builder = new StringBuilder();
        String name = str[str.length - 1];
        VirtualFolder parent;
        if (str.length < 2) {
            parent = root;
        } else {
            for (int i = 0; i < str.length - 1; i++) {
                parent_path_builder.append(str[i]).append("/");
            }
            parent = getFolder(parent_path_builder.toString());
        }
        if (parent == null) {
            return false;
        } else {
            return parent.addFolder(new VirtualFolder(this, parent, name));
        }
    }

    /**
     * Adds a file at the given path
     *
     * @param path Path to add a file to
     * @return Whether or not the operation was successful
     */
    public boolean addFile(String path) {
        String[] str = path.split("/");
        StringBuilder parent_path_builder = new StringBuilder();
        String name = str[str.length - 1];
        VirtualFolder parent;
        if (str.length < 2) {
            parent = root;
        } else {
            for (int i = 0; i < str.length - 1; i++) {
                parent_path_builder.append(str[i]).append("/");
            }
            parent = getFolder(parent_path_builder.toString());
        }
        if (parent == null) {
            return false;
        } else {
            return parent.addFile(new VirtualFile(parent, name));
        }
    }

    /**
     * Removes an object at the given path
     *
     * @param path Path to isolate the object from
     * @return Whether or not the operation was successful
     */
    public boolean removeObject(String path) {
        String[] str = path.split("/");
        StringBuilder parent_path_builder = new StringBuilder();
        String name = str[str.length - 1];
        VirtualFolder parent;
        if (str.length < 2) {
            parent = root;
        } else {
            for (int i = 0; i < str.length - 1; i++) {
                parent_path_builder.append(str[i]).append("/");
            }
            parent = getFolder(parent_path_builder.toString());
        }
        if (parent == null) {
            return false;
        } else {
            return parent.removeObject(name);
        }
    }

    /**
     * Executes the script at the path provided
     *
     * @param path Path to find the script at
     */
    public boolean executeScript(String path) {
        VirtualFile f = getFile(path);
        if (f != null) {
            f.trueFile.execute(machine);
            return true;
        } else {
            return false;
        }
    }

    public void saveAll() {
        root.saveChildren();
    }
}
