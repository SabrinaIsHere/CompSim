package com.morticia.compsim.Machine.Device.StaticDeviceLib;

import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class IOLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("print", new print());
        env.set("lib", library);
        return library;
    }

    public static class print extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue val) {
            if (val.isstring()) {
                System.out.println(val.tojstring());
            }
            return LuaNil.NIL;
        }
    }
}
