package com.morticia.compsim.Machine.User;

import com.morticia.compsim.Machine.Machine;
import com.morticia.compsim.Util.Constants;
import com.morticia.compsim.Util.Disk.DataHandler.Serializable;

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
    Machine machine;
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

    @Override
    public String toString() {
        return groupName;
    }
}
