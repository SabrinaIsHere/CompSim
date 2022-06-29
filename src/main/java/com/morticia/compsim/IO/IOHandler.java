package com.morticia.compsim.IO;

import com.morticia.compsim.Util.Disk.DiskUtil;

public class IOHandler extends Thread {
    public IOHandler() {
        super("IOHandler");
    }

    @Override
    public void run() {
        try {
            System.out.println("IOThread (" + Thread.currentThread().getId() + ") started");
            DiskUtil.init();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.interrupt();
    }
}
