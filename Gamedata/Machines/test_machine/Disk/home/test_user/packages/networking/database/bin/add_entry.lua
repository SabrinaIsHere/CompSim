--[[
This adds an entry to the database

[command] [name]
[command] [name] [type] [data]
--]]

database = globals.database_namespace
force = params.in_flags("-f")

function has(name)
	for i, entry in ipairs(database.entries) do
		if entry.name == name then
			return true
		end
	end
	return false
end

new_entry = database.entry {}

if #params.args == 1 then
	new_entry.name = params.args[1]
elseif #params.args > 2 then
	new_entry.name = params.args[1]
	local type = params.args[2]
	local data = params.text:gsub("^(.-)" .. type .. " (.-)$", "%2")
	if type == "string" then
		new_entry.data = data
	elseif type == "int" then
		new_entry.data = tonumber(data)
	elseif type == "bool" then
		new_entry.data = (data == "true")
	else
		params.err("Invalid data type\nTry '" .. params.command .. "' --help for more information")
		return
	end
	new_entry.type = type
else
	params.err("Missing operand\nTry '" .. params.command .. " --help' for more information.")
	return
end

if has(new_entry.name) then
	if force then
		database.entries:replace(new_entry)
	else
		params.err("Entry '" .. new_entry.name .. "' is taken. -f to overwrite")
		return
	end
else
	database.entries:add(new_entry)
end