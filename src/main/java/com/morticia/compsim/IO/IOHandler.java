package com.morticia.compsim.IO;

public class IOHandler extends Thread {
    public IOHandler() {
        super("IOHandler");
    }

    @Override
    public void run() {
        try {
            System.out.println("IOThread (" + Thread.currentThread().getId() + ") started");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.interrupt();
    }
}
