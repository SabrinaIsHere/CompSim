package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Filesystem.ExecutionPermissions;
import com.morticia.compsim.Machine.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles users for machines
 *
 * @author Morticia
 * @version 1.0
 * @since 7/6/22
 */

public class UserHandler {
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

    public void addGroup(UserGroup group) {
        if (getGroup(group.groupName) == null) {
            groups.add(group);
        }
    }
}
