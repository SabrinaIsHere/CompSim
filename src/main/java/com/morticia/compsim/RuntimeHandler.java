package com.morticia.compsim;

import com.morticia.compsim.IO.IOHandler;
import com.morticia.compsim.Machine.MachineHandler;
import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.Lua.LuaLib;

public class RuntimeHandler {
    public static IOHandler ioHandler;
    public static MachineHandler machineHandler;

    /**
     * Function called to start default routine, but the point is to be a library not an application
     */
    public static void entryPoint() {
        DiskUtil.init();
        LuaLib.initServerGlobals();

        ioHandler = new IOHandler();
        machineHandler = new MachineHandler();

        ioHandler.start();
        machineHandler.start();
    }

    public static void stop() {
        machineHandler.saveMachines();

        ioHandler.interrupt();
        machineHandler.interrupt();
    }
}
