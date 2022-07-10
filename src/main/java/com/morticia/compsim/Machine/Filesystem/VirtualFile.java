package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Disk.DiskFile;

import java.util.ArrayList;
import java.util.List;

/**
 * A data container meant to integrate DiskFiles into the virtual filesystem
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class VirtualFile implements Serializable {
    public String fileName;
    public VirtualFolder parent;
    public DiskFile trueFile;
    public Filesystem filesystem;

    public FilePerms filePerms;

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
        this.filePerms = new FilePerms(parent.filesystem.machine.userHandler.currUser);

        this.trueFile = new DiskFile(filesystem.getDiskDir() + parent.getPath(), fileName, true);
    }

    /**
     * This is used to create a file object without initializing it so serialized data can be parsed
     *
     * @param machine Machine this is attached to
     */
    public VirtualFile(Machine machine) {
        this.filesystem = machine.filesystem;
        this.filePerms = new FilePerms(machine.userHandler.currUser);
    }

    @Override
    public String getType() {
        return Constants.v_file_type;
    }

    @Override
    public String getDesig() {
        return this.parent.getPath() + "->" + this.fileName;
    }

    @Override
    public String serialize() { // TODO: 7/7/22 Find way to properly initialize filesystem data from this
        String var = prepParams(new String[][]{
                {"parent_folder", parent.getPath()},
                {"file_name", fileName},
                {"owner", filePerms.owner.userName},
                {"group", filePerms.group.groupName},
                {"file_perms", filePerms.getPerms()},
                {"can_execute", Boolean.toString(trueFile.execPerms.canExecute)},
                {"kernel_table_access", Boolean.toString(trueFile.execPerms.kernelTableAccess)},
                {"lib_access", trueFile.execPerms.libAccess.toString()}
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
                    this.fileName = i[1];
                    break;
                case "owner":
                    this.filePerms.owner = filesystem.machine.userHandler.getUser(i[1]);
                    break;
                case "group":
                    this.filePerms.group = filesystem.machine.userHandler.getGroup(i[1]);
                    break;
                case "file_perms":
                    this.filePerms.initPerms(i[1]);
                    this.trueFile = new DiskFile(filesystem.getDiskDir() + parent.getPath(), fileName, true);
                    break;
                case "can_execute":
                    trueFile.execPerms.canExecute = Boolean.parseBoolean(i[1]);
                    break;
                case "kernel_table_access":
                    trueFile.execPerms.kernelTableAccess = Boolean.parseBoolean(i[1]);
                    break;
                case "lib_access":
                    trueFile.execPerms.libAccess = new ArrayList<>(List.of(Serializable.getListMembers(i[1])));
                    break;
            }
        }
        this.parent.replaceFile(this);
    }
}
