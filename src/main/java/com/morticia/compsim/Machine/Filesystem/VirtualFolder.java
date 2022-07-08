package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Disk.DiskUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A data container that makes up the body of filesystem organization
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class VirtualFolder implements Serializable {
    public final boolean isRoot;

    public Filesystem filesystem;

    public String folderName;
    public VirtualFolder parent;

    public List<VirtualFolder> folders;
    public List<VirtualFile> files;

    public FilePerms perms;

    /**
     * Constructor
     *
     * @param filesystem The filesystem this is attached to
     * @param parent The parent folder of this object
     * @param name Name of this folder
     */
    public VirtualFolder(Filesystem filesystem, VirtualFolder parent, String name) {
        this.isRoot = false;

        this.filesystem = filesystem;

        this.folderName = name;
        this.parent = parent;

        this.folders = new ArrayList<>();
        this.files = new ArrayList<>();

        this.perms = new FilePerms(filesystem.machine.userHandler.currUser);
        // TODO: 7/7/22 Have perms inherit from parents, have it propograte when perms are changed

        // Attempt to initialize data from actual disk
        File f = new File(DiskUtil.getObjectivePath(filesystem.getDiskDir() + getPath()));
        if (f.exists() && f.isDirectory()) {
            File[] fs = DiskUtil.getFolderChildren(filesystem.getDiskDir() + getPath());
            for (File i : fs) {
                if (i.isFile()) {
                    addFile(new VirtualFile(this, i.getName()));
                } else {
                    addFolder(new VirtualFolder(filesystem, this, i.getName()));
                }
            }
        } else {
            f.mkdir();
        }

        parent.addFolder(this);
    }

    /**
     * Constructor (this is only used for making the root folder)
     *
     * @param filesystem Filesystem this is attached to
     */
    public VirtualFolder(Filesystem filesystem) {
        this.isRoot = true;

        this.filesystem = filesystem;
        this.filesystem.root = this;

        this.folderName = "root";
        this.parent = null;

        this.folders = new ArrayList<>();
        this.files = new ArrayList<>();

        this.perms = new FilePerms(filesystem.machine.userHandler.currUser);

        // Attempt to initialize data from actual disk
        File f = new File(DiskUtil.getObjectivePath(filesystem.getDiskDir()));
        if (f.exists() && f.isDirectory()) {
            File[] fs = DiskUtil.getFolderChildren(filesystem.getDiskDir());
            for (File i : fs) {
                if (i.isFile()) {
                    addFile(new VirtualFile(this, i.getName()));
                } else {
                    addFolder(new VirtualFolder(filesystem, this, i.getName()));
                }
            }
        }
    }

    /**
     * Constructor (mostly used during parsing of serialized folder data)
     *
     * @param machine The machine this is attached to
     */
    public VirtualFolder(Machine machine) {
        this.isRoot = false;

        this.filesystem = machine.filesystem;
        this.parent = null;

        this.folders = new ArrayList<>();
        this.files = new ArrayList<>();

        this.perms = new FilePerms(filesystem.machine.userHandler.currUser);
    }

    /**
     * Get the path to this folder from root (is a virtual path)
     *
     * @return The path to this folder
     */
    public String getPath() {
        if (isRoot) {
            return "/";
        } else {
            return parent.getPath() + folderName + "/";
        }
    }

    /**
     * Adds a folder to this folder
     *
     * @param folder Folder to add
     * @return Whether or not the operation was successful
     */
    public boolean addFolder(VirtualFolder folder) {
        // Don't add two folders of the same name
        for (VirtualFolder i : folders) {
            if (i.folderName.equals(folder.folderName)) {
                return false;
            }
        }
        folders.add(folder);
        if (folder.parent == null) {
            folder.parent = this;
        }
        return true;
    }

    /**
     * Adds a file to this folder
     *
     * @param file File to add
     * @return Whether or not operation was successful
     */
    public boolean addFile(VirtualFile file) {
        // Don't add two files of the same name
        for (VirtualFile i : files) {
            if (i.fileName.equals(file.fileName)) {
                return false;
            }
        }
        files.add(file);
        return true;
    }

    /**
     * Gets a folder of provided name from folder children
     *
     * @param f_name Name of the sought folder
     * @return Folder object corresponding to the given name, null if not found
     */
    public VirtualFolder getFolder(String f_name) {
        for (VirtualFolder i : folders) {
            if (i.folderName.equals(f_name)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Gets a file of provided name from folder children
     *
     * @param f_name Name of the sought file
     * @return File object corresponding to the given name, null if not found
     */
    public VirtualFile getFile(String f_name) {
        for (VirtualFile i : files) {
            if (i.fileName.equals(f_name)) {
                return i;
            }
        }
        return null;
    }

    public void replaceFile(VirtualFile f) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).fileName.equals(f.fileName)) {
                files.set(i, f);
            }
        }
    }

    public void replaceFolder(VirtualFolder f) {
        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).folderName.equals(f.folderName)) {
                folders.set(i, f);
            }
        }
    }

    public void saveChildren() {
        if (!isRoot) {
            filesystem.machine.dataHandler.add(this);
        }
        for (VirtualFolder i : folders) {
            i.saveChildren();
        }
        for (VirtualFile i : files) {
            filesystem.machine.dataHandler.add(i);
        }
    }

    @Override
    public String getType() {
        return Constants.v_folder_type;
    }

    @Override
    public String getDesig() {
        return parent.getPath() + "->" + folderName;
    }

    @Override
    public String serialize() {
        String var = prepParams(new String[][]{
                {"parent_folder", parent.getPath()},
                {"folder_name", folderName},
                {"owner", perms.owner.userName},
                {"group", perms.group.groupName},
                {"file_perms", perms.getPerms()},
        });
        return getPrefix() + var;
    }

    @Override
    public void parse(String txt) {
        List<String[]> str_1 = extractParams(txt);
        for (String[] i : str_1) {
            switch (i[0]) {
                case "n/a":
                    continue;
                case "parent_folder":
                    this.parent = filesystem.getfolder(i[1]);
                    break;
                case "file_name":
                    this.folderName = i[1];
                    break;
                case "owner":
                    this.perms.owner = filesystem.machine.userHandler.getUser(i[1]);
                    break;
                case "group":
                    this.perms.group = filesystem.machine.userHandler.getGroup(i[1]);
                    break;
                case "file_perms":
                    this.perms.initPerms(i[1]);
                    break;
            }
        }
        parent.replaceFolder(this);
    }
}
