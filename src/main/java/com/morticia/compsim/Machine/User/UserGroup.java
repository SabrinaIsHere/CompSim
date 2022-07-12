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

    public UserGroup(Machine machine, String name) {
        this.machine = machine;
        this.groupName = name;
        this.users = new ArrayList<>();
    }

    public User getUser(String name) {
        for (User i : users) {
            if (i.userName.equals(name)) {
                return i;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).userName.equals(user.userName)) {
                return false;
            }
        }
        users.add(user);
        user.groups.add(this);
        return true;
    }

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

    public static LuaTable toBlankTable(String name) {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(true));
        table.set("name", name);
        table.set("object_type", "user_group");
        return table;
    }
}
