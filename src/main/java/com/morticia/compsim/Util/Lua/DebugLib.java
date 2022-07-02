package com.morticia.compsim.Util.Lua;

import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

// TODO: 7/2/22 Remove this it's just for debugging
public class DebugLib extends TwoArgFunction {
    public DebugLib() {
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = new LuaTable();
        library.set("print", new l_print());
        env.set("compsim", library);
        return library;
    }

    static class l_print extends OneArgFunction {
        public l_print() {}

        @Override
        public LuaValue call(LuaValue luaValue) {
            System.out.println(luaValue.tojstring());
            return LuaNil.NIL;
        }
    }
}
