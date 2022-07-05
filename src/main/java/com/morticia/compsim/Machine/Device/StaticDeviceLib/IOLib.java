package com.morticia.compsim.Machine.Device.StaticDeviceLib;

import com.morticia.compsim.Machine.Machine;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class IOLib extends TwoArgFunction {
    public Machine machine;

    public IOLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("print", new print(machine));
        env.set("lib", library);
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
}
