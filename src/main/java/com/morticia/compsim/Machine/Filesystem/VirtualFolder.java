package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Util.Disk.DiskUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VirtualFolder {
    public final boolean isRoot;

    public Filesystem filesystem;

    public String folderName;
    public VirtualFolder parent;

    public List<VirtualFolder> folders;
    public List<VirtualFile> files;

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

    public VirtualFolder(Filesystem filesystem) {
        this.isRoot = true;

        this.filesystem = filesystem;

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

    public String getPath() {
        if (isRoot) {
            return "";
        } else {
            return parent.getPath() + "/" + folderName;
        }
    }

    public boolean addFolder(VirtualFolder folder) {
        // Don't add two folders of the same name
        for (VirtualFolder i : folders) {
            if (i.folderName.equals(folder.folderName)) {
                return false;
            }
        }
        folders.add(folder);
        return true;
    }

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

    public VirtualFolder getFolder(String f_name) {
        for (VirtualFolder i : folders) {
            if (i.folderName.equals(f_name)) {
                return i;
            }
        }
        return null;
    }

    public VirtualFile getFile(String f_name) {
        for (VirtualFile i : files) {
            if (i.fileName.equals(f_name)) {
                return i;
            }
        }
        return null;
    }
}
