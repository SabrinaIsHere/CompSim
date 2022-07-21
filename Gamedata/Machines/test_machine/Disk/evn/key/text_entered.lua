-- Avoids issues with io streams
params.terminal = params.m_terminal
print = params.terminal.print

-- Updates the terminal prefix
function set_terminal_prefix(shell)
	local start = terminal.set_color("[" .. usr.get_curr_user().name .. "@" .. machine.name, "37FF00")
	local mid = terminal.set_color(" " .. io.get_working_dir().name, "FFFFFF")
	local last = terminal.set_color("]$" .. htmlSpace, "37FF00")

	local t = start .. mid .. last
	shell.set_prefix(t);
end
params["update_prefix"] = set_terminal_prefix

-- Makes the terminal look more like the linux terminal
params.terminal.set_buffer('')
print(params.terminal.get_prefix() .. params.text)

-- Record output
output_table = {
	update = function(__self)
		params.terminal.print(__self[__self.index])
	end,

	read = function(__self)
		return params.terminal.get_line()
	end
};
stream.set_output(output_table)
params["output"] = output_table

-- Get commands
local commands = {}
string.gsub(params.text, "([^>]+)", function (w)
	w = string.gsub(w, '^%s*(.-)%s*$', '%1')
    table.insert(commands, w)
end)

local raw_text = params.text

-- Execute current command
for index, data in ipairs(commands) do
	local text = data
	local command, args, flags = terminal.parse(data)

	local input = {
		text=data,
		command=command,
		args=args,
		flags=flags,
		terminal=terminal,
		index=index,
		datastream=output_table
	}

	-- Search /cmd, ~/cmd, and the working dir for a file to execute
	local executable = io.get("/cmd/" .. command .. ".lua")

	if executable.is_null then
		executable = io.get("/home/" .. usr.get_curr_user().name .. "/cmd/" .. command .. ".lua")
	elseif executable.is_null then
		executable = io.get(io.get_working_dir.get_path() .. command .. ".lua")
	end

	if globals.is_shell_enabled then
		if command == "shell" then
			print("shell disabled")
			globals.is_shell_enabled = false
			set_terminal_prefix(params.terminal)
		else 
			load(params.text)()
		end
	elseif not executable.is_null and not executable.is_directory then
		executable.execute(input)
	else
		print("lua: " .. command .. ": command not found")
	end
	
	-- Reset the output table, done so there isn't contamination around output
	output_table = {
	update = function(__self)
		params.terminal.print(__self[__self.index])
	end,

	read = function(__self)
		return params.terminal.get_line()
	end
};
stream.set_output(output_table)
end
