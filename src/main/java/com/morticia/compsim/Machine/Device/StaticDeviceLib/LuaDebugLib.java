package com.morticia.compsim.Machine.Device.StaticDeviceLib;

import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaDebugLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("print", new l_print());
        env.set("debug", library);
        return library;
    }

    public static class l_print extends OneArgFunction {
        public l_print() {}

        @Override
        public LuaValue call(LuaValue luaValue) {
            if (luaValue.isstring()) {
                System.out.println(luaValue.tojstring());
            }
            return LuaNil.NIL;
        }
    }
}
