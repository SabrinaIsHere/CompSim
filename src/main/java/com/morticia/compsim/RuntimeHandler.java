package com.morticia.compsim;

import com.morticia.compsim.IO.IOHandler;
import com.morticia.compsim.Machine.MachineHandler;
import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.Lua.LuaLib;

public class RuntimeHandler {
    /**
     * Function called to start default routine, but the point is to be a library not an application
     */
    public static void entryPoint() {
        DiskUtil.init();
        LuaLib.initServerGlobals();

        IOHandler ioHandler = new IOHandler();
        MachineHandler machineHandler = new MachineHandler();

        machineHandler.initDefaultMachines();

        ioHandler.start();
        machineHandler.start();
    }
}
