package com.morticia.compsim.Util.Lua.Lib;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Machine.User.User;
import com.morticia.compsim.Machine.User.UserGroup;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class UserLib extends TwoArgFunction {
    public Machine machine;

    public UserLib(Machine machine) {
        this.machine = machine;
    }

    @Override
    public LuaValue call(LuaValue mod_name, LuaValue env) {
        LuaValue library = tableOf();
        library.set("get_user", new get_user(machine));
        library.set("get_group", new get_group(machine));
        library.set("create_user", new create_user(machine));
        library.set("create_group", new create_group(machine));
        env.set("usr", library);
        return library;
    }

    // Metadata functions

    public static class get_user extends OneArgFunction {
        Machine machine;

        public get_user(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue name) {
            User u = machine.userHandler.getUser(name.tojstring());
            if (u == null) {
                return User.toBlankTable(name.tojstring());
            } else {
                return u.toTable();
            }
        }
    }

    public static class get_group extends OneArgFunction {
        Machine machine;

        public get_group(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue name) {
            UserGroup g = machine.userHandler.getGroup(name.tojstring());
            if (g == null) {
                return UserGroup.toBlankTable(name.tojstring());
            } else {
                return g.toTable();
            }
        }
    }

    public static class create_user extends OneArgFunction {
        Machine machine;

        public create_user(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue name) {
            User u = new User(machine.userHandler, name.tojstring(), "pass", machine.userHandler.defaultExecPerms);
            if (machine.userHandler.addUser(u)) {
                return u.toTable();
            } else {
                return User.toBlankTable(name.tojstring());
            }
        }
    }

    public static class create_group extends OneArgFunction {
        Machine machine;

        public create_group(Machine machine) {
            this.machine = machine;
        }

        @Override
        public LuaValue call(LuaValue name) {
            UserGroup g = new UserGroup(machine, name.tojstring());
            if (machine.userHandler.addGroup(g)) {
                return g.toTable();
            } else {
                return UserGroup.toBlankTable(name.tojstring());
            }
        }
    }

    // Group table functions

    public static class set_group_name extends OneArgFunction {
        UserGroup group;

        public set_group_name(UserGroup group) {
            this.group = group;
        }

        @Override
        public LuaValue call(LuaValue name) {
            try {
                group.groupName = name.tojstring();
                return group.toTable();
            } catch (Exception e) {
                return UserGroup.toBlankTable(name.tojstring());
            }
        }
    }

    public static class get_users extends ZeroArgFunction {
        UserGroup group;

        public get_users(UserGroup group) {
            this.group = group;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (User i : group.users) {
                table.add(i.toTable());
            }
            return table;
        }
    }

    public static class remove_user extends OneArgFunction {
        UserGroup group;

        public remove_user(UserGroup group) {
            this.group = group;
        }

        @Override
        public LuaValue call(LuaValue name) {
            try {
                if (!group.removeUser(name.tojstring())) {
                    throw new Error("User not found");
                }
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class add_user extends OneArgFunction {
        UserGroup group;

        public add_user(UserGroup group) {
            this.group = group;
        }

        @Override
        public LuaValue call(LuaValue name) {
            try {
                if (!group.addUser(new User(group.handler, name.tojstring(), "pass", group.handler.defaultExecPerms))) {
                    throw new Error("User already exists");
                }
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    // User table functions

    public static class user_update extends ZeroArgFunction {
        User user;

        public user_update(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call() {
            return user.group.getUser(user.userName).toTable();
        }
    }

    public static class set_user_name extends OneArgFunction {
        User user;

        public set_user_name(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call(LuaValue name) {
            try {
                user.userName = name.tojstring();
                return user.toTable();
            } catch (Exception e) {
                return UserGroup.toBlankTable(name.tojstring());
            }
        }
    }

    public static class get_groups extends ZeroArgFunction {
        User user;

        public get_groups(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();
            for (UserGroup i : user.groups) {
                table.add(i.toTable());
            }
            return table;
        }
    }

    public static class add_to_group extends OneArgFunction {
        User user;

        public add_to_group(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call(LuaValue name) {
            try {
                UserGroup g = user.machine.userHandler.getGroup(name.tojstring());
                if (g == null) {
                    throw new Error("[" + name + "] group not found");
                }
                g.addUser(user);
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class remove_from_group extends OneArgFunction {
        User user;

        public remove_from_group(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call(LuaValue name) {
            try {
                UserGroup g = user.machine.userHandler.getGroup(name.tojstring());
                if (g == null) {
                    throw new Error("[" + name + "] group not found");
                }
                g.removeUser(user.userName);
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class get_default_perms extends ZeroArgFunction {
        User user;

        public get_default_perms(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(user.execPerms.toString());
        }
    }

    public static class set_default_perms extends OneArgFunction {
        User user;

        public set_default_perms(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call(LuaValue perms) {
            try {
                user.execPerms.fromString(perms.tojstring());
                return Err.getBErrorTable();
            } catch (Exception e) {
                return Err.getErrorTable(e.getMessage());
            }
        }
    }

    public static class get_home_dir extends ZeroArgFunction {
        User user;

        public get_home_dir(User user) {
            this.user = user;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(user.homeFolder.getPath());
        }
    }
}
