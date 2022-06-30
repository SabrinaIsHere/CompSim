package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Util.Disk.DiskFile;

public class VirtualFile {
    public String fileName;
    public VirtualFolder parent;
    public DiskFile trueFile;

    public VirtualFile(VirtualFolder parent, String fileName) {
        this.parent = parent;
        this.fileName = fileName;

        this.trueFile = new DiskFile(parent.getPath(), fileName, true);
    }
}
