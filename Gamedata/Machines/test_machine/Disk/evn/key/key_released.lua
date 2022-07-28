-- Tab entered
if params.key_code == 9 then
	local text = params.m_terminal.get_buffer()
	local command, args, flags = terminal.parse(text)

	if text == "" or text:match("^(%s)$") ~= nil then
		return
	end

	arg_loc = #args

	if arg_loc == 0 and (command:match("(/)") == nil) then
		matches = {}
		for index, path in ipairs(globals.paths) do
			folder = io.get(path)
			if not folder.is_null and folder.is_directory then
				for i, obj in ipairs(folder.get_children()) do
					if not obj.is_directory then
						name = obj.name:gsub("(.-)%.(.+)", "%1")
						if name:match("^(" .. command .. ")") ~= nil then
							matches[#matches + 1] = name
						end
					end
				end
			end
		end

		if #matches < 1 then
			return
		elseif #matches == 1 then
			new_buffer = params.m_terminal.get_buffer():gsub("(" .. command .. ")", matches[1])
			params.m_terminal.set_buffer(new_buffer)
			globals["prev_tab_autocomplete_text_called"] = true
		elseif globals.prev_tab_autocomplete_text == text and globals.prev_tab_autocomplete_text_called then
			print(params.m_terminal.get_prefix() .. text)
			buffer = ""
			for i, name in ipairs(matches) do
				buffer = buffer .. name .. globals.htmlTab
			end
			print(buffer)
			globals["prev_tab_autocomplete_text_called"] = false
		elseif not globals.prev_tab_autocomplete_text == text then
			globals["prev_tab_autocomplete_text_called"] = true
		end
	else
		-- This is gonna be dependant on the command so I'll figure out a system for it later
	end
	globals["prev_tab_autocomplete_text"] = text
end