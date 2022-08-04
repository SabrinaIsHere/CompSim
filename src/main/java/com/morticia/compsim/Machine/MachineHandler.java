package com.morticia.compsim.Machine;

import com.morticia.compsim.Machine.Networking.Network;
import com.morticia.compsim.Util.Disk.DataHandler.DataHandler;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Disk.DiskUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MachineHandler extends Thread {
    public static int numMachines = 0;

    public CopyOnWriteArrayList<Machine> machines;

    public DataHandler dataHandler;

    public MachineHandler() {
        super("MachineHandler");
        machines = new CopyOnWriteArrayList<>();
        dataHandler = new DataHandler(new DiskFile("/Metadata/", "networks.cfg", true));
    }

    @Override
    public void run() {
        dataHandler.load();

        for (Network i : Network.allNetworks) {
            for (Integer j : i.potential_networks) {
                Network net = Network.getNetwork(j);
                if (net != null) {
                    i.networks.add(net);
                }
            }
            i.potential_networks = null;
        }

        for (File i : DiskUtil.getFolderChildren("/Machines")) {
            machines.add(new Machine(i.getName()));
        }

        try {
            System.out.println("MachineThread (" + Thread.currentThread().getId() + ") started");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (!Thread.interrupted()) {
            for (Machine i : machines) {
                i.tick();
            }
        }

        saveNetworks();
        dataHandler.save();
    }

    public void initDefaultMachines() {
        machines.add(new Machine("test_machine"));
    }

    public void saveMachines() {
        for (Machine i : machines) {
            i.save();
        }
    }

    public void saveNetworks() {
        for (Network i : Network.allNetworks) {
            dataHandler.add(i);
        }
    }

    public void updateFilesystems() {
        for (Machine i : machines) {
            i.filesystem.root.update();
        }
    }

    public static int assignId() {
        return ++numMachines;
    }
}
