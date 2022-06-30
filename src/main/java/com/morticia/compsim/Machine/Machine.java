package com.morticia.compsim.Machine;

import com.morticia.compsim.Util.Disk.DiskUtil;

public class Machine {
    public int id;
    public String desig; // Stands for designation

    public Machine(String desig) {
        this.id = MachineHandler.assignId();
        this.desig = id + "_" + desig;

        if (!DiskUtil.populateMachine(this.desig)) {
            printError("machine population failed");
        }
    }

    public void tick() {

    }

    private void printError(String message) {
        System.out.println("[" + id + "/" + desig + "]: " + message);
    }
}
