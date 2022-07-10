local t = terminal.new_terminal()

while not t.is_ready() do

end
t = t.update()
t.set_prefix(terminal.set_color("[placeholder]$" .. htmlSpace, "37FF00"))

print('Terminal ready')