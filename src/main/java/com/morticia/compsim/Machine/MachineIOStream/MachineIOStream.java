package com.morticia.compsim.Machine.MachineIOStream;

/**
 * This is a basic IO stream class. It mimics the java implementation of the very basic byte writer
 *
 * @author Morticia
 * @version 1.0
 * @since 7/13/22
 */

public class MachineIOStream {
    IOComponent component;
    String desig;

    public MachineIOStream(String desig, IOComponent component) {
        this.component = component;
        this.desig = desig;
    }

    /**
     * Reads an integer from the IOComponent
     *
     * @return Integer read. -1 if nothing is available
     */
    public String read() {
        return component.readLine();
    }

    /**
     * Writes an integer to the IOComponent
     *
     * @param data Data to write
     */
    public void write(String data) {
        component.writeLine(data);
    }
}
