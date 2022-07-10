package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.Machine.Machine;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class TerminalLib extends TwoArgFunction {
    public Machine machine;

    public TerminalLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("new_terminal", new new_terminal(machine));
        library.set("get_terminal", new get_terminal(machine));
        library.set("get_curr_terminal", new get_curr_terminal(machine));
        library.set("set_color", new set_color());
        library.set("direct", new direct(machine));
        env.set("terminal", library);
        return library;
    }

    public static LuaTable getBlankTerminalTable(Machine machine, int id) {
        LuaTable retVal = new LuaTable();
        retVal.set("is_null", LuaValue.valueOf(true));
        retVal.set("id", id);
        retVal.set("update", new update(machine, id));
        retVal.set("is_ready", new TerminalLib.is_ready(machine, id));
        retVal.set("get_prefix", new TerminalLib.get_prefix(null));
        retVal.set("set_prefix", new TerminalLib.set_prefix(null));
        retVal.set("get_buffer", new get_buffer(null));
        retVal.set("set_buffer", new set_buffer(null));
        return retVal;
    }

    public static class update extends ZeroArgFunction {
        Machine machine;
        int id;

        public update(Machine machine, int id) {
            this.machine = machine;
            this.id = id;
        }

        @Override
        public LuaValue call() {
            try {
                return machine.guiHandler.getTerminal(id).toTable();
            } catch (Exception e) {
                return getBlankTerminalTable(machine, id);
            }
        }
    }

    public static class new_terminal extends ZeroArgFunction {
        Machine machine;

        public new_terminal(Machine machine) {
            this.machine = machine;
        }


        @Override
        public LuaValue call() {
            machine.guiHandler.startTerminal();
            // Yes this is kind of a mess but like I don't really care lmao
            while (machine.guiHandler.qeue.size() < 1) {

            }
            // Problem is that while the correct object is eventually created the variables are not updated
            Terminal t = machine.guiHandler.qeue.get(0);
            machine.guiHandler.qeue.remove(0);
            LuaTable table;
            if (t == null) {
                table = getBlankTerminalTable(machine, machine.guiHandler.terminals.size());
            } else {
                table = t.toTable();
            }
            return table;
        }
    }

    public static class get_terminal extends OneArgFunction {
        Machine machine;

        public get_terminal(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue id) {
            Terminal terminal = machine.guiHandler.getTerminal(id.toint());
            if (terminal == null) {
                return getBlankTerminalTable(machine, id.toint());
            } else {
                return terminal.toTable();
            }
        }
    }

    public static class get_curr_terminal extends ZeroArgFunction {
        Machine machine;

        public get_curr_terminal(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            if (machine.guiHandler.p_terminal == null) {
                return getBlankTerminalTable(machine, machine.guiHandler.terminals.size());
            } else {
                return machine.guiHandler.p_terminal.toTable();
            }
        }
    }

    public static class set_color extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue text, LuaValue color) {
            try {
                return LuaValue.valueOf(Terminal.wrapInColor(text.tojstring(), color.tojstring()));
            } catch (Exception e) {
                return LuaNil.NIL;
            }
        }
    }

    public static class direct extends OneArgFunction {
        Machine machine;

        public direct(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue t) {
            try {
                Terminal terminal = machine.guiHandler.getTerminal(t.toint());
                if (terminal == null) {
                    return Err.getErrorTable("terminal invalid");
                }
                terminal.machine.guiHandler.p_terminal = terminal;
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    // Terminal object functions
    public static class is_ready extends ZeroArgFunction {
        Machine machine;
        int id;

        Terminal terminal;

        public is_ready(Machine machine, int id) {
            this.machine = machine;
            this.id = id;

            this.terminal = machine.guiHandler.getTerminal(id);
        }

        @Override
        public LuaValue call() {
            if (terminal == null) {
                this.terminal = machine.guiHandler.getTerminal(id);
                if (terminal == null) {
                    return LuaValue.valueOf(false);
                } else {
                    return LuaValue.valueOf(terminal.ready);
                }
            } else {
                return LuaValue.valueOf(terminal.ready);
            }
        }
    }

    public static class get_prefix extends ZeroArgFunction {
        Terminal terminal;

        public get_prefix(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call() {
            try {
                return LuaValue.valueOf(terminal.getPrefix());
            } catch (Exception e) {
                return LuaNil.NIL;
            }
        }
    }

    public static class set_prefix extends OneArgFunction {
        Terminal terminal;

        public set_prefix(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call(LuaValue prefix) {
            try {
                terminal.setPrefix(prefix.tojstring());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class get_buffer extends ZeroArgFunction {
        Terminal terminal;

        public get_buffer(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call() {
            try {
                return LuaValue.valueOf(terminal.inputField.getText());
            } catch (Exception e) {
                return LuaValue.valueOf("");
            }
        }
    }

    public static class set_buffer extends OneArgFunction {
        Terminal terminal;

        public set_buffer(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call(LuaValue text) {
            try {
                terminal.inputField.setText(text.checkjstring());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }
}
