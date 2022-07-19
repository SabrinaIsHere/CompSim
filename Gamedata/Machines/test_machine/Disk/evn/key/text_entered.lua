-- Avoids issues with io streams
params.terminal = params.m_terminal
print = params.terminal.print

params["command"], params["args"], params["flags"] = terminal.parse(params.text)

-- Updates the terminal prefix
function set_terminal_prefix(shell)
	local start = terminal.set_color("[" .. usr.get_curr_user().name .. "@" .. machine.name, "37FF00")
	local mid = terminal.set_color(" " .. io.get_working_dir().name, "FFFFFF")
	local last = terminal.set_color("]$" .. htmlSpace, "37FF00")

	local t = start .. mid .. last
	shell.set_prefix(t);
end
params["update_prefix"] = set_terminal_prefix

-- Record output
---[[
output_table = {
	update = function(__self)
		print(__self[1])
		return "test";
	end,

	read = function(__self)
		return params.terminal.get_line()
	end
};
stream.set_output(output_table)--]]
params["output"] = output_table
params["input"] = {
	params=params,
	command=params.command,
	args=params.args,
	flags=params.flags,
	text=params.text,
	data=output_table.data
}

-- Makes the terminal look more like the linux terminal
params.terminal.set_buffer('')
print(params.terminal.get_prefix() .. params.text)

-- Search /cmd, ~/cmd, and the working dir for a file to execute
local executable = io.get("/cmd/" .. params.command .. ".lua")

if executable.is_null then
	executable = io.get("/home/" .. usr.get_curr_user().name .. "/cmd/" .. params.command .. ".lua")
elseif executable.is_null then
	executable = io.get(io.get_working_dir.get_path() .. params.command .. ".lua")
end

-- Output directions characters is going to be put in later, all the infrastructure is ready though

-- Execute the command if we're not in shell
if globals.is_shell_enabled then
	if params.command == "shell" then
		print("shell disabled")
		globals.is_shell_enabled = false
		set_terminal_prefix(params.terminal)
	else 
		load(params.text)()
	end
elseif not executable.is_null and not executable.is_directory then
	executable.execute(params.input)
else
	print("lua: " .. params.command .. ": command not found")
end