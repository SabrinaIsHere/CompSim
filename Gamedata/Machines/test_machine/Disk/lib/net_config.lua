globals["packet_valid"] = function(packet)
	val = not packet.is_null and packet.type == "packet" and packet.data ~= nil
	val = val and packet.data.port ~= nil and packet.data.protocol ~= nil and packet.data.payload ~= nil
	return val
end

registry = {}

-- Get all the networking packages
network_folder = io.get("/home/" .. usr.get_curr_user().name .. "/packages/networking")
if not network_folder.is_null and network_folder.is_directory then
	for i, obj in ipairs(network_folder.get_children()) do
		if not obj.is_null and obj.is_directory then
			config = io.get(obj.get_path() .. ".config.cfg")
			if not config.is_null and not config.is_directory then
				for j, line in ipairs(config.get_contents()) do
					if line:match("(port)=%d") ~= nil then
						registry[#registry + 1] = {
							port = tonumber(line:match("port=(%d)")),
							path = obj.get_path()
						}
					end
				end
			end
		end
	end
end

globals["ports_registry"] = registry