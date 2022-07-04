package com.morticia.compsim.Util.Log;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Disk.DiskFile;

import java.sql.Timestamp;
import java.util.Date;

public class LogHandler {
    DiskFile logFile;
    Machine machine;

    public LogHandler(Machine machine) {
        this.machine = machine;
        this.logFile = new DiskFile(machine.getMachineDir(), "log.txt", true);
    }

    public void log(String action) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        logFile.appendLine("[" + ts + "/" + machine.toString() + "]: " + action);
        logFile.writeBuffer();
    }
}
