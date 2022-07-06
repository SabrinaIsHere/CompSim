package com.morticia.compsim.Machine;

import com.morticia.compsim.Machine.Device.StaticDevice;
import com.morticia.compsim.Machine.Event.EventHandler;
import com.morticia.compsim.Machine.Filesystem.Filesystem;
import com.morticia.compsim.Machine.GUI.GUIHandler;
import com.morticia.compsim.Machine.User.UserHandler;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.Log.LogHandler;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    public int id;
    public String desig; // Stands for designation

    public Filesystem filesystem;

    public DiskFile metaFile;
    public DataHandler dataHandler;

    public EventHandler eventHandler;
    public LogHandler logHandler;
    public UserHandler userHandler;

    public GUIHandler guiHandler;

    public List<StaticDevice> staticDevices;

    public Machine(String desig) {
        this.id = MachineHandler.assignId();
        this.desig = id + "_" + desig;

        if (!DiskUtil.populateMachine(this.desig)) {
            printError("machine population failed");
        }

        this.logHandler = new LogHandler(this);
        this.logHandler.log("Machine booted");

        this.filesystem = new Filesystem(this);

        // Keep at the end
        this.metaFile = new DiskFile(getMachineDir(), "meta.dt", true);
        this.dataHandler = new DataHandler(metaFile);
        if (!dataHandler.load()) {
            save();
        }

        // TODO: 7/4/22 Load events from metafile
        this.eventHandler = new EventHandler(this);

        this.userHandler = new UserHandler(this);

        // TODO: 7/4/22 Change this when these events are registered via metafile
        this.guiHandler = new GUIHandler(this);
        // TODO: 7/5/22 Make it possible to register for events from lua, maybe not loaded from metafile?
        /*this.guiHandler.registerKeyEvents();
        this.guiHandler.startTerminal();*/ // this is commented out so debugging is easier

        // TODO: 7/3/22 Make this initialize from metafile (current setup is for debugging)
        staticDevices = new ArrayList<>();
        staticDevices.add(new StaticDevice("debug", this));

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

    @Override
    public String toString() {
        return this.desig;
    }
}
