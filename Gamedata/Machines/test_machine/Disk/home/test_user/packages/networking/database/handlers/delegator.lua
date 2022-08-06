--[[
This is called by event handlers when they determine that an event should be passed to this package.
It will determine which handler to call with the information and call it with a process object so 
everything will share globals
--]]

database = globals.database_namespace
handler_folder = io.get(database.root_path .. "/handlers")

if not handler_folder.is_null and handler_folder.is_directory then
	for i, child in ipairs(handler_folder.get_children()) do
		if not child.is_null and not child.is_directory then
			name = child.name:gsub("^(.+)%.(.-)$", "%1")
			if name ~= "delegator" and name == params.protocol then
				child.execute(params)
				return
			end
		end
	end
end