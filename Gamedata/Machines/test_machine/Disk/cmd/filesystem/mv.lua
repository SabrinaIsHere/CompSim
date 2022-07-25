--[[
This command moves objects from one place to another. Also handy for renaming
--]]

force = params.in_flags("-f")

function get_children(obj, children)
	if obj.is_null then
		return {}
	end

	len = 0
	for index in pairs(children) do
		len = index
	end

	children[len + 1] = obj
	if obj.is_directory then
		for index, child in ipairs(obj.get_children()) do
			get_children(child, children)
		end
	end
	return children
end

function copy_children(src, dst)
	if src.is_null or dst.is_null then
		return
	end

	local children = get_children(src, {})

	for i, obj in ipairs(children) do
		rel_path = obj.get_path():gsub("^(.+)(" .. src.name .. ")(.-)(/-)$", "%3")
		if not rel_path:match("/(.)") then
			goto skip
		end
		new_path = dst.get_path():gsub("(.-)/-$", "%1") .. rel_path

		if obj.is_directory then
			if not io.make_folder(new_path) then
				folder = io.get(new_path)
				if folder.is_directory then
					if force then
						folder.delete()
						if not io.make_folder(new_path) then
							params.err("could not make directory '" .. rel_path .. "'; reason unknown")
							return false
						end
					else
						params.err("omitting directory '" .. rel_path .. "'; directory exists. -f to overwrite")
						goto skip
					end
				else
					params.err("could not make directory '" .. rel_path .. "'; reason unknown")
					return false
				end
			end
		else
			if not io.make_file(new_path) then
				file = io.get(new_path)
				if not file.is_directory then
					if force then
						file.delete()
						if not io.make_file(new_path) then
							params.err("could not make file '" .. rel_path .. "'; reason unknown")
							return false
						end
					else
						params.err("omitting file '" .. rel_path .. "'; file exists. -f to overwrite")
						goto skip
					end
				else
					params.err("could not make file '" .. rel_path .. "'; reason unknown")
					return false
				end
			end

			file = io.get(new_path)
			if file.is_null or file.is_directory then
				params.err("could not copy contents to '" .. rel_path .. "'; error acquiring object")
				return false
			end
			file.set_contents(obj.get_contents())
		end
		::skip::
	end
	return true
end

paths = {}
len = 1
dest = ""
if params.args[2] ~= nil then
	-- Get destination first
	args_num = 0
	for i in pairs(params.args) do
		args_num = i
	end
	dest = {params.get_objective(params.args[args_num]), params.args[args_num]}
	params.args[args_num] = nil

	-- Get paths to move to destination. Annoyingly complicated, ik, blame the linux devs that decided
	-- to make mv *.lua whatever_dir a valid command
	all = false
	len = 0
	for index, data in ipairs(params.args) do
		if data:match("(.-)(*.-)$") then
			local ending = data:gsub("^(.-)*(%..+)$", "%2")
			local rel_path = data:gsub("(.-)(*.-)$", "%1")
			local dir = io.get(io.get_working_dir().get_path() .. rel_path)
			for i, obj in ipairs(dir.get_children()) do
				if (not obj.is_directory) and (data:match("*") or obj.name:match("^(.+)(" .. ending .. ")$")) then
					len = len + 1
					paths[len] = {obj.get_path(), obj.name}
				end
			end
			all = true
			goto last
		end
	end
	if not all then
		for index, data in ipairs(params.args) do
			paths[index] = {params.get_objective(data), data}
		end
	end
	::last::
else
	params.err("Missing operand\nTry '" .. params.command .. " --help' for more information.")
	return
end

len = 0
for i in pairs(paths) do
	len = i
end

if len > 1 then
	-- mv sources to directory
	dst = io.get(dest[1])
	if dst.is_null or not dst.is_directory then
		params.err("cannot move to '" .. dest[2] .. "': No such directory")
		return
	end

	for index, path in ipairs(paths) do
		src = io.get(path[1])
		if src.is_null then
			params.err("could not move '" .. path[2] .. "': No such file or directory")
			goto skip
		end

		parent_path = src.get_path():gsub("(.+)/(" .. src.name .. ")", "%1")
		rel_path = src.get_path():gsub("^(" .. parent_path .. ")(.+)/-$", "%2")
		new_path = dst.get_path() .. rel_path
		
		if src.is_directory then
			if not io.make_folder(new_path) then
				if force then
					folder = io.get(new_path)
					if folder.is_directory then
						folder.delete()
						if not io.make_folder(new_path) then
							params.err("could not overwrite directory '" .. rel_path .. "': Reason unknown")
							return
						end
					else
						params.err("could not overwrite directory '" .. rel_path .. "': Reason unknown")
						return
					end
				else
					params.err("omitting '" .. rel_path .."'; directory already exists")
					goto skip
				end
			end

			folder = io.get(new_path)
			if folder.is_null or not folder.is_directory then
				params.err("could not move '" .. rel_path .. "'; Reason unknown")
				return
			end
			copy_children(src, folder)
		else
			if not io.make_file(new_path) then
				if force then
					file = io.get(new_path)
					if not file.is_null then
						if not file.is_directory then
							file.delete()
							if not io.make_file(new_path) then
								params.err("could not overwrite file '" .. rel_path .. "'; Reason unknown")
								return
							end
						else
							params.err("could not overwrite file '" .. rel_path .. "': Reason unknown")
							return
						end
					else
						params.err("could not overwrite file '" .. rel_path .. "'; Reason unknown")
						return
					end
				else
					params.err("omitting '" .. rel_path .."'; file already exists")
					goto skip
				end
			end

			file = io.get(new_path)
			if file.is_null or file.is_directory then
				params.err("could not write contents of '" .. rel_path .. "; Reason unknown")
				return
			end
			file.set_contents(src.get_contents())
		end

		src.delete()

		::skip::
	end
else
	-- mv source to dst
	path = paths[1]
	src = io.get(path[1])
	dst = io.get(dest[1])

	if src.is_null then
		params.err("could not move '" .. path[2] .. "': No such file or directory")
		return
	end

	if not dst.is_null then
		if force then
			dst.delete()
			dst = io.get(dest[1])
			if not dst.is_null then
				params.err("could not move " .. path[2] .. "': '" .. dest[2] .. "' already exists and could not be deleted")
				return
			end
		else
			params.err("omitting '" .. path[2] .. "; '" .. dest[2] .. "' already exists")
			return
		end
	end

	if src.is_directory then
		if not io.make_folder(dest[1]) then
			params.err("could not make directory '" .. dest[2] .. "'")
			return
		end

		dst = io.get(dest[1])
		if dst.is_null or (not dst.is_directory) then
			params.err("could not make directory '" .. dest[2] .."'")
			return
		end

		if not copy_children(src, dst) then
			return
		end
	else
		if not io.make_file(dest[1]) then
			params.err("could not make file '" .. dest[2] .. "'")
			return
		end

		dst = io.get(dest[1])
		if dst.is_null or dst.is_directory then
			params.err("could not make file '" .. dest[2] .."'")
			return
		end
		file.set_contents(src.get_contents())
	end
	src.delete()
end
