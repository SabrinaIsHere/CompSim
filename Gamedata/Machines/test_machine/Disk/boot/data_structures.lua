registry = {
	data_file = nil,
	base_entry = {
		type = "entry",
		name = ""
	},

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
				contents = self.data_file.get_contents()
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
				self.data_file.set_contents(contents)
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
					match = match and (v.param == "*" or entry[v.param] == v.value)
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

registry_mt = {
	__call = function(self, init)
		return setmetatable(init or {}, {
			__index = registry,
			__newindex = function(self, key, value)
				if value.datatype == "entry" then
					-- Add entry/update entry id
					rawset(self, key, value)
					value.id = key

					-- Write data to data file
					contents = data_file.get_contents()
					for i, line in ipairs(contents) do
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
		})
	end
}

setmetatable(registry, registry_mt)

data_structures_namespace = {
	registry = registry
}
globals["data_structures"] = data_structures_namespace