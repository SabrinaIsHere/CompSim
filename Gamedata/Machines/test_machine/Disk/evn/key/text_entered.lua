-- Avoids issues with io streams
params.terminal = params.m_terminal
print = params.terminal.print

-- Utility functions since a lot of commands need it
function get_objective(path)
	if path:sub(1, #"/") == "/" then
		return path
	else 
		return io.get_working_dir().get_path() .. path
	end
end

function help(txt)
	return txt:find("%s+%-%-help")
end

-- Updates the terminal prefix
function set_terminal_prefix()
	local dir = io.get_working_dir()
	local dir_name

	if dir.get_path() == "/home/" .. usr.get_curr_user().name .. "/" then
		dir_name = "~"
	else
		dir_name = dir.name
	end

	local start = terminal.set_color("[" .. usr.get_curr_user().name .. "@" .. machine.name, "37FF00")
	local mid = terminal.set_color(" " .. dir_name, "FFFFFF")
	local last = terminal.set_color("]$" .. htmlSpace, "37FF00")

	local t = start .. mid .. last
	params.m_terminal.set_prefix(t)
end
params["update_prefix"] = set_terminal_prefix

-- Makes the terminal look more like the linux terminal
params.terminal.set_buffer('')
print(params.terminal.get_prefix() .. params.text)

-- Record output
output_table = {
	update = function(__self, data)
		if __self.do_print then
			params.terminal.write(data)
		end
	end,

	read = function(__self)
		return params.terminal.get_line()
	end,
	index = 0,
	do_print=true
};
stream.set_output(output_table)
params["output"] = output_table

-- Get commands
local commands = {}
string.gsub(params.text, "([^>]+)", function (w)
	w = string.gsub(w, '^%s*(.-)%s*$', '%1')
    table.insert(commands, w)
end)

-- Shell
if globals.is_shell_enabled then
	if params.text:match("^shell%s-") then
		print("shell disabled")
		globals.is_shell_enabled = false
		set_terminal_prefix()
		return
	else 
		load(params.text)()
		return
	end
end

len = 0
for index in pairs(commands) do
	len = index
end

continue = true

-- Execute current command
for index, data in ipairs(commands) do
	if not continue then
		return
	end

	local text = data
	local command, args, flags = terminal.parse(data)

	function err(msg)
		print(command .. ": " .. msg)
		continue = false
	end

	function in_flags(val)
		for i, d in ipairs(flags) do
			if d == val then
				return true
			end
		end
		return false
	end

	function in_args(val)
		for i, d in ipairs(args) do
			if d == val then
				return true
			end
		end
		return false
	end

	output_table.do_print = index == len

	local input = {
		terminal=params.terminal,
		text=data,
		command=command,
		args=args,
		flags=flags,
		index=index,
		datastream=output_table,
		err=err,
		set_terminal_prefix=set_terminal_prefix,
		home_dir=io.get("/home/" .. usr.get_curr_user().name),
		get_objective=get_objective,
		help=help,
		in_args=in_args,
		in_flags=in_flags,
	}

	-- Search /cmd, ~/cmd, and the working dir for a file to execute
	local executable = io.get("/cmd/" .. command .. ".lua")

	if executable.is_null then
		executable = io.get("/home/" .. usr.get_curr_user().name .. "/cmd/" .. command .. ".lua")
	end
	if executable.is_null then
		local t_command = command:gsub("(.)(%.lua)$", "%1")
		executable = io.get(io.get_working_dir().get_path() .. t_command .. ".lua")
	end

	if not executable.is_null and not executable.is_directory then
		if (help(text:gsub("(.)(%.lua)$", "%1"))) then
			-- Get and print help message if it's needed
			print(globals.help_msgs[executable.name:gsub("(.)(%.lua)$", "%1")])
		else
			executable.execute(input)
		end
	elseif index > 1 then
		p = io.get_working_dir().get_path() .. command
		io.make_file(p)
		file = io.get(p)
		if (not file.is_null) and (not file.is_directory) then
			file.set_contents(output_table)
		end
	else
		print("lua: " .. command .. ": command not found")
	end
end
