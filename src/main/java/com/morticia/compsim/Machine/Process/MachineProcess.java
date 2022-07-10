package com.morticia.compsim.Machine.Process;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Lua.LuaLib;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class mostly serves to group files and make sharing data between files easier
 *
 * @author Morticia
 * @version 1.0
 * @since 7/10/22
 */

public class MachineProcess {
    Machine machine;
    ProcessHandler handler;
    int id;
    String processName;
    // 0 = active, 1 = ready, 2 = waiting, 3 = interrupted
    int statusCode;
    String statusMsg;
    boolean continuous;

    VirtualFolder workingDir;
    VirtualFile rootFile;
    VirtualFile currFile;

    ExecutionPermissions execPerms;

    Globals globals;

    Terminal processTerminal;

    public MachineProcess(ProcessHandler handler, String processName, String rootFilePath) {
        this.machine = handler.machine;
        this.handler = handler;
        this.id = handler.assignId();
        this.processName = processName;
        this.statusCode = 2;
        updateStatusMsg();
        // TODO: 7/10/22 Somehow find a way to know if things are background or not
        this.continuous = false;
        this.rootFile = machine.filesystem.getFile(rootFilePath);
        try {
            this.workingDir = rootFile.parent;
        } catch (NullPointerException e) {
            System.out.println("[" + rootFilePath + "] file not found");
        }
        this.currFile = rootFile;
        this.execPerms = new ExecutionPermissions();
        execPerms.canExecute = true;
        execPerms.setLibAccess(new String[] {
                "std",
                "process"
        });
        updateGlobals();
        this.processTerminal = machine.guiHandler.p_terminal;
    }

    public void updateGlobals() {
        LuaLib lib = new LuaLib(execPerms);
        if (processTerminal == null) {
            this.globals = lib.prepUserGlobals(machine);
        } else {
            this.globals = lib.prepUserGlobals(machine, processTerminal);
        }
    }

    public void setStatus(int code) {
        this.statusCode = code;
        updateStatusMsg();
    }

    public void updateStatusMsg() {
        switch (this.statusCode) {
            case 0 -> statusMsg = "active";
            case 1 -> statusMsg = "ready";
            case 2 -> statusMsg = "waiting";
            case 3 -> statusMsg = "interrupted";
        }
    }

    public List<String> getProcessData() {
        List<String> l = new ArrayList<>();
        l.add("status: " + statusMsg);
        return l;
    }

    public void start() {
        setStatus(0);
        execFile(rootFile.getPath());
    }

    public void execFile(String path) {
        VirtualFile f = machine.filesystem.getFile(path);
        if (f == null) {
            System.out.println("[" + path + "] file not found");
            return;
        }

        // Not using the DiskFile#execute so I have more granularity
        if (execPerms.canExecute) {
            // Add data
            // TODO: 7/10/22 Add process table data
            try {
                globals.set("params", toTable());
                globals.loadfile(f.trueFile.path.toString()).call();
            } catch (Exception e) {
                machine.guiHandler.p_terminal.println(Terminal.wrapInColor(e.getLocalizedMessage(), "f7261b"));
                e.printStackTrace();
            }
        }
    }

    public void kill() {
        statusCode = 3;
        updateStatusMsg();
    }

    public LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("process_id", id);
        table.set("process_name", processName);
        table.set("terminal", processTerminal.toTable());
        return table;
    }
}
