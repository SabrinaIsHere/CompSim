package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Machine;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class ExLib extends TwoArgFunction {
    public Machine machine;

    public ExLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue mod_name, LuaValue env) {
        LuaTable library = tableOf();
        library.set("execute", new execute(machine));
        library.set("execute_args", new execute_args(machine));
        env.set("ex", library);
        return library;
    }

    public static class execute extends OneArgFunction {
        Machine machine;

        public execute(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue path) {
            try {
                String p = path.tojstring();
                machine.filesystem.executeScript(p);
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class execute_args extends TwoArgFunction {
        Machine machine;

        public execute_args(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue path, LuaValue args) {
            try {
                String p = path.tojstring();
                machine.filesystem.getFile(p).trueFile.execute(machine, args);
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }
}
