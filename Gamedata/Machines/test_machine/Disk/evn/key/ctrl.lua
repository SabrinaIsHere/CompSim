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

if params.key_code == 81 then
	globals.is_shell_enabled = false
	print(params.m_terminal.get_prefix())
	set_terminal_prefix()
end