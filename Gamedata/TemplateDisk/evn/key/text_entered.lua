print = params.terminal.print

params.terminal.set_buffer('')
print(params.terminal.get_prefix() .. params.text)

-- This is for debugging I'm doing this with scripts later
if (params.command == "discover_network") then
	network.discover_network(args[1])
elseif (params.command == "get_networks") then
	for index, data in ipairs(network.get_network().get_known_networks()) do
		print(data.global_id)
	end
elseif (params.command == "send_packet") then
	-- command, network, machine, data
	local packet = network.get_packet(args[1], args[2], args[3])
	network.get_network().get_known_networks()[params.args[1]].send(packet)
end