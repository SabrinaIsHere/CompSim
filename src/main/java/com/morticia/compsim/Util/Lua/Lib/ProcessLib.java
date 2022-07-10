package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.Process.MachineProcess;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ProcessLib extends TwoArgFunction {
    Machine machine;

    public ProcessLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("get_processes", new get_processes(machine));
        library.set("get_process", new get_process(machine));
        env.set("process", library);
        return library;
    }

    public static class get_processes extends ZeroArgFunction {
        Machine machine;

        public get_processes(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (MachineProcess i : machine.processHandler.processes) {
                table.add(i.toTable());
            }
            return table;
        }
    }

    public static class get_process extends OneArgFunction {
        Machine machine;

        public get_process(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue id) {
            for (MachineProcess i : machine.processHandler.processes) {
                if (i.id == id.toint()) {
                    return i.toTable();
                }
            }
            return MachineProcess.getBlankTable(id.toint());
        }
    }

    public static class add_globals extends TwoArgFunction {
        MachineProcess process;

        public add_globals(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call(LuaValue name, LuaValue value) {
            try {
                process.globals.set(name, value);
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class reset_globals extends ZeroArgFunction {
        MachineProcess process;

        public reset_globals(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call() {
            try {
                process.updateGlobals();
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class reset_globals_when_complete extends OneArgFunction {
        MachineProcess process;

        public reset_globals_when_complete(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            try {
                process.resetGlobalsWhenComplete = luaValue.toboolean();
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class pass_globals extends OneArgFunction {
        MachineProcess process;

        public pass_globals(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            try {
                process.passGlobalsToFork = luaValue.toboolean();
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class fork extends OneArgFunction {
        MachineProcess process;

        public fork(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call(LuaValue name) {
            return process.handler.addProcess(name.tojstring()).toTable();
        }
    }

    public static class set_file extends OneArgFunction {
        MachineProcess process;

        public set_file(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call(LuaValue path) {
            try {
                process.rootFile = process.handler.machine.filesystem.getFile(path.tojstring());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class start extends ZeroArgFunction {
        MachineProcess process;

        public start(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call() {
            try {
                process.start();
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class run extends OneArgFunction {
        MachineProcess process;

        public run(MachineProcess process) {
            this.process = process;
        }

        @Override
        public LuaValue call(LuaValue path) {
            try {
                process.execFile(path.tojstring());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }
}
