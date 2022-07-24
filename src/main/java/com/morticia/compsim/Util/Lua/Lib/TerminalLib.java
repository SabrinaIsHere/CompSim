package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineIOStream.IOComponent;
import com.morticia.compsim.Machine.MachineIOStream.MachineIOStream;
import com.morticia.compsim.Util.Lua.LuaParamData;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
        library.set("parse", new parse());
        env.set("terminal", library);
        return library;
    }

    public static LuaTable getBlankTerminalTable(Machine machine, int id) {
        LuaTable retVal = new LuaTable();
        retVal.set("is_null", LuaValue.valueOf(true));
        retVal.set("id", id);
        retVal.set("object_type", "terminal");
        retVal.set("update", new update(machine, id));
        retVal.set("is_ready", new TerminalLib.is_ready(machine, id));
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
                    return Err.getErrorTable("terminal invalid", terminal.getStream());
                }
                terminal.machine.defaultStream = terminal.getStream();
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage(), machine.defaultStream);
            }
        }
    }

    public static class parse extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs text) {
            LuaValue command;
            LuaTable args = new LuaTable();
            LuaTable flags = new LuaTable();

            String str = text.arg1().checkjstring().strip();

            List<String> str_1 = new ArrayList<>(List.of(str.split(" ")));
            command = LuaValue.valueOf(str_1.get(0));
            str_1.remove(0);
            for (String i : str_1) {
                i = i.strip();
                if (i.startsWith("-")) {
                    flags.set(flags.length() + 1, i);
                } else {
                    args.set(args.length() + 1, i);
                }
            }

            return varargsOf(new LuaValue[]{
                    command,
                    args,
                    flags
            });
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
                return Err.getErrorTable(e.getMessage(), terminal.machine.defaultStream);
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
                return Err.getErrorTable(e.getMessage(), terminal.machine.defaultStream);
            }
        }
    }

    public static class print extends OneArgFunction {
        public MachineIOStream stream;

        public print(MachineIOStream stream) {
            this.stream = stream;
        }

        @Override
        public LuaValue call(LuaValue out) {
            try {
                stream.write(out.toString() + "\n");
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage(), stream);
            }
        }
    }

    public static class write extends OneArgFunction {
        public MachineIOStream stream;

        public write(MachineIOStream stream) {
            this.stream = stream;
        }

        @Override
        public LuaValue call(LuaValue out) {
            try {
                stream.write(out.toString());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage(), stream);
            }
        }
    }

    public static class get_title extends ZeroArgFunction {
        Terminal terminal;

        public get_title(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(terminal.getTitle());
        }
    }

    public static class set_title extends OneArgFunction {
        Terminal terminal;

        public set_title(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call(LuaValue new_title) {
            terminal.setTitle(new_title.tojstring());
            return LuaNil.NIL;
        }
    }

    public static class set_output extends ZeroArgFunction {
        Terminal terminal;

        public set_output(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call() {
            terminal.machine.defaultStream.component = terminal;
            return LuaValue.NIL;
        }
    }

    public static class get_text extends ZeroArgFunction {
        Terminal terminal;

        public get_text(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(((JLabel) terminal.centerPanel.getComponent(terminal.cmpn)).getText());
        }
    }

    public static class set_text extends OneArgFunction {
        Terminal terminal;

        public set_text(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call(LuaValue txt) {
            try {
                ((JLabel) terminal.centerPanel.getComponent(terminal.cmpn)).setText(txt.checkjstring());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage(), terminal.getStream());
            }
        }
    }

    public static class get_line extends OneArgFunction {
        Terminal terminal;

        public get_line(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public LuaValue call(LuaValue in) {
            if (in.isstring()) {
                return LuaValue.valueOf(terminal.nextLine(in.checkjstring()));
            } else {
                return LuaValue.valueOf(terminal.nextLine());
            }
        }
    }
}
