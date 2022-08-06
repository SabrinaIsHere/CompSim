data_file_path = path:gsub("^(.-)/boot/(.-)$", "%1/data/entries.dt")
data_file = io.get(data_file_path)
if data_file.is_null or data_file.is_directory then
	io.make_file(data_file_path)
	data_file = io.get(data_file_path)
end

-- Used to convert between serialized and useable data
value_key_base = {
	type = "",

	fits = function(self, value)
		return false
	end,
	convert = function(self, value)
		return value
	end
}

value_key_base_mt = {
	__call = function(self, init)
		return setmetatable(init or {}, {
			__index = value_key_base
		})
	end
}
setmetatable(value_key_base, value_key_base_mt)

--[[ So if I need to add another one I can just copy paste from this blank
value_key_base {
	type = "",
	fits = function(self, value)
	end,
	convert = function(self, value)
	end
},
--]]

-- I know this looks like it's overkill for these two but I want to make it easy to extend later with more complicated
-- data types
value_keys = {
	value_key_base {
		type = "bool",
		fits = function(self, value)
			return value == "true" or value == "false"
		end,
		convert = function(self, value)
			return value == "true"
		end
	},
	value_key_base {
		type = "int",
		fits = function(self, value)
			return tonumber(value) ~= nil
		end,
		convert = function(self, value)
			return tonumber(value)
		end
	},
}

function convert_value(val)
	for i, key in ipairs(value_keys) do
		if key:fits(val) then
			return key:convert(val)
		end
	end
	return val
end

-- These are what comprises the database
database_entry = {
	-- This is used to identify the kind of table when added to the table on the backend
	datatype="database_entry",
	-- This is front end data
	name="default",
	id=-1,
	type="string",
	data="nichts",

	-- Initialize data from a string to aid in serialization
	fromstring = function(self, text)
		self.name = text:gsub("^%[(.-)/(.-)%]: %[(.-)%]/%[(.-)%]$", "%2")
		self.type = text:gsub("^%[(.-)/(.-)%]: %[(.-)%]/%[(.-)%]$", "%3")
		local data = text:gsub("^%[(.-)/(.-)%]: %[(.-)%]/%[(.-)%]$", "%4")

		for i, key in ipairs(value_keys) do
			if key.type == self.type and key:fits(data) then
				data = key:convert(data)
				break
			end
		end
		self.data = data
	end
}

database_entry_mt = {
	__call = function(this, init)
		return setmetatable(init or {}, {
			__index = database_entry,
			__tostring = function(self)
				return "[" .. tostring(self.id) .. "/" .. self.name .. "]: " .. "[" ..  self.type .. "]/" .. "[" .. tostring(self.data) .. "]"
			end,
			__metatable = nil
		})
	end,

	__metatable = nil
}

setmetatable(database_entry, database_entry_mt)

entries_mt = {
	__newindex = function(self, key, value)
		if value.datatype == "database_entry" then
			-- Add entry/update entry id
			rawset(self, key, value)
			value.id = key

			-- Write data to data file
			contents = data_file.get_contents()
			for i, line in ipairs(contents) do
				-- add_entry yuki string hallo
				if line:gsub("^(.-)/(.-)%]:(.-)$", "%2") == value.name then
					contents[i] = tostring(value)
					goto skip
				end
			end
			contents[#contents + 1] = tostring(value)
			::skip::
			data_file.set_contents(contents)
		else
			error("Invalid entry given")
		end
	end
}
entries = {
	has = function(self, name)
		for i, entry in ipairs(self) do
			if entry.name == name then
				return true
			end
		end
		return false
	end,
	get = function(self, text)
		for i, entry in ipairs(self) do
			if entry.name == text then
				return entry, i
			end
		end
		return nil
	end,
	add = function(self, entry)
		if self:has(entry.name) then
			return false
		end
		self[#self + 1] = entry
		return true
	end,
	replace = function(self, entry)
		for i, e in ipairs(self) do
			if e.name == entry.name then
				self[i] = nil
				self[i] = entry
				return true
			end
		end
		return false
	end,
	remove = function(self, text) 
		for i, entry in ipairs(self) do
			if entry.name == text then
				self[i] = nil
				if i > 1 then
					for j = i, #self do
						self[j - 1] = self[j]
					end
					self[#self] = nil
				end
				contents = data_file.get_contents()
				for i, line in ipairs(contents) do
					-- add_entry yuki string hallo
					if line:gsub("^(.-)/(.-)%]:(.-)$", "%2") == text then
						contents[i] = nil
						for j = i, #contents do
							contents[j - 1] = contents[j]
						end
						contents[#contents] = nil
					end
				end
				data_file.set_contents(contents)
				return true
			end
		end
		return false
	end,
	query = function(self, request)
		-- Returns a list of all entries matching the data given
		queries = {}
		request:gsub("%[(.-)%]", function(capture)
			queries[#queries + 1] = capture
		end)

		returns = {}
		for i, query in ipairs(queries) do
			values = {}
			query:gsub("([^,%s]+)", function(capture)
				local value = convert_value(capture:match("=(.+)[%s]-"))
				
				values[#values + 1] = {param=capture:match("[%s]-(.+)="), value=value}
			end)

			for i, entry in ipairs(self) do
				match = true
				for j, v in ipairs(values) do
					match = match and (entry[v.param] == v.value)
					if not match then
						goto skip
					end
				end
				if match then
					returns[#returns + 1] = entry
				end
				::skip::
			end
		end
		return returns
	end
}
setmetatable(entries, entries_mt)

namespace = {
	entry = database_entry,
	entries = entries,
	package_table = globals.package_base {},
	root_path = path:gsub("(.-)(/boot/boot.lua)", "%1"),
	convert_value = convert_value,
	value_keys = value_keys
}

namespace.package_table:init(path:gsub("^(.-)/boot/(.-)$", "%1"))

globals["database_namespace"] = namespace

help_msgs = io.get(path:gsub("^(.+)(boot.lua)$", "%1help_msgs.lua"))
if not help_msgs.is_null and not help_msgs.is_directory then
	help_msgs.execute({})
end

for i, line in ipairs(data_file.get_contents()) do
	new_entry = database_entry {}
	new_entry:fromstring(line)
	entries[#entries + 1] = new_entry
end