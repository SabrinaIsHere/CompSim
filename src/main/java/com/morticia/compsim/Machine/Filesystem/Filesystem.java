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

    // Like a working directory in linux
    public VirtualFolder currFolder;

    public Filesystem(Machine machine) {
        this.machine = machine;

        // Initializes data from disk recursively
        this.root = new VirtualFolder(this);

        this.currFolder = root;
        this.events = root.getFolder("evn");

        machine.userHandler.root.homeFolder = getFolder("/root");
        if (machine.userHandler.root.homeFolder == null) {
            this.machine.userHandler.root.homeFolder = new VirtualFolder(this, root, "root");
            //machine.filesystem.getFolder("/home").addFolder(homeFolder);
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
            if (i + 1 == str_list.length) {
                return f.getFolder(i_str);
            } else {
                f = f.getFolder(i_str);
            }
        }

        return null;
    }

    /**
     * Get the file at the provided path
     *
     * @param path Path to wanted file
     * @return The file at the provided path
     */
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

    public FilesystemObject getObject(String name) {
        VirtualFolder folder = getFolder(name);
        if (folder == null) {
            VirtualFile file = getFile(name);
            if (file == null) {
                return null;
            } else {
                return file;
            }
        } else {
            return folder;
        }
    }

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
    public void executeScript(String path) {
        VirtualFile f = getFile(path);
        if (f != null) {
            f.trueFile.execute(machine);
        } else {
            // TODO: 7/2/22 This should probably just return a boolean but I need this for debugging
            System.out.println("Could not find file at " + path);
        }
    }

    public void saveAll() {
        root.saveChildren();
    }

    public void parseAll() {

    }
}
