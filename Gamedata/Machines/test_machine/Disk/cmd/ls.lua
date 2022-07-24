--[[ 
There are a lot of things in this command that seem a little weird, it's all to make the behavior
very closely mimic the actual linux command
sidenote, yes I need to do padding to make the entries line up but that seems like a huge pain and
it's also somewhat unnecessary at the moment. The biggest pain in the ass is the auto text wrapping,
I'd have to figure out how to feed information about it to this and the whole thing would take days

also I coded most of this while hungry or tired, don't blame me if there's anything dumb
--]]

-- This figures out if I should list the contents of all children of a given path. Makes the * symbol work
function get_multiple(path)
	local t = {}
	if string.match(path, "/%*$") or string.match(path, "%*$") then
		-- Removes the asterisk
		path = params.get_objective(path:gsub("(.)(/%*)$", "%1"))
		parent = io.get(path)
		if not parent.is_null then
			for index, data in ipairs(parent.get_children()) do
				t[index] = data
			end
		end
	else
		t[1] = io.get(params.get_objective(path))
	end
	return t
end

-- Applies the correct color to a filesystem object
function color(object, txt)
	if object == nil or object.is_null then
		return ""
	elseif object.is_directory then
		return terminal.set_color(txt, "0052FF")
	else
		if string.match(object.name, "(.)%.lua") then
			return terminal.set_color(txt, "37FF00")
		elseif object.name:match("(.)%.txt") then
			return terminal.set_color(txt, "008F0C")
		else
			return terminal.set_color(txt, "FFFFFF")
		end
	end
end

function has(t, val)
	for i, d in ipairs(t) do
		if d == val then
			return true
		end
	end
	return false
end

-- Gets proper text based on options
function eval(file)
	local newline = true
	local print_text = color(file, file.name) .. globals.htmlTab
	
	if has(params.flags, "-l") then
		newline = false
		print_text = file.get_perms() .. " " .. file.get_owner().name .. " " .. file.get_group().name .. " " .. print_text .. "\n"
	end

	return print_text, newline, ((not file.name:match("^(%.)")) or has(params.flags, "-a"))
end

-- Determine path(s)
local paths = {}
if params.datastream.index > 0 then
	local t = params.datastream[params.datastream.index]
	paths[1] = {params.get_objective(t), t}
elseif params.args[1] ~= nil then
	for index, data in ipairs(params.args) do
		for i, d in ipairs(get_multiple(data)) do
			paths[index + i - 1] = {d.get_path(), d.name}
		end
	end
else
	paths[1] = {io.get_working_dir().get_path(), "~"}
end

table.sort(paths)

function get_len(t)
	local len = 0
	for index in pairs(t) do
		len = index
	end
	return len
end

-- List entries in path(s)
for i, d in ipairs(paths) do
	if not (paths[2] == nil) then
		print(d[2] ..":")
	end

	folder = io.get(d[1])
	if folder == nil or folder.is_null then
		params.err("cannot access '" .. d[2] .. "': No such file or directory")
		goto skip
	elseif not folder.is_directory then
		print(color(folder))
		goto skip
	end

	-- Sort based on type of thing being listed
	buffer = {}
	folders = 0
	lua = 0
	txt = 0
	other = 0
	for i, object in ipairs(folder.get_children()) do
		if object ~= nil and not object.is_null then
			if object.is_directory then
				buffer[folders + 1] = object
				folders = folders + 1
			else
				if string.match(object.name, "(.)%.lua") then
					buffer[folders + lua + 1] = object
					lua = lua + 1
				elseif object.name:match("(.)%.txt") then
					buffer[folders + lua + txt + 1] = object
					txt = txt + 1
				else
					buffer[folders + lua + txt + other + 1] = object
					other = other + 1
				end
			end
		end
	end

	local newline = false
	for j, k in ipairs(buffer) do
		txt, newline, print = eval(k)
		if print then
			params.terminal.write(txt)
		end
	end
	if newline and print then
		params.terminal.write("\n")
	end
	::skip::
end