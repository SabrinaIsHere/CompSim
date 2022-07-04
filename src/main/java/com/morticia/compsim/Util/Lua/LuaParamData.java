package com.morticia.compsim.Util.Lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LuaParamData {
    public boolean stdList;

    List<String> data;

    public LuaParamData(List<String> data, boolean stdList) {
        this.stdList = stdList;
        this.data = data;
    }

    public LuaParamData(String[] data, boolean stdList) {
        this.stdList = stdList;
        this.data = Arrays.asList(data);
    }

    public LuaTable toLuaTable() {
        LuaTable table = new LuaTable();
        table.set("tableType", stdList ? "standard" : "keyed");
        if (stdList) {
            for (String i : data) {
                table.insert(table.length(), LuaValue.valueOf(i));
            }
            return table;
        } else {
            for (String i : data) {
                String[] str_1 = i.split(":");
                if (str_1.length >= 2) {
                    table.set(str_1[0].strip(), str_1[1].strip());
                }
            }
        }
        return table;
    }
}
