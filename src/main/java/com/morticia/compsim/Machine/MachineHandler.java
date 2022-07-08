package com.morticia.compsim.Machine;

import java.util.ArrayList;
import java.util.List;

public class MachineHandler extends Thread {
    public static int numMachines = 0;

    List<Machine> machines;

    public MachineHandler() {
        super("MachineHandler");
        machines = new ArrayList<>();
    }

    @Override
    public void run() {
        initDefaultMachines();

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
