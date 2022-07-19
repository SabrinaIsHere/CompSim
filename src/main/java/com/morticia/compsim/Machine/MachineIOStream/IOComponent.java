package com.morticia.compsim.Machine.MachineIOStream;

import com.morticia.compsim.Util.Lua.Lib.StreamLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * This interface integrates a class into the MachineIOStream class
 *
 * @author Morticia
 * @version 1.0
 * @since 7/13/22
 */

public interface IOComponent {
    String readLine();
    void writeLine(String data);
    LuaTable getAllData();
    default LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("type", "io_component");
        table.set("read", new StreamLib.read(this));
        table.set("write", new StreamLib.write(this));
        table.set("get_data", new StreamLib.get_data(this));
        return table;
    }
}
