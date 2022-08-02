 for i, evn in ipairs(event.get_events()) do if evn.name == "packet_received" then globals["packet_evn"] = evn end end

