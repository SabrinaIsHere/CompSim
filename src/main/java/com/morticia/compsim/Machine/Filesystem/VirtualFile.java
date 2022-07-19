package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineIOStream.IOComponent;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Lua.Lib.IOLib;
import org.luaj.vm2.LuaTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A data container meant to integrate DiskFiles into the virtual filesystem
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class VirtualFile extends FilesystemObject implements Serializable, IOComponent {
    public DiskFile trueFile;

    /**
     * Constructor
     *
     * @param parent Parent folder of this object
     * @param _name Name of this file
     */
    public VirtualFile(VirtualFolder parent, String _name) {
        super(parent.filesystem, _name, parent);

        this.trueFile = new DiskFile(filesystem.getDiskDir() + parent.getPath(), _name, true);
    }

    /**
     * This is used to create a file object without initializing it so serialized data can be parsed
     *
     * @param machine Machine this is attached to
     */
    public VirtualFile(Machine machine) {
        super(machine.filesystem, null, null);
    }

    @Override
    public String getType() {
        return Constants.v_file_type;
    }

    @Override
    public String getDesig() {
        return this.parent.getPath() + "->" + this._name;
    }

    @Override
    public String serialize() { // TODO: 7/7/22 Find way to properly initialize filesystem data from this
        String var = prepParams(new String[][]{
                {"parent_folder", parent.getPath()},
                {"file_name", _name},
                {"owner", perms.owner.userName},
                {"group", perms.group.groupName},
                {"file_perms", perms.getPerms()},
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
                    this.parent = filesystem.getFolder(i[1]);
                    break;
                case "file_name":
                    this._name = i[1];
                    break;
                case "owner":
                    this.perms.owner = filesystem.machine.userHandler.getUser(i[1]);
                    break;
                case "group":
                    this.perms.group = filesystem.machine.userHandler.getGroup(i[1]);
                    break;
                case "file_perms":
                    this.perms.initPerms(i[1]);
                    this.trueFile = new DiskFile(filesystem.getDiskDir() + parent.getPath(), _name, true);
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

    @Override
    public LuaTable toTable() {
        LuaTable table = super.toTable();
        table.set("type", "file");
        table.set("get_contents", new IOLib.get_contents(this));
        table.set("set_contents", new IOLib.set_contents(this));
        table.set("execute", new IOLib.execute(this));
        table.set("set_output", new IOLib.set_output(this));
        return table;
    }

    int index = 0;
    @Override
    public String readLine() {
        if (index + 1 >= trueFile.getNumLines()) {
            index = 0;
            return null;
        }
        index++;
        return trueFile.contents.get(index);
    }

    @Override
    public void writeLine(String data) {
        trueFile.appendLine(data);
    }

    @Override
    public LuaTable getAllData() {
        LuaTable table = new LuaTable();
        for (int i = 0; i < trueFile.contents.size(); i++) {
            table.set(i + 1, trueFile.contents.get(i));
        }
        return table;
    }
}
