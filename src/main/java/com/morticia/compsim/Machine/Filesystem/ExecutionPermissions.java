package com.morticia.compsim.Machine.Filesystem;

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
        StringBuilder sb = new StringBuilder();
        sb.append("Can Execute: ").append(canExecute);
        sb.append("  Sees Kernel Tables: ").append(kernelTableAccess);
        sb.append("  Library Access: {");
        for (int i = 0; i < libAccess.size(); i++) {
            String str = libAccess.get(i);
            sb.append(str);
            if (!(i + 1 < libAccess.size())) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
