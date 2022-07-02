package com.morticia.compsim.Machine.Filesystem;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPermissions {
    public boolean canExecute;
    public boolean kernelTableAccess;
    public boolean IODevice;

    public List<String> deviceAccess;

    public ExecutionPermissions() {
        this.deviceAccess = new ArrayList<>();
        this.IODevice = false;
    }
}
