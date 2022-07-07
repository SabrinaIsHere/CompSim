package com.morticia.compsim.Util.Disk.DataHandler;

/**
 * Interface implemented by anything that can be serialized and saved
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public interface Serializable {
    default String getPrefix() {
        return "[d_" + getDesig() + "/t_" + getType() + "]: ";
    }

    String getType();
    String getDesig();
    String serialize();
    void parse(String data);
}
