--[[
Lists all registered clients
--]]

database = globals.database_namespace

for i, client in ipairs(database.clients) do
	print(tostring(client))
end