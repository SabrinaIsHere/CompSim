--[[
This command copies stuff from one place to another. It took me forever, and yes some of this could
probably be moved to functions but honestly whatever I'm just happy it's done and working
--]]

function get_paths(obj, paths)
	if obj.is_null then
		return
	end
	len = 0
	for index in pairs(paths) do
		len = index
	end
	paths[len + 1] = obj
	if obj.is_directory then
		for index, object in ipairs(obj.get_children()) do
			get_paths(object, paths)
		end
	end
	return paths
end

force = params.in_flags("-f")
recursive = params.in_flags("-r") or params.in_flags("-R")

local paths = {}
local dest
len = 0
if params.args[2] ~= nil then
	-- Get destination first
	args_num = 0
	for i in pairs(params.args) do
		args_num = i
	end
	dest = {params.get_objective(params.args[args_num]), params.args[args_num]}
	params.args[args_num] = nil

	-- Get paths to move to destination. Annoyingly complicated, ik, blame the linux devs that decided
	-- to make cp *.lua whatever_dir a valid command
	all = false
	for index, data in ipairs(params.args) do
		if data:match("(.-)(*.-)$") then
			local ending = data:gsub("^(.-)*(%..+)$", "%2")
			local rel_path = data:gsub("(.-)(*.-)$", "%1")
			local dir = io.get(io.get_working_dir().get_path() .. rel_path)
			for i, obj in ipairs(dir.get_children()) do
				if (data:match("*") or obj.name:match("^(.+)(" .. ending .. ")$")) then
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
	params.err("Missing operand\nTry 'cp --help' for more information.")
	return
end

len = 0
for index in pairs(paths) do
	len = index
end

if len == 0 then
	return
end

if len > 1 then
	-- Copy several sources to [dest] directory
	dst = io.get(dest[1])

	if dst.is_null or (not dst.is_directory) then
		io.make_folder(dest[1])
		dst = io.get(dest[1])
		if dst.is_null or (dst.is_directory) then
			params.err("directory '" .. dest .. "' does not exist and could not be created")
			return
		end
	end

	for index, path in ipairs(paths) do
		src = io.get(path[1])

		if src.is_null then
			params.err("cannot copy '" .. path[2] .. "': No such file or directory")
			goto skip
		end

		if src.is_directory then
			if recursive then
				children = get_paths(src, {})
				for i, obj in ipairs(children) do
					local str = src.get_path():gsub("^(.-)(" .. src.name .. ")(.-)$", "%1")
					rel_path = obj.get_path():gsub("^(" .. str .. ")(.-)(/-)$", "%2")
					new_path = dst.get_path() .. rel_path
					
					if obj.is_directory then
						if not io.make_folder(new_path) then
							if force then
								folder = io.get(new_path)
								if folder.is_directory then
									folder.delete()
									if not io.make_folder(new_path) then
										params.err("could not copy '" .. rel_path .. "': Error creating file")
										goto skip
									end
								else
									params.err("could not copy directory '" .. rel_path .. "': Error acquiring object")
									goto skip
								end
							else
								params.err("could not copy directory '" .. rel_path .."': Directory already exists")
								goto skip
							end
						end
					else
						if not io.make_file(new_path) then
							if force then
								file = io.get(new_path)
								if not file.is_directory then
									file.delete()
									if not io.make_file(new_path) then
										params.err("could not copy '" .. rel_path .. "': Error creating file")
										goto skip
									end
								else
									params.err("could not copy file '" .. rel_path .. "': Error acquiring object")
									goto skip
								end
							else
								params.err("could not copy file '" .. rel_path .."': file already exists")
								goto skip
							end
						end

						file = io.get(new_path)
						if file.is_null or file.is_directory then
							params.err("could not copy contents of '" .. rel_path .. "': error acquiring file object")
							goto skip
						end
						file.set_contents(obj.get_contents())
					end

				end
			else
				params.err("-r not specified; ommitting directory '" .. path[2] .. "'")
				goto skip
			end
		else
			new_path = dst.get_path() .. src.name
			if not io.make_file(new_path) then
				if force then
					err_msg = "could not copy '" .. path[2] .. "': destination file exists and could not be deleted"
					file = io.get(new_path)
					if file.is_null or file.is_directory then
						params.err(err_msg)
						goto skip
					else
						file.delete()
						if not io.make_file(new_path) then
							params.err(err_msg)
							goto skip
						end
					end
				else
					params.err("could not copy '" .. path[2] .."': File already exists")
					goto skip
				end
			end

			file = io.get(new_path)
			if file.is_null or file.is_directory then
				params.err("could not copy contents of '" .. path[2] .. "': error acquiring object")
				goto skip
			end
			file.set_contents(src.get_contents())
		end

		::skip::
	end
else
	-- Copy one source to dest
	path = paths[1]
	src = io.get(path[1])
	dest_path = params.get_objective(dest)

	if src.is_null then
		params.err("cannot copy '" .. path[2] .. "': No such file or directory")
		return
	end
	
	function get_dst(folder)
		dst = io.get(dest_path)

		if dst.is_null then
			if folder then
				io.make_folder(dest_path)
			else
				io.make_file(dest_path)
			end
			dst = io.get(dest_path)
			if dst.is_null or dst.is_directory == folder then
				params.err("'" .. dest .. "' already exists")
				return
			end
		else 
			if force then
				dst.delete()
				if folder then
					io.make_folder(dest_path)
				else
					io.make_file(dest_path)
				end
				dst = io.get(dest_path)
				if dst.is_null or dst.is_directory == folder then
					params.err("'" .. dest .. "' cannot be made")
					return
				end
			else
				params.err("directory '" .. dest .. "' already exists")
				return
			end
		end
	end

	if src.is_directory then
		if recursive then
			get_dst()
			children = get_paths(src, {})
			for i, obj in ipairs(children) do
				rel_path = obj.get_path():gsub("(" .. src.get_path() .. ")(.-)(/-)$", "%2")
				if obj.is_directory then
					-- Create directory at new path
					new_path = dst.get_path() .. rel_path
					if not io.make_folder(new_path) then
						local err_msg = "cannot copy '" .. rel_path .. "': File already exists"
						if force then
							folder = io.get(new_path)
							if folder.is_null or not folder.is_directory then
								params.err(err_msg)
								return
							else
								folder.delete()
							end
							
							if not io.make_folder(new_path) then
								params.err(err_msg)
								return
							end
						else
							params.err(err_msg)
							return
						end
					end
				else
					-- Create a file object at new path
					new_path = dst.get_path() .. rel_path
					if not io.make_file(new_path) then
						local err_msg = "cannot copy '" .. rel_path .. "': File already exists"
						if force then
							file = io.get(new_path)
							if file.is_null or file.is_directory then
								params.err(err_msg)
								return
							else
								file.delete()
							end

							if not io.make_file(new_path) then
								params.err(err_msg)
								return
							end
						else
							params.err(err_msg)
							return
						end
					end

					-- Copy contents
					contents = obj.get_contents()
					new_file = io.get(new_path)
					if new_file.is_null or new_file.is_directory then
						params.err("could not copy contents of '" .. rel_path .. "': File doesn't exist")
						return
					end
					new_file.set_contents(contents)
				end
			end
		else
			params.err("-r not specified; ommitting directory '" .. src.name .. "'")
		end
	else
		get_dst()
		contents = src.get_contents()
		dst.set_contents(contents)
	end
end
