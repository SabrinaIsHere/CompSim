packet_data = {
    port = 2, -- Redirect to std implementation
    protocol = "client", -- Tells std what to do with the data
    payload="operation=add",
    return_port = 1,
    return_protocol = "print"
}

packet = network.get_packet(-1, 1, packet_data)
net = network.get_network()

if not net.send(packet) then
    print("Packet could not be sent.")
end