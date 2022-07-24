package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.Lua.Lib.IOLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class FilesystemObject {
    public Filesystem filesystem;
    public String _name;
    public VirtualFolder parent;
    public FilePerms perms;

    public boolean remove;

    LuaTable luaInstance;

    public FilesystemObject(Filesystem filesystem, String _name, VirtualFolder parent) {
        this.filesystem = filesystem;
        this._name = _name;
        this.parent = parent;
        this.perms = new FilePerms(filesystem.machine.userHandler.currUser);
        this.remove = false;
    }

    public String getPath() {
        return parent.getPath() + _name;
    }

    public LuaTable instanceTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("type", "filesystem_object");
        table.set("is_directory", LuaValue.valueOf(this instanceof VirtualFolder));
        table.set("name", _name);
        table.set("get_perms", new IOLib.get_perms(this));
        table.set("set_perms", new IOLib.set_perms(this));
        table.set("get_owner", new IOLib.get_owner(this));
        table.set("get_group", new IOLib.get_group(this));
        table.set("set_owner", new IOLib.set_owner(this));
        table.set("set_group", new IOLib.set_group(this));
        table.set("update", new IOLib.update(this));
        table.set("get_path", new IOLib.get_path(this));
        table.set("get_parent", new IOLib.get_parent(this));
        table.set("delete", new IOLib.delete(this));
        return table;
    }

    public LuaTable toTable() {
        return luaInstance;
    }

    public static LuaTable getBlankTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(true));
        table.set("object_type", "filesystem_object");
        table.set("is_directory", LuaValue.valueOf(false));
        table.set("name", "n/a");
        return table;
    }

    public void delete() {
        luaInstance = getBlankTable();
        parent.removeObject(_name);
        DiskUtil.deleteFolder(parent.filesystem.getDiskDir() + "/" + getPath());
    }
}
