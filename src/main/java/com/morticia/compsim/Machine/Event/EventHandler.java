package com.morticia.compsim.Machine.Event;

import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Lua.LuaParamData;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    public List<DiskFile> eventHandlers;
    public List<Event> events;

    public Machine machine;

    public EventHandler(Machine machine) {
        this.machine = machine;
        this.eventHandlers = new ArrayList<>();
        this.events = new ArrayList<>();

        registerEventHandlers(machine.filesystem.events);
    }

    public void registerEventHandlers(VirtualFolder f) {
        for (VirtualFolder i : f.folders) {
            registerEventHandlers(i);
        }

        for (VirtualFile i : f.files) {
            // TODO: 7/4/22 Think about registering events from files present
            eventHandlers.add(i.trueFile);
        }
    }

    public void triggerEvent(String eventName, List<String> data) {
        Event event = null;
        DiskFile eventHandler = null;

        for (Event i : events) {
            if (i.eventName.equals(eventName)) {
                event = i;
            }
        }

        if (event == null) {
            return;
        }

        for (DiskFile i : eventHandlers) {
            if (i.fileName.equals(eventName + ".lua")) {
                eventHandler = i;
            }
        }

        if (eventHandler == null) {
            return;
        }

        List<String> params = new ArrayList<>();
        params.addAll(event.eventData);
        params.addAll(data);

        eventHandler.execute(new LuaParamData(params, false));
    }
}
