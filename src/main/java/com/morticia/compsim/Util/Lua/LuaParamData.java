package com.morticia.compsim.Util.Lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Arrays;
import java.util.List;

/**
 * Class to simplify passing data to lua scripts
 *
 * @author Morticia
 * @version 1.0
 * @since 7/4/22
 */

public class LuaParamData {
    public boolean stdList;

    public List<String> data;
    public LuaTable table;

    /**
     * Constructor
     *
     * @param data Data to include with execution
     * @param stdList Interprets as a keyed table if this isn't set
     */
    public LuaParamData(List<String> data, boolean stdList) {
        this.stdList = stdList;
        this.data = data;
        this.table = toLuaTable();
    }

    /**
     * Constructor
     *
     * @param data Data to include with execution
     * @param stdList Interprets as a keyed table if this isn't set
     */
    public LuaParamData(String[] data, boolean stdList) {
        this.stdList = stdList;
        this.data = Arrays.asList(data);
        this.table = toLuaTable();
    }

    public LuaParamData addTable(String tableName, LuaTable table) {
        this.table.set(tableName, table);
        return this;
    }

    /**
     * Makes a table from the data in this object
     *
     * @return Table representing given data
     */
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
