--[[
This command makes directories at the given path(s)
--]]

function has(t, val)
	for i, d in ipairs(t) do
		if d == val then
			return true
		end
	end
	return false
end

local paths = {}
if params.datastream.index > 0 then
	local t = params.datastream[params.datastream.index]
	paths[1] = {params.get_objective(t), t}
elseif params.args[1] ~= nil then
	for index, data in ipairs(params.args) do
		paths[index] = {params.get_objective(data), data}
	end
else
	params.err("Missing operand\nTry '" .. params.command .. " --help' for more information.")
	return
end

-- Iterates through paths and attempts to make folders. Assumes paths are objective
for index, path in ipairs(paths) do
	if params.in_flags("-p") then
		local prev = ""
		for i in path[1]:gmatch("([^/]+)") do
			prev = prev .. "/" .. i
			io.make_folder(prev)
		end
	else
		if not io.make_folder(path[1]) then
			folder = io.get(path[1])
			if folder.is_null or not folder.is_directory then
				params.err("cannot make directory '" .. path[2] .. "': Invalid path")
			else
				params.err("Cannot make directory '" .. path[2] .. "'. Directory exists")
			end
		end
	end
end