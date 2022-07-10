package com.morticia.compsim.Machine.Process;

import com.morticia.compsim.Machine.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * These objects make it easier to enumerate and contain the processes on a machine
 *
 * @author Morticia
 * @version 1.0
 * @since 7/10/22
 */

public class ProcessHandler {
    public Machine machine;
    public List<MachineProcess> processes;

    public ProcessHandler(Machine machine) {
        this.machine = machine;
        this.processes = new ArrayList<>();
    }

    public int assignId() {
        return processes.size();
    }
}
