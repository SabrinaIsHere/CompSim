package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles users for machines
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public class UserHandler implements Serializable {
    public Machine machine;
    public List<User> users;
    public List<UserGroup> groups;
    // This is at this scope so things can be easily compared to it or executed with it
    public ExecutionPermissions rootExecPerms;
    public User root;
    public User currUser;

    public UserHandler(Machine machine) {
        this.machine = machine;
        // TODO: 7/5/22 Load users from metafile / defaults
        this.rootExecPerms = new ExecutionPermissions();
        this.rootExecPerms.canExecute = true;
        this.rootExecPerms.kernelTableAccess = true;
        this.rootExecPerms.setLibAccess(new String[] {"all"});
        this.users = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.root = new User(this, "root", "root", rootExecPerms);
        this.currUser = new User(this, "test_user", "123", new ExecutionPermissions());

        // TODO: 7/7/22 Remove this, init from metafile
        this.users.add(root);
        this.users.add(currUser);
    }

    public User getUser(String name) {
        for (User i : users) if (i.userName.equals(name)) return i;
        return null;
    }

    public UserGroup getGroup(String name) {
        for (UserGroup i : groups) if (i.groupName.equals(name)) return i;
        return null;
    }

    public void addUser(User user) {
        if (getUser(user.userName) == null) {
            users.add(user);
        }
    }

    public void addGroup(UserGroup group) {
        if (getGroup(group.groupName) == null) {
            groups.add(group);
        }
    }

    public void removeUser(String userName) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).userName.equals(userName)) {
                users.remove(i);
                return;
            }
        }
    }

    public void removeGroup(String groupName) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).groupName.equals(groupName)) {
                groups.remove(i);
                return;
            }
        }
    }

    public void saveUsers() {
        for (User i : users) {
            machine.dataHandler.add(i);
        }
    }

    @Override
    public String getType() {
        return Constants.user_handler_type;
    }

    @Override
    public String getDesig() {
        return Constants.user_handler_type;
    }

    @Override
    public String serialize() {
        return getPrefix() + prepParams(new String[][]{
                {"groups", groups.toString()}
        });
    }

    @Override
    public void parse(String txt) {
        List<String[]> var = extractParams(txt);
        for (String[] i : var) {
            switch (i[0]) {
                case "n/a":
                    continue;
                case "groups":
                    String[] str_1 = getListMembers(i[1]);
                    for (String j : str_1) {
                        addGroup(new UserGroup(machine, j));
                    }
                    break;
            }
        }
    }
}
