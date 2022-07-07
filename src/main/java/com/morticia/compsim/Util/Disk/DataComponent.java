package com.morticia.compsim.Util.Disk;

import com.morticia.compsim.Util.Disk.DataHandler.Serializable;

public class DataComponent implements Serializable {
    public Object data;
    public int index;
    public String type;
    public String desig;

    public DataComponent(Object data, int index, String type, String desig) {
        this.data = data;
        this.index = index;
        this.type = type;
        this.desig = desig;
    }

    // So I can make the object and initialize later
    public DataComponent() {

    }

    @Override
    public String toString() {
        try {
            return getPrefix() + ((Serializable) data).serialize();
        } catch (Exception e) {
            return getPrefix() + data.toString();
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDesig() {
        return desig;
    }

    @Override
    public String serialize() {
        return this.toString();
    }

    @Override
    public void parse(String txt) {
        try {
            Serializable s = (Serializable) data;
            s.parse(txt);
        } catch (Exception e) {
            data = txt;
        }
    }
}
