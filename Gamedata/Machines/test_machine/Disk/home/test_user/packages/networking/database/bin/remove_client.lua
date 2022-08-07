--[[
This removes a client from the registry. From now on this client will be treated as a stranger

[command] [name]
--]]

database = globals.database_namespace

if #params.args == 1 then
	if not database.clients:remove(params.args[1]) then
		params.err("no such client '" .. params.args[1] .. "'")
	end
else
	params.err("Missing operand;\nTry 'remove_client --help' for more information.")
end