ports_registry = globals.ports_registry
packet = params.packet
data = packet.data

function err(msg)
	-- Later this will send back a packet describing the error
	print(msg)
end

function ret(port, protocol, payload)
	net = network.discover_network(packet.sender_network)
	if not net.is_null then
		new_packet = network.get_packet(-1, packet.sender_addr, {
			port = port,
			protocol = protocol,
			payload = payload
		})
		net.send(new_packet)
	end
end

if not globals.packet_valid(packet) then
	err("packet_invalid")
	return
end

if ports_registry ~= nil then
	for i, reg in ipairs(ports_registry) do
		if reg.port == data.port then
			handler = io.get(reg.path .. "handlers/delegator.lua")
			if not handler.is_null and not handler.is_directory then
				data["err"] = err
				data["ret"] = ret
				handler.execute(data)
			end
		end
	end
else
	err("Error: no ports registry")
end