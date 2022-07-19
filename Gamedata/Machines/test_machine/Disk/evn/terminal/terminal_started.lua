params.terminal.set_title(machine.name)

local start = terminal.set_color("[" .. usr.get_curr_user().name .. "@" .. machine.name, "37FF00")
local mid = terminal.set_color(" " .. io.get_working_dir().name, "FFFFFF")
local last = terminal.set_color("]$" .. htmlSpace, "37FF00")

local t = start .. mid .. last

params.terminal.set_prefix(t)