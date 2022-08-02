package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Event.Event;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Lua.LuaParamData;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.ArrayList;
import java.util.List;

public class EventLib extends TwoArgFunction {
    public Machine machine;

    public EventLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue mod_name, LuaValue env) {
        LuaTable library = tableOf();
        library.set("register_event", new register_event(machine));
        library.set("get_events", new get_events(machine));
        env.set("event", library);
        return library;
    }

    public static class register_event extends TwoArgFunction {
        Machine machine;

        public register_event(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue name, LuaValue type) {
            Event event = new Event(machine, name.tojstring(), type.tojstring());
            if (machine.eventHandler.registerEvent(event)) {
                return event.toTable();
            } else {
                return Event.getBlankTable(event.eventName);
            }
        }
    }

    public static class get_events extends ZeroArgFunction {
        Machine machine;

        public get_events(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (Event i : machine.eventHandler.eventList) {
                table.set(table.length() + 1, i.toTable());
            }
            return table;
        }
    }

    public static class trigger extends OneArgFunction {
        Event event;

        public trigger(Event event) {
            this.event = event;
        }

        @Override
        public LuaValue call(LuaValue data) {
            LuaParamData d = new LuaParamData(new ArrayList<>(event.eventData), false);
            d.assimilateTable(data.checktable());
            event.machine.eventHandler.triggerEvent(event.eventName, d);
            return LuaNil.NIL;
        }
    }
}
