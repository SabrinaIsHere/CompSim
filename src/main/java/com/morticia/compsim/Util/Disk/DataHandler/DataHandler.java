package com.morticia.compsim.Util.Disk.DataHandler;

import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.Networking.Network;
import com.morticia.compsim.Machine.Networking.NetworkHandler;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataComponent;
import com.morticia.compsim.Util.Disk.DiskFile;

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
    public Machine machine;
    public DiskFile file;
    public List<Serializable> data;

    public DataHandler(Machine machine, DiskFile file) {
        this.machine = machine;
        this.file = file;
        this.data = new ArrayList<>();
    }

    public DataHandler(DiskFile file) {
        this.machine = null;
        this.file = file;
        this.data = new ArrayList<>();
    }

    public boolean hasEntry(String entry) {
        for (Serializable i : data) {
            System.out.println(i);
            if (i.getDesig().equals(entry)) {
                return true;
            }
        }
        return false;
    }

    public Serializable getEntry(String entry) {
        for (Serializable i : data) {
            if (i.getDesig().equals(entry)) {
                return i;
            }
        }
        return null;
    }

    public void setEntry(Serializable comp) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getDesig().equals(comp.getDesig())) {
                data.set(i, comp);
            }
        }
    }

    public void add(Serializable s) {
        for (int i = 0; i < data.size(); i++) {
            Serializable entry = data.get(i);
            if (entry.getDesig().equals(s.getDesig())) {
                data.set(i, s);
                return;
            }
        }
        data.add(s);
    }

    public void add(Object obj, String type, String desig) {
        if (hasEntry(desig)) {
            setEntry(new DataComponent(obj, type, desig));
        } else {
            data.add(new DataComponent(obj, type, desig));
        }
        System.out.println(desig);
    }

    /**
     * Creates a string from data, so it can be stored in a file
     *
     * @return String containing data this class holds
     */
    public String serialize() {
        StringBuilder s = new StringBuilder();

        for (Serializable i : data) {
            s.append(i.serialize()).append("\n");
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
            if (str_3.length < 2) {
                continue;
            }
            d.data = str_3[1].replaceFirst(" ", "");
            // Type/designation
            String j = str_3[0];
            j = j.replaceAll("\\[", "").replaceAll("]", "");
            String[] str_2 = j.split("--");
            for (String k : str_2) {
                k = k.strip();
                if (k.startsWith("d_")) {
                    k = k.replaceFirst("d_", "");
                    d.desig = k;
                } else if (k.startsWith("t_")) {
                    k = k.replaceFirst("t_", "");
                    d.type = k;
                }
            }
            d.data = parseData((String) d.data, d.type);
            if (d.data != null) {
                data.add(d);
            }
        }
    }

    public Object parseData(String data, String type) {
        switch (type) {
            case Constants.str_type:
                // Is string so just return
                return data;
            case Constants.v_folder_type:
                VirtualFolder v = new VirtualFolder(machine);
                v.parse(data);
                if (v.remove) {
                    return null;
                }
                return v;
            case Constants.v_file_type:
                VirtualFile f = new VirtualFile(machine);
                f.parse(data);
                if (f.remove) {
                    return null;
                }
                return f;
            case Constants.network_handler_type:
                NetworkHandler h = new NetworkHandler(machine);
                h.parse(data);
                machine.networkHandler = h;
                return h;
            case Constants.network_type:
                Network n = new Network(false);
                n.parse(data);
                return n;
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
