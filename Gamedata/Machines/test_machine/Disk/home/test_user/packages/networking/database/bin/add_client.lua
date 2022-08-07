--[[
This adds a new client to the registry, thereby setting their access level

[command] [name] [network_id] [machine_id] [access]
--]]

database = globals.database_namespace

if #params.args == 4 then
	if tonumber(params.args[2]) ~= nil and tonumber(params.args[3]) ~= nil and tonumber(params.args[4]) ~= nil then
		new_client = database.client {
			name = params.args[1],
			network_id = tonumber(params.args[2]),
			machine_id = tonumber(params.args[3]),
			access = tonumber(params.args[4])
		}
		database.clients:add(new_client)
	else
		params.err("Invalid operand(s);\nTry 'add_client --help' for more information.")
	end
elseif #params.args < 4 then
	params.err("Missing operand(s);\nTry 'add_client --help' for more information.")
else
	params.err("Excess operand(s);\nTry 'add_client -- help' for more information.")
end