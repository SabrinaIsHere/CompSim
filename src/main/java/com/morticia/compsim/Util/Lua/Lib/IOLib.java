package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Filesystem.FilesystemObject;
import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineIOStream.MachineIOStream;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.ArrayList;
import java.util.List;

public class IOLib extends TwoArgFunction {
    public Machine machine;

    public IOLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue mod_name, LuaValue env) {
        LuaValue library = tableOf();
        library.set("move", new move(machine));
        library.set("get", new get(machine));
        library.set("get_working_dir", new get_working_dir(machine));
        library.set("set_working_dir", new set_working_dir(machine));
        library.set("make_folder", new make_folder(machine));
        library.set("make_file", new make_file(machine));
        env.set("io", library);
        return library;
    }

    public static class move extends TwoArgFunction {
        Machine machine;

        public move(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue old_path, LuaValue new_path) {
            try {
                String o_path = old_path.tojstring();
                String n_path = new_path.tojstring();
                FilesystemObject o = machine.filesystem.getObject(o_path);
                if (o == null) {
                    throw new Error("Invalid path [" + o_path + "]");
                }
                VirtualFolder parent = machine.filesystem.getFolder(n_path);
                if (parent == null) {
                    throw new Error("Invalid path [" + n_path + "]");
                }
                if (parent.addChild(o)) {
                    machine.filesystem.removeObject(o_path);
                }
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class get extends OneArgFunction {
        Machine machine;

        public get(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue p) {
            String path = p.tojstring();
            FilesystemObject o = machine.filesystem.getObject(path);
            if (o == null) {
                return FilesystemObject.getBlankTable();
            } else {
                return o.toTable();
            }
        }
    }

    public static class get_working_dir extends ZeroArgFunction {
        Machine machine;

        public get_working_dir(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            VirtualFolder f = machine.filesystem.currFolder;
            if (f == null) {
                return FilesystemObject.getBlankTable();
            } else {
                return f.toTable();
            }
        }
    }

    public static class set_working_dir extends OneArgFunction {
        Machine machine;

        public set_working_dir(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue p) {
            String path = p.tojstring();
            VirtualFolder f = machine.filesystem.getFolder(path);
            if (f == null) {
                return Err.getErrorTable("Invalid path provided");
            } else {
                machine.filesystem.currFolder = f;
                return Err.getBErrorTable();
            }
        }
    }

    public static class make_folder extends OneArgFunction {
        Machine machine;

        public make_folder(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue p) {
            String path = p.tojstring();
            if (machine.filesystem.addFolder(path)) {
                return Err.getBErrorTable();
            } else {
                return Err.getErrorTable("Invalid path given");
            }
        }
    }

    public static class make_file extends OneArgFunction {
        Machine machine;

        public make_file(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue p) {
            String path = p.tojstring();
            if (machine.filesystem.addFile(path)) {
                return Err.getBErrorTable();
            } else {
                return Err.getErrorTable("Invalid path given");
            }
        }
    }

    // All the functions past this are added to filesystem object tables, starting with file functions

    public static class get_contents extends ZeroArgFunction {
        VirtualFile file;

        public get_contents(VirtualFile file) {
            this.file = file;
        }

        @Override
        public LuaValue call() {
            LuaTable retVal = new LuaTable();
            for (String i : file.trueFile.contents) {
                retVal.add(LuaValue.valueOf(i));
            }
            return retVal;
        }
    }

    public static class set_contents extends OneArgFunction {
        VirtualFile file;

        public set_contents(VirtualFile file) {
            this.file = file;
        }

        @Override
        public LuaValue call(LuaValue new_contents) {
            List<String> container = new ArrayList<>();
            for (int i = 1; i <= new_contents.length(); i++) {
                container.add(new_contents.get(i).tojstring());
            }
            file.trueFile.contents = container;
            return LuaNil.NIL;
        }
    }

    public static class execute extends OneArgFunction {
        VirtualFile file;

        public execute(VirtualFile file) {
            this.file = file;
        }

        @Override
        public LuaValue call(LuaValue args) {
            try {
                file.trueFile.execute(file.filesystem.machine, args);
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class set_output extends ZeroArgFunction {
        VirtualFile file;

        public set_output(VirtualFile file) {
            this.file = file;
        }

        @Override
        public LuaValue call() {
            file.filesystem.machine.defaultStream = new MachineIOStream(file.getPath(), file);
            return LuaValue.NIL;
        }
    }

    // Folder functions

    public static class get_children extends ZeroArgFunction {
        VirtualFolder folder;

        public get_children(VirtualFolder folder) {
            this.folder = folder;
        }

        @Override
        public LuaValue call() {
            LuaTable retVal = new LuaTable();
            for (VirtualFolder i : folder.folders) {
                retVal.add(i.toTable());
            }
            for (VirtualFile i : folder.files) {
                retVal.add(i.toTable());
            }
            return retVal;
        }
    }

    public static class remove_child extends OneArgFunction {
        VirtualFolder folder;

        public remove_child(VirtualFolder folder) {
            this.folder = folder;
        }

        @Override
        public LuaValue call(LuaValue child) {
            try {
                String str = child.tojstring();
                if (!folder.removeObject(str)) {
                    throw new Error("[" + str + "] child not found");
                }
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class add_child extends OneArgFunction {
        VirtualFolder folder;

        public add_child(VirtualFolder folder) {
            this.folder = folder;
        }

        @Override
        public LuaValue call(LuaValue child) {
            try {
                FilesystemObject o;
                if (child.get("is_directory").toboolean()) {
                    o = new VirtualFolder(folder.filesystem, folder, child.get("name").tojstring());
                } else {
                    o = new VirtualFile(folder, child.get("name").tojstring());
                }
                if (!folder.addChild(o)) {
                    throw new Error("[" + o._name + "] child not found");
                }
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    // This applies to all filesystem objects

    public static class get_perms extends ZeroArgFunction {
        FilesystemObject object;

        public get_perms(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(object.perms.getPerms());
        }
    }

    public static class set_perms extends OneArgFunction {
        FilesystemObject object;

        public set_perms(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call(LuaValue perms) {
            object.perms.initPerms(perms.tojstring());
            return LuaNil.NIL;
        }
    }
}
