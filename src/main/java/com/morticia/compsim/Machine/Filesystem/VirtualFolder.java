package com.morticia.compsim.Machine.Filesystem;

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

public class VirtualFolder {
    public final boolean isRoot;

    public Filesystem filesystem;

    public String folderName;
    public VirtualFolder parent;

    public List<VirtualFolder> folders;
    public List<VirtualFile> files;

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
     * Get the path to this folder from root (is a virtual path)
     *
     * @return The path to this folder
     */
    public String getPath() {
        if (isRoot) {
            return "";
        } else {
            return parent.getPath() + "/" + folderName;
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
}
