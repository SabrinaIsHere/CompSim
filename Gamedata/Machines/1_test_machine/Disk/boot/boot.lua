local t = terminal.new_terminal()

while not t.is_ready() do

end
t = t.update()

print('Terminal ready')

local new_terminal = terminal.new_terminal()
while not new_terminal.is_ready() do

end
new_terminal = new_terminal.update()

t.set_prefix(terminal.set_color("[placeholder]$" .. htmlSpace, "37FF00"))
new_terminal.set_prefix(terminal.set_color("[placeholder]$" .. htmlSpace, "37FF00"))