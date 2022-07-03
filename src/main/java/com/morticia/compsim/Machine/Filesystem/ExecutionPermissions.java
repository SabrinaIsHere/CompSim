package com.morticia.compsim.Machine.Filesystem;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPermissions {
    public boolean canExecute;
    public boolean kernelTableAccess;

    public List<String> libAccess;

    public ExecutionPermissions() {
        this.libAccess = new ArrayList<>();
    }

    public void setLibAccess(String[] newAccess) {
        libAccess.clear();
        libAccess.addAll(List.of(newAccess));
    }
}
