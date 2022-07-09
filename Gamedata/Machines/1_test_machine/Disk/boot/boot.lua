local t = terminal.new_terminal()

while not t.is_ready() do

end

t = t.update()

print('Terminal ready')

t.set_prefix(terminal.set_color("[placeholder]$" .. htmlSpace, "37FF00"))