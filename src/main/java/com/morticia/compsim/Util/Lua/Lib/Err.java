package com.morticia.compsim.Util.Lua.Lib;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * This handles any errors custom lua functions will have
 *
 * @author Morticia
 * @version 1.0
 * @since 7/10/22
 */

public class Err extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        env.set("err", library);
        return library;
    }

    public static LuaTable getErrorTable(String message) {
        LuaTable t = new LuaTable();
        t.set("object_type", "error");
        t.set("error", LuaValue.valueOf(true));
        t.set("message", message);
        return t;
    }

    public static LuaTable getBErrorTable() {
        LuaTable t = new LuaTable();
        t.set("object_type", "error");
        t.set("error", LuaValue.valueOf(false));
        t.set("message", "no error");
        return t;
    }
}
