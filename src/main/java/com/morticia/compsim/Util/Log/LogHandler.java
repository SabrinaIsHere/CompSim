package com.morticia.compsim.Util.Log;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Disk.DiskFile;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Class to handle logging to help debugging and clarity
 *
 * @author Morticia
 * @version 1.0
 * @since 7/4/22
 */

public class LogHandler {
    DiskFile logFile;
    Machine machine;

    /**
     * Constructor
     *
     * @param machine Machine this is attached to
     */
    public LogHandler(Machine machine) {
        this.machine = machine;
        this.logFile = new DiskFile(machine.getMachineDir(), "log.txt", true);
    }

    /**
     * Logs an event to the relevant file
     *
     * @param action Message to be included in log
     */
    public void log(String action) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        logFile.appendLine("[" + ts + "/" + machine.toString() + "]: " + action);
        logFile.writeBuffer();
    }
}
