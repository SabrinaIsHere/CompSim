package com.morticia.compsim.Machine.Networking;

import com.morticia.compsim.Machine.Event.Event;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Lua.LuaParamData;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by machines to handle new network connections and transmissions over old ones
 *
 * @author Morticia
 * @version 1.0
 * @since 7/15/22
 */

public class NetworkHandler {
    public Network network;

    public Machine machine;
    public int address;
    public List<Socket> sockets;

    public NetworkHandler(Machine machine) {
        // TODO: 7/15/22 load networks from dataHandler
        this.network = new Network();
        this.network.members.add(machine);
        this.machine = machine;

        this.address = network.assignId();
        this.sockets = new ArrayList<>();
    }

    public void receivePacket(Packet packet) {
        List<String> params = new ArrayList<>(machine.eventHandler.getEvent("packet_received").eventData);
        params.add("sender_id: " + packet.sender.networkHandler.address);
        params.add("sender_desig: " + packet.sender.desig);
        LuaParamData d = new LuaParamData(params, false);
        d.addTable("data", packet.data);

        machine.eventHandler.triggerEvent("packet_received", d);
    }

    public boolean sendPacket(Packet packet) {
        return network.sendPacket(packet);
    }

    public void joinNetwork(int n) {
        if (n < network.networks.size()) {
            network = network.networks.get(n);
        }
    }

    public Socket requestNewSocket(int n, int m) {
        try {
            network.networks.get(n).members.get(m).networkHandler.openSocket(machine);
            Socket s = new Socket(machine, network.networks.get(n).members.get(m));
            sockets.add(s);
            return s;
        } catch (Exception e) {
            return new Socket(null, null);
        }
    }

    public void openSocket(Machine requester) {
        machine.eventHandler.triggerEvent("socket_requested", new String[] {
                "requester_network_id: " + requester.networkHandler.network.globalId,
                "requester_addr" + requester.networkHandler.address
        });
    }

    public void registerNetworkEvents() {
        machine.eventHandler.eventList.add(new Event(machine, "packet_received", "network"));
    }
}
