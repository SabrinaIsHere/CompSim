--[[
This is used by packets to get entries depending on given criteria in the payload
--]]

database = globals.database_namespace

ret = database.entries:query(params.payload)

params.ret(ret)