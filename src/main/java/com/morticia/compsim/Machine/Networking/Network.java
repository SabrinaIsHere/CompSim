package com.morticia.compsim.Machine.Networking;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Lua.Lib.NetworkLib;
import com.morticia.compsim.Util.Lua.LuaLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Network implements Serializable {
    // These two are mostly useful for debugging, this won't be visible to machines
    public static List<Network> allNetworks = new ArrayList<>();
    public int globalId;

    public List<Network> networks;
    public List<Machine> members;
    // You don't have to pay attention to this it's just for serialization
    public List<Integer> potential_networks;

    public Network() {
        globalId = assignGlobalId();
        allNetworks.add(this);

        networks = new ArrayList<>();
        members = new ArrayList<>();
        potential_networks = new ArrayList<>();
    }

    public Network(boolean getId) {
        if (getId) {
            globalId = assignGlobalId();
            allNetworks.add(this);
        }

        networks = new ArrayList<>();
        members = new ArrayList<>();
        potential_networks = new ArrayList<>();
    }

    // Assigns a unique, partially random id to every network. This is so you can't just iterate
    // through every number from zero and find all the sequential networks too easily
    public static int assignGlobalId() {
        int id;
        l:
        while (true) {
            id = (((allNetworks.size() + 1) * new Random().nextInt(20)) / 2) * 153;
            for (Network i : allNetworks) {
                if (i.globalId == id) {
                    continue l;
                }
            }
            break;
        }
        return id;
    }

    public static Network getNetwork(int id) {
        for (Network i : allNetworks) {
            if (i.globalId == id) {
                return i;
            }
        }
        return null;
    }

    public int assignId() {
        return members.size();
    }

    public boolean sendPacket(Packet packet) {
        Network n;
        if (packet.receiverNetwork < 0) {
            n = this;
        } else if (packet.receiverNetwork < networks.size()) {
            n = networks.get(packet.receiverNetwork);
        } else {
            return false;
        }
        return n.routePacket(packet);
    }

    public boolean routePacket(Packet packet) {
        if (packet.receiver < members.size() && packet.receiver >= 0) {
            members.get(packet.receiver).networkHandler.receivePacket(packet);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + globalId + "]: " + members.toString();
    }

    public LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("type", "network");
        table.set("global_id", globalId);
        table.set("get_known_networks", new NetworkLib.get_known_networks(this));
        table.set("get_members", new NetworkLib.get_members(this));
        table.set("send", new NetworkLib.network_send(this));
        return table;
    }

    public LuaTable toDietTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("type", "network");
        table.set("global_id", globalId);
        return table;
    }

    public static LuaTable getBlankTable(int id) {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(true));
        table.set("type", "network");
        table.set("global_id", id);
        return table;
    }

    @Override
    public String getType() {
        return Constants.network_type;
    }

    @Override
    public String getDesig() {
        return Integer.toString(globalId);
    }

    @Override
    public String serialize() {
        List<String> network_ids = new ArrayList<>();
        for (Network i : networks) {
            network_ids.add(Integer.toString(i.globalId));
        }

        return getPrefix() + prepParams(new String[][]{
                {"id", Integer.toString(globalId)},
                {"known_networks", network_ids.toString()}
        });
    }

    @Override
    public void parse(String txt) {
        List<String[]> var = extractParams(txt);
        l:
        for (String[] i : var) {
            switch (i[0]) {
                case "n/a":
                    continue;
                case "id":
                    int id = Integer.parseInt(i[1]);
                    for (Network j : allNetworks) {
                        if (j.globalId == id) {
                            continue l;
                        }
                    }
                    this.globalId = id;
                    allNetworks.add(this);
                    break;
                case "known_networks":
                    String[] str = Serializable.getListMembers(i[1]);
                    for (String j : str) {
                        potential_networks.add(Integer.parseInt(j));
                    }
            }
        }
    }
}
