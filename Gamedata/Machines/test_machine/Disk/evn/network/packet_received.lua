ports_registry = globals.ports_registry
packet = params.packet
data = packet.data

function err(msg)
	-- Later this will send back a packet describing the error
	print(msg)
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
				handler.execute(data)
			end
		end
	end
else
	err("Error: no ports registry")
end