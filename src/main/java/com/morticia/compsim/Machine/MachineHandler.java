package com.morticia.compsim.Machine;

import com.morticia.compsim.Util.Disk.DiskUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MachineHandler extends Thread {
    public static int numMachines = 0;

    public CopyOnWriteArrayList<Machine> machines;

    public MachineHandler() {
        super("MachineHandler");
        machines = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        //initDefaultMachines();

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
    }

    public void initDefaultMachines() {
        machines.add(new Machine("test_machine"));
    }

    public void saveMachines() {
        for (Machine i : machines) {
            i.save();
        }
    }

    public static int assignId() {
        return ++numMachines;
    }
}
