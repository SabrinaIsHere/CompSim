package com.morticia.compsim.Util.Lua;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.Lua.Tables.ReadOnlyLuaTable;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.OneArgFunction;
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

    public Globals prepUserGlobals() {
        // TODO: 7/2/22 Pass arguments, for terminal + processes made from lua 
        Globals userGlobals = new Globals();

        // Standard globals everyone has
        userGlobals.load(new JseBaseLib());
        userGlobals.load(new PackageLib());
        userGlobals.load(new Bit32Lib());
        userGlobals.load(new TableLib());
        userGlobals.load(new JseStringLib());
        userGlobals.load(new JseMathLib());

        // TODO: 7/2/22 remove this when my IO becomes available
        userGlobals.load(new JseIoLib());

        userGlobals.load(new DebugLib());
        userGlobals.set("print", new DebugLib.l_print());

        // Special globals you need perms for
        for (String i : execPerms.deviceAccess) {
            if (i.equals("")) {
                // TODO: 7/2/22 Device interface stuff, these should come from the ROM folder
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