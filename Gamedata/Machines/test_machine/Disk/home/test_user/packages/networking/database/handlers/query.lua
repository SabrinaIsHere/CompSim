--[[
This is used by packets to get entries depending on given criteria in the payload
--]]

database = globals.database_namespace

relevant_entries = database.entries:query(params.payload)

-- Copy to new objects so other machines can't directly manipulate query results
ret = {}
for i, entry in ipairs(relevant_entries) do
	if entry.access <= current_access then
		new_data = database.entry {
			name = entry.name,
			id = entry.id,
			type = entry.type,
			data = entry.data
		}
		ret[#ret] = new_data
	end
end

params.ret(ret)