package com.morticia.compsim.Util.Disk;

public class DataComponent {
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
        return "[d_" + desig + "/ t_" + type + "]: " + data.toString();
    }
}
