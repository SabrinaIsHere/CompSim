package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Lua.Lib.UserLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A data container making it easier to group users
 *
 * @author Morticia
 * @version 1.0
 * @since 7/7/22
 */

public class UserGroup {
    public Machine machine;
    public UserHandler handler;
    public String groupName;
    public List<User> users;

    /**
     * Constructor
     *
     * @param machine Machine this is attached to
     * @param name Name of this group
     */
    public UserGroup(Machine machine, String name) {
        this.machine = machine;
        this.groupName = name;
        this.users = new ArrayList<>();
    }

    /**
     * Gets the user of the given name
     *
     * @param name Name of the user to get
     * @return The user. Null if no user is found
     */
    public User getUser(String name) {
        for (User i : users) {
            if (i.userName.equals(name)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Adds a given user to the group
     *
     * @param user User to add
     * @return Whether or not operation was successful
     */
    public boolean addUser(User user) {
        for (User value : users) {
            if (value.userName.equals(user.userName)) {
                return false;
            }
        }
        users.add(user);
        user.groups.add(this);
        return true;
    }

    /**
     * Removes a user from the group
     *
     * @param name Name of the user to remove
     * @return Whether or not the operation was successful
     */
    public boolean removeUser(String name) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).userName.equals(name)) {
                users.remove(i);
                users.get(i).groups.remove(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return groupName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserGroup) return ((UserGroup) obj).groupName.equals(this.groupName);
        return false;
    }

    /**
     * Returns this object as a lua table which can be passed to a lua script
     *
     * @return Table representing this object
     */
    public LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("name", groupName);
        table.set("object_type", "user_group");
        table.set("set_name", new UserLib.set_group_name(this));
        table.set("get_users", new UserLib.get_users(this));
        table.set("remove_user", new UserLib.remove_user(this));
        table.set("add_user", new UserLib.add_user(this));
        return table;
    }

    /**
     * Gets a table without data but that is formatted similarly to a full table
     *
     * @param name Name of the object
     * @return Blank table
     */
    public static LuaTable toBlankTable(String name) {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(true));
        table.set("name", name);
        table.set("object_type", "user_group");
        return table;
    }
}
