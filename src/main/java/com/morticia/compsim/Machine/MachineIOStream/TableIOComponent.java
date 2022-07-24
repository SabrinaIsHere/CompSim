package com.morticia.compsim.Machine.MachineIOStream;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class TableIOComponent implements IOComponent {
    LuaTable table;

    public TableIOComponent(LuaTable table) {
        this.table = table;
    }

    @Override
    public String readLine() {
        try {
            return table.get("read").call(table).checkjstring();
        } catch (Exception ignored) {}
        return table.get(1).toString();
    }

    @Override
    public void writeLine(String data) {
        table.set("index", table.length() + 1);
        table.set(table.length() + 1, data);
        try {
            table.get("update").call(table, LuaValue.valueOf(data));
        } catch (Exception ignored) {}
    }

    @Override
    public LuaTable getAllData() {
        return table;
    }
}
