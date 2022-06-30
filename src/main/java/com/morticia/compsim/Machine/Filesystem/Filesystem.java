package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Machine.Machine;

/**
 * This could all be handled in the machine, technically, but I thought separating it would help with clutter
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class Filesystem {
    public Machine machine;

    public VirtualFolder root;

    public Filesystem(Machine machine) {
        this.machine = machine;

        // Initializes data from disk recursively
        this.root = new VirtualFolder(this);
    }

    public String getDiskDir() {
        return machine.getMachineDir() + "/Disk";
    }
}
