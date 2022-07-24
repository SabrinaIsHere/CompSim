-- Determine path the command is setting to
local path
if params.datastream.index > 0 then
	path = params.datastream[params.datastream.index]
elseif not (params.args[1] == nil) then
	if not (params.args[2] == nil) then
		params.err("too many arguments")
		return
	end
	-- If this is an objective or relative path
	arg = params.args[1]
	if string.sub(arg, 1, 1) == "/" then
		path = arg
	else
		path = io.get_working_dir().get_path() .. arg
	end
else
	path = "/home/" .. usr.get_curr_user().name
end

-- Set working directory and handle error
error = io.set_working_dir(path)
if not error.error then
	params.set_terminal_prefix(params.terminal)
else
	params.err("No such file or directory")
end