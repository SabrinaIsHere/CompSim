--[[
catra is the best shera character and you think I'm wrong walk into the sea

this just lists the contents of files
--]]

ln_end = ""
if params.in_flags("-e") or params.in_flags("-E") then
	ln_end = "$"
end

function eval(txt, index)
	local start = ""

	if params.in_flags("-n") then
		start = tostring(index) .. globals.htmlTab .. htmlSpace
	end

	return start .. txt .. ln_end
end

local paths = {}
if not (params.args[1] == nil) then
	for index, data in ipairs(params.args) do
		paths[index] = {params.get_objective(data), data}
	end
else
	-- Has to be done with a buffer otherwise it gets stuck in an infinite printing loop
	local buffer = {}
	for index, val in ipairs(params.datastream) do
		buffer[index] = eval(val, index)
	end
	for index, val in ipairs(buffer) do
		print(val:gsub("(.)([\n]+)$", "%1"))
	end
	return
end

for i, path in ipairs(paths) do
	local file = io.get(path[1])

	if file.is_null then
		params.err(path[2] .. ": No such file or directory")
		return
	elseif file.is_directory then
		params.err(path[2] .. ": Is a directory")
		return
	end

	for index, line in ipairs(file.get_contents()) do
		print(eval(line, index))
	end
end
