color = "FFFF00"

print(terminal.set_color("Lua (" .. _VERSION .. "-Lua v.5.2" .. ") Shell Enabled. 'Ctrl+q' or 'shell' to Quit.", color))
globals.is_shell_enabled = true
params.terminal.set_prefix(terminal.set_color(">" .. htmlSpace, color))
