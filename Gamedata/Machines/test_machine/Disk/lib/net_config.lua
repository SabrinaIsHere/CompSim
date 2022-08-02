globals["packet_valid"] = function(packet)
	val = not packet.is_null and packet.type == "packet" and packet.data ~= nil
	val = val and packet.data.port ~= nil and packet.data.protocol ~= nil and packet.data.payload ~= nil
	return val
end

registry = {}

std = io.get("/home/" .. usr.get_curr_user().name .. "/packages/networking/std")
if not std.is_null and std.is_directory then
	registry[1] = {
		port = 1,
		path = std.get_path()
	}
else
	print("Error: no standard networking implementation")
end

globals["ports_registry"] = registry