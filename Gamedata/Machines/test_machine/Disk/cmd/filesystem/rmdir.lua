--[[
Removes folders 
--]]

force = params.in_flags("-f")

local paths = {}
if params.datastream.index > 0 then
	local t = params.datastream[params.datastream.index]
	paths[1] = {params.get_objective(t), t}
elseif params.args[1] ~= nil then
	del_all = false
	for index, data in ipairs(params.args) do
		if data:match("(.-)(*.-)$") then
			local rel_path = data:gsub("(.-)(*.-)$", "%1")
			local dir = io.get(io.get_working_dir().get_path() .. rel_path)
			len = 0
			for i, obj in ipairs(dir.get_children()) do
				if obj.is_directory then
					len = len + 1
					paths[len] = {obj.get_path(), obj.name}
				end
			end
			del_all = true
			goto last
		end
	end
	if not del_all then
		for index, data in ipairs(params.args) do
			paths[index] = {params.get_objective(data), data}
		end
	end
	::last::
else
	params.err("Missing operand\nTry '" .. params.command .. " --help' for more information.")
	return
end

for i, path in ipairs(paths) do
	dir = io.get(path[1])

	if dir.is_null then
		if not force then
			params.err("could not delete '" .. path[2] .. "': No such file or directory")
		end
		goto skip
	elseif dir.get_children()[1] ~= nil and not force then
		params.err("omitting '" .. paths[2] .. "'; directory not empty")
		goto skip
	end

	if dir.is_directory then
		dir.delete()
	end

	::skip::
end
