package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Filesystem.FilesystemObject;
import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
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
                return Err.getErrorTable(e.getMessage(), machine.defaultStream);
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
            String path = p.checkjstring();
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
            VirtualFolder f = machine.filesystem.getFolder(machine.filesystem.currFolder.getPath());
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
            String path = p.checkjstring();
            VirtualFolder f = machine.filesystem.getFolder(path);
            if (f != null) {
                machine.filesystem.currFolder = f;
                return Err.getBErrorTable();
            } else {
                return Err.getErrorTable("Invalid path");
            }
        }
    }

    public static class make_folder extends VarArgFunction {
        Machine machine;

        public make_folder(Machine machine) {
            this.machine = machine;
        }

        @Override
        public Varargs invoke(Varargs varargs) {
            String path = varargs.arg1().checkjstring();
            return LuaValue.varargsOf(new LuaValue[]{
                    LuaValue.valueOf(machine.filesystem.addFolder(path)),
                    machine.filesystem.getFolder(path).toTable()
            });
        }
    }

    public static class make_file extends VarArgFunction {
        Machine machine;

        public make_file(Machine machine) {
            this.machine = machine;
        }

        @Override
        public Varargs invoke(Varargs varargs) {
            String path = varargs.arg1().checkjstring();
            return LuaValue.varargsOf(new LuaValue[]{
                    LuaValue.valueOf(machine.filesystem.addFile(path)),
                    machine.filesystem.getFile(path).toTable()
            });
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
                retVal.set(retVal.length() + 1, LuaValue.valueOf(i));
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
            file.trueFile.writeBuffer();
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
                return file.trueFile.execute(file.filesystem.machine, args);
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage(), file.filesystem.machine.defaultStream);
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
            file.filesystem.machine.defaultStream.component = file;
            return LuaValue.NIL;
        }
    }

    public static class __call extends VarArgFunction {
        VirtualFile file;

        public __call(VirtualFile file) {
            this.file = file;
        }

        @Override
        public Varargs invoke(Varargs varargs) {
            try {
                return file.trueFile.execute(file.filesystem.machine, varargs.arg1());
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage(), file.filesystem.machine.defaultStream);
            }
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
                retVal.set(retVal.length() + 1, i.toTable());
            }
            for (VirtualFile i : folder.files) {
                retVal.set(retVal.length() + 1, i.toTable());
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
                return Err.getErrorTable(e.getMessage(), folder.filesystem.machine.defaultStream);
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
                return Err.getErrorTable(e.getMessage(), folder.filesystem.machine.defaultStream);
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

    public static class get_owner extends ZeroArgFunction {
        FilesystemObject object;

        public get_owner(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            return object.perms.owner.toTable();
        }
    }

    public static class get_group extends ZeroArgFunction {
        FilesystemObject object;

        public get_group(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            return object.perms.group.toTable();
        }
    }

    public static class set_owner extends OneArgFunction {
        FilesystemObject object;

        public set_owner(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call(LuaValue owner) {
            LuaTable table = owner.checktable();
            object.perms.owner = object.filesystem.machine.userHandler.getUser(table.get("name").checkjstring());
            return LuaNil.NIL;
        }
    }

    public static class set_group extends OneArgFunction {
        FilesystemObject object;

        public set_group(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call(LuaValue group) {
            LuaTable table = group.checktable();
            object.perms.group = object.filesystem.machine.userHandler.getGroup(table.get("name").checkjstring());
            return LuaNil.NIL;
        }
    }

    public static class update extends ZeroArgFunction {
        FilesystemObject object;

        public update(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            return object.toTable();
        }
    }

    public static class get_path extends ZeroArgFunction {
        FilesystemObject object;

        public get_path(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(object.getPath());
        }
    }

    public static class get_parent extends ZeroArgFunction {
        FilesystemObject object;

        public get_parent(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            if (object.parent == null) {
                return object.toTable();
            } else {
                return object.parent.toTable();
            }
        }
    }

    public static class delete extends ZeroArgFunction {
        FilesystemObject object;

        public delete(FilesystemObject object) {
            this.object = object;
        }

        @Override
        public LuaValue call() {
            object.delete();
            return LuaNil.NIL;
        }
    }
}
