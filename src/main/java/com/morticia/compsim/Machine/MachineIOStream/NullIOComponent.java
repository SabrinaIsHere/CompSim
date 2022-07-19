package com.morticia.compsim.Machine.MachineIOStream;

import org.luaj.vm2.LuaTable;

public class NullIOComponent implements IOComponent{
    @Override
    public String readLine() {
        return "";
    }

    @Override
    public void writeLine(String data) {
        System.out.println(data);
    }

    @Override
    public LuaTable getAllData() {
        return new LuaTable();
    }

    @Override
    public LuaTable toTable() {
        return new LuaTable();
    }
}
