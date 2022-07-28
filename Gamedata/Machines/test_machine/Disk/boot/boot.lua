globals["htmlTab"] = htmlSpace .. htmlSpace .. htmlSpace .. htmlSpace
globals["is_shell_enabled"] = false
globals["paths"] = {}
globals["prev_tab_autocomplete_text_called"] = true
-- Doesn't do much but it's handy for conditionally overwriting config files or whatever
globals["debug"] = true

-- Temporary io stream so there aren't nullpointer exceptions
tempIO = {}
stream.set_output(tempIO)

-- Make a terminal
local t = terminal.new_terminal()
while not t.is_ready() do

end
t = t.update()

t.set_output()
print = t.print

-- Get help messages
h = io.get("/lib/help_msgs.lua")
if not h.is_null then
	h()
end

-- Create/load system paths (to look for executables)
path_file = io.get("/.cfg/path.cfg")
if path_file.is_null or path_file.is_directory or globals.debug then
	io.make_file("/.cfg/path.cfg")
	path_file = io.get("/.cfg/path.cfg")
	contents = {
		"/cmd/"
	}
	cmd_folder = io.get("/cmd/")
	if not cmd_folder.is_null and cmd_folder.is_directory then
		for i, obj in ipairs(cmd_folder.get_children()) do
			if obj.is_directory then
				contents[i + 1] = obj.get_path()
			end
		end
	end
	path_file.set_contents(contents)
end

globals.paths = path_file.get_contents()
globals.path_file = path_file

-- Deal with boot scripts
scripts_file = io.get("/.cfg/start.cfg")
if scripts_file.is_null or scripts_file.is_directory then
	io.make_file("/.cfg/start.cfg")
	scripts_file = io.get("/.cfg/start.cfg")
end

for i, script in ipairs(scripts_file.get_contents()) do
	file = io.get(script)
	if not file.is_null and not file.is_directory then
		file.execute()
	end
end

-- Packages infrastructure
package_base = {
	name = "",
	base_folder = nil,

	init = function(self, home_path)
		-- Get/create base folder
		base_folder = io.get(home_path)
		if base_folder.is_null or not base_folder.is_directory then
			if not io.make_folder(home_path) then
				return
			end
			base_folder = io.get(home_path)
		end
		self.base_folder = base_folder

		-- Create/load from config file
		local conf_path = base_folder.get_path() .. ".config.cfg"
		local config = io.get(conf_path)
		if config.is_null or config.is_directory then
			io.make_file(conf_path)
			config = io.get(conf_path)
			-- Get name from end of path
			local contents = {
				home_path:gsub("^(.+)/(.-)(/-)$", "%2")
			}
			config.set_contents(contents)
		else
			local contents = config.get_contents()
			self.name = contents[1]
		end

		-- Register/create boot file
		io.make_folder(base_folder.get_path() .. "boot")
		local boot_path = base_folder.get_path() .. "boot/boot.lua"
		boot = io.get(boot_path)
		if boot.is_null or boot.is_directory then
			if not io.make_file(boot_path) then
				return
			end
			boot = io.get(boot_path)
		end
		start_scripts_file = io.get("/.cfg/start.cfg")
		scripts = start_scripts_file.get_contents()
		len = 0
		for i in pairs(scripts) do
			len = i
		end
		scripts[len + 1] = boot_path
		start_scripts_file.set_contents(scripts)

		-- Register command folder to path
		io.make_folder(base_folder.get_path() .. "bin")
		for i in pairs(globals.paths) do
			len = i
		end
		globals.paths[len + 1] = base_folder.get_path() .. "cmd"

		-- Create other needed folders
		io.make_folder(base_folder.get_path() .. "data")
		io.make_folder(base_folder.get_path() .. "bin")
	end
}

packge_mt = {
	__tostring=function(this)
		return "Package [" .. this.name .. "]"
	end,
	__call=function(this, init)
		return setmetatable(init or {}, {
			__index = package_base
		})
	end,

	__metatable=nil
}

setmetatable(package_base, packge_mt)

globals["package_base"] = package_base