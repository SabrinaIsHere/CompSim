package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Machine;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class IOLib extends TwoArgFunction {
    public Machine machine;

    public IOLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("print", new print(machine));
        library.set("terminalReady", new terminal_ready(machine));
        env.set("io", library);
        return library;
    }

    public static class print extends OneArgFunction {
        Machine machine;

        public print(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue val) {
            if (val.isstring()) {
                machine.guiHandler.p_terminal.println(val.tojstring());
            }
            return LuaNil.NIL;
        }
    }

    // TODO: 7/8/22 Move this into terminal lib when it's made
    public static class terminal_ready extends ZeroArgFunction {
        Machine machine;

        public terminal_ready(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(machine.guiHandler.p_terminal != null);
        }
    }
}
