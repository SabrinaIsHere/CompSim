package com.morticia.compsim.IO;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.Machine.Event.Event;
import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.UI.GUI.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IOHandler extends Thread {
    public List<Terminal> terminals;
    public CopyOnWriteArrayList<Event> events;

    // TODO: 7/5/22 Think about ways to have several terminals at once, several mainframes?
    public MainFrame mainFrame;

    public IOHandler() {
        super("IOHandler");
        this.terminals = new ArrayList<>();
        this.events = new CopyOnWriteArrayList<>();
        this.mainFrame = new MainFrame();
    }

    @Override
    public void run() {
        try {
            System.out.println("IOThread (" + Thread.currentThread().getId() + ") started");
            DiskUtil.init();
            //UI.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (!Thread.interrupted()) {
            for (Event i : events) {
                if (i.eventName.equals("end")) {
                    this.interrupt();
                    return;
                } else if (i.eventName.equals("start_terminal")) {
                    Terminal terminal = new Terminal(i.machine, Integer.parseInt(i.eventType));
                    mainFrame.show(terminal);
                    i.machine.guiHandler.terminals.add(terminal);
                    i.machine.guiHandler.p_terminal = terminal;
                    i.machine.guiHandler.qeue.add(terminal);
                    terminals.add(terminal);
                    i.machine.logHandler.log("[" + terminal.id + "]: Terminal Initiated");
                    events.remove(i);
                } else if (i.eventName.equals("end_terminal")) {
                    terminals.removeIf(j -> j.machine.desig.equals(i.eventType));
                    events.remove(i);
                }
            }
        }
    }
}
