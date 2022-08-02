packet_data = {
    port = 1, -- Redirect to std implementation
    protocol = "print", -- Tells std what to do with the data
    payload="printed"
}

packet = network.get_packet(-1, 1, packet_data)
net = network.get_network()

if not net.send(packet) then
    print("Packet could not be sent.")
end