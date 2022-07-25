--[[
This is going a script to make and populate new packages because it's easier than remembering the exact
file structure every time

format: [command] [name] [category]
--]]

if params.args[2] ~= nil then
	cat_path = "/home/" .. usr.get_curr_user().name .. "/packages/" .. params.args[2] .. "/"
	package_path = cat_path .. params.args[1]
	io.make_folder(cat_path)
	new_package = globals.package_base {}
	new_package:init(package_path)
else
	params.err("Usage: [command] [package_name] [package_category]")
	return
end