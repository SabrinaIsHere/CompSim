package com.morticia.compsim.Machine.Event;

import com.morticia.compsim.IO.GUI.Terminal;
import com.morticia.compsim.Machine.Filesystem.VirtualFile;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Disk.DiskFile;
import com.morticia.compsim.Util.Lua.LuaParamData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class to handle events for machines
 *
 * @author Morticia
 * @version 1.0
 * @since 7/4/22
 */

public class EventHandler {
    public List<DiskFile> eventHandlers;
    // This is a group of events
    public List<Event> eventList;
    public CopyOnWriteArrayList<String[]> events;

    public Machine machine;

    /**
     * Constructor
     *
     * @param machine Machine this is attached to
     */
    public EventHandler(Machine machine) {
        this.machine = machine;
        this.eventHandlers = new ArrayList<>();
        this.eventList = new ArrayList<>();
        this.events = new CopyOnWriteArrayList<>();

        registerEventHandlers(machine.filesystem.events);
    }

    /**
     * Registers event handlers from the folder provided. Recursively checks all folders
     *
     * @param f Folder to register from
     */
    public void registerEventHandlers(VirtualFolder f) {
        for (VirtualFolder i : f.folders) {
            registerEventHandlers(i);
        }

        for (VirtualFile i : f.files) {
            // TODO: 7/4/22 Think about registering events from files present
            eventHandlers.add(i.trueFile);
        }
    }

    /**
     * Adds an event to the qeue of events to be processed
     *
     * @param eventName Name of the event to add
     * @param data Additional data to pass to the lua handler
     */
    public void addEvent(String eventName, List<String> data) {
        events.add(new String[] {eventName, data.toString()});
    }

    /**
     * Adds an event to the qeue of events to be processed
     *
     * @param eventName Name of the event to add
     * @param data Additional data to pass to the lua handler
     */
    public void addEvent(String eventName, String[] data) {
        events.add(new String[] {eventName, (List.of(data)).toString()});
    }

    /**
     * Triggers an event, calling a handler with the name and data given. You should not really be calling this function
     *
     * @param eventName Name of the event to trigger
     * @param data Data to include in execution globals
     */
    public void triggerEvent(String eventName, List<String> data) {
        Event event = null;
        DiskFile eventHandler = null;

        for (Event i : eventList) {
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

        Terminal t = machine.guiHandler.p_terminal;
        if (t == null) {
            eventHandler.execute(machine, new LuaParamData(params, false));
        } else {
            eventHandler.execute(machine, new LuaParamData(params, false).addTable("terminal", t.toTable()));
        }

        StringBuilder sb = new StringBuilder("[");

        for (String i : params) {
            sb.append(i).append(" | ");
        }
        sb.replace(sb.lastIndexOf(" | "), sb.length(), "");
        sb.append("]");

        machine.logHandler.log("[" + eventName + "] event triggered: " + sb);
    }

    /**
     * Triggers an event, calling a handler with the name and data given. You should not really be calling this function
     *
     * @param eventName Name of the event to trigger
     * @param data Data to include in execution globals
     */
    public void triggerEvent(String eventName, String[] data) {
        triggerEvent(eventName, Arrays.asList(data));
    }

    public void handleEvents() {
        if (events.size() > 0) {
            String[] str = events.get(0);
            triggerEvent(str[0], Serializable.getListMembers(str[1]));
            events.remove(0);
        }
    }

    public boolean registerEvent(Event event) {
        for (Event i : eventList) {
            if (i.eventName.equals(event.eventName)) {
                return false;
            }
        }
        eventList.add(event);
        return true;
    }
}
