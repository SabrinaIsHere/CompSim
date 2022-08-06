--[[
Removes an entry from the database

[command] [name]
--]]
database = globals.database_namespace

name = ""
if #params.args == 1 then
	name = params.args[1]
elseif #params.args > 1 then
	params.err("Excess operand(s)\nTry '" .. params.command .. " --help' for more information.")
	return
else
	params.err("Missing operand\nTry '" .. params.command .. " --help' for more information.")
	return
end

if not database.entries:remove(name) then
	params.err(name .. ": No such entry")
	return
end