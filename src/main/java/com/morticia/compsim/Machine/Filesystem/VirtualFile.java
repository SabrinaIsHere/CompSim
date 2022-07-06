package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Util.Disk.DiskFile;

/**
 * A data container meant to integrate DiskFiles into the virtual filesystem
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class VirtualFile {
    public String fileName;
    public VirtualFolder parent;
    public DiskFile trueFile;
    public Filesystem filesystem;

    /**
     * Constructor
     *
     * @param parent Parent folder of this object
     * @param fileName Name of this file
     */
    public VirtualFile(VirtualFolder parent, String fileName) {
        this.parent = parent;
        this.fileName = fileName;
        this.filesystem = parent.filesystem;

        this.trueFile = new DiskFile(filesystem.getDiskDir() + parent.getPath(), fileName, true);
    }
}
