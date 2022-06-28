package com.morticia.compsim.Machine;

import java.util.ArrayList;
import java.util.List;

public class MachineHandler extends Thread {
    List<Machine> machines;

    public MachineHandler() {
        super("MachineHandler");
        machines = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            System.out.println("MachineThread (" + Thread.currentThread().getId() + ") started");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            for (Machine i : machines) {
                i.tick();
            }
        }
    }

    public void initDefaultMachines() {
        machines.add(new Machine(0, "machine_1"));
    }
}
