globals["htmlTab"] = htmlSpace .. htmlSpace .. htmlSpace .. htmlSpace
globals["is_shell_enabled"] = false

tempIO = {}
stream.set_output(tempIO)

local t = terminal.new_terminal()
while not t.is_ready() do

end
t = t.update()

t.set_output()
print = t.print

h = io.get("/lib/help_msgs.lua")
if not h.is_null then
	h()
end