package com.morticia.compsim.Machine.Networking;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.MachineIOStream.IOComponent;
import com.morticia.compsim.Util.Lua.Lib.NetworkLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.List;

/**
 * This class serves as a point of reference for an established connection. Good for efficiency
 *
 * @author Morticia
 * @version 1.0
 * @since 7/15/22
 */

public class Socket implements IOComponent {
    public Machine sender;
    public Machine receiver;

    public boolean open;

    public int id;

    public Socket(Machine sender, Machine receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.open = false;
        this.id = sender.networkHandler.sockets.size();
    }

    public void send(LuaTable data) {
        if (open) {
            receiver.networkHandler.receivePacket(new Packet(sender, data));
        }
    }

    @Override
    public String readLine() {
        return "n/a";
    }

    @Override
    public void writeLine(String data) {
        if (open) {
            LuaTable table = new LuaTable();
            table.set("data", data);
            send(table);
        }
    }

    @Override
    public LuaTable getAllData() {
        return new LuaTable();
    }

    @Override
    public LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("type", "socket");
        table.set("id", id);
        table.set("sender_id", sender.networkHandler.address);
        table.set("sender_name", sender.desig);
        table.set("receiver_addr", receiver.networkHandler.address);
        table.set("receiver_network_addr", receiver.networkHandler.network.globalId);
        table.set("receiver_name", receiver.desig);
        table.set("send", new NetworkLib.socket_send(this));
        table.set("set_output", new NetworkLib.set_output(this));
        table.set("receiver_name", receiver.desig);
        return table;
    }

    public static LuaTable geteBlankTable(int id) {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(true));
        table.set("type", "socket");
        table.set("receiver_id", id);
        return table;
    }
}
