package com.morticia.compsim.Machine.Networking;

import com.morticia.compsim.Machine.Machine;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * Holds data for network operations
 *
 * @author Morticia
 * @version 1.0
 * @since 7/15/22
 */

public class Packet {
    public Machine sender;
    // Id of the network in the networks list
    public int receiverNetwork;
    public int receiver;

    public LuaTable data;

    public Packet(Machine sender, int receiver, int receiverNetwork, LuaTable data) {
        this.sender = sender;
        this.receiver = receiver;
        this.receiverNetwork = receiverNetwork;
        this.data = data;
    }

    public Packet(Machine sender, LuaTable data) {
        this.sender = sender;
        this.data = data;
        // TODO: 7/15/22 Format metadata in data table
    }

    public static Packet fromTable(LuaTable table, Network n) {
        return new Packet(n.members.get(table.get("sender_addr").checkint() - 1),
                table.get("addr").checkint() - 1,
                table.get("network_addr").checkint() - 1,
                table.get("data").checktable());
    }

    public LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("type", "packet");
        table.set("sender_addr", sender.networkHandler.address);
        table.set("network_addr", receiverNetwork);
        table.set("addr", receiver);
        table.set("data", data);
        return table;
    }
}
