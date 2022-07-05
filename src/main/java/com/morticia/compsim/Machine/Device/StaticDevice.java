package com.morticia.compsim.Machine.Device;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Disk.DiskUtil;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StaticDevice {
    public Machine machine;
    public String type;

    public Path path;

    public StaticDevice(String type, Machine machine) {
        this.machine = machine;
        this.type = type;

        this.path = Path.of(machine.getMachineDir() + "/Devices/" + type);

        DiskUtil.populateStaticDevice(this);

        boot();
    }

    public void boot() {
        DiskFile bootFile = new DiskFile(path.toString() + "/boot/", "boot.lua", false);
        bootFile.execPerms.setLibAccess(new String[]{"all"});
        bootFile.execute(machine);
    }

    public List<String> getFunctions() {
        return new ArrayList<>();
    }

    public LuaValue getFunction() {
        // Get function from interface folder
        return LuaNil.NIL;
    }
}
