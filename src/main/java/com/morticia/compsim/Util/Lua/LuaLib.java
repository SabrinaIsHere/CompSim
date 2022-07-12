package com.morticia.compsim.Util.Lua;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.Util.Lua.Lib.ExLib;
import com.morticia.compsim.Util.Lua.Lib.IOLib;
import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Lua.Lib.TerminalLib;
import com.morticia.compsim.Util.Lua.Lib.UserLib;
import com.morticia.compsim.Util.Lua.Tables.ReadOnlyLuaTable;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.*;

/**
 * This is an object which other objects executing lua code will use to automatically handle libraries and permissions
 *
 * @author Morticia
 * @version 1.0
 * @since 7/2/22
 */

public class LuaLib {
    public static Globals serverGlobals;

    public ExecutionPermissions execPerms;

    /**
     * Constructor
     *
     * @param execPerms Permissions to use when executing
     */
    public LuaLib(ExecutionPermissions execPerms) {
        this.execPerms = execPerms;
    }

    public static void initServerGlobals() {
        serverGlobals = new Globals();
        serverGlobals.load(new JseBaseLib());
        serverGlobals.load(new PackageLib());
        serverGlobals.load(new JseStringLib());
        serverGlobals.load(new JseMathLib());

        LoadState.install(serverGlobals);
        LuaC.install(serverGlobals);

        LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
    }

    /**
     * Prepares user globals using given exec perms
     *
     * @param machine Machine to execute on
     * @return The globals created
     */
    public Globals prepUserGlobals(Machine machine) {
        // TODO: 7/2/22 Pass arguments, for terminal + processes made from lua 
        Globals userGlobals = new Globals();

        // Standard globals everyone has
        userGlobals.load(new JseBaseLib());
        userGlobals.load(new PackageLib());
        userGlobals.load(new Bit32Lib());
        userGlobals.load(new TableLib());
        userGlobals.load(new JseStringLib());
        userGlobals.load(new JseMathLib());

        userGlobals.set("htmlSpace", "&nbsp;");

        // Special globals you need perms for
        label:
        for (String i : execPerms.libAccess) {
            // TODO: 7/2/22 Device interface stuff
            switch (i) {
                case "all":
                    userGlobals.load(new TerminalLib(machine));
                    userGlobals.set("print", new TerminalLib.print(machine.guiHandler.p_terminal));
                    userGlobals.load(new TerminalLib(machine));
                    userGlobals.load(new IOLib(machine));
                    userGlobals.load(new ExLib(machine));
                    break label;
                case "std":
                    userGlobals.load(new TerminalLib(machine));
                    userGlobals.set("print", new TerminalLib.print(machine.guiHandler.p_terminal));
                    break;
                case "terminal":
                    userGlobals.load(new TerminalLib(machine));
                    break;
                case "io":
                    userGlobals.load(new IOLib(machine));
                    break;
                case "ex":
                    userGlobals.load(new ExLib(machine));
                    break;
                case "usr":
                    userGlobals.load(new UserLib(machine));
                    break;
            }
        }

        if (execPerms.kernelTableAccess) {
            // TODO: 7/2/22 Kernel table
        }

        LoadState.install(userGlobals);
        LuaC.install(userGlobals);

        return userGlobals;
    }

    public Globals prepUserGlobals(Machine machine, Terminal terminal) {
        // TODO: 7/2/22 Pass arguments, for terminal + processes made from lua
        Globals userGlobals = new Globals();

        // Standard globals everyone has
        userGlobals.load(new JseBaseLib());
        userGlobals.load(new PackageLib());
        userGlobals.load(new Bit32Lib());
        userGlobals.load(new TableLib());
        userGlobals.load(new JseStringLib());
        userGlobals.load(new JseMathLib());

        userGlobals.set("htmlSpace", "&nbsp;");

        // Special globals you need perms for
        label:
        for (String i : execPerms.libAccess) {
            // TODO: 7/2/22 Device interface stuff
            switch (i) {
                case "all":
                    userGlobals.load(new IOLib(machine));
                    userGlobals.load(new TerminalLib(machine));
                    userGlobals.set("print", new TerminalLib.print(terminal));
                    break label;
                case "std":
                    userGlobals.load(new TerminalLib(machine));
                    userGlobals.set("print", new TerminalLib.print(terminal));
                    break;
                case "io":
                    userGlobals.load(new IOLib(machine));
                    break;
            }
        }

        if (execPerms.kernelTableAccess) {
            // TODO: 7/2/22 Kernel table
        }

        LoadState.install(userGlobals);
        LuaC.install(userGlobals);

        return userGlobals;
    }
}