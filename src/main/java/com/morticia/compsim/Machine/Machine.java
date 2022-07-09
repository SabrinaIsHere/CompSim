package com.morticia.compsim.Machine;

import com.morticia.compsim.Machine.Device.StaticDevice;
import com.morticia.compsim.Machine.Event.EventHandler;
import com.morticia.compsim.Machine.Filesystem.Filesystem;
import com.morticia.compsim.Machine.GUI.GUIHandler;
import com.morticia.compsim.Machine.User.UserHandler;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataComponent;
import com.morticia.compsim.Util.Disk.DataHandler.DataHandler;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Disk.DiskUtil;
import com.morticia.compsim.Util.Log.LogHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Object centralizing the factors needed for emulation
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

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

    /**
     * Constructor
     *
     * @param desig Designation of this machine, determines name of folder data is stored in
     */
    public Machine(String desig) {
        this.id = MachineHandler.assignId();
        this.desig = id + "_" + desig;

        if (!DiskUtil.populateMachine(this.desig)) {
            printError("machine population failed");
        }

        this.logHandler = new LogHandler(this);
        this.logHandler.log("Machine booted");

        this.userHandler = new UserHandler(this);

        this.filesystem = new Filesystem(this);

        // Keep at the end
        this.metaFile = new DiskFile(getMachineDir(), "meta.dt", true);
        this.dataHandler = new DataHandler(this, metaFile);
        if (!dataHandler.load()) {
            save();
        }

        // TODO: 7/4/22 Load events from metafile
        this.eventHandler = new EventHandler(this);

        // TODO: 7/4/22 Change this when these events are registered via metafile
        this.guiHandler = new GUIHandler(this);
        // TODO: 7/5/22 Make it possible to register for events from lua, maybe not loaded from metafile?
        this.guiHandler.registerKeyEvents();
        //this.guiHandler.startTerminal(); // this is commented out so debugging is easier

        // TODO: 7/3/22 Make this initialize from metafile (current setup is for debugging)
        staticDevices = new ArrayList<>();
        staticDevices.add(new StaticDevice("debug", this));

        // Execute boot script
        try {
            // Gives terminal time to initialize
            Thread.sleep(300);
        } catch (Exception ignored) {}

        filesystem.getFile("boot/boot.lua").trueFile.execPerms.setLibAccess(new String[] {"all"});
        filesystem.executeScript("/boot/boot.lua");

        //System.out.println(filesystem.getFile("/boot/boot.lua").serialize());
    }

    /**
     * Handles all operations that need to happen on a continuous basis
     */
    public void tick() {
        guiHandler.update();
    }

    /**
     * Saves all metadata needed
     */
    public void save() {
        dataHandler.add(new DataComponent(desig, Constants.str_type, "machine_desig"));
        dataHandler.add(userHandler);
        userHandler.saveUsers();
        filesystem.saveAll();
        dataHandler.save();
    }

    /**
     * Gets the path to this machine relative to the Gamedata folder
     *
     * @return Path to this machine's folder
     */
    public String getMachineDir() {
        return "/Machines/" + desig;
    }

    private void printError(String message) {
        System.out.println("[" + id + "/" + desig + "]: " + message);
    }

    @Override
    public String toString() {
        // TODO: 7/6/22 Function to get all data in this machine and return as string 
        return this.desig;
    }
}
