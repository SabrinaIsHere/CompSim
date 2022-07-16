package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.Networking.Network;
import com.morticia.compsim.Machine.Networking.Packet;
import com.morticia.compsim.Machine.Networking.Socket;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class NetworkLib extends TwoArgFunction {
    Machine machine;

    public NetworkLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue mod_name, LuaValue env) {
        LuaTable library = tableOf();
        library.set("get_network", new get_network(machine));
        library.set("discover_network", new discover_network(machine));
        library.set("get_socket", new get_socket(machine));
        library.set("get_sockets", new get_sockets(machine));
        library.set("open_socket", new open_socket(machine));
        env.set("network", library);
        return library;
    }

    public static class get_network extends ZeroArgFunction {
        Machine machine;

        public get_network(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            return machine.networkHandler.network.toTable();
        }
    }

    public static class discover_network extends OneArgFunction {
        Machine machine;

        public discover_network(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue id) {
            for (Network i : machine.networkHandler.network.networks) {
                if (i.globalId == id.toint()) {
                    return i.toTable();
                }
            }
            for (Network i : Network.allNetworks) {
                if (i.globalId == id.toint()) {
                    machine.networkHandler.network.networks.add(i);
                    return i.toTable();
                }
            }
            return Network.getBlankTable(id.toint());
        }
    }

    public static class get_socket extends OneArgFunction {
        Machine machine;

        public get_socket(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue id) {
            for (Network i : machine.networkHandler.network.networks) {
                if (i.globalId == id.toint()) {
                    return i.toTable();
                }
            }
            return Socket.geteBlankTable(id.toint());
        }
    }

    public static class get_sockets extends ZeroArgFunction {
        Machine machine;

        public get_sockets(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (Socket i : machine.networkHandler.sockets) {
                table.set(table.length(), i.toTable());
            }
            return table;
        }
    }

    public static class open_socket extends TwoArgFunction {
        Machine machine;

        public open_socket(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue network_id, LuaValue machine_id) {
            return machine.networkHandler.requestNewSocket(network_id.toint(), machine_id.toint()).toTable();
        }
    }

    // Network functions

    public static class get_known_networks extends ZeroArgFunction {
        Network network;

        public get_known_networks(Network network) {
            this.network = network;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (Network i : network.networks) {
                table.set(table.length(), i.toTable());
            }
            return table;
        }
    }

    public static class get_members extends ZeroArgFunction {
        Network network;

        public get_members(Network network) {
            this.network = network;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (Machine i : network.members) {
                table.set(table.length(), i.desig);
            }
            return table;
        }
    }

    public static class network_send extends OneArgFunction {
        Network network;

        public network_send(Network network) {
            this.network = network;
        }

        @Override
        public LuaValue call(LuaValue packet) {
            network.sendPacket(Packet.fromTable(packet.checktable(), network));
            return LuaNil.NIL;
        }
    }

    // Socket functions

    public static class socket_send extends OneArgFunction {
        Socket socket;

        public socket_send(Socket socket) {
            this.socket = socket;
        }

        @Override
        public LuaValue call(LuaValue data) {
            socket.send(data.checktable());
            return LuaNil.NIL;
        }
    }

    public static class set_output extends ZeroArgFunction {
        Socket socket;

        public set_output(Socket socket) {
            this.socket = socket;
        }

        @Override
        public LuaValue call() {
            socket.sender.defaultStream.component = socket;
            return LuaNil.NIL;
        }
    }
}
