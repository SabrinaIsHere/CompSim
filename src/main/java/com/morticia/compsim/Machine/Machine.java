package com.morticia.compsim.Machine;

import com.morticia.compsim.Machine.Filesystem.Filesystem;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Disk.DiskUtil;

public class Machine {
    public int id;
    public String desig; // Stands for designation

    public Filesystem filesystem;

    public DiskFile metaFile;
    public DataHandler dataHandler;

    public Machine(String desig) {
        this.id = MachineHandler.assignId();
        this.desig = id + "_" + desig;

        if (!DiskUtil.populateMachine(this.desig)) {
            printError("machine population failed");
        }

        this.filesystem = new Filesystem(this);

        // Keep at the end
        this.metaFile = new DiskFile(getMachineDir(), "meta.dt", true);
        this.dataHandler = new DataHandler(metaFile);
        if (!dataHandler.load()) {
            save();
        }

        // Execute boot script
        filesystem.executeScript("/boot/boot.lua");
    }

    public void tick() {

    }

    public void save() {
        dataHandler.add(desig, Constants.str_type, "machine_desig");
        dataHandler.save();
    }

    public String getMachineDir() {
        return "/Machines/" + desig;
    }

    private void printError(String message) {
        System.out.println("[" + id + "/" + desig + "]: " + message);
    }
}
