package com.morticia.compsim.Machine.Event;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store data about events
 *
 * @author Morticia
 * @version 1.0
 * @since 7/4/22
 */

// TODO: 7/4/22 Should probably refactor this since I'm definitely going to step on toes with the name
public class Event {
    public Machine machine;

    public String eventName;
    // Events will be sortable into folders by event type, if it can't find the event it'll look for
    // a folder with the name of the event type for the file to execute
    public String eventType;
    public List<String> eventData;
    public ExecutionPermissions eventHandlerExecPerms;

    /**
     * Constructor
     *
     * @param machine Machine this event is attached to
     * @param eventName The name of this event (relevant when it's being triggered/handled)
     * @param eventType Type of event (relevant to sorting of handlers)
     * @param eventData Keyed data included when handlers are executed
     * @param execPerms Permissions used when handler is executed
     */
    public Event(Machine machine, String eventName, String eventType, List<String> eventData, ExecutionPermissions execPerms) {
        this.machine = machine;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventHandlerExecPerms = execPerms;

        this.eventData = new ArrayList<>();
        this.eventData.add("event_name: " + eventName);
        this.eventData.add("event_type: " + eventType);
        // TODO: 7/4/22 Add in machine data via 'toLuaTable' function in machine
        this.eventData.addAll(eventData);
    }

    /**
     * Constructor
     *
     * @param machine Machine this is attached to
     * @param eventName The name of this event (relevant when it's being triggered/handled)
     * @param eventType Type of event (relevant to sorting of handlers)
     */
    public Event(Machine machine, String eventName, String eventType) {
        this.machine = machine;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventHandlerExecPerms = new ExecutionPermissions();
        this.eventHandlerExecPerms.canExecute = true;

        this.eventData = new ArrayList<>();
        this.eventData.add("event_name: " + eventName);
        this.eventData.add("event_type: " + eventType);
        // TODO: 7/4/22 Add in machine data via 'toLuaTable' function in machine
    }
}
