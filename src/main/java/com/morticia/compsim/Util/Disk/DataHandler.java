package com.morticia.compsim.Util.Disk;

import com.morticia.compsim.Util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to make it easier to save/load data
 *
 * @author Morticia
 * @version 1.0
 * @since 7/1/22
 */

public class DataHandler {
    DiskFile file;
    List<DataComponent> data;

    public DataHandler(DiskFile file) {
        this.file = file;
        this.data = new ArrayList<>();
    }

    public boolean hasEntry(String entry) {
        for (DataComponent i : data) {
            if (i.desig.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    public DataComponent getEntry(String entry) {
        for (DataComponent i : data) {
            if (i.desig.equals(entry)) {
                return i;
            }
        }
        return null;
    }

    public void setEntry(DataComponent comp) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).desig.equals(comp.desig)) {
                data.set(i, comp);
            }
        }
    }

    public void add(Object obj, String type, String desig) {
        if (hasEntry(desig)) {
            setEntry(new DataComponent(obj, data.size() + 1, type, desig));
        } else {
            data.add(new DataComponent(obj, data.size() + 1, type, desig));
        }
    }

    /**
     * Creates a string from data, making it storable for later
     *
     * @return String containing data this class holds
     */
    public String serialize() {
        StringBuilder s = new StringBuilder();

        for (DataComponent i : data) {
            s.append(i.toString()).append("\n");
        }

        return s.toString();
    }

    /**
     * Parses serialized data into more usable forms
     *
     * @param str String to parse
     */
    public void parse(String str) {
        String[] str_1 = str.split("\n");
        for (String i : str_1) {
            DataComponent d = new DataComponent();
            // Data
            String[] str_3 = i.split(":");
            d.data = str_3[1].replaceFirst(" ", "");
            // Type/designation
            String j = str_3[0];
            j.replaceAll("\\[", "");
            j.replaceAll("]", "");
            String[] str_2 = i.split("/");
            for (String k : str_2) {
                k.strip();
                if (k.startsWith("_d")) {
                    k.replaceFirst("d_", "");
                    d.desig = k;
                } else if (k.startsWith("_t")) {
                    k.replaceFirst("t_", "");
                    d.type = k;
                }
            }
            d.index = data.size() + 1;
            d.data = parseData((String) d.data, d.type);
            data.add(d);
        }
    }

    public Object parseData(String data, String type) {
        // TODO: 7/1/22 Make this interpret the data type and store as correct thing
        if (type.equals(Constants.str_type)) {
            // Is string so just return
            return data;
        }
        return data;
    }

    public boolean save() {
        file.setLines(serialize());
        return file.writeBuffer();
    }

    public boolean load() {
        if (!file.readBuffer()) {
            return false;
        }
        parse(file.getLines());
        return true;
    }
}
