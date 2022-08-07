--[[
This handles all client related networking operations.
The packet payload contains several assigned parameters, these tell the handler what data to use
and how to process it

operation - The operation to execute. Given via the [operation] parameter
	add: Adds the origin of the packet to the registry. Will not update permissions
	remove: Removes a client of the name specified by the [name] parameter (if this client)
has sufficient permissions)
	list: Returns a list of basic information about clients. Must have a privilege level
greater than or equal to another client for it to be visible
	set: Sets a client of the name in the [name] parameter to the given [data] string
. You must have a greater privilege level than the target to do this. Privilege 
levels can not be elevated beyond the privilege level of the client requesting the operation
--]]

-- Note: this is not as of yet bug tested, though it's all stuff I've done before
-- so I doubt there's anything especially egregious

database = globals.database_namespace

parameters = {}
params.payload:gsub("([^,%s]+)", function(capture)
	parameters[capture:match("(.+)=")] = capture:match("=(.+)")
end)

if parameters.operation == nil then
	params.err("no given operation")
	return
end

if parameters.operation == "add" then
	new_client = database.client {}
	new_client:init_from_packet(params)
	new_client.name = parameters.name or "default"
	database:add(new_client)
elseif parameters.operation == "remove" then
	access = 0
	for i, client in ipairs(database.clients) do
		if client.network_id == params.network_id and client.machine_id == params.sender_addr then
			access = client.access
		end
	end
	for i, client in ipairs(database.clients) do
		if client.name == parameters.name and client.access < access then
			database.clients:remove(client.name)
			break
		end
	end
elseif parameters.operation == "list" then
	access = 0
	for i, client in ipairs(database.clients) do
		if client.network_id == params.network_id and client.machine_id == params.sender_addr then
			access = client.access
		end
	end
	ret = {}
	for i, client in ipairs(database.clients) do
		if client.access <= access then
			ret[#ret + 1] = tostring(client)
		end
	end
	params.ret(ret)
	return
elseif parameters.operation == "set" then
	if parameters.data == nil then
		params.err("no new data given")
		return
	end
	access = 0
	for i, client in ipairs(database.clients) do
		if client.network_id == params.network_id and client.machine_id == params.sender_addr then
			access = client.access
		end
	end
	for i, client in ipairs(database.clients) do
		if client.name == parameters.name and client.access < access then
			clients:remove(client.name)
			client:fromstring(parameters.data)
			clients:add(client)
			break
		end
	end
else
	params.err("operation not recognized")
	return
end