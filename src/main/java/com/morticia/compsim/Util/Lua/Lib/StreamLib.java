package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineIOStream.IOComponent;
import com.morticia.compsim.Machine.MachineIOStream.TableIOComponent;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class StreamLib extends TwoArgFunction {
    Machine machine;

    public StreamLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue mod_name, LuaValue env) {
        LuaTable library = tableOf();
        library.set("get_output", new get_output(machine));
        library.set("set_output", new set_output(machine));
        env.set("stream", library);
        return library;
    }

    public static class get_output extends ZeroArgFunction {
        Machine machine;

        public get_output(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            return machine.defaultStream.component.toTable();
        }
    }

    public static class set_output extends OneArgFunction {
        Machine machine;

        public set_output(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue table) {
            machine.defaultStream.component = new TableIOComponent(table.checktable());
            return LuaNil.NIL;
        }
    }

    public static class read extends ZeroArgFunction {
        IOComponent component;

        public read(IOComponent component) {
            this.component = component;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(component.readLine());
        }
    }

    public static class write extends OneArgFunction {
        IOComponent component;

        public write(IOComponent component) {
            this.component = component;
        }

        @Override
        public LuaValue call(LuaValue data) {
            component.writeLine(data.checkjstring());
            return LuaNil.NIL;
        }
    }

    public static class get_data extends ZeroArgFunction {
        IOComponent component;

        public get_data(IOComponent component) {
            this.component = component;
        }

        @Override
        public LuaValue call() {
            return component.getAllData();
        }
    }
}
