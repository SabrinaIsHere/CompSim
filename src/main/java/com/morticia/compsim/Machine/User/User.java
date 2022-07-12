package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Filesystem.VirtualFolder;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;
import com.morticia.compsim.Util.Lua.Lib.UserLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to make security/userspace more doable for machines
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public class User implements Serializable {
    public Machine machine;
    public UserGroup group;
    public List<UserGroup> groups;
    public String userName;
    public String password;
    public ExecutionPermissions execPerms;
    public VirtualFolder homeFolder;

    /**
     * Constructor
     *
     * @param handler The handler attached to this user (machine var is set from this)
     * @param userName The name of this user
     * @param password Password needed to assume this user
     * @param execPerms Permissions used when this user executes scripts
     */
    public User(UserHandler handler, String userName, String password, ExecutionPermissions execPerms) {
        this.machine = handler.machine;
        this.userName = userName;
        this.password = password;
        this.execPerms = execPerms;
        this.group = new UserGroup(machine, userName);
        this.groups = new ArrayList<>();
        handler.addGroup(group);
        if (machine.filesystem != null) {
            if (userName.equals("root") && handler.root == null) {
                this.homeFolder = machine.filesystem.getFolder("/root");
                if (homeFolder == null) {
                    this.homeFolder = new VirtualFolder(machine.filesystem, machine.filesystem.root, "root");
                    //machine.filesystem.getFolder("/home").addFolder(homeFolder);
                }
            } else {
                this.homeFolder = machine.filesystem.getFolder("/home/" + userName);
                if (homeFolder == null) {
                    this.homeFolder = new VirtualFolder(machine.filesystem, machine.filesystem.getFolder("/home"), userName);
                    //machine.filesystem.getfolder("/home").addFolder(homeFolder);
                }
            }
        }
        machine.logHandler.log("User [" + userName + "] initialized");
    }

    @Override
    public String getType() {
        return Constants.user_type;
    }

    @Override
    public String getDesig() {
        return userName;
    }

    @Override
    public String serialize() {
        String var = prepParams(new String[][]{
                {"user_name", userName},
                {"password", password},
                {"group", group.groupName},
        });
        return getPrefix() + var;
    }

    @Override
    public void parse(String txt) {
        // This function assumes that groups have already been initialized
        List<String[]> var = extractParams(txt);
        for (String[] i : var) {
            switch (i[0]) {
                case "n/a":
                    continue;
                case "user_name":
                    this.userName = i[1];
                    break;
                case "password":
                    this.password = i[1];
                    break;
                case "group":
                    this.group = machine.userHandler.getGroup(i[1]);
                    break;
            }
        }
        if (this.group == null) {
            this.group = new UserGroup(machine, userName);
            this.machine.userHandler.addGroup(group);
        }
        // If there's an existing user it gets overridden. This is so default users don't interfere with saved data
        this.machine.userHandler.removeUser(userName);
        this.machine.userHandler.addUser(this);
    }

    public LuaTable toTable() {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(false));
        table.set("name", userName);
        table.set("object_type", "user");
        table.set("user_update", new UserLib.user_update(this));
        table.set("set_user_name", new UserLib.set_user_name(this));
        table.set("get_groups", new UserLib.get_groups(this));
        table.set("add_to_group", new UserLib.add_to_group(this));
        table.set("remove_group", new UserLib.remove_from_group(this));
        table.set("get_default_perms", new UserLib.get_default_perms(this));
        table.set("set_default_perms", new UserLib.set_default_perms(this));
        table.set("get_home_dir", new UserLib.get_home_dir(this));
        return table;
    }

    public static LuaTable toBlankTable(String name) {
        LuaTable table = new LuaTable();
        table.set("is_null", LuaValue.valueOf(true));
        table.set("name", name);
        table.set("object_type", "user");
        return table;
    }
}
