package com.morticia.compsim.Machine.Process;

import com.morticia.compsim.Machine.Filesystem.VirtualFile;
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
    public VirtualFile stdEntry;

    /**
     * Constructor
     *
     * @param machine Machine this handler is handling processes for
     */
    public ProcessHandler(Machine machine) {
        this.machine = machine;
        this.processes = new ArrayList<>();
        this.stdEntry = machine.filesystem.getFile("/root/process/std_entry.lua");
    }

    /**
     * Assigns an id for a process
     *
     * @return The id assigned
     */
    public int assignId() {
        return processes.size();
    }

    /**
     * Adds a process to this handler
     *
     * @param pName Name of the process
     * @return The process added
     */
    public MachineProcess addProcess(String pName) {
        for (MachineProcess i : processes) {
            if (i.processName.equals(pName)) {
                return null;
            }
        }
        MachineProcess p = new MachineProcess(this, pName, stdEntry.getPath());
        processes.add(p);
        return p;
    }
}
