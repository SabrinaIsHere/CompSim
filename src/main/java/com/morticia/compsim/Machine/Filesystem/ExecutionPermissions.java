package com.morticia.compsim.Machine.Filesystem;

import com.morticia.compsim.Util.Disk.DataHandler.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data container providing utilities to determine what scripts can and can't do
 *
 * @author Morticia
 * @version 1.0
 * @since 6/30/22
 */

public class ExecutionPermissions {
    public boolean canExecute;
    public boolean kernelTableAccess;

    public List<String> libAccess;

    /**
     * Constructor
     */
    public ExecutionPermissions() {
        this.libAccess = new ArrayList<>();
    }

    /**
     * Clears libAccess variable and sets it to the given values
     *
     * @param newAccess New values to set libAccess to
     */
    public void setLibAccess(String[] newAccess) {
        libAccess.clear();
        libAccess.addAll(List.of(newAccess));
    }

    /**
     * Whether or not this object is identical to the provided one
     *
     * @param obj Obj to compare to
     * @return Whether or not object is identical
     */
    @Override
    public boolean equals(Object obj) {
        try {
            ExecutionPermissions var = (ExecutionPermissions) obj;
            return var.canExecute == canExecute && var.kernelTableAccess == kernelTableAccess && var.libAccess.equals(libAccess);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Makes this into a string
     *
     * @return This object's data as a string
     */
    @Override
    public String toString() {
        return "Can Execute: " + canExecute +
                " |  Sees Kernel Tables: " + kernelTableAccess +
                " |  Library Access: " + libAccess;
    }

    public void fromString(String val) {
        String[] str = val.split("\\|");
        for (String i : str) {
            i = i.strip();
            if (i.startsWith("Can Execute: ")) {
                this.canExecute = Boolean.parseBoolean(i.replaceFirst("Can Execute: ", ""));
            } else if (i.startsWith("Sees Kernel Tables: ")) {
                this.canExecute = Boolean.parseBoolean(i.replaceFirst("Sees Kernel Tables: ", ""));
            } else if (i.startsWith("Library Access: ")) {
                String[] str_1 = Serializable.getListMembers(i.replaceFirst("Library Access: ", ""));
                setLibAccess(str_1);
            }
        }
    }
}
