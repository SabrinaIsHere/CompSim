print = params.terminal.print

params.terminal.set_buffer('')
print(params.terminal.get_prefix() .. params.text)

-- This is for debugging I'm doing this with scripts later
if params.command == "discover_network" then
	local n = network.discover_network(params.args[1])
	print("[" .. n.global_id .. "] null: " .. tostring(n.is_null))
elseif (params.command == "get_networks") then
	for index, data in ipairs(network.get_network().get_known_networks()) do
		print(data.global_id)
	end
elseif (params.command == "send_packet") then
	-- command, network, machine, data
	local packet = network.get_packet(params.args[1], params.args[2], {params.args[3]})
	local n = network.get_network()
	if not n.is_null then
		if n.send(packet) then
			print("Packet sent")
		end
	end
else
	print("Please enter a valid command")
end