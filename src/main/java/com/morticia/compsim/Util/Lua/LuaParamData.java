package com.morticia.compsim.Util.Lua;

import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.ArrayList;
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
    private final __index indexFunc;

    /**
     * Constructor
     *
     * @param data Data to include with execution
     * @param stdList Interprets as a keyed table if this isn't set
     */
    public LuaParamData(List<String> data, boolean stdList) {
        this.stdList = stdList;
        this.data = data;
        this.indexFunc = new __index();
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
        this.indexFunc = new __index();
        this.table = new LuaTable();
    }

    public LuaParamData addTable(String tableName, LuaTable table) {
        this.table.set(tableName, table);
        return this;
    }

    public LuaParamData assimilateTable(LuaTable new_table) {
        // Technically this is just multiple table inheritance but eh
        indexFunc.tables.add(new_table);
        return this;
    }

    /**
     * Makes a table from the data in this object
     *
     * @return Table representing given data
     */
    public LuaTable toLuaTable() {
        LuaTable meta = new LuaTable();
        meta.set("__index", indexFunc);
        LuaTable table = new LuaTable();
        table.setmetatable(meta);

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
                    String val = str_1[1].strip();
                    try {
                        table.set(str_1[0].strip(), Integer.parseInt(val));
                    } catch (Exception e) {
                        table.set(str_1[0].strip(), val);
                    }
                }
            }
        }
        return table;
    }

    public static class __index extends TwoArgFunction {
        List<LuaTable> tables;

        public __index() {
            this.tables = new ArrayList<>();
        }

        @Override
        public LuaValue call(LuaValue das, LuaValue key) {
            for (LuaTable i : tables) {
                if (!i.get(key).isnil()) {
                    return i.get(key);
                }
            }
            return LuaNil.NIL;
        }
    }
}
