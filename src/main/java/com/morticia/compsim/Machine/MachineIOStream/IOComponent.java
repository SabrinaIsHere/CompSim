package com.morticia.compsim.Machine.MachineIOStream;

import org.luaj.vm2.LuaTable;

/**
 * This interface integrates a class into the MachineIOStream class
 *
 * @author Morticia
 * @version 1.0
 * @since 7/13/22
 */

public interface IOComponent {
    String readLine();
    void writeLine(String data);
    LuaTable toTable();
}
