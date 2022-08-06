--[[
This is pretty simple, it just lists all the entries in the database
--]]

database = globals.database_namespace

for i, entry in ipairs(database.entries) do
	print(tostring(entry))
end