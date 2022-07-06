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

    @Override
    public boolean equals(Object obj) {
        try {
            ExecutionPermissions var = (ExecutionPermissions) obj;
            return var.canExecute == canExecute && var.kernelTableAccess == kernelTableAccess && var.libAccess.equals(libAccess);
        } catch (Exception e) {
            return false;
        }
    }

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
